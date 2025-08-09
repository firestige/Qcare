import React from 'react';
import {
  Card,
  Row,
  Col,
  Table,
  Tag,
  Progress,
  Statistic,
  Timeline,
} from 'antd';
import {
  AppstoreOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';

const AppTracing: React.FC = () => {
  // 模拟数据
  const applicationData = [
    {
      key: '1',
      name: 'user-service',
      status: 'running',
      instances: 3,
      cpu: 65,
      memory: 72,
      requests: 1234,
      errors: 2,
      responseTime: '45ms',
    },
    {
      key: '2',
      name: 'order-service',
      status: 'running',
      instances: 2,
      cpu: 45,
      memory: 58,
      requests: 856,
      errors: 0,
      responseTime: '32ms',
    },
    {
      key: '3',
      name: 'payment-service',
      status: 'warning',
      instances: 2,
      cpu: 88,
      memory: 91,
      requests: 423,
      errors: 5,
      responseTime: '125ms',
    },
  ];

  const traceData = [
    {
      color: 'green',
      children: (
        <>
          <p>
            <strong>API Gateway</strong> - 接收请求
            <br />
            <small>响应时间: 5ms</small>
          </p>
        </>
      ),
    },
    {
      color: 'blue',
      children: (
        <>
          <p>
            <strong>User Service</strong> - 用户验证
            <br />
            <small>响应时间: 15ms</small>
          </p>
        </>
      ),
    },
    {
      color: 'blue',
      children: (
        <>
          <p>
            <strong>Order Service</strong> - 订单处理
            <br />
            <small>响应时间: 25ms</small>
          </p>
        </>
      ),
    },
    {
      color: 'green',
      children: (
        <>
          <p>
            <strong>Response</strong> - 返回结果
            <br />
            <small>总响应时间: 45ms</small>
          </p>
        </>
      ),
    },
  ];

  const columns = [
    {
      title: '应用名称',
      dataIndex: 'name',
      key: 'name',
      render: (text: string) => (
        <span>
          <AppstoreOutlined style={{ marginRight: 8 }} />
          {text}
        </span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const config = {
          running: {
            color: 'green',
            text: '运行中',
            icon: <CheckCircleOutlined />,
          },
          warning: {
            color: 'orange',
            text: '告警',
            icon: <ExclamationCircleOutlined />,
          },
          stopped: {
            color: 'red',
            text: '已停止',
            icon: <ClockCircleOutlined />,
          },
        };
        const { color, text, icon } =
          config[status as keyof typeof config] || config.stopped;
        return (
          <Tag color={color} icon={icon}>
            {text}
          </Tag>
        );
      },
    },
    {
      title: '实例数',
      dataIndex: 'instances',
      key: 'instances',
    },
    {
      title: 'CPU使用率',
      dataIndex: 'cpu',
      key: 'cpu',
      render: (cpu: number) => (
        <Progress
          percent={cpu}
          size="small"
          status={cpu > 80 ? 'exception' : 'normal'}
        />
      ),
    },
    {
      title: '内存使用率',
      dataIndex: 'memory',
      key: 'memory',
      render: (memory: number) => (
        <Progress
          percent={memory}
          size="small"
          status={memory > 85 ? 'exception' : 'normal'}
        />
      ),
    },
    {
      title: '请求数/分钟',
      dataIndex: 'requests',
      key: 'requests',
    },
    {
      title: '错误数',
      dataIndex: 'errors',
      key: 'errors',
      render: (errors: number) => (
        <span style={{ color: errors > 0 ? '#ff4d4f' : '#52c41a' }}>
          {errors}
        </span>
      ),
    },
    {
      title: '平均响应时间',
      dataIndex: 'responseTime',
      key: 'responseTime',
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
              title="运行中的应用"
              value={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="告警应用"
              value={1}
              valueStyle={{ color: '#cf1322' }}
              prefix={<ExclamationCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="总请求数/分钟"
              value={2513}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="平均响应时间"
              value={67}
              suffix="ms"
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        {/* 应用列表 */}
        <Col span={16}>
          <Card title="应用监控列表" style={{ height: '500px' }}>
            <Table
              dataSource={applicationData}
              columns={columns}
              pagination={{ pageSize: 10 }}
              size="small"
            />
          </Card>
        </Col>

        {/* 调用链追踪 */}
        <Col span={8}>
          <Card title="调用链追踪示例" style={{ height: '500px' }}>
            <Timeline items={traceData} />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default AppTracing;
