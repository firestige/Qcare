// Arthas 相关类型定义

export interface ArthasInstance {
  key: string;
  name: string;
  pid: string;
  status: 'running' | 'stopped';
  host: string;
  port: string;
  memory: string;
  cpu: string;
  arthasStatus: 'connected' | 'disconnected';
  uptime?: string;
  jvmVersion?: string;
  mainClass?: string;
}

export interface ArthasStats {
  totalInstances: number;
  runningInstances: number;
  connectedInstances: number;
  todayDiagnosticCount: number;
  reportCount: number;
}

export interface ArthasCommand {
  command: string;
  description: string;
  category: 'common' | 'diagnostic' | 'monitor';
  parameters?: string[];
  examples?: string[];
}

export interface CommandExecutionResult {
  success: boolean;
  output: string;
  error?: string;
  timestamp: string;
  duration: number;
}

export interface ArthasSearchParams {
  keyword?: string;
  status?: 'running' | 'stopped';
  arthasStatus?: 'connected' | 'disconnected';
  host?: string;
}

// API 响应类型
export interface ArthasApiResponse {
  success: boolean;
  data: {
    stats: ArthasStats;
    instances: ArthasInstance[];
  };
  message?: string;
}

export interface CommandExecuteRequest {
  instanceName: string;
  command: string;
  parameters?: Record<string, any>;
}

export interface CommandExecuteResponse {
  success: boolean;
  data: CommandExecutionResult;
  message?: string;
}

export interface ArthasConnectionRequest {
  instanceName: string;
  host: string;
  port: string;
}

export interface ArthasConnectionResponse {
  success: boolean;
  message: string;
}

// 状态配置
export const InstanceStatusConfig = {
  running: {
    color: 'green',
    text: '运行中',
  },
  stopped: {
    color: 'red',
    text: '已停止',
  },
} as const;

export const ArthasStatusConfig = {
  connected: {
    color: 'blue',
    text: '已连接',
  },
  disconnected: {
    color: 'default',
    text: '未连接',
  },
} as const;
