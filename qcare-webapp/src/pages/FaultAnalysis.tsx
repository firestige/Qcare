import React, { useState } from 'react';
import {
  Card,
  Row,
  Col,
  Table,
  Tag,
  Button,
  Drawer,
  Descriptions,
  Alert,
  Statistic,
  Timeline,
  Select,
  DatePicker,
  Space,
} from 'antd';
import {
  BugOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  EyeOutlined,
  ReloadOutlined,
} from '@ant-design/icons';

const { RangePicker } = DatePicker;

const FaultAnalysis: React.FC = () => {
  const [selectedFault, setSelectedFault] = useState<any>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);

  // 模拟故障数据
  const faultData = [
    {
      key: '1',
      id: 'FAULT-2025-001',
      title: '用户服务响应超时',
      service: 'user-service',
      level: 'critical',
      status: 'investigating',
      startTime: '2025-08-01 14:30:25',
      duration: '2小时30分钟',
      affectedUsers: 1250,
      description: 'user-service 服务出现大量超时，响应时间超过5秒',
    },
    {
      key: '2',
      id: 'FAULT-2025-002',
      title: '数据库连接池耗尽',
      service: 'order-service',
      level: 'high',
      status: 'resolved',
      startTime: '2025-08-01 09:15:10',
      duration: '45分钟',
      affectedUsers: 580,
      description: '订单服务数据库连接池达到上限，新请求无法获取连接',
    },
    {
      key: '3',
      id: 'FAULT-2025-003',
      title: '缓存服务异常',
      service: 'redis-cluster',
      level: 'medium',
      status: 'monitoring',
      startTime: '2025-07-31 16:45:30',
      duration: '15分钟',
      affectedUsers: 200,
      description: 'Redis集群部分节点响应异常，命中率下降',
    },
  ];

  const levelConfig = {
    critical: { color: 'red', text: '严重', icon: <BugOutlined /> },
    high: { color: 'orange', text: '高', icon: <WarningOutlined /> },
    medium: { color: 'yellow', text: '中', icon: <ClockCircleOutlined /> },
    low: { color: 'blue', text: '低', icon: <CheckCircleOutlined /> },
  };

  const statusConfig = {
    investigating: { color: 'processing', text: '调查中' },
    resolved: { color: 'success', text: '已解决' },
    monitoring: { color: 'warning', text: '监控中' },
  };

  const columns = [
    {
      title: '故障ID',
      dataIndex: 'id',
      key: 'id',
      width: 150,
    },
    {
      title: '故障标题',
      dataIndex: 'title',
      key: 'title',
      width: 200,
    },
    {
      title: '服务',
      dataIndex: 'service',
      key: 'service',
      width: 150,
    },
    {
      title: '级别',
      dataIndex: 'level',
      key: 'level',
      width: 100,
      render: (level: string) => {
        const config = levelConfig[level as keyof typeof levelConfig];
        return (
          <Tag color={config.color} icon={config.icon}>
            {config.text}
          </Tag>
        );
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const config = statusConfig[status as keyof typeof statusConfig];
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 180,
    },
    {
      title: '持续时间',
      dataIndex: 'duration',
      key: 'duration',
      width: 120,
    },
    {
      title: '影响用户',
      dataIndex: 'affectedUsers',
      key: 'affectedUsers',
      width: 100,
      render: (users: number) => (
        <span style={{ color: users > 1000 ? '#ff4d4f' : '#1890ff' }}>
          {users.toLocaleString()}
        </span>
      ),
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
            setSelectedFault(record);
            setDrawerVisible(true);
          }}
        >
          详情
        </Button>
      ),
    },
  ];

  const timelineData = selectedFault
    ? [
        {
          color: 'red',
          children: (
            <>
              <p>
                <strong>故障发生</strong>
              </p>
              <p>{selectedFault.startTime}</p>
              <p>系统检测到异常指标</p>
            </>
          ),
        },
        {
          color: 'blue',
          children: (
            <>
              <p>
                <strong>告警触发</strong>
              </p>
              <p>{selectedFault.startTime}</p>
              <p>自动告警系统发送通知</p>
            </>
          ),
        },
        {
          color: 'orange',
          children: (
            <>
              <p>
                <strong>开始调查</strong>
              </p>
              <p>运维团队开始故障排查</p>
            </>
          ),
        },
        ...(selectedFault.status === 'resolved'
          ? [
              {
                color: 'green',
                children: (
                  <>
                    <p>
                      <strong>故障解决</strong>
                    </p>
                    <p>问题已修复，服务恢复正常</p>
                  </>
                ),
              },
            ]
          : []),
      ]
    : [];

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
              title="今日故障"
              value={3}
              valueStyle={{ color: '#cf1322' }}
              prefix={<BugOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="调查中"
              value={1}
              valueStyle={{ color: '#fa8c16' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已解决"
              value={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="平均解决时间"
              value={47}
              suffix="分钟"
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 筛选工具栏 */}
      <Card style={{ marginTop: '16px' }}>
        <Space>
          <Select placeholder="选择故障级别" style={{ width: 120 }} allowClear>
            <Select.Option value="critical">严重</Select.Option>
            <Select.Option value="high">高</Select.Option>
            <Select.Option value="medium">中</Select.Option>
            <Select.Option value="low">低</Select.Option>
          </Select>
          <Select placeholder="选择状态" style={{ width: 120 }} allowClear>
            <Select.Option value="investigating">调查中</Select.Option>
            <Select.Option value="resolved">已解决</Select.Option>
            <Select.Option value="monitoring">监控中</Select.Option>
          </Select>
          <RangePicker />
          <Button type="primary" icon={<ReloadOutlined />}>
            刷新
          </Button>
        </Space>
      </Card>

      {/* 故障列表 */}
      <Card title="故障分析列表" style={{ marginTop: '16px' }}>
        <Table
          dataSource={faultData}
          columns={columns}
          pagination={{ pageSize: 10 }}
          scroll={{ x: 1200 }}
        />
      </Card>

      {/* 故障详情抽屉 */}
      <Drawer
        title="故障详情"
        placement="right"
        size="large"
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
      >
        {selectedFault && (
          <div>
            <Alert
              message={`故障级别: ${levelConfig[selectedFault.level as keyof typeof levelConfig]?.text}`}
              type={selectedFault.level === 'critical' ? 'error' : 'warning'}
              showIcon
              style={{ marginBottom: '16px' }}
            />

            <Descriptions title="基本信息" bordered column={1}>
              <Descriptions.Item label="故障ID">
                {selectedFault.id}
              </Descriptions.Item>
              <Descriptions.Item label="故障标题">
                {selectedFault.title}
              </Descriptions.Item>
              <Descriptions.Item label="影响服务">
                {selectedFault.service}
              </Descriptions.Item>
              <Descriptions.Item label="开始时间">
                {selectedFault.startTime}
              </Descriptions.Item>
              <Descriptions.Item label="持续时间">
                {selectedFault.duration}
              </Descriptions.Item>
              <Descriptions.Item label="影响用户">
                {selectedFault.affectedUsers.toLocaleString()}
              </Descriptions.Item>
              <Descriptions.Item label="故障描述">
                {selectedFault.description}
              </Descriptions.Item>
            </Descriptions>

            <Card title="处理时间线" style={{ marginTop: '16px' }}>
              <Timeline items={timelineData} />
            </Card>
          </div>
        )}
      </Drawer>
    </div>
  );
};

export default FaultAnalysis;
