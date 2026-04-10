@echo off
chcp 65001 > nul
echo ========================================
echo 低代码平台 - 数据库升级脚本
echo ========================================
echo.

REM 设置数据库连接信息
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=lowcode_platform
set DB_USER=root
set DB_PASS=1234

echo 数据库配置:
echo   主机: %DB_HOST%:%DB_PORT%
echo   数据库: %DB_NAME%
echo   用户: %DB_USER%
echo.

REM 检查MySQL是否可用
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 找不到MySQL客户端！
    echo.
    echo 请将MySQL的bin目录添加到系统PATH环境变量中，例如:
    echo   set PATH=%PATH%;C:\Program Files\MySQL\MySQL Server 8.0\bin
    echo.
    pause
    exit /b 1
)

echo [1/5] 执行数据集配置表创建脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < docs\sql\refactor\001_create_dataset_config.sql
if %errorlevel% neq 0 (
    echo [错误] 执行失败！
    pause
    exit /b 1
)
echo [成功] 数据集配置表创建完成
echo.

echo [2/5] 执行字段元数据表创建脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < docs\sql\refactor\002_create_field_metadata.sql
if %errorlevel% neq 0 (
    echo [错误] 执行失败！
    pause
    exit /b 1
)
echo [成功] 字段元数据表创建完成
echo.

echo [3/5] 执行页面配置表创建脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < docs\sql\refactor\003_create_page_config_new.sql
if %errorlevel% neq 0 (
    echo [错误] 执行失败！
    pause
    exit /b 1
)
echo [成功] 页面配置表创建完成
echo.

echo [4/5] 执行表单配置表创建脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < docs\sql\refactor\004_create_form_config_new.sql
if %errorlevel% neq 0 (
    echo [错误] 执行失败！
    pause
    exit /b 1
)
echo [成功] 表单配置表创建完成
echo.

echo [5/5] 执行表格配置表创建脚本...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < docs\sql\refactor\005_create_table_config_new.sql
if %errorlevel% neq 0 (
    echo [错误] 执行失败！
    pause
    exit /b 1
)
echo [成功] 表格配置表创建完成
echo.

echo ========================================
echo 所有SQL脚本执行完成！
echo ========================================
echo.
pause
