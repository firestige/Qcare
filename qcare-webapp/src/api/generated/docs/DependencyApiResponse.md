# DependencyApiResponse

## Properties

| Name        | Type                                                          | Description  | Notes                             |
| ----------- | ------------------------------------------------------------- | ------------ | --------------------------------- |
| **success** | **boolean**                                                   | 请求是否成功 | [default to undefined]            |
| **data**    | [**DependencyApiResponseData**](DependencyApiResponseData.md) |              | [default to undefined]            |
| **message** | **string**                                                    | 附加消息     | [optional] [default to undefined] |

## Example

```typescript
import { DependencyApiResponse } from './api';

const instance: DependencyApiResponse = {
  success,
  data,
  message,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
