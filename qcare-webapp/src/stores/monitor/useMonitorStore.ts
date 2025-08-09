// 监控状态管理
import { createStoreWithDevtools } from '../factory';
import type {
  MonitorState,
  PerformanceMetrics,
  MonitorLog,
  MonitorAlert,
} from '../types';

// 创建监控Store
export const useMonitorStore = createStoreWithDevtools<MonitorState>(
  (set, get) => ({
    // 初始状态
    metrics: {
      memoryUsage: 0,
      cpuUsage: 0,
      networkLatency: 0,
      errorRate: 0,
      activeUsers: 0,
      totalRequests: 0,
      successRequests: 0,
      errorRequests: 0,
    },
    logs: [],
    alerts: [],
    online: navigator.onLine,
    loading: false,
    error: null,
    lastUpdated: 0,

    // 更新性能指标
    updateMetrics: (newMetrics: Partial<PerformanceMetrics>) => {
      const currentMetrics = get().metrics;
      const updatedMetrics = { ...currentMetrics, ...newMetrics };

      set({
        metrics: updatedMetrics,
        lastUpdated: Date.now(),
      });
    },

    // 添加日志
    addLog: (log: Omit<MonitorLog, 'id' | 'timestamp'>) => {
      const logs = [
        {
          ...log,
          id: Date.now().toString(36) + Math.random().toString(36).substr(2),
          timestamp: Date.now(),
        },
        ...get().logs,
      ].slice(0, 1000); // 保持最近1000条日志

      set({ logs, lastUpdated: Date.now() });
    },

    // 添加警报
    addAlert: (alert: Omit<MonitorAlert, 'id' | 'timestamp' | 'read'>) => {
      const alerts = [
        {
          ...alert,
          id: Date.now().toString(36) + Math.random().toString(36).substr(2),
          timestamp: Date.now(),
          read: false,
        },
        ...get().alerts,
      ];

      set({ alerts, lastUpdated: Date.now() });
    },

    // 标记警报为已读
    markAlertAsRead: (id: string) => {
      const alerts = get().alerts.map((alert: MonitorAlert) =>
        alert.id === id ? { ...alert, read: true } : alert
      );
      set({ alerts, lastUpdated: Date.now() });
    },

    // 清除警报
    clearAlert: (id: string) => {
      const alerts = get().alerts.filter(
        (alert: MonitorAlert) => alert.id !== id
      );
      set({ alerts, lastUpdated: Date.now() });
    },

    // 清除所有警报
    clearAllAlerts: () => {
      set({ alerts: [], lastUpdated: Date.now() });
    },

    // 设置在线状态
    setOnlineStatus: (online: boolean) => {
      set({ online, lastUpdated: Date.now() });
    },

    // 设置加载状态
    setLoading: (loading: boolean) => {
      set({ loading });
    },

    // 设置错误状态
    setError: (error: string | null) => {
      set({ error });
    },

    // 重置状态
    reset: () => {
      set({
        metrics: {
          memoryUsage: 0,
          cpuUsage: 0,
          networkLatency: 0,
          errorRate: 0,
          activeUsers: 0,
          totalRequests: 0,
          successRequests: 0,
          errorRequests: 0,
        },
        logs: [],
        alerts: [],
        loading: false,
        error: null,
        lastUpdated: Date.now(),
      });
    },
  }),
  'MonitorStore'
);

// 监控工具方法
export const monitor = {
  // 记录性能指标
  recordMetrics: (metrics: Partial<PerformanceMetrics>) => {
    const store = useMonitorStore as any;
    store.getState().updateMetrics(metrics);
  },

  // 记录日志
  log: (
    level: 'info' | 'warn' | 'error' | 'debug',
    message: string,
    data?: any
  ) => {
    const store = useMonitorStore as any;
    store.getState().addLog({
      level,
      message,
      data: data ? JSON.stringify(data) : undefined,
    });
  },

  // 记录警报
  alert: (
    level: 'info' | 'warning' | 'error' | 'critical',
    title: string,
    message: string
  ) => {
    const store = useMonitorStore as any;
    store.getState().addAlert({
      level,
      title,
      message,
    });
  },

  // 开始性能监控
  startPerformanceMonitoring: () => {
    // 监控内存使用
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      monitor.recordMetrics({
        memoryUsage: Math.round(
          (memory.usedJSHeapSize / memory.jsHeapSizeLimit) * 100
        ),
      });
    }

    // 监控网络状态
    const updateOnlineStatus = () => {
      const store = useMonitorStore as any;
      store.getState().setOnlineStatus(navigator.onLine);
    };

    window.addEventListener('online', updateOnlineStatus);
    window.addEventListener('offline', updateOnlineStatus);

    // 返回清理函数
    return () => {
      window.removeEventListener('online', updateOnlineStatus);
      window.removeEventListener('offline', updateOnlineStatus);
    };
  },
};

export default useMonitorStore;
