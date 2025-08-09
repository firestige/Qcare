# ScanResponseScanResult

## Properties

| Name                     | Type       | Description    | Notes                             |
| ------------------------ | ---------- | -------------- | --------------------------------- |
| **totalScanned**         | **number** | 扫描的依赖总数 | [optional] [default to undefined] |
| **vulnerabilitiesFound** | **number** | 发现的漏洞总数 | [optional] [default to undefined] |
| **criticalCount**        | **number** | 严重漏洞数量   | [optional] [default to undefined] |
| **highCount**            | **number** | 高危漏洞数量   | [optional] [default to undefined] |
| **mediumCount**          | **number** | 中危漏洞数量   | [optional] [default to undefined] |
| **lowCount**             | **number** | 低危漏洞数量   | [optional] [default to undefined] |
| **scanTime**             | **string** | 扫描时间       | [optional] [default to undefined] |

## Example

```typescript
import { ScanResponseScanResult } from './api';

const instance: ScanResponseScanResult = {
  totalScanned,
  vulnerabilitiesFound,
  criticalCount,
  highCount,
  mediumCount,
  lowCount,
  scanTime,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
