import React, { useState } from 'react';
import {
  Card,
  Row,
  Col,
  Button,
  Input,
  Table,
  Tag,
  Modal,
  Typography,
  Alert,
  Tabs,
  List,
  Progress,
  Space,
  Spin,
} from 'antd';
import {
  CodeOutlined,
  PlayCircleOutlined,
  DownloadOutlined,
  FileTextOutlined,
  MonitorOutlined,
  BugOutlined,
  ReloadOutlined,
  SearchOutlined,
  LinkOutlined,
  DisconnectOutlined,
} from '@ant-design/icons';
import { useArthas } from '../hooks/useArthas';
import { InstanceStatusConfig, ArthasStatusConfig } from '../types/arthas';
import type { ArthasInstance, CommandExecutionResult } from '../types/arthas';

const { Text } = Typography;
const { TabPane } = Tabs;
const { Search } = Input;

const Arthas: React.FC = () => {
  const [selectedInstance, setSelectedInstance] = useState<string>('');
  const [command, setCommand] = useState<string>('');
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [commandResult, setCommandResult] =
    useState<CommandExecutionResult | null>(null);

  // 使用 Arthas Hook
  const {
    stats,
    instances,
    commonCommands,
    diagnosticCommands,
    loading,
    executing,
    error,
    executeCommand,
    connectInstance,
    disconnectInstance,
    downloadArthas,
    deployArthas,
    refreshData,
    searchParams,
    setSearchParams,
  } = useArthas();

  // 处理搜索
  const handleSearch = (value: string) => {
    setSearchParams({
      ...searchParams,
      keyword: value,
    });
  };

  // 处理连接实例
  const handleConnect = async (instance: ArthasInstance) => {
    const success = await connectInstance(
      instance.name,
      instance.host,
      instance.port
    );
    if (success) {
      // 连接成功后的逻辑
    }
  };

  // 处理断开连接
  const handleDisconnect = async (instanceName: string) => {
    await disconnectInstance(instanceName);
  };

  // 执行 Arthas 命令
  const handleExecuteCommand = async () => {
    if (!command.trim() || !selectedInstance) return;

    const result = await executeCommand({
      instanceName: selectedInstance,
      command: command.trim(),
    });

    if (result) {
      setCommandResult(result);
    }
  };

  // 处理下载
  const handleDownload = async () => {
    await downloadArthas();
  };

  // 处理一键部署
  const handleDeploy = async () => {
    if (!selectedInstance) return;
    await deployArthas(selectedInstance);
  };

  const columns = [
    {
      title: '实例名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'PID',
      dataIndex: 'pid',
      key: 'pid',
    },
    {
      title: '主机',
      dataIndex: 'host',
      key: 'host',
    },
    {
      title: '端口',
      dataIndex: 'port',
      key: 'port',
    },
    {
      title: '内存使用',
      dataIndex: 'memory',
      key: 'memory',
    },
    {
      title: 'CPU使用',
      dataIndex: 'cpu',
      key: 'cpu',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const config =
          InstanceStatusConfig[status as keyof typeof InstanceStatusConfig];
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: 'Arthas状态',
      dataIndex: 'arthasStatus',
      key: 'arthasStatus',
      render: (status: string) => {
        const config =
          ArthasStatusConfig[status as keyof typeof ArthasStatusConfig];
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: ArthasInstance) => (
        <Space>
          {record.arthasStatus === 'disconnected' ? (
            <Button
              size="small"
              icon={<LinkOutlined />}
              onClick={() => handleConnect(record)}
              loading={loading}
            >
              连接
            </Button>
          ) : (
            <Button
              size="small"
              icon={<DisconnectOutlined />}
              onClick={() => handleDisconnect(record.name)}
              loading={loading}
            >
              断开
            </Button>
          )}
          <Button
            type="primary"
            size="small"
            icon={<CodeOutlined />}
            onClick={() => {
              setSelectedInstance(record.name);
              setModalVisible(true);
            }}
            disabled={record.arthasStatus === 'disconnected'}
          >
            诊断
          </Button>
        </Space>
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
      <Spin spinning={loading}>
        <Row gutter={[16, 16]}>
          {/* 状态概览 */}
          <Col span={24}>
            <Card title="Arthas 实例工具">
              <Row gutter={16}>
                <Col span={6}>
                  <Card size="small">
                    <div style={{ textAlign: 'center' }}>
                      <MonitorOutlined
                        style={{ fontSize: '24px', color: '#1890ff' }}
                      />
                      <div style={{ marginTop: '8px' }}>
                        <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                          {stats.runningInstances}
                        </div>
                        <div>运行实例</div>
                      </div>
                    </div>
                  </Card>
                </Col>
                <Col span={6}>
                  <Card size="small">
                    <div style={{ textAlign: 'center' }}>
                      <CodeOutlined
                        style={{ fontSize: '24px', color: '#52c41a' }}
                      />
                      <div style={{ marginTop: '8px' }}>
                        <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                          {stats.connectedInstances}
                        </div>
                        <div>已连接Arthas</div>
                      </div>
                    </div>
                  </Card>
                </Col>
                <Col span={6}>
                  <Card size="small">
                    <div style={{ textAlign: 'center' }}>
                      <BugOutlined
                        style={{ fontSize: '24px', color: '#fa8c16' }}
                      />
                      <div style={{ marginTop: '8px' }}>
                        <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                          {stats.todayDiagnosticCount}
                        </div>
                        <div>今日诊断次数</div>
                      </div>
                    </div>
                  </Card>
                </Col>
                <Col span={6}>
                  <Card size="small">
                    <div style={{ textAlign: 'center' }}>
                      <FileTextOutlined
                        style={{ fontSize: '24px', color: '#722ed1' }}
                      />
                      <div style={{ marginTop: '8px' }}>
                        <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                          {stats.reportCount}
                        </div>
                        <div>诊断报告</div>
                      </div>
                    </div>
                  </Card>
                </Col>
              </Row>
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
          {/* 实例列表 */}
          <Col span={16}>
            <Card
              title="Java 实例列表"
              extra={
                <Space>
                  <Search
                    placeholder="搜索实例..."
                    style={{ width: 200 }}
                    prefix={<SearchOutlined />}
                    onSearch={handleSearch}
                    allowClear
                  />
                  <Button
                    icon={<ReloadOutlined />}
                    onClick={refreshData}
                    loading={loading}
                  >
                    刷新
                  </Button>
                </Space>
              }
            >
              {error && (
                <Alert
                  message="数据加载失败"
                  description={error}
                  type="warning"
                  showIcon
                  style={{ marginBottom: '16px' }}
                />
              )}
              <Table
                dataSource={instances}
                columns={columns}
                pagination={false}
                size="small"
                loading={loading}
              />
            </Card>
          </Col>

          {/* 快速操作 */}
          <Col span={8}>
            <Card title="快速操作">
              <div style={{ marginBottom: '16px' }}>
                <Button
                  type="primary"
                  icon={<DownloadOutlined />}
                  block
                  style={{ marginBottom: '8px' }}
                  onClick={handleDownload}
                  loading={loading}
                >
                  下载 Arthas
                </Button>
                <Button
                  icon={<PlayCircleOutlined />}
                  block
                  style={{ marginBottom: '8px' }}
                  onClick={handleDeploy}
                  loading={loading}
                  disabled={!selectedInstance}
                >
                  一键部署
                </Button>
                <Button icon={<FileTextOutlined />} block>
                  查看文档
                </Button>
              </div>

              <Alert
                message="提示"
                description="Arthas 是阿里开源的 Java 诊断工具，可以帮助开发者快速定位和解决线上问题。"
                type="info"
                showIcon
              />
            </Card>
          </Col>
        </Row>

        {/* Arthas 控制台模态框 */}
        <Modal
          title={`Arthas 诊断控制台 - ${selectedInstance}`}
          open={modalVisible}
          onCancel={() => setModalVisible(false)}
          width={1000}
          footer={null}
        >
          <Tabs defaultActiveKey="1">
            <TabPane tab="命令执行" key="1">
              <div style={{ marginBottom: '16px' }}>
                <Input.Group compact>
                  <Input
                    style={{ width: 'calc(100% - 100px)' }}
                    placeholder="输入 Arthas 命令..."
                    value={command}
                    onChange={(e) => setCommand(e.target.value)}
                    onPressEnter={handleExecuteCommand}
                  />
                  <Button
                    type="primary"
                    onClick={handleExecuteCommand}
                    loading={executing}
                    style={{ width: '100px' }}
                  >
                    执行
                  </Button>
                </Input.Group>
              </div>

              <div
                style={{
                  height: '300px',
                  border: '1px solid #d9d9d9',
                  padding: '8px',
                  backgroundColor: '#000',
                  color: '#00ff00',
                  fontFamily: 'monospace',
                  overflow: 'auto',
                }}
              >
                <div>Arthas 控制台</div>
                <div>$ {command}</div>
                {executing && (
                  <div>
                    <Progress percent={50} showInfo={false} size="small" />
                    <div>正在执行命令...</div>
                  </div>
                )}
                {commandResult && (
                  <div>
                    <div>执行结果:</div>
                    <pre
                      style={{
                        color: commandResult.success ? '#00ff00' : '#ff4444',
                      }}
                    >
                      {commandResult.output}
                    </pre>
                    {commandResult.error && (
                      <div style={{ color: '#ff4444' }}>
                        错误: {commandResult.error}
                      </div>
                    )}
                  </div>
                )}
              </div>
            </TabPane>

            <TabPane tab="常用命令" key="2">
              <List
                dataSource={commonCommands}
                renderItem={(item) => (
                  <List.Item
                    actions={[
                      <Button
                        key="use"
                        size="small"
                        onClick={() => setCommand(item.command)}
                      >
                        使用
                      </Button>,
                    ]}
                  >
                    <List.Item.Meta
                      title={<Text code>{item.command}</Text>}
                      description={item.description}
                    />
                  </List.Item>
                )}
              />
            </TabPane>

            <TabPane tab="诊断命令" key="3">
              <List
                dataSource={diagnosticCommands}
                renderItem={(item) => (
                  <List.Item
                    actions={[
                      <Button
                        key="use"
                        size="small"
                        onClick={() => setCommand(item.command)}
                      >
                        使用
                      </Button>,
                    ]}
                  >
                    <List.Item.Meta
                      title={<Text code>{item.command}</Text>}
                      description={item.description}
                    />
                  </List.Item>
                )}
              />
            </TabPane>
          </Tabs>
        </Modal>
      </Spin>
    </div>
  );
};

export default Arthas;
