// 依赖管理Hook
import { useState, useEffect, useCallback } from 'react';
import { message } from 'antd';
import {
  dependenciesApi,
  securityApi,
  DependenciesGetStatusEnum,
  DependenciesGetSecurityEnum,
} from '../api';
import {
  mockDependencyStats,
  mockDependencyTree,
  mockDependencyData,
} from '../data/mockDependencyData';
import type {
  DependencyStats,
  DependencyTreeNode,
  DependencyItem,
  DependencySearchParams,
} from '../types/dependency';

interface UseDependenciesOptions {
  autoFetch?: boolean;
  showMessage?: boolean;
}

interface UseDependenciesReturn {
  // 数据状态
  stats: DependencyStats;
  treeData: DependencyTreeNode[];
  dependencies: DependencyItem[];

  // 加载状态
  loading: boolean;
  error: string | null;

  // 操作方法
  fetchDependencies: (params?: DependencySearchParams) => Promise<void>;
  scanSecurity: () => Promise<boolean>;
  refreshData: () => Promise<void>;

  // 搜索和过滤
  searchParams: DependencySearchParams;
  setSearchParams: (params: DependencySearchParams) => void;
}

export const useDependencies = (
  options: UseDependenciesOptions = {}
): UseDependenciesReturn => {
  const { autoFetch = true, showMessage = true } = options;

  // 状态定义
  const [stats, setStats] = useState<DependencyStats>(mockDependencyStats);
  const [treeData, setTreeData] =
    useState<DependencyTreeNode[]>(mockDependencyTree);
  const [dependencies, setDependencies] =
    useState<DependencyItem[]>(mockDependencyData);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [searchParams, setSearchParams] = useState<DependencySearchParams>({});

  // 获取依赖数据
  const fetchDependencies = useCallback(
    async (params?: DependencySearchParams) => {
      setLoading(true);
      setError(null);

      try {
        const searchParameters = params || searchParams;
        const response = await dependenciesApi.dependenciesGet(
          searchParameters.keyword,
          searchParameters.status as DependenciesGetStatusEnum,
          searchParameters.security as DependenciesGetSecurityEnum,
          searchParameters.service
        );

        if (response.data && response.data.data) {
          setStats(response.data.data.stats);
          setTreeData(response.data.data.tree);
          setDependencies(response.data.data.dependencies);

          if (showMessage) {
            message.success('依赖数据获取成功');
          }
        } else {
          throw new Error('获取依赖数据失败');
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '获取依赖数据失败';
        setError(errorMessage);

        // 使用mock数据作为备选
        console.warn(
          'Failed to fetch from API, using mock data:',
          errorMessage
        );
        setStats(mockDependencyStats);
        setTreeData(mockDependencyTree);
        setDependencies(mockDependencyData);

        if (showMessage) {
          message.warning('API请求失败，使用演示数据');
        }
      } finally {
        setLoading(false);
      }
    },
    [searchParams, showMessage]
  );

  // 安全扫描
  const scanSecurity = useCallback(async (): Promise<boolean> => {
    try {
      setLoading(true);
      const response = await securityApi.dependenciesScanPost();

      if (response.data.success) {
        if (showMessage) {
          message.success('安全扫描完成');
        }

        // 刷新数据以获取最新的安全状态
        await fetchDependencies();
        return true;
      } else {
        throw new Error(response.data.message || '安全扫描失败');
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '安全扫描失败';
      if (showMessage) {
        message.error(errorMessage);
      }
      return false;
    } finally {
      setLoading(false);
    }
  }, [fetchDependencies, showMessage]);

  // 刷新数据
  const refreshData = useCallback(async () => {
    await fetchDependencies();
  }, [fetchDependencies]);

  // 更新搜索参数
  const handleSetSearchParams = useCallback(
    (params: DependencySearchParams) => {
      setSearchParams(params);
    },
    []
  );

  // 初始化数据获取
  useEffect(() => {
    if (autoFetch) {
      fetchDependencies();
    }
  }, [autoFetch]); // 移除fetchDependencies依赖以避免无限循环

  // 当搜索参数变化时重新获取数据
  useEffect(() => {
    if (autoFetch && Object.keys(searchParams).length > 0) {
      fetchDependencies(searchParams);
    }
  }, [searchParams, autoFetch]); // 这里可以保留searchParams依赖

  return {
    // 数据状态
    stats,
    treeData,
    dependencies,

    // 加载状态
    loading,
    error,

    // 操作方法
    fetchDependencies,
    scanSecurity,
    refreshData,

    // 搜索和过滤
    searchParams,
    setSearchParams: handleSetSearchParams,
  };
};
