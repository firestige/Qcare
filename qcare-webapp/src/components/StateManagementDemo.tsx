// 状态管理使用示例
import React from 'react';
import {
  useAuthStore,
  useLayoutStore,
  useNotificationStore,
  useMonitorStore,
  notify,
  monitor,
  switchStateManager,
} from '../stores';

// 示例：用户登录组件
export const LoginExample: React.FC = () => {
  const { login, logout, user, isAuthenticated, loading } = useAuthStore();

  const handleLogin = async () => {
    try {
      await login({ username: 'admin', password: '123456' });
      notify.success('登录成功', '欢迎回来！');
    } catch (error) {
      notify.error('登录失败', '用户名或密码错误');
    }
  };

  const handleLogout = () => {
    logout();
    notify.info('已退出', '您已安全退出系统');
  };

  if (loading) return <div>登录中...</div>;

  return (
    <div>
      {isAuthenticated ? (
        <div>
          <h3>欢迎, {user?.username}!</h3>
          <button onClick={handleLogout}>退出登录</button>
        </div>
      ) : (
        <button onClick={handleLogin}>登录</button>
      )}
    </div>
  );
};

// 示例：布局控制组件
export const LayoutExample: React.FC = () => {
  const {
    sidebarCollapsed,
    theme,
    language,
    toggleSidebar,
    setTheme,
    setLanguage,
  } = useLayoutStore();

  return (
    <div>
      <h3>布局设置</h3>
      <div>
        <button onClick={toggleSidebar}>
          {sidebarCollapsed ? '展开' : '收起'}侧边栏
        </button>
      </div>
      <div>
        <label>主题: </label>
        <select
          value={theme}
          onChange={(e) => setTheme(e.target.value as 'light' | 'dark')}
        >
          <option value="light">浅色</option>
          <option value="dark">深色</option>
        </select>
      </div>
      <div>
        <label>语言: </label>
        <select
          value={language}
          onChange={(e) => setLanguage(e.target.value as 'zh' | 'en')}
        >
          <option value="zh">中文</option>
          <option value="en">English</option>
        </select>
      </div>
    </div>
  );
};

