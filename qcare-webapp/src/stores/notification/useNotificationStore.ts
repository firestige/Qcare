// 通知状态管理
import { createStoreWithDevtools } from '../factory';
import type { NotificationState, Notification } from '../types';

// 生成唯一ID
const generateId = () =>
  Date.now().toString(36) + Math.random().toString(36).substr(2);

// 创建通知Store
export const useNotificationStore = createStoreWithDevtools<NotificationState>(
  (set, get) => ({
    // 初始状态
    notifications: [],
    unreadCount: 0,
    loading: false,
    error: null,
    lastUpdated: 0,

    // 添加通知
    addNotification: (notification) => {
      const newNotification: Notification = {
        ...notification,
        id: generateId(),
        timestamp: Date.now(),
        read: false,
      };

      const notifications = [newNotification, ...get().notifications];
      const unreadCount = notifications.filter((n) => !n.read).length;

      set({
        notifications,
        unreadCount,
        lastUpdated: Date.now(),
      });

      // 自动移除成功类型的通知（5秒后）
      if (notification.type === 'success') {
        setTimeout(() => {
          get().removeNotification(newNotification.id);
        }, 5000);
      }
    },

    // 标记为已读
    markAsRead: (id) => {
      const notifications = get().notifications.map(
        (notification: Notification) =>
          notification.id === id
            ? { ...notification, read: true }
            : notification
      );
      const unreadCount = notifications.filter(
        (n: Notification) => !n.read
      ).length;

      set({
        notifications,
        unreadCount,
        lastUpdated: Date.now(),
      });
    },

    // 标记全部为已读
    markAllAsRead: () => {
      const notifications = get().notifications.map(
        (notification: Notification) => ({
          ...notification,
          read: true,
        })
      );

      set({
        notifications,
        unreadCount: 0,
        lastUpdated: Date.now(),
      });
    },

    // 移除通知
    removeNotification: (id) => {
      const notifications = get().notifications.filter(
        (n: Notification) => n.id !== id
      );
      const unreadCount = notifications.filter(
        (n: Notification) => !n.read
      ).length;

      set({
        notifications,
        unreadCount,
        lastUpdated: Date.now(),
      });
    },

    // 清空所有通知
    clearAll: () => {
      set({
        notifications: [],
        unreadCount: 0,
        lastUpdated: Date.now(),
      });
    },
  }),
  'NotificationStore'
);

// 便捷的通知方法
export const notify = {
  success: (title: string, message: string) => {
    const store = useNotificationStore as any;
    store.getState().addNotification({
      type: 'success',
      title,
      message,
    });
  },

  error: (title: string, message: string) => {
    const store = useNotificationStore as any;
    store.getState().addNotification({
      type: 'error',
      title,
      message,
    });
  },

  warning: (title: string, message: string) => {
    const store = useNotificationStore as any;
    store.getState().addNotification({
      type: 'warning',
      title,
      message,
    });
  },

  info: (title: string, message: string) => {
    const store = useNotificationStore as any;
    store.getState().addNotification({
      type: 'info',
      title,
      message,
    });
  },
};

export default useNotificationStore;
