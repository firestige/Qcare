# DependencyDetailResponseData

## Properties

| Name               | Type                                                                                                                   | Description | Notes                             |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------- | ----------- | --------------------------------- |
| **name**           | **string**                                                                                                             |             | [optional] [default to undefined] |
| **currentVersion** | **string**                                                                                                             |             | [optional] [default to undefined] |
| **latestVersion**  | **string**                                                                                                             |             | [optional] [default to undefined] |
| **description**    | **string**                                                                                                             |             | [optional] [default to undefined] |
| **license**        | **string**                                                                                                             |             | [optional] [default to undefined] |
| **homepage**       | **string**                                                                                                             |             | [optional] [default to undefined] |
| **repository**     | **string**                                                                                                             |             | [optional] [default to undefined] |
| **maintainer**     | **string**                                                                                                             |             | [optional] [default to undefined] |
| **downloads**      | **number**                                                                                                             |             | [optional] [default to undefined] |
| **versionHistory** | [**Array&lt;DependencyDetailResponseDataVersionHistoryInner&gt;**](DependencyDetailResponseDataVersionHistoryInner.md) |             | [optional] [default to undefined] |
| **securityReport** | [**DependencyDetailResponseDataSecurityReport**](DependencyDetailResponseDataSecurityReport.md)                        |             | [optional] [default to undefined] |
| **usageInfo**      | [**DependencyDetailResponseDataUsageInfo**](DependencyDetailResponseDataUsageInfo.md)                                  |             | [optional] [default to undefined] |

## Example

```typescript
import { DependencyDetailResponseData } from './api';

const instance: DependencyDetailResponseData = {
  name,
  currentVersion,
  latestVersion,
  description,
  license,
  homepage,
  repository,
  maintainer,
  downloads,
  versionHistory,
  securityReport,
  usageInfo,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
