# DependencyApiResponseData

## Properties

| Name             | Type                                                         | Description | Notes                  |
| ---------------- | ------------------------------------------------------------ | ----------- | ---------------------- |
| **stats**        | [**DependencyStats**](DependencyStats.md)                    |             | [default to undefined] |
| **tree**         | [**Array&lt;DependencyTreeNode&gt;**](DependencyTreeNode.md) |             | [default to undefined] |
| **dependencies** | [**Array&lt;DependencyItem&gt;**](DependencyItem.md)         |             | [default to undefined] |

## Example

```typescript
import { DependencyApiResponseData } from './api';

const instance: DependencyApiResponseData = {
  stats,
  tree,
  dependencies,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
