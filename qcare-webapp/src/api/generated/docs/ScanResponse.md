# ScanResponse

## Properties

| Name           | Type                                                    | Description  | Notes                             |
| -------------- | ------------------------------------------------------- | ------------ | --------------------------------- |
| **success**    | **boolean**                                             | 扫描是否成功 | [default to undefined]            |
| **message**    | **string**                                              | 扫描结果消息 | [optional] [default to undefined] |
| **scanResult** | [**ScanResponseScanResult**](ScanResponseScanResult.md) |              | [optional] [default to undefined] |

## Example

```typescript
import { ScanResponse } from './api';

const instance: ScanResponse = {
  success,
  message,
  scanResult,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
