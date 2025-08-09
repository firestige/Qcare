# ToolsApi

All URIs are relative to _http://localhost:8080/api_

| Method                                                                | HTTP request                             | Description                  |
| --------------------------------------------------------------------- | ---------------------------------------- | ---------------------------- |
| [**arthasInstanceIdConnectPost**](#arthasinstanceidconnectpost)       | **POST** /arthas/{instanceId}/connect    | 启动并连接目标实例的 Arthas  |
| [**arthasInstanceIdDisconnectPost**](#arthasinstanceiddisconnectpost) | **POST** /arthas/{instanceId}/disconnect | 断开与目标实例 Arthas 的连接 |
| [**arthasInstanceIdReconnectPost**](#arthasinstanceidreconnectpost)   | **POST** /arthas/{instanceId}/reconnect  | 重新连接目标实例的 Arthas    |
| [**arthasInstanceIdWsGet**](#arthasinstanceidwsget)                   | **GET** /arthas/{instanceId}/ws          | Arthas 控制台 WebSocket      |
| [**arthasInstancesGet**](#arthasinstancesget)                         | **GET** /arthas/instances                | 查询所有实例列表             |

# **arthasInstanceIdConnectPost**

> OperationResponse arthasInstanceIdConnectPost()

启动并连接指定实例的 Arthas。连接成功后，前端应通过 WebSocket 连接服务器以实时获取 Arthas 控制台输出。

### Example

```typescript
import { ToolsApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new ToolsApi(configuration);

let instanceId: string; //实例唯一标识 (default to undefined)

const { status, data } =
  await apiInstance.arthasInstanceIdConnectPost(instanceId);
```

### Parameters

| Name           | Type         | Description  | Notes                 |
| -------------- | ------------ | ------------ | --------------------- |
| **instanceId** | [**string**] | 实例唯一标识 | defaults to undefined |

### Return type

**OperationResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | 连接成功    | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **arthasInstanceIdDisconnectPost**

> OperationResponse arthasInstanceIdDisconnectPost()

断开指定实例的 Arthas 连接

### Example

```typescript
import { ToolsApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new ToolsApi(configuration);

let instanceId: string; //实例唯一标识 (default to undefined)

const { status, data } =
  await apiInstance.arthasInstanceIdDisconnectPost(instanceId);
```

### Parameters

| Name           | Type         | Description  | Notes                 |
| -------------- | ------------ | ------------ | --------------------- |
| **instanceId** | [**string**] | 实例唯一标识 | defaults to undefined |

### Return type

**OperationResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | 断开成功    | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **arthasInstanceIdReconnectPost**

> OperationResponse arthasInstanceIdReconnectPost()

重新连接指定实例的 Arthas

### Example

```typescript
import { ToolsApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new ToolsApi(configuration);

let instanceId: string; //实例唯一标识 (default to undefined)

const { status, data } =
  await apiInstance.arthasInstanceIdReconnectPost(instanceId);
```

### Parameters

| Name           | Type         | Description  | Notes                 |
| -------------- | ------------ | ------------ | --------------------- |
| **instanceId** | [**string**] | 实例唯一标识 | defaults to undefined |

### Return type

**OperationResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description  | Response headers |
| ----------- | ------------ | ---------------- |
| **200**     | 重新连接成功 | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **arthasInstanceIdWsGet**

> arthasInstanceIdWsGet()

建立 WebSocket 连接以实时接收 Arthas 控制台输出和发送命令。 客户端应在调用 `/connect` 成功后，使用此接口建立 WebSocket 连接。 WebSocket 地址示例：`ws://localhost:8080/api/arthas/{instanceId}/ws`

### Example

```typescript
import { ToolsApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new ToolsApi(configuration);

let instanceId: string; //实例唯一标识 (default to undefined)

const { status, data } = await apiInstance.arthasInstanceIdWsGet(instanceId);
```

### Parameters

| Name           | Type         | Description  | Notes                 |
| -------------- | ------------ | ------------ | --------------------- |
| **instanceId** | [**string**] | 实例唯一标识 | defaults to undefined |

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details

| Status code | Description          | Response headers |
| ----------- | -------------------- | ---------------- |
| **101**     | WebSocket 连接已升级 | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **arthasInstancesGet**

> ArthasInstanceListResponse arthasInstancesGet()

获取所有应用实例及其 Arthas 状态

### Example

```typescript
import { ToolsApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new ToolsApi(configuration);

const { status, data } = await apiInstance.arthasInstancesGet();
```

### Parameters

This endpoint does not have any parameters.

### Return type

**ArthasInstanceListResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | 实例列表    | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
