// 状态管理工厂 - 防腐层的核心
import ZustandAdapter from './adapters/zustand';
import ReduxAdapter from './adapters/redux';
import type { StateManager } from './types';

// 可切换的状态管理类型
export type StateManagerType = 'zustand' | 'redux';

class StateManagerFactory {
  private static instance: StateManagerFactory;
  private currentAdapter: StateManager;
  private adapterType: StateManagerType;

  private constructor() {
    // 默认使用Zustand
    this.adapterType = 'zustand';
    this.currentAdapter = new ZustandAdapter();
  }

  public static getInstance(): StateManagerFactory {
    if (!StateManagerFactory.instance) {
      StateManagerFactory.instance = new StateManagerFactory();
    }
    return StateManagerFactory.instance;
  }

  // 切换状态管理器
  public switchAdapter(type: StateManagerType): void {
    if (this.adapterType === type) {
      return; // 已经是当前类型，无需切换
    }

    console.warn(`Switching state manager from ${this.adapterType} to ${type}`);

    switch (type) {
      case 'zustand':
        this.currentAdapter = new ZustandAdapter();
        break;
      case 'redux':
        this.currentAdapter = new ReduxAdapter();
        break;
      default:
        throw new Error(`Unsupported state manager type: ${type}`);
    }

    this.adapterType = type;
  }

  // 获取当前适配器
  public getAdapter(): StateManager {
    return this.currentAdapter;
  }

  // 获取当前适配器类型
  public getCurrentType(): StateManagerType {
    return this.adapterType;
  }

  // 创建store的便捷方法
  public create<T>(creator: (set: any, get: any) => T) {
    return this.currentAdapter.create(creator);
  }

  public createWithDevtools<T>(
    creator: (set: any, get: any) => T,
    name?: string
  ) {
    return (
      this.currentAdapter.createWithDevtools?.(creator, name) ||
      this.currentAdapter.create(creator)
    );
  }

  public createPersisted<T>(
    creator: (set: any, get: any) => T,
    options: { name: string; version?: number }
  ) {
    return (
      this.currentAdapter.createPersisted?.(creator, options) ||
      this.currentAdapter.create(creator)
    );
  }
}

// 导出单例实例
export const stateManager = StateManagerFactory.getInstance();

// 便捷的导出函数
export const createStore = <T>(creator: (set: any, get: any) => T) =>
  stateManager.create(creator);

export const createStoreWithDevtools = <T>(
  creator: (set: any, get: any) => T,
  name?: string
) => stateManager.createWithDevtools(creator, name);

export const createPersistedStore = <T>(
  creator: (set: any, get: any) => T,
  options: { name: string; version?: number }
) => stateManager.createPersisted(creator, options);

// 切换状态管理器的便捷函数
export const switchStateManager = (type: StateManagerType) => {
  stateManager.switchAdapter(type);

  // 可以在这里添加迁移逻辑
  if (process.env.NODE_ENV === 'development') {
    console.log(`State manager switched to: ${type}`);
  }
};

export default StateManagerFactory;
