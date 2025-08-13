import React, { useState } from 'react';
import {
  Card,
  Row,
  Col,
  Table,
  Tag,
  Progress,
  Statistic,
  Select,
  DatePicker,
  Button,
  Space,
  Tabs,
  List,
  Tooltip,
  Modal,
  Alert,
  Timeline,
} from 'antd';
import {
  GlobalOutlined,
  WifiOutlined,
  ThunderboltOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  EyeOutlined,
  ReloadOutlined,
  LineChartOutlined,
  SafetyOutlined,
} from '@ant-design/icons';

const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

const NetworkAnalysis: React.FC = () => {
  const [selectedConnection, setSelectedConnection] = useState<any>(null);
  const [modalVisible, setModalVisible] = useState<boolean>(false);

  // 网络连接数据
  const networkData = [
    {
      key: '1',
      sourceService: 'user-service',
      targetService: 'database-mysql',
      sourceIP: '192.168.1.10',
      targetIP: '192.168.1.100',
      protocol: 'TCP',
      port: 3306,
      connections: 45,
      throughput: '12.5 MB/s',
      latency: '2.3ms',
      status: 'healthy',
      errorRate: 0.02,
    },
    {
      key: '2',
      sourceService: 'order-service',
      targetService: 'redis-cluster',
      sourceIP: '192.168.1.11',
      targetIP: '192.168.1.200',
      protocol: 'TCP',
      port: 6379,
      connections: 78,
      throughput: '8.9 MB/s',
      latency: '1.8ms',
      status: 'healthy',
      errorRate: 0.01,
    },
    {
      key: '3',
      sourceService: 'payment-service',
      targetService: 'external-api',
      sourceIP: '192.168.1.12',
      targetIP: '203.107.54.123',
      protocol: 'HTTPS',
      port: 443,
      connections: 23,
      throughput: '1.2 MB/s',
      latency: '45.6ms',
      status: 'warning',
      errorRate: 2.5,
    },
    {
      key: '4',
      sourceService: 'gateway',
      targetService: 'user-service',
      sourceIP: '192.168.1.1',
      targetIP: '192.168.1.10',
      protocol: 'HTTP',
      port: 8080,
      connections: 156,
      throughput: '24.7 MB/s',
      latency: '5.2ms',
      status: 'error',
      errorRate: 5.8,
    },
  ];

  // 网络拓扑数据
  const topologyData = [
    {
      id: 'gateway',
      name: 'API Gateway',
      type: 'gateway',
      status: 'healthy',
      connections: 3,
    },
    {
      id: 'user-service',
      name: 'User Service',
      type: 'service',
      status: 'healthy',
      connections: 2,
    },
    {
      id: 'order-service',
      name: 'Order Service',
      type: 'service',
      status: 'healthy',
      connections: 2,
    },
    {
      id: 'payment-service',
      name: 'Payment Service',
      type: 'service',
      status: 'warning',
      connections: 1,
    },
    {
      id: 'database',
      name: 'MySQL Database',
      type: 'database',
      status: 'healthy',
      connections: 1,
    },
    {
      id: 'redis',
      name: 'Redis Cluster',
      type: 'cache',
      status: 'healthy',
      connections: 1,
    },
  ];

  // 网络安全事件
  const securityEvents = [
    {
      time: '14:32:15',
      type: 'suspicious',
      description:
        '检测到异常连接模式：payment-service 到外部 API 的连接频率异常',
      severity: 'medium',
    },
    {
      time: '14:28:45',
      description: '发现潜在的 DDoS 攻击：来自 203.107.54.0/24 网段的大量连接',
      type: 'attack',
      severity: 'high',
    },
    {
      time: '14:25:32',
      description: 'SSL证书即将过期：external-api.example.com (剩余3天)',
      type: 'warning',
      severity: 'low',
    },
    {
      time: '14:20:18',
      description: '网络延迟异常：user-service 到 database 的连接延迟超过阈值',
      type: 'performance',
      severity: 'medium',
    },
  ];

  const statusConfig = {
    healthy: { color: 'green', text: '正常', icon: <CheckCircleOutlined /> },
    warning: { color: 'orange', text: '警告', icon: <WarningOutlined /> },
    error: { color: 'red', text: '错误', icon: <CloseCircleOutlined /> },
  };

  const severityConfig = {
    low: { color: 'blue', text: '低' },
    medium: { color: 'orange', text: '中' },
    high: { color: 'red', text: '高' },
  };

  const columns = [
    {
      title: '源服务',
      dataIndex: 'sourceService',
      key: 'sourceService',
      width: 120,
    },
    {
      title: '目标服务',
      dataIndex: 'targetService',
      key: 'targetService',
      width: 120,
    },
    {
      title: '源IP',
      dataIndex: 'sourceIP',
      key: 'sourceIP',
      width: 120,
    },
    {
      title: '目标IP',
      dataIndex: 'targetIP',
      key: 'targetIP',
      width: 120,
    },
    {
      title: '协议/端口',
      key: 'protocol',
      width: 100,
      render: (_: any, record: any) => (
        <span>
          {record.protocol}:{record.port}
        </span>
      ),
    },
    {
      title: '连接数',
      dataIndex: 'connections',
      key: 'connections',
      width: 80,
      render: (connections: number) => (
        <span style={{ fontWeight: 'bold' }}>{connections}</span>
      ),
    },
    {
      title: '吞吐量',
      dataIndex: 'throughput',
      key: 'throughput',
      width: 100,
    },
    {
      title: '延迟',
      dataIndex: 'latency',
      key: 'latency',
      width: 80,
      render: (latency: string) => {
        const value = parseFloat(latency);
        const color =
          value > 10 ? '#ff4d4f' : value > 5 ? '#fa8c16' : '#52c41a';
        return <span style={{ color }}>{latency}</span>;
      },
    },
    {
      title: '错误率',
      dataIndex: 'errorRate',
      key: 'errorRate',
      width: 100,
      render: (rate: number) => (
        <Progress
          percent={rate}
          size="small"
          status={rate > 3 ? 'exception' : rate > 1 ? 'active' : 'success'}
          format={(percent) => `${percent}%`}
        />
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
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
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: any, record: any) => (
        <Button
          type="primary"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => {
            setSelectedConnection(record);
            setModalVisible(true);
          }}
        >
          详情
        </Button>
      ),
    },
  ];

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
              title="总连接数"
              value={302}
              valueStyle={{ color: '#1890ff' }}
              prefix={<WifiOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="总吞吐量"
              value={47.3}
              suffix="MB/s"
              valueStyle={{ color: '#3f8600' }}
              prefix={<ThunderboltOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="平均延迟"
              value={13.7}
              suffix="ms"
              valueStyle={{ color: '#722ed1' }}
              prefix={<LineChartOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="安全事件"
              value={4}
              valueStyle={{ color: '#cf1322' }}
              prefix={<SafetyOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* 筛选工具栏 */}
      <Card style={{ marginTop: '16px' }}>
        <Space>
          <Select placeholder="选择协议" style={{ width: 120 }} allowClear>
            <Select.Option value="TCP">TCP</Select.Option>
            <Select.Option value="HTTP">HTTP</Select.Option>
            <Select.Option value="HTTPS">HTTPS</Select.Option>
            <Select.Option value="UDP">UDP</Select.Option>
          </Select>
          <Select placeholder="选择状态" style={{ width: 120 }} allowClear>
            <Select.Option value="healthy">正常</Select.Option>
            <Select.Option value="warning">警告</Select.Option>
            <Select.Option value="error">错误</Select.Option>
          </Select>
          <RangePicker />
          <Button type="primary" icon={<ReloadOutlined />}>
            刷新
          </Button>
        </Space>
      </Card>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        {/* 网络连接表 */}
        <Col span={16}>
          <Card title="网络连接分析">
            <Table
              dataSource={networkData}
              columns={columns}
              pagination={{ pageSize: 8 }}
              size="small"
              scroll={{ x: 1200 }}
            />
          </Card>
        </Col>

        {/* 网络拓扑和安全事件 */}
        <Col span={8}>
          <Tabs defaultActiveKey="1">
            <TabPane tab="网络拓扑" key="1">
              <Card style={{ height: '400px' }}>
                <List
                  dataSource={topologyData}
                  renderItem={(item) => (
                    <List.Item>
                      <List.Item.Meta
                        avatar={
                          <Tooltip
                            title={`${item.type} - ${item.connections} 个连接`}
                          >
                            <GlobalOutlined
                              style={{
                                fontSize: '24px',
                                color:
                                  statusConfig[
                                    item.status as keyof typeof statusConfig
                                  ]?.color === 'green'
                                    ? '#52c41a'
                                    : '#fa8c16',
                              }}
                            />
                          </Tooltip>
                        }
                        title={item.name}
                        description={
                          <div>
                            <Tag>{item.type}</Tag>
                            <Tag
                              color={
                                statusConfig[
                                  item.status as keyof typeof statusConfig
                                ]?.color
                              }
                            >
                              {
                                statusConfig[
                                  item.status as keyof typeof statusConfig
                                ]?.text
                              }
                            </Tag>
                          </div>
                        }
                      />
                    </List.Item>
                  )}
                />
              </Card>
            </TabPane>
            <TabPane tab="安全事件" key="2">
              <Card style={{ height: '400px' }}>
                <Timeline
                  items={securityEvents.map((event, index) => ({
                    color:
                      severityConfig[
                        event.severity as keyof typeof severityConfig
                      ]?.color,
                    children: (
                      <div key={index}>
                        <div
                          style={{
                            display: 'flex',
                            alignItems: 'center',
                            marginBottom: '4px',
                          }}
                        >
                          <span
                            style={{ fontWeight: 'bold', marginRight: '8px' }}
                          >
                            {event.time}
                          </span>
                          <Tag
                            color={
                              severityConfig[
                                event.severity as keyof typeof severityConfig
                              ]?.color
                            }
                          >
                            {
                              severityConfig[
                                event.severity as keyof typeof severityConfig
                              ]?.text
                            }
                          </Tag>
                        </div>
                        <p style={{ margin: 0, fontSize: '12px' }}>
                          {event.description}
                        </p>
                      </div>
                    ),
                  }))}
                />
              </Card>
            </TabPane>
          </Tabs>
        </Col>
      </Row>

      {/* 连接详情模态框 */}
      <Modal
        title="网络连接详情"
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        width={800}
        footer={[
          <Button key="close" onClick={() => setModalVisible(false)}>
            关闭
          </Button>,
        ]}
      >
        {selectedConnection && (
          <div>
            {selectedConnection.status === 'error' && (
              <Alert
                message="连接异常"
                description="该网络连接存在异常，建议立即检查网络配置和服务状态。"
                type="error"
                showIcon
                style={{ marginBottom: '16px' }}
              />
            )}

            <Row gutter={16}>
              <Col span={12}>
                <Card title="连接信息" size="small">
                  <p>
                    <strong>源服务:</strong> {selectedConnection.sourceService}
                  </p>
                  <p>
                    <strong>目标服务:</strong>{' '}
                    {selectedConnection.targetService}
                  </p>
                  <p>
                    <strong>协议:</strong> {selectedConnection.protocol}
                  </p>
                  <p>
                    <strong>端口:</strong> {selectedConnection.port}
                  </p>
                </Card>
              </Col>
              <Col span={12}>
                <Card title="性能指标" size="small">
                  <p>
                    <strong>连接数:</strong> {selectedConnection.connections}
                  </p>
                  <p>
                    <strong>吞吐量:</strong> {selectedConnection.throughput}
                  </p>
                  <p>
                    <strong>延迟:</strong> {selectedConnection.latency}
                  </p>
                  <p>
                    <strong>错误率:</strong> {selectedConnection.errorRate}%
                  </p>
                </Card>
              </Col>
            </Row>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default NetworkAnalysis;
