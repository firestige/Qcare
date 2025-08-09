# DependenciesApi

All URIs are relative to _http://localhost:8080/api_

| Method                                                      | HTTP request                        | Description  |
| ----------------------------------------------------------- | ----------------------------------- | ------------ |
| [**dependenciesDetailNameGet**](#dependenciesdetailnameget) | **GET** /dependencies/detail/{name} | 获取依赖详情 |
| [**dependenciesGet**](#dependenciesget)                     | **GET** /dependencies               | 获取依赖列表 |

# **dependenciesDetailNameGet**

> DependencyDetailResponse dependenciesDetailNameGet()

获取指定依赖的详细信息，包括版本历史、安全报告等

### Example

```typescript
import { DependenciesApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new DependenciesApi(configuration);

let name: string; //依赖名称 (default to undefined)

const { status, data } = await apiInstance.dependenciesDetailNameGet(name);
```

### Parameters

| Name     | Type         | Description | Notes                 |
| -------- | ------------ | ----------- | --------------------- |
| **name** | [**string**] | 依赖名称    | defaults to undefined |

### Return type

**DependencyDetailResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description      | Response headers |
| ----------- | ---------------- | ---------------- |
| **200**     | 成功获取依赖详情 | -                |
| **404**     | 依赖不存在       | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **dependenciesGet**

> DependencyApiResponse dependenciesGet()

获取系统中所有依赖项的详细信息，支持搜索和过滤

### Example

```typescript
import { DependenciesApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new DependenciesApi(configuration);

let keyword: string; //搜索关键词，可匹配依赖名称或描述 (optional) (default to undefined)
let status: 'latest' | 'outdated' | 'critical'; //依赖状态过滤 (optional) (default to undefined)
let security: 'safe' | 'warning' | 'vulnerable'; //安全级别过滤 (optional) (default to undefined)
let service: string; //使用该依赖的服务名称 (optional) (default to undefined)

const { status, data } = await apiInstance.dependenciesGet(
  keyword,
  status,
  security,
  service
);
```

### Parameters

| Name         | Type                  | Description                      | Notes                                                                                                   |
| ------------ | --------------------- | -------------------------------- | ------------------------------------------------------------------------------------------------------- | ------------ | -------------------------------- |
| **keyword**  | [**string**]          | 搜索关键词，可匹配依赖名称或描述 | (optional) defaults to undefined                                                                        |
| **status**   | [\*\*&#39;latest&#39; | &#39;outdated&#39;               | &#39;critical&#39;**]**Array<&#39;latest&#39; &#124; &#39;outdated&#39; &#124; &#39;critical&#39;>\*\*  | 依赖状态过滤 | (optional) defaults to undefined |
| **security** | [\*\*&#39;safe&#39;   | &#39;warning&#39;                | &#39;vulnerable&#39;**]**Array<&#39;safe&#39; &#124; &#39;warning&#39; &#124; &#39;vulnerable&#39;>\*\* | 安全级别过滤 | (optional) defaults to undefined |
| **service**  | [**string**]          | 使用该依赖的服务名称             | (optional) defaults to undefined                                                                        |

### Return type

**DependencyApiResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description      | Response headers |
| ----------- | ---------------- | ---------------- |
| **200**     | 成功获取依赖列表 | -                |
| **400**     | 请求参数错误     | -                |
| **401**     | 未授权访问       | -                |
| **500**     | 服务器内部错误   | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
