// Arthas 管理Hook
import { useState, useEffect, useCallback } from 'react';
import { message } from 'antd';
import { arthasService } from '../services/arthasService';
import {
  mockArthasStats,
  mockArthasInstances,
  mockCommonCommands,
  mockDiagnosticCommands,
} from '../data/mockArthasData';
import type {
  ArthasStats,
  ArthasInstance,
  ArthasCommand,
  ArthasSearchParams,
  CommandExecutionResult,
  CommandExecuteRequest,
} from '../types/arthas';

interface UseArthasOptions {
  autoFetch?: boolean;
  showMessage?: boolean;
}

interface UseArthasReturn {
  // 数据状态
  stats: ArthasStats;
  instances: ArthasInstance[];
  commonCommands: ArthasCommand[];
  diagnosticCommands: ArthasCommand[];

  // 加载状态
  loading: boolean;
  executing: boolean;
  error: string | null;

  // 操作方法
  fetchInstances: (params?: ArthasSearchParams) => Promise<void>;
  connectInstance: (
    instanceName: string,
    host: string,
    port: string
  ) => Promise<boolean>;
  disconnectInstance: (instanceName: string) => Promise<boolean>;
  executeCommand: (
    request: CommandExecuteRequest
  ) => Promise<CommandExecutionResult | null>;
  downloadArthas: () => Promise<boolean>;
  deployArthas: (instanceName: string) => Promise<boolean>;
  refreshData: () => Promise<void>;

  // 搜索和过滤
  searchParams: ArthasSearchParams;
  setSearchParams: (params: ArthasSearchParams) => void;
}

