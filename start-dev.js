#!/usr/bin/env node

/**
 * 低代码平台开发环境启动脚本（增强版）
 *
 * 功能：
 * - 自动检测并关闭占用端口的进程
 * - 环境检查（Java、Maven、Node.js、MySQL、Redis）
 * - 后端健康检查，等待服务真正就绪
 * - 支持命令行参数（--frontend-only、--backend-only、--no-check）
 * - 彩色输出和启动动画
 * - 自动打开浏览器
 * - 完整的日志输出管理
 *
 * 使用示例：
 *   node start-dev.js              # 启动前后端
 *   node start-dev.js --frontend   # 只启动前端
 *   node start-dev.js --backend    # 只启动后端
 *   node start-dev.js --no-check   # 跳过环境检查
 */

const { spawn, exec } = require('child_process');
const path = require('path');
const fs = require('fs');
const http = require('http');

// ==================== 配置 ====================
const CONFIG = {
  BACKEND_PORT: 8765,
  FRONTEND_PORT: 3000,
  PROJECT_ROOT: __dirname,
  BACKEND_DIR: path.join(__dirname,  'backend'),
  FRONTEND_DIR: path.join(__dirname,  'frontend'),
  MYSQL_HOST: 'localhost',
  MYSQL_PORT: 3306,
  REDIS_HOST: 'localhost',
  REDIS_PORT: 6379,
  BACKEND_STARTUP_TIMEOUT: 60000,  // 后端启动超时时间（毫秒）
  HEALTH_CHECK_INTERVAL: 2000,     // 健康检查间隔
  AUTO_OPEN_BROWSER: false         // 是否自动打开浏览器（Vite已内置）
};

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m',
  white: '\x1b[37m',
  gray: '\x1b[90m',
  bright: '\x1b[1m'
};