// 示例：通知系统组件
export const NotificationExample: React.FC = () => {
  const {
    notifications,
    unreadCount,
    markAsRead,
    removeNotification,
    clearAll,
  } = useNotificationStore();

  const handleTest = () => {
    notify.success('测试成功', '这是一个成功通知');
    notify.warning('测试警告', '这是一个警告通知');
    notify.error('测试错误', '这是一个错误通知');
    notify.info('测试信息', '这是一个信息通知');
  };

  return (
    <div>
      <h3>通知系统 (未读: {unreadCount})</h3>
      <button onClick={handleTest}>测试通知</button>
      <button onClick={clearAll}>清空所有</button>

      <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
        {notifications.map((notification) => (
          <div
            key={notification.id}
            style={{
              padding: '8px',
              margin: '4px 0',
              border: '1px solid #ddd',
              borderRadius: '4px',
              background: notification.read ? '#f5f5f5' : '#fff',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <strong>{notification.title}</strong>
              <div>
                {!notification.read && (
                  <button onClick={() => markAsRead(notification.id)}>
                    标记已读
                  </button>
                )}
                <button onClick={() => removeNotification(notification.id)}>
                  删除
                </button>
              </div>
            </div>
            <p>{notification.message}</p>
            <small>{new Date(notification.timestamp).toLocaleString()}</small>
          </div>
        ))}
      </div>
    </div>
  );
};

// 示例：监控面板组件
export const MonitorExample: React.FC = () => {
  const {
    metrics,
    alerts,
    online,
    logs,
    markAlertAsRead,
    clearAlert,
    clearAllAlerts,
  } = useMonitorStore();

  React.useEffect(() => {
    // 开始性能监控
    const cleanup = monitor.startPerformanceMonitoring();

    // 模拟一些监控数据
    const interval = setInterval(() => {
      monitor.recordMetrics({
        memoryUsage: Math.random() * 100,
        cpuUsage: Math.random() * 100,
        networkLatency: Math.random() * 200,
      });

      monitor.log('info', `系统运行正常 - ${new Date().toLocaleTimeString()}`);

      // 随机生成警报
      if (Math.random() > 0.9) {
        monitor.alert('warning', '内存使用率高', '当前内存使用率超过 80%');
      }
    }, 5000);

    return () => {
      cleanup();
      clearInterval(interval);
    };
  }, []);

  return (
    <div>
      <h3>系统监控</h3>
      <div>网络状态: {online ? '在线' : '离线'}</div>

      <h4>性能指标</h4>
      <div>
        <div>内存使用率: {metrics.memoryUsage.toFixed(1)}%</div>
        <div>CPU使用率: {metrics.cpuUsage.toFixed(1)}%</div>
        <div>网络延迟: {metrics.networkLatency.toFixed(0)}ms</div>
      </div>

      <h4>警报 ({alerts.filter((a) => !a.read).length} 未读)</h4>
      <button onClick={clearAllAlerts}>清空所有警报</button>
      <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
        {alerts.map((alert) => (
          <div
            key={alert.id}
            style={{
              padding: '8px',
              margin: '4px 0',
              border: '1px solid #ddd',
              borderRadius: '4px',
              background: alert.read
                ? '#f5f5f5'
                : alert.level === 'error'
                  ? '#ffebee'
                  : '#fff3e0',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <strong>
                [{alert.level.toUpperCase()}] {alert.title}
              </strong>
              <div>
                {!alert.read && (
                  <button onClick={() => markAlertAsRead(alert.id)}>
                    标记已读
                  </button>
                )}
                <button onClick={() => clearAlert(alert.id)}>删除</button>
              </div>
            </div>
            <p>{alert.message}</p>
          </div>
        ))}
      </div>

      <h4>系统日志</h4>
      <div style={{ maxHeight: '150px', overflowY: 'auto', fontSize: '12px' }}>
        {logs.slice(0, 10).map((log) => (
          <div key={log.id} style={{ margin: '2px 0' }}>
            <span
              style={{
                color:
                  log.level === 'error'
                    ? 'red'
                    : log.level === 'warn'
                      ? 'orange'
                      : 'black',
              }}
            >
              [{log.level.toUpperCase()}]
            </span>
            <span>
              {' '}
              {new Date(log.timestamp).toLocaleTimeString()}: {log.message}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};

// 示例：状态管理切换
export const StateManagerSwitchExample: React.FC = () => {
  const [currentAdapter, setCurrentAdapter] = React.useState<
    'zustand' | 'redux'
  >('zustand');

  const handleSwitch = (adapter: 'zustand' | 'redux') => {
    switchStateManager(adapter);
    setCurrentAdapter(adapter);
    notify.info('切换成功', `已切换到 ${adapter.toUpperCase()} 状态管理`);
  };

  return (
    <div>
      <h3>状态管理切换</h3>
      <p>当前使用: {currentAdapter.toUpperCase()}</p>
      <button
        onClick={() => handleSwitch('zustand')}
        disabled={currentAdapter === 'zustand'}
      >
        切换到 Zustand
      </button>
      <button
        onClick={() => handleSwitch('redux')}
        disabled={currentAdapter === 'redux'}
      >
        切换到 Redux
      </button>
    </div>
  );
};

// 完整的演示组件
export const StateManagementDemo: React.FC = () => {
  return (
    <div style={{ padding: '20px', maxWidth: '1200px' }}>
      <h1>状态管理系统演示</h1>

      <div
        style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}
      >
        <div
          style={{
            border: '1px solid #ddd',
            padding: '15px',
            borderRadius: '8px',
          }}
        >
          <LoginExample />
        </div>

        <div
          style={{
            border: '1px solid #ddd',
            padding: '15px',
            borderRadius: '8px',
          }}
        >
          <LayoutExample />
        </div>

        <div
          style={{
            border: '1px solid #ddd',
            padding: '15px',
            borderRadius: '8px',
          }}
        >
          <NotificationExample />
        </div>

        <div
          style={{
            border: '1px solid #ddd',
            padding: '15px',
            borderRadius: '8px',
          }}
        >
          <MonitorExample />
        </div>
      </div>

      <div
        style={{
          border: '1px solid #ddd',
          padding: '15px',
          borderRadius: '8px',
          marginTop: '20px',
        }}
      >
        <StateManagerSwitchExample />
      </div>
    </div>
  );
};

export default StateManagementDemo;
