# DependencyTreeNode

## Properties

| Name         | Type                                                         | Description  | Notes                             |
| ------------ | ------------------------------------------------------------ | ------------ | --------------------------------- |
| **title**    | **string**                                                   | 节点标题     | [default to undefined]            |
| **key**      | **string**                                                   | 节点唯一标识 | [default to undefined]            |
| **children** | [**Array&lt;DependencyTreeNode&gt;**](DependencyTreeNode.md) |              | [optional] [default to undefined] |

## Example

```typescript
import { DependencyTreeNode } from './api';

const instance: DependencyTreeNode = {
  title,
  key,
  children,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