function log(message, color = 'reset') {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

function clearLine() {
  process.stdout.write('\r\x1b[K');
}

// ==================== 工具函数 ====================

/**
 * 解析命令行参数
 */
function parseArgs() {
  const args = process.argv.slice(2);
  const options = {
    frontendOnly: false,
    backendOnly: false,
    skipCheck: false,
    verbose: false
  };

  for (const arg of args) {
    if (arg === '--frontend' || arg === '-f') options.frontendOnly = true;
    if (arg === '--backend' || arg === '-b') options.backendOnly = true;
    if (arg === '--no-check' || arg === '-n') options.skipCheck = true;
    if (arg === '--verbose' || arg === '-v') options.verbose = true;
  }

  return options;
}

/**
 * 延迟函数
 */
function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 执行命令并获取输出
 */
function execCommand(cmd, options = {}) {
  return new Promise((resolve, reject) => {
    exec(cmd, { ...options, encoding: 'utf-8' }, (error, stdout, stderr) => {
      if (error) {
        reject({ error, stdout, stderr });
      } else {
        resolve(stdout);
      }
    });
  });
}

/**
 * 检查命令是否存在
 */
async function commandExists(cmd) {
  try {
    const platform = process.platform;
    const checkCmd = platform === 'win32' ? `where ${cmd}` : `which ${cmd}`;
    await execCommand(checkCmd);
    return true;
  } catch {
    return false;
  }
}

/**
 * 获取命令版本
 */
async function getVersion(cmd, versionFlag = '--version') {
  try {
    const output = await execCommand(`${cmd} ${versionFlag}`);
    return output.trim().split('\n')[0];
  } catch {
    return null;
  }
}

/**
 * HTTP 健康检查
 */
function checkHealth(url, timeout = 5000) {
  return new Promise((resolve) => {
    const req = http.get(url, { timeout }, (res) => {
      resolve(res.statusCode === 200);
    });

    req.on('error', () => resolve(false));
    req.on('timeout', () => {
      req.destroy();
      resolve(false);
    });
  });
}

// ==================== 端口管理 ====================

/**
 * 检测端口是否被占用
 */
async function checkPort(port) {
  const platform = process.platform;

  try {
    if (platform === 'win32') {
      const stdout = await execCommand(`netstat -ano | findstr :${port}`);
      if (!stdout || stdout.trim() === '') return false;

      const lines = stdout.trim().split('\n');
      const pids = new Set();
      for (const line of lines) {
        const parts = line.trim().split(/\s+/);
        const pid = parts[parts.length - 1];
        if (pid && pid !== '0') pids.add(pid);
      }
      return Array.from(pids);
    } else {
      const stdout = await execCommand(`lsof -ti:${port}`);
      if (!stdout || stdout.trim() === '') return false;
      return stdout.trim().split('\n').filter(Boolean);
    }
  } catch {
    return false;
  }
}

/**
 * 关闭占用端口的进程
 */
async function killPort(port, name) {
  const pids = await checkPort(port);

  if (!pids || pids.length === 0) {
    log(`✓ ${name} 端口 ${port} 未被占用`, 'green');
    return true;
  }

  log(`⚠ ${name} 端口 ${port} 已被占用 (PID: ${pids.join(', ')})`, 'yellow');

  const platform = process.platform;
  const killPromises = pids.map(pid => {
    const cmd = platform === 'win32' ? `taskkill /F /PID ${pid}` : `kill -9 ${pid}`;
    return execCommand(cmd).catch(() => null);
  });

  await Promise.all(killPromises);
  await delay(1500);
  log(`✓ 已关闭占用端口 ${port} 的进程`, 'green');
  return true;
}

// ==================== 环境检查 ====================

/**
 * 检查 Java 环境
 */
async function checkJava() {
  log('检查 Java 环境...', 'gray');

  if (!await commandExists('java')) {
    log('✗ 未找到 Java，请安装 JDK 17 或更高版本', 'red');
    log('  下载地址: https://adoptium.net/', 'yellow');
    return false;
  }

  try {
    const version = await execCommand('java -version 2>&1');
    const versionStr = version.split('\n')[0];
    log(`✓ ${versionStr}`, 'green');

    // 检查版本是否 >= 17
    const match = version.match(/version "?(\d+)/);
    if (match && parseInt(match[1]) < 17) {
      log('⚠ Java 版本过低，需要 JDK 17 或更高版本', 'yellow');
      return false;
    }
  } catch {
    log('✗ 无法获取 Java 版本', 'red');
    return false;
  }

  return true;
}

/**
 * 检查 Maven 环境
 */
async function checkMaven() {
  log('检查 Maven 环境...', 'gray');

  if (!await commandExists('mvn')) {
    log('✗ 未找到 Maven，请先安装 Maven', 'red');
    log('  下载地址: https://maven.apache.org/download.cgi', 'yellow');
    return false;
  }

  try {
    const version = await getVersion('mvn', '-v');
    if (version) {
      log(`✓ ${version}`, 'green');
    }
  } catch {
    log('⚠ 无法获取 Maven 版本', 'yellow');
  }

  return true;
}

/**
 * 检查 Node.js 环境
 */
async function checkNode() {
  log('检查 Node.js 环境...', 'gray');

  if (!await commandExists('node')) {
    log('✗ 未找到 Node.js，请先安装 Node.js', 'red');
    log('  下载地址: https://nodejs.org/', 'yellow');
    return false;
  }

  try {
    const version = await getVersion('node');
    if (version) {
      log(`✓ ${version}`, 'green');
    }
  } catch {
    log('⚠ 无法获取 Node.js 版本', 'yellow');
  }

  return true;
}

/**
 * 检查 MySQL 连接
 */
async function checkMySQL() {
  log('检查 MySQL 连接...', 'gray');

  try {
    // 尝试连接 MySQL 端口
    const net = require('net');
    await new Promise((resolve, reject) => {
      const socket = new net.Socket();
      socket.setTimeout(3000);
      socket.connect(CONFIG.MYSQL_PORT, CONFIG.MYSQL_HOST, () => {
        socket.destroy();
        resolve();
      });
      socket.on('error', reject);
      socket.on('timeout', () => {
        socket.destroy();
        reject(new Error('timeout'));
      });
    });
    log(`✓ MySQL 已运行 (${CONFIG.MYSQL_HOST}:${CONFIG.MYSQL_PORT})`, 'green');
    return true;
  } catch {
    log('⚠ 无法连接到 MySQL，请确保 MySQL 已启动', 'yellow');
    log('  如果使用 Docker，请运行: docker start <mysql容器名>', 'gray');
    return false;
  }
}

/**
 * 检查 Redis 连接
 */
async function checkRedis() {
  log('检查 Redis 连接...', 'gray');

  try {
    const net = require('net');
    await new Promise((resolve, reject) => {
      const socket = new net.Socket();
      socket.setTimeout(3000);
      socket.connect(CONFIG.REDIS_PORT, CONFIG.REDIS_HOST, () => {
        socket.destroy();
        resolve();
      });
      socket.on('error', reject);
      socket.on('timeout', () => {
        socket.destroy();
        reject(new Error('timeout'));
      });
    });
    log(`✓ Redis 已运行 (${CONFIG.REDIS_HOST}:${CONFIG.REDIS_PORT})`, 'green');
    return true;
  } catch {
    log('⚠ 无法连接到 Redis，请确保 Redis 已启动', 'yellow');
    log('  如果使用 Docker，请运行: docker start <redis容器名>', 'gray');
    return false;
  }
}

/**
 * 执行完整的环境检查
 */
async function checkEnvironment(skip = false) {
  if (skip) {
    log('⚠ 跳过环境检查', 'yellow');
    return true;
  }

  log('\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'cyan');
  log('环境检查', 'cyan');
  log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n', 'cyan');

  const results = await Promise.all([
    checkJava(),
    checkMaven(),
    checkNode(),
    checkMySQL(),
    checkRedis()
  ]);

  const allPassed = results.every(r => r === true);

  if (allPassed) {
    log('\n✓ 环境检查通过', 'green');
  } else {
    log('\n⚠ 部分环境检查未通过，但继续启动...', 'yellow');
  }

  return true;
}

// ==================== 服务启动 ====================

/**
 * 启动后端服务
 */
async function startBackend(options = {}) {
  const { verbose = false } = options;

  if (!fs.existsSync(CONFIG.BACKEND_DIR)) {
    log(`✗ 后端目录不存在: ${CONFIG.BACKEND_DIR}`, 'red');
    return null;
  }

  log('\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'cyan');
  log('启动后端服务', 'cyan');
  log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'cyan');

  // 检查 Maven
  if (!await commandExists('mvn')) {
    log('✗ 未找到 Maven，无法启动后端', 'red');
    return null;
  }

  // Windows 下需要使用 shell 来解析命令
  const useShell = process.platform === 'win32';

  const mvn = spawn('mvn', ['spring-boot:run'], {
    cwd: CONFIG.BACKEND_DIR,
    stdio: ['ignore', 'pipe', 'pipe'],
    shell: useShell,
    env: { ...process.env }
  });

  let backendStarted = false;
  let outputBuffer = '';

  const handleOutput = (data, isError = false) => {
    const output = data.toString();
    outputBuffer += output;

    const color = isError ? 'yellow' : 'blue';

    // 显示重要信息
    if (output.includes('Started') || output.includes('ERROR') ||
        output.includes('Exception') || output.includes('BUILD FAILURE')) {
      for (const line of output.split('\n')) {
        if (line.trim()) {
          log(line.trim(), color);
        }
      }
    }

    // 检测启动完成
    if (output.includes('Started LowcodeApplication')) {
      backendStarted = true;
    }
  };

  mvn.stdout.on('data', (data) => handleOutput(data, false));
  mvn.stderr.on('data', (data) => handleOutput(data, true));

  mvn.on('close', (code) => {
    if (code !== 0 && code !== null) {
      log(`\n✗ 后端服务退出 (代码: ${code})`, 'red');
    }
  });

  // 等待后端启动
  log('正在启动后端服务...', 'yellow');

  const startTime = Date.now();
  while (Date.now() - startTime < CONFIG.BACKEND_STARTUP_TIMEOUT) {
    await delay(CONFIG.HEALTH_CHECK_INTERVAL);

    // 显示进度
    const elapsed = Math.floor((Date.now() - startTime) / 1000);
    clearLine();
    process.stdout.write(`⏳ 等待后端启动... (${elapsed}s)`);

    // 检查是否已启动（通过日志或健康检查）
    if (backendStarted || await checkHealth(`http://localhost:${CONFIG.BACKEND_PORT}/actuator/health`)) {
      clearLine();
      log('✓ 后端服务启动成功', 'green');
      return mvn;
    }
  }

  clearLine();
  log('⚠ 后端启动超时，请检查日志', 'yellow');
  if (verbose) {
    log('\n最近输出:', 'gray');
    log(outputBuffer.slice(-500), 'gray');
  }
  return mvn;
}

/**
 * 启动前端服务
 */
async function startFrontend(options = {}) {
  const { verbose = false } = options;

  if (!fs.existsSync(CONFIG.FRONTEND_DIR)) {
    log(`✗ 前端目录不存在: ${CONFIG.FRONTEND_DIR}`, 'red');
    return null;
  }

  log('\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'cyan');
  log('启动前端服务', 'cyan');
  log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'cyan');

  // 检查 node_modules
  const nodeModulesPath = path.join(CONFIG.FRONTEND_DIR, 'node_modules');
  if (!fs.existsSync(nodeModulesPath)) {
    log('⚠ 未检测到 node_modules，正在安装依赖...', 'yellow');
    log('这可能需要几分钟，请耐心等待...\n', 'gray');

  const npmInstall = spawn('npm', ['install'], {
    cwd: CONFIG.FRONTEND_DIR,
    stdio: 'inherit',
    shell: process.platform === 'win32',
    env: { ...process.env }
  });

    await new Promise((resolve) => {
      npmInstall.on('close', (code) => {
        if (code === 0) {
          log('✓ 依赖安装完成', 'green');
        } else {
          log('✗ 依赖安装失败', 'red');
        }
        resolve();
      });
    });
  }

  // 启动前端
  const useShell = process.platform === 'win32';
  const npm = spawn('npm', ['run', 'dev'], {
    cwd: CONFIG.FRONTEND_DIR,
    stdio: ['ignore', 'pipe', 'pipe'],
    shell: useShell,
    env: { ...process.env, FORCE_COLOR: '1' }  // 强制彩色输出
  });

  let frontendStarted = false;

  npm.stdout.on('data', (data) => {
    const output = data.toString();

    // 显示完整输出（Vite 的输出很有用）
    for (const line of output.split('\n')) {
      if (line.trim()) {
        log(line, 'reset');
      }
    }

    if (output.includes('Local:') || output.includes('ready in')) {
      frontendStarted = true;
    }
  });

  npm.stderr.on('data', (data) => {
    const output = data.toString();
    for (const line of output.split('\n')) {
      if (line.trim() && !line.includes('DEPRECATION')) {
        log(line, 'yellow');
      }
    }
  });

  npm.on('close', (code) => {
    if (code !== 0 && code !== null) {
      log(`\n✗ 前端服务退出 (代码: ${code})`, 'red');
    }
  });

  // 等待前端启动
  log('等待前端启动...', 'yellow');

  const startTime = Date.now();
  while (Date.now() - startTime < 30000) {
    await delay(1000);

    if (frontendStarted || await checkHealth(`http://localhost:${CONFIG.FRONTEND_PORT}`)) {
      log('✓ 前端服务启动成功', 'green');
      break;
    }
  }

  return npm;
}

/**
 * 自动打开浏览器
 */
function openBrowser(url) {
  const platform = process.platform;
  let cmd;

  if (platform === 'win32') {
    cmd = `start "" "${url}"`;
  } else if (platform === 'darwin') {
    cmd = `open "${url}"`;
  } else {
    cmd = `xdg-open "${url}"`;
  }

  exec(cmd, (error) => {
    // 忽略错误，不影响主流程
    if (error) {
      log('⚠ 无法自动打开浏览器，请手动访问', 'yellow');
    }
  });
}

// ==================== 主函数 ====================

let backendProcess = null;
let frontendProcess = null;

async function main() {
  const options = parseArgs();

  // 显示欢迎信息
  log('\n╔════════════════════════════════════════╗', 'cyan');
  log('║   低代码平台 - 开发环境启动工具 v2.0   ║', 'cyan');
  log('╚════════════════════════════════════════╝\n', 'cyan');

  // 检查并关闭端口
  log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'cyan');
  log('检查端口占用情况', 'cyan');
  log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n', 'cyan');

  if (!options.backendOnly) {
    await killPort(CONFIG.FRONTEND_PORT, '前端');
  }
  if (!options.frontendOnly) {
    await killPort(CONFIG.BACKEND_PORT, '后端');
  }

  // 环境检查
  await checkEnvironment(options.skipCheck);

  // 启动服务
  const startTime = Date.now();

  if (!options.frontendOnly) {
    backendProcess = await startBackend(options);
  }

  if (!options.backendOnly) {
    frontendProcess = await startFrontend(options);
  }

  // 等待一下再显示访问信息
  await delay(2000);

  const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);

  log('\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'green');
  log(`✓ 服务启动完成！(耗时: ${elapsed}s)`, 'green');
  log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', 'green');

  log('\n📋 访问地址:\n', 'cyan');

  if (frontendProcess || options.frontendOnly) {
    log(`  🌐 前端应用:    ${colors.blue}http://localhost:${CONFIG.FRONTEND_PORT}`, 'reset');
  }

  if (backendProcess || options.backendOnly) {
    log(`  ⚙️  后端 API:    ${colors.blue}http://localhost:${CONFIG.BACKEND_PORT}`, 'reset');
    log(`  📚 API 文档:    ${colors.blue}http://localhost:${CONFIG.BACKEND_PORT}/doc.html`, 'reset');
    log(`  📊 Druid 监控:  ${colors.blue}http://localhost:${CONFIG.BACKEND_PORT}/druid/`, 'reset');
    log(`                    ${colors.gray}用户名: admin  密码: admin123`, 'reset');
  }

  log('\n💡 提示:', 'yellow');
  log('  按 Ctrl+C 停止所有服务\n', 'gray');

  // 自动打开浏览器
  if (CONFIG.AUTO_OPEN_BROWSER && frontendProcess && !options.backendOnly) {
    await delay(1000);
    log('🚀 正在打开浏览器...\n', 'cyan');
    openBrowser(`http://localhost:${CONFIG.FRONTEND_PORT}`);
  }

  // 处理退出信号
  const cleanup = async () => {
    log('\n\n🛑 正在停止服务...', 'yellow');

    if (backendProcess) {
      log('  停止后端服务...', 'gray');
      backendProcess.kill('SIGTERM');
    }

    if (frontendProcess) {
      log('  停止前端服务...', 'gray');
      frontendProcess.kill('SIGTERM');
    }

    // 强制退出
    setTimeout(() => {
      process.exit(0);
    }, 3000);
  };

  process.on('SIGINT', cleanup);
  process.on('SIGTERM', cleanup);

  // Windows 下处理 Ctrl+C
  if (process.platform === 'win32') {
    process.on('message', (msg) => {
      if (msg === 'shutdown') {
        cleanup();
      }
    });
  }

  // 保持进程运行
  await new Promise(() => {});
}

// 错误处理
main().catch((error) => {
  log(`\n✗ 启动失败: ${error.message}`, 'red');
  if (process.env.DEBUG) {
    console.error(error);
  }
  process.exit(1);
});
