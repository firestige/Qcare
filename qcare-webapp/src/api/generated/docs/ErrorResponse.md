# ErrorResponse

## Properties

| Name        | Type                        | Description  | Notes                             |
| ----------- | --------------------------- | ------------ | --------------------------------- |
| **success** | **boolean**                 |              | [default to undefined]            |
| **message** | **string**                  | 错误消息     | [default to undefined]            |
| **code**    | **string**                  | 错误代码     | [optional] [default to undefined] |
| **details** | **{ [key: string]: any; }** | 详细错误信息 | [optional] [default to undefined] |

## Example

```typescript
import { ErrorResponse } from './api';

const instance: ErrorResponse = {
  success,
  message,
  code,
  details,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
