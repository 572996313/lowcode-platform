@echo off
chcp 65001 > nul
echo ========================================
echo 低代码平台 - 一键启动脚本
echo ========================================
echo.

REM 检查后端是否已启动
echo [检查] 后端服务状态...
curl -s http://localhost:8765/doc.html >nul 2>&1
if %errorlevel% equ 0 (
    echo [信息] 后端服务已在运行 (http://localhost:8765)
) else (
    echo [启动] 后端服务...
    start "低代码平台-后端" cmd /k "cd backend && mvn spring-boot:run"
    echo [等待] 等待后端服务启动...
    timeout /t 30 /nobreak >nul
)
echo.

REM 检查前端是否已启动
echo [检查] 前端服务状态...
curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo [信息] 前端服务已在运行 (http://localhost:3000)
) else (
    echo [启动] 前端服务...
    start "低代码平台-前端" cmd /k "cd frontend && npm run dev"
    echo [等待] 等待前端服务启动...
    timeout /t 10 /nobreak >nul
)
echo.

echo ========================================
echo 启动完成！
echo ========================================
echo.
echo 访问地址:
echo   前端: http://localhost:3000
echo   后端API文档: http://localhost:8765/doc.html
echo   Druid监控: http://localhost:8765/druid (admin/admin123)
echo.
pause
