import React, { useState } from 'react';
import {
  Card,
  Row,
  Col,
  Table,
  Tag,
  Button,
  Progress,
  Select,
  DatePicker,
  Space,
  Modal,
  Steps,
  List,
  Descriptions,
  Alert,
  Upload,
  message,
  Statistic,
  Timeline,
} from 'antd';
import {
  PlayCircleOutlined,
  PauseCircleOutlined,
  StopOutlined,
  UploadOutlined,
  DownloadOutlined,
  EyeOutlined,
  ReloadOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  FileTextOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons';

const { RangePicker } = DatePicker;
const { Step } = Steps;

const TrafficReplay: React.FC = () => {
  const [selectedReplay, setSelectedReplay] = useState<any>(null);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [currentStep, setCurrentStep] = useState<number>(0);

  // 流量回放任务数据
  const replayData = [
    {
      key: '1',
      name: '用户登录流量回放',
      source: 'production-2025-07-30',
      target: 'staging-env',
      status: 'running',
      progress: 65,
      startTime: '2025-08-01 14:30:00',
      duration: '25分钟',
      totalRequests: 15420,
      completedRequests: 10023,
      successRate: 98.5,
      errorCount: 12,
      avgResponseTime: '145ms',
    },
    {
      key: '2',
      name: '订单处理流量回放',
      source: 'production-2025-07-29',
      target: 'test-env',
      status: 'completed',
      progress: 100,
      startTime: '2025-08-01 13:15:00',
      duration: '42分钟',
      totalRequests: 8934,
      completedRequests: 8934,
      successRate: 99.2,
      errorCount: 3,
      avgResponseTime: '98ms',
    },
    {
      key: '3',
      name: '支付接口流量回放',
      source: 'production-2025-07-28',
      target: 'dev-env',
      status: 'failed',
      progress: 35,
      startTime: '2025-08-01 12:00:00',
      duration: '18分钟',
      totalRequests: 5672,
      completedRequests: 1985,
      successRate: 89.3,
      errorCount: 156,
      avgResponseTime: '256ms',
    },
    {
      key: '4',
      name: '搜索功能流量回放',
      source: 'production-2025-07-31',
      target: 'staging-env',
      status: 'pending',
      progress: 0,
      startTime: '预计 15:30:00',
      duration: '预计 30分钟',
      totalRequests: 12345,
      completedRequests: 0,
      successRate: 0,
      errorCount: 0,
      avgResponseTime: '-',
    },
  ];

  // 流量源数据
  const trafficSources = [
    {
      name: 'production-2025-07-31',
      description: '生产环境 7月31日 全天流量',
      size: '2.3GB',
      requests: 245678,
      duration: '24小时',
      captured: '2025-07-31',
    },
    {
      name: 'production-2025-07-30',
      description: '生产环境 7月30日 高峰时段流量',
      size: '1.8GB',
      requests: 156423,
      duration: '4小时',
      captured: '2025-07-30',
    },
    {
      name: 'production-2025-07-29',
      description: '生产环境 7月29日 业务流量',
      size: '1.2GB',
      requests: 89234,
      duration: '8小时',
      captured: '2025-07-29',
    },
  ];

  // 回放步骤
  const replaySteps = [
    {
      title: '选择流量源',
      description: '选择要回放的流量数据源',
    },
    {
      title: '配置目标环境',
      description: '设置回放的目标测试环境',
    },
    {
      title: '设置回放参数',
      description: '配置回放速度、过滤条件等',
    },
    {
      title: '开始回放',
      description: '启动流量回放任务',
    },
  ];

  const statusConfig = {
    running: {
      color: 'processing',
      text: '进行中',
      icon: <PlayCircleOutlined />,
    },
    completed: {
      color: 'success',
      text: '已完成',
      icon: <CheckCircleOutlined />,
    },
    failed: {
      color: 'error',
      text: '失败',
      icon: <ExclamationCircleOutlined />,
    },
    pending: {
      color: 'default',
      text: '等待中',
      icon: <ClockCircleOutlined />,
    },
    paused: { color: 'warning', text: '已暂停', icon: <PauseCircleOutlined /> },
  };

  const columns = [
    {
      title: '任务名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
    },
    {
      title: '流量源',
      dataIndex: 'source',
      key: 'source',
      width: 150,
    },
    {
      title: '目标环境',
      dataIndex: 'target',
      key: 'target',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const config = statusConfig[status as keyof typeof statusConfig];
        return (
          <Tag color={config.color} icon={config.icon}>
            {config.text}
          </Tag>
        );
      },
    },
    {
      title: '进度',
      dataIndex: 'progress',
      key: 'progress',
      width: 120,
      render: (progress: number, record: any) => (
        <Progress
          percent={progress}
          size="small"
          status={record.status === 'failed' ? 'exception' : 'normal'}
        />
      ),
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 150,
    },
    {
      title: '成功率',
      dataIndex: 'successRate',
      key: 'successRate',
      width: 100,
      render: (rate: number) => (
        <span
          style={{
            color: rate > 95 ? '#52c41a' : rate > 90 ? '#fa8c16' : '#ff4d4f',
          }}
        >
          {rate > 0 ? `${rate}%` : '-'}
        </span>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_: any, record: any) => (
        <Space>
          {record.status === 'running' && (
            <Button size="small" icon={<PauseCircleOutlined />}>
              暂停
            </Button>
          )}
          {record.status === 'paused' && (
            <Button size="small" icon={<PlayCircleOutlined />} type="primary">
              继续
            </Button>
          )}
          {(record.status === 'running' || record.status === 'paused') && (
            <Button size="small" icon={<StopOutlined />} danger>
              停止
            </Button>
          )}
          <Button
            size="small"
            icon={<EyeOutlined />}
            onClick={() => {
              setSelectedReplay(record);
              setModalVisible(true);
            }}
          >
            详情
          </Button>
        </Space>
      ),
    },
  ];

  const handleUpload = (info: any) => {
    if (info.file.status === 'done') {
      message.success(`${info.file.name} 流量文件上传成功`);
    } else if (info.file.status === 'error') {
      message.error(`${info.file.name} 流量文件上传失败`);
    }
  };

  return (
    <div
      style={{
        padding: '24px',
        backgroundColor: '#f5f5f5',
        minHeight: '100vh',
      }}
    >
      <Row gutter={[16, 16]}>
        {/* 统计卡片 */}
        <Col span={6}>
          <Card>
            <Statistic
              title="总回放任务"
              value={4}
              valueStyle={{ color: '#1890ff' }}
              prefix={<PlayCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="运行中任务"
              value={1}
              valueStyle={{ color: '#3f8600' }}
              prefix={<ThunderboltOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日完成"
              value={2}
              valueStyle={{ color: '#722ed1' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="平均成功率"
              value={95.7}
              suffix="%"
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 操作工具栏 */}
      <Card style={{ marginTop: '16px' }}>
        <Space>
          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            onClick={() => setCreateModalVisible(true)}
          >
            创建回放任务
          </Button>
          <Upload
            accept=".har,.pcap,.json"
            showUploadList={false}
            onChange={handleUpload}
          >
            <Button icon={<UploadOutlined />}>上传流量文件</Button>
          </Upload>
          <Select placeholder="选择环境" style={{ width: 120 }} allowClear>
            <Select.Option value="staging">Staging</Select.Option>
            <Select.Option value="test">Test</Select.Option>
            <Select.Option value="dev">Dev</Select.Option>
          </Select>
          <RangePicker />
          <Button icon={<ReloadOutlined />}>刷新</Button>
        </Space>
      </Card>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        {/* 回放任务列表 */}
        <Col span={16}>
          <Card title="流量回放任务">
            <Table
              dataSource={replayData}
              columns={columns}
              pagination={{ pageSize: 8 }}
              size="small"
              scroll={{ x: 1200 }}
            />
          </Card>
        </Col>

        {/* 流量源列表 */}
        <Col span={8}>
          <Card
            title="可用流量源"
            extra={
              <Button size="small" icon={<DownloadOutlined />}>
                导出
              </Button>
            }
          >
            <List
              dataSource={trafficSources}
              renderItem={(item) => (
                <List.Item
                  actions={[
                    <Button
                      size="small"
                      type="link"
                      onClick={() => setCreateModalVisible(true)}
                    >
                      使用
                    </Button>,
                  ]}
                >
                  <List.Item.Meta
                    avatar={
                      <FileTextOutlined
                        style={{ fontSize: '20px', color: '#1890ff' }}
                      />
                    }
                    title={item.name}
                    description={
                      <div>
                        <div>{item.description}</div>
                        <div
                          style={{
                            fontSize: '12px',
                            color: '#666',
                            marginTop: '4px',
                          }}
                        >
                          <span>大小: {item.size}</span> |
                          <span> 请求数: {item.requests.toLocaleString()}</span>
                        </div>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      {/* 回放详情模态框 */}
      <Modal
        title="回放任务详情"
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        width={900}
        footer={[
          <Button key="close" onClick={() => setModalVisible(false)}>
            关闭
          </Button>,
        ]}
      >
        {selectedReplay && (
          <div>
            {selectedReplay.status === 'failed' && (
              <Alert
                message="回放失败"
                description="流量回放过程中遇到错误，请检查目标环境状态和网络连接。"
                type="error"
                showIcon
                style={{ marginBottom: '16px' }}
              />
            )}

            <Row gutter={16}>
              <Col span={12}>
                <Descriptions title="基本信息" bordered column={1} size="small">
                  <Descriptions.Item label="任务名称">
                    {selectedReplay.name}
                  </Descriptions.Item>
                  <Descriptions.Item label="流量源">
                    {selectedReplay.source}
                  </Descriptions.Item>
                  <Descriptions.Item label="目标环境">
                    {selectedReplay.target}
                  </Descriptions.Item>
                  <Descriptions.Item label="开始时间">
                    {selectedReplay.startTime}
                  </Descriptions.Item>
                  <Descriptions.Item label="持续时间">
                    {selectedReplay.duration}
                  </Descriptions.Item>
                </Descriptions>
              </Col>
              <Col span={12}>
                <Descriptions title="执行统计" bordered column={1} size="small">
                  <Descriptions.Item label="总请求数">
                    {selectedReplay.totalRequests.toLocaleString()}
                  </Descriptions.Item>
                  <Descriptions.Item label="已完成">
                    {selectedReplay.completedRequests.toLocaleString()}
                  </Descriptions.Item>
                  <Descriptions.Item label="成功率">
                    {selectedReplay.successRate}%
                  </Descriptions.Item>
                  <Descriptions.Item label="错误数">
                    {selectedReplay.errorCount}
                  </Descriptions.Item>
                  <Descriptions.Item label="平均响应时间">
                    {selectedReplay.avgResponseTime}
                  </Descriptions.Item>
                </Descriptions>
              </Col>
            </Row>

            {selectedReplay.status === 'running' && (
              <Card title="实时监控" style={{ marginTop: '16px' }}>
                <Timeline
                  items={[
                    {
                      color: 'green',
                      children: '回放任务启动成功',
                    },
                    {
                      color: 'blue',
                      children: '开始处理请求数据',
                    },
                    {
                      color: 'blue',
                      children: `已处理 ${selectedReplay.completedRequests.toLocaleString()} 个请求`,
                    },
                    {
                      dot: <ClockCircleOutlined style={{ fontSize: '16px' }} />,
                      children: '正在进行中...',
                    },
                  ]}
                />
              </Card>
            )}
          </div>
        )}
      </Modal>

      {/* 创建回放任务模态框 */}
      <Modal
        title="创建流量回放任务"
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          setCurrentStep(0);
        }}
        width={800}
        footer={[
          <Button key="cancel" onClick={() => setCreateModalVisible(false)}>
            取消
          </Button>,
          <Button
            key="prev"
            disabled={currentStep === 0}
            onClick={() => setCurrentStep(currentStep - 1)}
          >
            上一步
          </Button>,
          <Button
            key="next"
            type="primary"
            disabled={currentStep === replaySteps.length - 1}
            onClick={() => setCurrentStep(currentStep + 1)}
          >
            下一步
          </Button>,
          <Button
            key="start"
            type="primary"
            disabled={currentStep !== replaySteps.length - 1}
            onClick={() => {
              message.success('流量回放任务创建成功！');
              setCreateModalVisible(false);
              setCurrentStep(0);
            }}
          >
            开始回放
          </Button>,
        ]}
      >
        <Steps current={currentStep} style={{ marginBottom: '24px' }}>
          {replaySteps.map((step, index) => (
            <Step
              key={index}
              title={step.title}
              description={step.description}
            />
          ))}
        </Steps>

        <div style={{ minHeight: '200px' }}>
          {currentStep === 0 && (
            <Card title="选择流量源">
              <List
                dataSource={trafficSources}
                renderItem={(item) => (
                  <List.Item>
                    <List.Item.Meta
                      title={item.name}
                      description={`${item.description} - ${item.size}`}
                    />
                  </List.Item>
                )}
              />
            </Card>
          )}

          {currentStep === 1 && (
            <Card title="配置目标环境">
              <Space direction="vertical" style={{ width: '100%' }}>
                <div>
                  <label>目标环境:</label>
                  <Select
                    style={{ width: '200px', marginLeft: '8px' }}
                    placeholder="选择环境"
                  >
                    <Select.Option value="staging">
                      Staging Environment
                    </Select.Option>
                    <Select.Option value="test">Test Environment</Select.Option>
                    <Select.Option value="dev">Dev Environment</Select.Option>
                  </Select>
                </div>
                <div>
                  <label>基础URL:</label>
                  <input
                    type="text"
                    placeholder="https://staging-api.example.com"
                    style={{
                      marginLeft: '8px',
                      padding: '4px 8px',
                      width: '300px',
                    }}
                  />
                </div>
              </Space>
            </Card>
          )}

          {currentStep === 2 && (
            <Card title="设置回放参数">
              <Space direction="vertical" style={{ width: '100%' }}>
                <div>
                  <label>回放速度:</label>
                  <Select
                    style={{ width: '150px', marginLeft: '8px' }}
                    defaultValue="1x"
                  >
                    <Select.Option value="0.5x">0.5x (慢速)</Select.Option>
                    <Select.Option value="1x">1x (正常)</Select.Option>
                    <Select.Option value="2x">2x (快速)</Select.Option>
                    <Select.Option value="5x">5x (极速)</Select.Option>
                  </Select>
                </div>
                <div>
                  <label>并发数:</label>
                  <input
                    type="number"
                    defaultValue={10}
                    style={{
                      marginLeft: '8px',
                      padding: '4px 8px',
                      width: '100px',
                    }}
                  />
                </div>
              </Space>
            </Card>
          )}

          {currentStep === 3 && (
            <Card title="确认回放设置">
              <Descriptions column={1}>
                <Descriptions.Item label="流量源">
                  production-2025-07-31
                </Descriptions.Item>
                <Descriptions.Item label="目标环境">
                  Staging Environment
                </Descriptions.Item>
                <Descriptions.Item label="回放速度">
                  1x (正常)
                </Descriptions.Item>
                <Descriptions.Item label="预计时间">
                  约 30 分钟
                </Descriptions.Item>
              </Descriptions>
            </Card>
          )}
        </div>
      </Modal>
    </div>
  );
};

export default TrafficReplay;
