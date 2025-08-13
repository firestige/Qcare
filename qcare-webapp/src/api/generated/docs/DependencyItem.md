# DependencyItem

## Properties

| Name              | Type                    | Description          | Notes                             |
| ----------------- | ----------------------- | -------------------- | --------------------------------- |
| **key**           | **string**              | 依赖唯一标识         | [default to undefined]            |
| **name**          | **string**              | 依赖名称             | [default to undefined]            |
| **version**       | **string**              | 当前版本             | [default to undefined]            |
| **latestVersion** | **string**              | 最新版本             | [default to undefined]            |
| **status**        | **string**              | 版本状态             | [default to undefined]            |
| **security**      | **string**              | 安全级别             | [default to undefined]            |
| **license**       | **string**              | 许可证               | [default to undefined]            |
| **size**          | **string**              | 文件大小             | [default to undefined]            |
| **usedBy**        | **Array&lt;string&gt;** | 使用该依赖的服务列表 | [default to undefined]            |
| **description**   | **string**              | 依赖描述             | [optional] [default to undefined] |
| **homepage**      | **string**              | 项目主页             | [optional] [default to undefined] |
| **repository**    | **string**              | 代码仓库             | [optional] [default to undefined] |
| **lastUpdate**    | **string**              | 最后更新日期         | [optional] [default to undefined] |

## Example

```typescript
import { DependencyItem } from './api';

const instance: DependencyItem = {
  key,
  name,
  version,
  latestVersion,
  status,
  security,
  license,
  size,
  usedBy,
  description,
  homepage,
  repository,
  lastUpdate,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
