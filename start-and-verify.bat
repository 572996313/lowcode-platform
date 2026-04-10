@echo off
chcp 65001 > nul
echo ========================================
echo 低代码平台 - 启动并验证
echo ========================================
echo.

REM 检查后端是否已启动
echo [1/4] 检查后端服务...
curl -s http://localhost:8765/doc.html >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] 后端服务已在运行
) else (
    echo [!] 后端服务未运行，正在启动...
    start "低代码平台-后端" cmd /k "cd backend && mvn spring-boot:run"
    echo [√] 后端服务启动中，请等待30秒...
    timeout /t 30 /nobreak >nul
)
echo.

REM 检查前端是否已启动
echo [2/4] 检查前端服务...
curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] 前端服务已在运行
) else (
    echo [!] 前端服务未运行，正在启动...
    start "低代码平台-前端" cmd /k "cd frontend && npm run dev"
    echo [√] 前端服务启动中，请等待10秒...
    timeout /t 10 /nobreak >nul
)
echo.

REM 验证API
echo [3/4] 验证后端API...
curl -s http://localhost:8765/api/form-config-new/all >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] 表单API正常
) else (
    echo [×] 表单API未响应
)

curl -s http://localhost:8765/api/table-config-new/all >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] 表格API正常
) else (
    echo [×] 表格API未响应
)

curl -s http://localhost:8765/api/page-config-new/all >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] 页面API正常
) else (
    echo [×] 页面API未响应
)
echo.

REM 完成
echo [4/4] 启动完成！
echo.
echo ========================================
echo 访问地址：
echo   前端: http://localhost:3000
echo   后端API文档: http://localhost:8765/doc.html
echo   Druid监控: http://localhost:8765/druid
echo.
echo 验证步骤：
echo   1. 登录系统 (admin/123456)
echo   2. 在左侧菜单找到「低代码管理」分组
echo   3. 测试以下功能：
echo      - 数据集管理：查看数据集列表
echo      - 表单管理：查看、搜索、预览、删除表单
echo      - 表格管理：查看、搜索、预览、删除表格
echo      - 页面管理：查看、搜索、预览、发布、删除页面
echo ========================================
echo.
pause
