# SecurityApi

All URIs are relative to _http://localhost:8080/api_

| Method                                            | HTTP request                | Description  |
| ------------------------------------------------- | --------------------------- | ------------ |
| [**dependenciesScanPost**](#dependenciesscanpost) | **POST** /dependencies/scan | 安全漏洞扫描 |

# **dependenciesScanPost**

> ScanResponse dependenciesScanPost()

对系统中的所有依赖进行安全漏洞扫描

### Example

```typescript
import { SecurityApi, Configuration } from './api';

const configuration = new Configuration();
const apiInstance = new SecurityApi(configuration);

const { status, data } = await apiInstance.dependenciesScanPost();
```

### Parameters

This endpoint does not have any parameters.

### Return type

**ScanResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
| ----------- | ----------- | ---------------- |
| **200**     | 扫描完成    | -                |
| **500**     | 扫描失败    | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
