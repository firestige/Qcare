// 布局状态管理
import { createPersistedStore } from '../factory';
import type { LayoutState } from '../types';

// 创建布局Store（持久化到localStorage）
export const useLayoutStore = createPersistedStore<LayoutState>(
  (set, get) => ({
    // 初始状态
    sidebarCollapsed: false,
    theme: 'light',
    language: 'zh',
    loading: false,
    error: null,
    lastUpdated: 0,

    // 切换侧边栏
    toggleSidebar: () => {
      set({
        sidebarCollapsed: !get().sidebarCollapsed,
        lastUpdated: Date.now(),
      });
    },

    // 设置主题
    setTheme: (theme) => {
      set({
        theme,
        lastUpdated: Date.now(),
      });

      // 应用主题到document
      document.documentElement.setAttribute('data-theme', theme);
    },

    // 设置语言
    setLanguage: (language) => {
      set({
        language,
        lastUpdated: Date.now(),
      });
    },
  }),
  {
    name: 'icare-layout-store',
    version: 1,
  }
);

export default useLayoutStore;
