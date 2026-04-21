package com.lowcode.generator;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 轻量级模板引擎
 *
 * 支持语法：
 * - ${varName}           变量替换
 * - ${item.method()}      对象方法调用
 * - <#list list as item>...</#list> 循环
 */
@Slf4j
public class TemplateEngine {

    private static final Pattern LIST_PATTERN = Pattern.compile(
        "<#list\\s+(\\w+)\\s+as\\s+(\\w+)>((?:(?!<#/list>).)*)</#list>",
        Pattern.DOTALL
    );

    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * 渲染模板
     *
     * @param templateName 模板文件名（从 classpath 读取）
     * @param data         渲染数据
     * @return 渲染后的代码
     */
    public String render(String templateName, Map<String, Object> data) {
        // 1. 读取模板文件
        String template = loadTemplate(templateName);
        if (template == null) {
            throw new RuntimeException("模板文件不存在: " + templateName);
        }

        // 2. 处理循环指令
        template = processListDirective(template, data);

        // 3. 替换变量
        template = processVariables(template, data);

        return template;
    }

    /**
     * 从 classpath 读取模板文件
     */
    private String loadTemplate(String templateName) {
        // 确保模板名前缀正确
        if (!templateName.startsWith("/")) {
            templateName = "/" + templateName;
        }
        if (!templateName.startsWith("/templates")) {
            templateName = "/templates" + templateName;
        }

        try (InputStream is = getClass().getResourceAsStream(templateName)) {
            if (is == null) {
                log.error("模板文件未找到: {}", templateName);
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                return content.toString();
            }
        } catch (IOException e) {
            log.error("读取模板失败: {}", templateName, e);
            throw new RuntimeException("读取模板失败: " + templateName, e);
        }
    }

    /**
     * 处理循环指令 <#list list as item>...</#list>
     */
    private String processListDirective(String template, Map<String, Object> data) {
        Matcher matcher = LIST_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String listName = matcher.group(1);     // 列表变量名
            String itemName = matcher.group(2);    // 项变量名
            String itemTemplate = matcher.group(3); // 项模板

            Object listObj = data.get(listName);
            if (listObj instanceof List<?> list) {
                StringBuilder itemResult = new StringBuilder();
                for (Object item : list) {
                    // 处理项模板中的变量
                    String itemContent = processItemVariables(itemTemplate, item);
                    itemResult.append(itemContent);
                }
                matcher.appendReplacement(result, Matcher.quoteReplacement(itemResult.toString()));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 处理项中的变量，包括方法调用
     */
    private String processItemVariables(String template, Object item) {
        // 处理 ${item.field} 和 ${item.method()}
        Matcher matcher = VAR_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String expression = matcher.group(1);
            String value = evaluateExpression(expression, item);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value : ""));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 替换顶层变量
     */
    private String processVariables(String template, Map<String, Object> data) {
        Matcher matcher = VAR_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String expression = matcher.group(1);
            // 检查是否是顶层变量（不是 item.field 形式）
            if (!expression.contains(".")) {
                Object value = data.get(expression);
                matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value.toString() : ""));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 执行表达式，支持：
     * - item.field      获取属性
     * - item.method()   调用方法
     */
    private String evaluateExpression(String expression, Object item) {
        try {
            if (expression.contains(".")) {
                String[] parts = expression.split("\\.");
                Object current = item;

                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i];

                    // 检查是否有方法调用
                    if (part.endsWith("()")) {
                        String methodName = part.substring(0, part.length() - 2);
                        Method method = findMethod(current.getClass(), methodName);
                        if (method != null) {
                            current = method.invoke(current);
                        } else {
                            return null;
                        }
                    } else {
                        // 获取属性
                        String getterName = "get" + capitalize(part);
                        Method getter = findMethod(current.getClass(), getterName);
                        if (getter != null) {
                            current = getter.invoke(current);
                        } else {
                            return null;
                        }
                    }
                }

                return current != null ? current.toString() : "";
            }
            return null;
        } catch (Exception e) {
            log.debug("表达式求值失败: {}", expression, e);
            return null;
        }
    }

    /**
     * 查找方法（支持继承）
     */
    private Method findMethod(Class<?> clazz, String methodName) {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                    return method;
                }
            }
        }
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}