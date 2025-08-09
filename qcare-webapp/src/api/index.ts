import { apiClient } from './config';
import { Configuration, DependenciesApi, SecurityApi } from './generated';

// 创建 API 配置
const configuration = new Configuration({
  basePath: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
});

// 创建 API 实例
export const dependenciesApi = new DependenciesApi(
  configuration,
  undefined,
  apiClient
);
export const securityApi = new SecurityApi(configuration, undefined, apiClient);

// 导出类型
export * from './generated';

// 错误处理工具函数
export const handleApiError = (error: any) => {
  if (error.response) {
    // 服务器响应错误
    console.error('API Error:', error.response.status, error.response.data);
    return {
      status: error.response.status,
      message: error.response.data?.message || 'API request failed',
      data: error.response.data,
    };
  } else if (error.request) {
    // 请求发送失败
    console.error('Network Error:', error.request);
    return {
      status: 0,
      message: 'Network error - please check your connection',
      data: null,
    };
  } else {
    // 其他错误
    console.error('Error:', error.message);
    return {
      status: -1,
      message: error.message || 'Unknown error occurred',
      data: null,
    };
  }
};
