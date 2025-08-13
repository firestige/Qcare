// 状态管理入口文件
export { default as useAuthStore } from './auth/useAuthStore';
export { default as useLayoutStore } from './layout/useLayoutStore';
export {
  default as useNotificationStore,
  notify,
} from './notification/useNotificationStore';
export { default as useMonitorStore, monitor } from './monitor/useMonitorStore';

// 导出工厂和适配器
export {
  default as factory,
  createStore,
  createStoreWithDevtools,
  createPersistedStore,
} from './factory';
export type { StateManager, UseStore } from './types';

// 导出所有类型
export type {
  BaseState,
  AsyncAction,
  User,
  AuthState,
  LayoutState,
  Notification,
  NotificationState,
  PerformanceMetrics,
  MonitorLog,
  MonitorAlert,
  MonitorState,
  AppStores,
} from './types';

// 便捷的状态管理切换
export const switchStateManager = (adapter: 'zustand' | 'redux') => {
  const { default: factory } = require('./factory');
  factory.switchAdapter(adapter);
};
