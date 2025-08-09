// Arthas API 服务
import type {
  ArthasApiResponse,
  ArthasSearchParams,
  CommandExecuteRequest,
  CommandExecuteResponse,
  ArthasConnectionRequest,
  ArthasConnectionResponse,
} from '../types/arthas';

// API 基础配置
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
const API_TIMEOUT = 30000; // 30秒超时

// HTTP 客户端工具函数
const createApiClient = () => {
  const request = async (
    endpoint: string,
    options: RequestInit = {}
  ): Promise<Response> => {
    const url = `${API_BASE_URL}${endpoint}`;
    const token = localStorage.getItem('authToken');

    const defaultHeaders: HeadersInit = {
      'Content-Type': 'application/json',
    };

    if (token) {
      defaultHeaders['Authorization'] = `Bearer ${token}`;
    }

    const config: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    };

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), API_TIMEOUT);

    try {
      const response = await fetch(url, {
        ...config,
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return response;
    } catch (error) {
      clearTimeout(timeoutId);
      throw error;
    }
  };

  return {
    get: (endpoint: string) => request(endpoint, { method: 'GET' }),
    post: (endpoint: string, data?: any) =>
      request(endpoint, {
        method: 'POST',
        body: data ? JSON.stringify(data) : undefined,
      }),
    put: (endpoint: string, data?: any) =>
      request(endpoint, {
        method: 'PUT',
        body: data ? JSON.stringify(data) : undefined,
      }),
    delete: (endpoint: string) => request(endpoint, { method: 'DELETE' }),
  };
};

const apiClient = createApiClient();

// Arthas API 服务
export const arthasService = {
  /**
   * 获取 Arthas 实例列表
   */
  async getInstances(params?: ArthasSearchParams): Promise<ArthasApiResponse> {
    const queryParams = new URLSearchParams();

    if (params?.keyword) queryParams.append('keyword', params.keyword);
    if (params?.status) queryParams.append('status', params.status);
    if (params?.arthasStatus)
      queryParams.append('arthasStatus', params.arthasStatus);
    if (params?.host) queryParams.append('host', params.host);

    const endpoint = `/arthas/instances${queryParams.toString() ? `?${queryParams}` : ''}`;
    const response = await apiClient.get(endpoint);
    return response.json();
  },

  /**
   * 连接到 Arthas 实例
   */
  async connectInstance(
    request: ArthasConnectionRequest
  ): Promise<ArthasConnectionResponse> {
    const response = await apiClient.post('/arthas/connect', request);
    return response.json();
  },

  /**
   * 断开 Arthas 实例连接
   */
  async disconnectInstance(
    instanceName: string
  ): Promise<ArthasConnectionResponse> {
    const response = await apiClient.post('/arthas/disconnect', {
      instanceName,
    });
    return response.json();
  },

  /**
   * 执行 Arthas 命令
   */
  async executeCommand(
    request: CommandExecuteRequest
  ): Promise<CommandExecuteResponse> {
    const response = await apiClient.post('/arthas/execute', request);
    return response.json();
  },

  /**
   * 获取命令执行历史
   */
  async getCommandHistory(instanceName: string): Promise<{
    success: boolean;
    data: Array<{
      command: string;
      timestamp: string;
      result: string;
    }>;
  }> {
    const response = await apiClient.get(`/arthas/history/${instanceName}`);
    return response.json();
  },

  /**
   * 下载 Arthas 工具
   */
  async downloadArthas(): Promise<{
    success: boolean;
    downloadUrl: string;
    version: string;
  }> {
    const response = await apiClient.get('/arthas/download');
    return response.json();
  },

  /**
   * 一键部署 Arthas 到指定实例
   */
  async deployArthas(instanceName: string): Promise<{
    success: boolean;
    message: string;
  }> {
    const response = await apiClient.post('/arthas/deploy', { instanceName });
    return response.json();
  },

  /**
   * 获取 Arthas 统计信息
   */
  async getStats(): Promise<{
    success: boolean;
    data: {
      totalInstances: number;
      runningInstances: number;
      connectedInstances: number;
      todayDiagnosticCount: number;
      reportCount: number;
    };
  }> {
    const response = await apiClient.get('/arthas/stats');
    return response.json();
  },
};
