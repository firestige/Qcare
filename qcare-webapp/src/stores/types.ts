// 状态管理抽象层接口定义
// 这个接口层让我们可以在不同状态管理方案之间切换

export interface StoreSubscription {
  unsubscribe: () => void;
}

export interface StoreApi<T> {
  getState: () => T;
  setState: (partial: Partial<T> | ((state: T) => Partial<T>)) => void;
  subscribe: (listener: (state: T, prevState: T) => void) => StoreSubscription;
  destroy?: () => void;
}

export interface StoreCreator<T> {
  (
    set: (partial: Partial<T> | ((state: T) => Partial<T>)) => void,
    get: () => T
  ): T;
}

// 通用Hook接口
export interface UseStore<T> {
  (): T;
  <U>(selector: (state: T) => U): U;
  <U>(selector: (state: T) => U, equalityFn?: (a: U, b: U) => boolean): U;
}

// 异步操作接口
export interface AsyncAction<T = any, R = any> {
  (params?: T): Promise<R>;
}

// 状态管理器接口
export interface StateManager {
  create: <T>(creator: StoreCreator<T>) => UseStore<T>;
  createWithDevtools?: <T>(
    creator: StoreCreator<T>,
    name?: string
  ) => UseStore<T>;
  createPersisted?: <T>(
    creator: StoreCreator<T>,
    options: { name: string; version?: number }
  ) => UseStore<T>;
}

// 基础状态接口
export interface BaseState {
  loading?: boolean;
  error?: string | null;
  lastUpdated?: number;
}

// 用户状态接口
export interface User {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  role: string;
  permissions: string[];
}

export interface AuthState extends BaseState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: AsyncAction<{ username: string; password: string }, User>;
  logout: () => void;
  refreshToken: AsyncAction<void, string>;
  updateUser: (user: Partial<User>) => void;
}

// 布局状态接口
export interface LayoutState extends BaseState {
  sidebarCollapsed: boolean;
  theme: 'light' | 'dark';
  language: 'zh' | 'en';
  toggleSidebar: () => void;
  setTheme: (theme: 'light' | 'dark') => void;
  setLanguage: (language: 'zh' | 'en') => void;
}

// 通知状态接口
export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  timestamp: number;
  read: boolean;
}

export interface NotificationState extends BaseState {
  notifications: Notification[];
  unreadCount: number;
  addNotification: (
    notification: Omit<Notification, 'id' | 'timestamp' | 'read'>
  ) => void;
  markAsRead: (id: string) => void;
  markAllAsRead: () => void;
  removeNotification: (id: string) => void;
  clearAll: () => void;
}

// 监控数据状态接口
export interface PerformanceMetrics {
  memoryUsage: number;
  cpuUsage: number;
  networkLatency: number;
  errorRate: number;
  activeUsers: number;
  totalRequests: number;
  successRequests: number;
  errorRequests: number;
}

export interface MonitorLog {
  id: string;
  level: 'info' | 'warn' | 'error' | 'debug';
  message: string;
  data?: string;
  timestamp: number;
}

export interface MonitorAlert {
  id: string;
  level: 'info' | 'warning' | 'error' | 'critical';
  title: string;
  message: string;
  timestamp: number;
  read: boolean;
}

export interface MonitorState extends BaseState {
  metrics: PerformanceMetrics;
  logs: MonitorLog[];
  alerts: MonitorAlert[];
  online: boolean;
  updateMetrics: (metrics: Partial<PerformanceMetrics>) => void;
  addLog: (log: Omit<MonitorLog, 'id' | 'timestamp'>) => void;
  addAlert: (alert: Omit<MonitorAlert, 'id' | 'timestamp' | 'read'>) => void;
  markAlertAsRead: (id: string) => void;
  clearAlert: (id: string) => void;
  clearAllAlerts: () => void;
  setOnlineStatus: (online: boolean) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  reset: () => void;
}

// 导出所有store的联合类型
export interface AppStores {
  auth: AuthState;
  layout: LayoutState;
  notification: NotificationState;
  monitor: MonitorState;
}
