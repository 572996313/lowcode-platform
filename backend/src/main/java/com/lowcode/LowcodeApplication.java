package com.lowcode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 低代码平台启动类
 */
@SpringBootApplication
@MapperScan("com.lowcode.mapper")
public class LowcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LowcodeApplication.class, args);
        System.out.println("====================================");
        System.out.println("  低代码平台启动成功!");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
}