export const useArthas = (options: UseArthasOptions = {}): UseArthasReturn => {
  const { autoFetch = true, showMessage = true } = options;

  // 状态定义
  const [stats, setStats] = useState<ArthasStats>(mockArthasStats);
  const [instances, setInstances] =
    useState<ArthasInstance[]>(mockArthasInstances);
  const [commonCommands] = useState<ArthasCommand[]>(mockCommonCommands);
  const [diagnosticCommands] = useState<ArthasCommand[]>(
    mockDiagnosticCommands
  );
  const [loading, setLoading] = useState<boolean>(false);
  const [executing, setExecuting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [searchParams, setSearchParams] = useState<ArthasSearchParams>({});

  // 获取实例数据
  const fetchInstances = useCallback(
    async (params?: ArthasSearchParams) => {
      setLoading(true);
      setError(null);

      try {
        const searchParameters = params || searchParams;
        const response = await arthasService.getInstances(searchParameters);

        if (response.success && response.data) {
          setStats(response.data.stats);
          setInstances(response.data.instances);

          if (showMessage) {
            message.success('Arthas 实例数据获取成功');
          }
        } else {
          throw new Error('获取 Arthas 实例数据失败');
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '获取 Arthas 实例数据失败';
        setError(errorMessage);

        // 使用mock数据作为备选
        console.warn(
          'Failed to fetch from API, using mock data:',
          errorMessage
        );
        setStats(mockArthasStats);
        setInstances(mockArthasInstances);

        if (showMessage) {
          message.warning('API请求失败，使用演示数据');
        }
      } finally {
        setLoading(false);
      }
    },
    [searchParams, showMessage]
  );

  // 连接实例
  const connectInstance = useCallback(
    async (
      instanceName: string,
      host: string,
      port: string
    ): Promise<boolean> => {
      try {
        setLoading(true);
        const response = await arthasService.connectInstance({
          instanceName,
          host,
          port,
        });

        if (response.success) {
          if (showMessage) {
            message.success(`成功连接到实例 ${instanceName}`);
          }

          // 刷新数据以获取最新状态
          await fetchInstances();
          return true;
        } else {
          throw new Error(response.message || '连接实例失败');
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '连接实例失败';
        if (showMessage) {
          message.error(errorMessage);
        }
        return false;
      } finally {
        setLoading(false);
      }
    },
    [fetchInstances, showMessage]
  );

  // 断开实例连接
  const disconnectInstance = useCallback(
    async (instanceName: string): Promise<boolean> => {
      try {
        setLoading(true);
        const response = await arthasService.disconnectInstance(instanceName);

        if (response.success) {
          if (showMessage) {
            message.success(`已断开实例 ${instanceName} 的连接`);
          }

          // 刷新数据以获取最新状态
          await fetchInstances();
          return true;
        } else {
          throw new Error(response.message || '断开连接失败');
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '断开连接失败';
        if (showMessage) {
          message.error(errorMessage);
        }
        return false;
      } finally {
        setLoading(false);
      }
    },
    [fetchInstances, showMessage]
  );

  // 执行命令
  const executeCommand = useCallback(
    async (
      request: CommandExecuteRequest
    ): Promise<CommandExecutionResult | null> => {
      try {
        setExecuting(true);
        const response = await arthasService.executeCommand(request);

        if (response.success && response.data) {
          if (showMessage) {
            message.success('命令执行成功');
          }
          return response.data;
        } else {
          throw new Error('命令执行失败');
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '命令执行失败';
        if (showMessage) {
          message.error(errorMessage);
        }

        // 返回模拟结果用于演示
        return {
          success: false,
          output: `模拟输出: 执行命令 ${request.command}\n错误: ${errorMessage}`,
          error: errorMessage,
          timestamp: new Date().toISOString(),
          duration: 1000,
        };
      } finally {
        setExecuting(false);
      }
    },
    [showMessage]
  );

  // 下载 Arthas
  const downloadArthas = useCallback(async (): Promise<boolean> => {
    try {
      setLoading(true);
      const response = await arthasService.downloadArthas();

      if (response.success) {
        if (showMessage) {
          message.success(`Arthas ${response.version} 下载成功`);
        }

        // 在实际应用中，这里会触发文件下载
        window.open(response.downloadUrl, '_blank');
        return true;
      } else {
        throw new Error('下载 Arthas 失败');
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : '下载 Arthas 失败';
      if (showMessage) {
        message.error(errorMessage);
      }
      return false;
    } finally {
      setLoading(false);
    }
  }, [showMessage]);

  // 一键部署
  const deployArthas = useCallback(
    async (instanceName: string): Promise<boolean> => {
      try {
        setLoading(true);
        const response = await arthasService.deployArthas(instanceName);

        if (response.success) {
          if (showMessage) {
            message.success(`Arthas 部署到 ${instanceName} 成功`);
          }

          // 刷新数据以获取最新状态
          await fetchInstances();
          return true;
        } else {
          throw new Error(response.message || '部署 Arthas 失败');
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '部署 Arthas 失败';
        if (showMessage) {
          message.error(errorMessage);
        }
        return false;
      } finally {
        setLoading(false);
      }
    },
    [fetchInstances, showMessage]
  );

  // 刷新数据
  const refreshData = useCallback(async () => {
    await fetchInstances();
  }, [fetchInstances]);

  // 更新搜索参数
  const handleSetSearchParams = useCallback((params: ArthasSearchParams) => {
    setSearchParams(params);
  }, []);

  // 初始化数据获取
  useEffect(() => {
    if (autoFetch) {
      fetchInstances();
    }
  }, [autoFetch]); // 移除fetchInstances依赖以避免无限循环

  // 当搜索参数变化时重新获取数据
  useEffect(() => {
    if (autoFetch && Object.keys(searchParams).length > 0) {
      fetchInstances(searchParams);
    }
  }, [searchParams, autoFetch]); // 这里可以保留searchParams依赖

  return {
    // 数据状态
    stats,
    instances,
    commonCommands,
    diagnosticCommands,

    // 加载状态
    loading,
    executing,
    error,

    // 操作方法
    fetchInstances,
    connectInstance,
    disconnectInstance,
    executeCommand,
    downloadArthas,
    deployArthas,
    refreshData,

    // 搜索和过滤
    searchParams,
    setSearchParams: handleSetSearchParams,
  };
};
