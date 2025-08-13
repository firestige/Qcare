# ArthasInstanceInfo

## Properties

| Name             | Type       | Description  | Notes                             |
| ---------------- | ---------- | ------------ | --------------------------------- |
| **instanceId**   | **string** | 实例唯一标识 | [optional] [default to undefined] |
| **name**         | **string** | 实例名称     | [optional] [default to undefined] |
| **pid**          | **number** | 进程ID       | [optional] [default to undefined] |
| **host**         | **string** | 主机IP       | [optional] [default to undefined] |
| **port**         | **number** | 端口         | [optional] [default to undefined] |
| **memoryUsage**  | **string** | 内存使用情况 | [optional] [default to undefined] |
| **cpuUsage**     | **string** | CPU使用情况  | [optional] [default to undefined] |
| **status**       | **string** | 实例状态     | [optional] [default to undefined] |
| **arthasStatus** | **string** | Arthas 状态  | [optional] [default to undefined] |

## Example

```typescript
import { ArthasInstanceInfo } from './api';

const instance: ArthasInstanceInfo = {
  instanceId,
  name,
  pid,
  host,
  port,
  memoryUsage,
  cpuUsage,
  status,
  arthasStatus,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
