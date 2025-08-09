// 依赖管理相关类型定义

export interface DependencyTreeNode {
  title: string;
  key: string;
  icon?: React.ReactNode;
  children?: DependencyTreeNode[];
}

export interface DependencyItem {
  key: string;
  name: string;
  version: string;
  latestVersion: string;
  status: 'latest' | 'outdated' | 'critical';
  security: 'safe' | 'warning' | 'vulnerable';
  license: string;
  size: string;
  usedBy: string[];
  description?: string;
  homepage?: string;
  repository?: string;
  lastUpdate?: string;
}

export interface DependencyStats {
  totalDependencies: number;
  safeDependencies: number;
  outdatedDependencies: number;
  vulnerableDependencies: number;
}

export interface DependencySearchParams {
  keyword?: string;
  status?: string;
  security?: string;
  service?: string;
}

export interface DependencyApiResponse {
  success: boolean;
  data: {
    stats: DependencyStats;
    tree: DependencyTreeNode[];
    dependencies: DependencyItem[];
  };
  message?: string;
}

export const StatusConfig = {
  latest: { color: 'green', text: '最新' },
  outdated: { color: 'orange', text: '过期' },
  critical: { color: 'red', text: '严重过期' },
} as const;

export const SecurityConfig = {
  safe: { color: 'green', text: '安全' },
  warning: { color: 'orange', text: '警告' },
  vulnerable: { color: 'red', text: '漏洞' },
} as const;
