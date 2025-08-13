// 认证状态管理
import { createStoreWithDevtools } from '../factory';
import type { AuthState, User } from '../types';

// 模拟API调用
const authApi = {
  async login(credentials: {
    username: string;
    password: string;
  }): Promise<User> {
    // 模拟API延迟
    await new Promise((resolve) => setTimeout(resolve, 1000));

    if (
      credentials.username === 'admin' &&
      credentials.password === 'admin123'
    ) {
      return {
        id: '1',
        username: 'admin',
        email: 'admin@icare.com',
        avatar: '/images/avatar-default.png',
        role: 'admin',
        permissions: ['read', 'write', 'delete', 'admin'],
      };
    }

    throw new Error('用户名或密码错误');
  },

  async refreshToken(): Promise<string> {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return 'new-jwt-token-' + Date.now();
  },
};

// 创建认证Store
export const useAuthStore = createStoreWithDevtools<AuthState>(
  (set, get) => ({
    // 初始状态
    user: null,
    token: localStorage.getItem('auth-token'),
    isAuthenticated: false,
    loading: false,
    error: null,
    lastUpdated: 0,

    // 登录方法
    login: async (credentials) => {
      if (!credentials) {
        throw new Error('登录凭据不能为空');
      }

      set({ loading: true, error: null });

      try {
        const user = await authApi.login(credentials);
        const token = 'jwt-token-' + Date.now(); // 模拟JWT token

        // 保存到localStorage
        localStorage.setItem('auth-token', token);
        localStorage.setItem('user', JSON.stringify(user));

        set({
          user,
          token,
          isAuthenticated: true,
          loading: false,
          lastUpdated: Date.now(),
        });

        return user;
      } catch (error) {
        const errorMessage =
          error instanceof Error ? error.message : '登录失败';
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          loading: false,
          error: errorMessage,
        });
        throw error;
      }
    },

    // 登出方法
    logout: () => {
      localStorage.removeItem('auth-token');
      localStorage.removeItem('user');

      set({
        user: null,
        token: null,
        isAuthenticated: false,
        error: null,
        lastUpdated: Date.now(),
      });
    },

    // 刷新Token
    refreshToken: async () => {
      try {
        const newToken = await authApi.refreshToken();
        localStorage.setItem('auth-token', newToken);

        set({
          token: newToken,
          lastUpdated: Date.now(),
        });

        return newToken;
      } catch (error) {
        // Token刷新失败，可能需要重新登录
        get().logout();
        throw error;
      }
    },

    // 更新用户信息
    updateUser: (userUpdate) => {
      const currentUser = get().user;
      if (!currentUser) return;

      const updatedUser = { ...currentUser, ...userUpdate };
      localStorage.setItem('user', JSON.stringify(updatedUser));

      set({
        user: updatedUser,
        lastUpdated: Date.now(),
      });
    },
  }),
  'AuthStore'
);

// 初始化认证状态（从localStorage恢复）
const initializeAuth = () => {
  const token = localStorage.getItem('auth-token');
  const userStr = localStorage.getItem('user');

  if (token && userStr) {
    try {
      const user = JSON.parse(userStr);
      // 使用store的内部方法来设置初始状态
      const store = useAuthStore as any;
      if (store.setState) {
        store.setState({
          user,
          token,
          isAuthenticated: true,
          lastUpdated: Date.now(),
        });
      }
    } catch (error) {
      console.error('Failed to parse stored user data:', error);
      localStorage.removeItem('auth-token');
      localStorage.removeItem('user');
    }
  }
};

// 应用启动时初始化
if (typeof window !== 'undefined') {
  initializeAuth();
}

export default useAuthStore;
