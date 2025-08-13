# 状态管理防腐层设计文档

## 🎯 设计目标

本状态管理系统设计了一个**防腐层/胶水层**，允许在不同的状态管理库（如 Zustand、Redux 等）之间无缝切换，避免供应商锁定，提高系统的可维护性和灵活性。

## 🏗️ 架构设计

### 核心组件

```
src/stores/
├── types.ts              # 所有状态类型定义
├── factory.ts            # 状态管理工厂
├── adapters/             # 适配器层
│   ├── zustand.ts       # Zustand 适配器
│   └── redux.ts         # Redux 适配器（待实现）
├── auth/                # 认证状态管理
├── layout/              # 布局状态管理
├── notification/        # 通知状态管理
├── monitor/             # 监控状态管理
└── index.ts             # 统一导出
```

### 设计模式

1. **适配器模式**: 为不同状态管理库提供统一接口
2. **工厂模式**: 统一创建和管理状态存储实例
3. **单例模式**: 确保全局状态管理器唯一性

## 🔧 核心接口

### StateManager 接口

```typescript
interface StateManager {
  create<T>(initialState: StateCreator<T>): UseStore<T>;
  createWithDevtools<T>(
    initialState: StateCreator<T>,
    name?: string
  ): UseStore<T>;
  createPersisted<T>(
    initialState: StateCreator<T>,
    persistConfig: PersistConfig
  ): UseStore<T>;
}
```

### 状态类型定义

```typescript
interface BaseState {
  loading: boolean;
  error: string | null;
  lastUpdated: number;
}

interface AsyncAction<TParams, TResult> {
  (params: TParams): Promise<TResult>;
}
```

## 🚀 使用方式

### 1. 基本使用

```typescript
import { useAuthStore, useLayoutStore } from '@/stores';

// 认证状态
const { user, login, logout, isAuthenticated } = useAuthStore();

// 布局状态
const { theme, setTheme, toggleSidebar } = useLayoutStore();
```

### 2. 通知系统

```typescript
import { notify } from '@/stores';

// 显示通知
notify.success('操作成功', '数据已保存');
notify.error('操作失败', '网络连接异常');
notify.warning('注意', '磁盘空间不足');
notify.info('提示', '有新的更新可用');
```

### 3. 监控系统

```typescript
import { monitor } from '@/stores';

// 记录性能指标
monitor.recordMetrics({
  memoryUsage: 75,
  cpuUsage: 60,
  networkLatency: 120,
});

// 记录日志
monitor.log('info', '系统运行正常');
monitor.alert('warning', '内存使用率高', '当前内存使用率超过 80%');
```

### 4. 状态管理库切换

```typescript
import { switchStateManager } from '@/stores';

// 切换到 Redux
switchStateManager('redux');

// 切换回 Zustand
switchStateManager('zustand');
```

## 🎨 特性说明

### 1. 类型安全

- 完全使用 TypeScript 编写
- 严格的类型检查
- 智能代码提示

### 2. 开发工具支持

- Redux DevTools 集成
- 状态时间旅行调试
- 性能监控

### 3. 数据持久化

- 自动 localStorage 同步
- 可配置的持久化策略
- 数据恢复机制

### 4. 异步操作支持

- 统一的异步 Action 接口
- 自动加载状态管理
- 错误处理机制

## 📦 已实现的状态模块

### 1. 认证状态 (AuthState)

- 用户登录/登出
- Token 管理
- 用户信息更新
- 持久化登录状态

### 2. 布局状态 (LayoutState)

- 侧边栏展开/收起
- 主题切换 (浅色/深色)
- 语言切换 (中文/英文)
- 布局配置持久化

### 3. 通知状态 (NotificationState)

- 多类型通知 (成功/错误/警告/信息)
- 未读消息计数
- 自动过期清理
- 批量操作支持

### 4. 监控状态 (MonitorState)

- 性能指标监控
- 系统日志记录
- 警报管理
- 在线状态检测

## 🔄 切换机制

### 当前支持的适配器

1. **Zustand 适配器** ✅
   - 轻量级状态管理
   - 优秀的 TypeScript 支持
   - 最小化样板代码

2. **Redux 适配器** 🚧
   - 企业级状态管理
   - 成熟的生态系统
   - 强大的中间件支持

### 切换流程

```typescript
// 工厂模式确保切换的平滑过渡
StateManagerFactory.switchAdapter('redux');

// 所有现有的 store hooks 继续工作
const authStore = useAuthStore(); // 现在使用 Redux 实现
```

## 🛠️ 扩展指南

### 添加新的状态模块

1. 在 `types.ts` 中定义状态接口
2. 创建对应的状态管理文件
3. 使用工厂函数创建 store
4. 在 `index.ts` 中导出

```typescript
// 1. 定义类型
interface MyState extends BaseState {
  data: any[];
  fetchData: AsyncAction<void, any[]>;
}

// 2. 创建 store
export const useMyStore = createStoreWithDevtools<MyState>(
  (set, get) => ({
    // 状态实现
  }),
  'MyStore'
);
```

### 添加新的适配器

1. 实现 `StateManager` 接口
2. 在 `adapters/` 目录创建适配器文件
3. 在工厂中注册适配器

```typescript
class MyAdapter implements StateManager {
  create<T>(initialState: StateCreator<T>): UseStore<T> {
    // 适配器实现
  }
  // ... 其他方法
}
```

## 🔍 最佳实践

### 1. 状态设计

- 保持状态扁平化
- 明确的状态边界
- 合理的状态粒度

### 2. 异步操作

- 统一的错误处理
- 加载状态管理
- 乐观更新策略

### 3. 性能优化

- 按需订阅状态
- 避免不必要的重渲染
- 合理使用 memo

### 4. 调试和监控

- 利用 DevTools 调试
- 添加详细的操作日志
- 监控状态变化性能

## 🧪 测试策略

### 单元测试

- 状态逻辑测试
- 异步操作测试
- 边界条件测试

### 集成测试

- 组件与状态集成
- 跨模块状态交互
- 持久化功能测试

### E2E 测试

- 完整用户流程
- 状态切换场景
- 错误恢复流程

## 📈 性能考虑

### 内存管理

- 及时清理无用状态
- 合理的状态生命周期
- 避免内存泄漏

### 渲染优化

- 细粒度状态订阅
- 智能的更新策略
- 减少重复计算

### 网络优化

- 请求去重
- 缓存策略
- 离线支持

## 🔐 安全考虑

### 数据保护

- 敏感数据不持久化
- Token 过期处理
- 安全的状态传输

### 权限控制

- 基于角色的状态访问
- 操作权限验证
- 安全的状态更新

## 🚀 演示页面

访问 `/state-demo` 路径可以查看完整的状态管理系统演示，包括：

- 用户认证演示
- 布局控制演示
- 通知系统演示
- 监控面板演示
- 状态管理切换演示

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 创建 Pull Request

## 📄 许可证

MIT License
