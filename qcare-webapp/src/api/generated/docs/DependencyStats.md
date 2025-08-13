# DependencyStats

## Properties

| Name                       | Type       | Description      | Notes                  |
| -------------------------- | ---------- | ---------------- | ---------------------- |
| **totalDependencies**      | **number** | 总依赖数         | [default to undefined] |
| **safeDependencies**       | **number** | 安全依赖数       | [default to undefined] |
| **outdatedDependencies**   | **number** | 过期依赖数       | [default to undefined] |
| **vulnerableDependencies** | **number** | 存在漏洞的依赖数 | [default to undefined] |

## Example

```typescript
import { DependencyStats } from './api';

const instance: DependencyStats = {
  totalDependencies,
  safeDependencies,
  outdatedDependencies,
  vulnerableDependencies,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
