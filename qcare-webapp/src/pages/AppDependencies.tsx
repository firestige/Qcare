import React, { useState } from 'react';
import {
  Card,
  Row,
  Col,
  Tree,
  Table,
  Tag,
  Button,
  Input,
  Space,
  Modal,
  Descriptions,
  Alert,
  Spin,
} from 'antd';
import {
  NodeIndexOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  SecurityScanOutlined,
} from '@ant-design/icons';
import { useDependencies } from '../hooks/useDependencies';
import { StatusConfig, SecurityConfig } from '../types/dependency';
import type { DependencyItem, DependencyTreeNode } from '../types/dependency';

const { Search } = Input;

const AppDependencies: React.FC = () => {
  const [selectedNode, setSelectedNode] = useState<DependencyItem | null>(null);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [expandedKeys, setExpandedKeys] = useState<React.Key[]>(['0-0']);

  // 使用依赖管理Hook
  const {
    stats,
    treeData,
    dependencies,
    loading,
    error,
    scanSecurity,
    refreshData,
    searchParams,
    setSearchParams,
  } = useDependencies();

  // 添加图标到树节点
  const addIconsToTreeData = (nodes: DependencyTreeNode[]): any[] => {
    return nodes.map((node) => {
      const newNode: any = { ...node };

      // 根据节点类型添加图标
      if (node.key === '0-0') {
        newNode.icon = <NodeIndexOutlined />;
      } else if (node.title.includes('service')) {
        newNode.icon = <NodeIndexOutlined style={{ color: '#52c41a' }} />;
      } else if (node.title.includes(':')) {
        // 依赖包，根据安全性添加图标
        const depName = node.title.split(':')[0];
        const dep = dependencies.find((d) => d.name === depName);
        if (dep) {
          if (dep.security === 'vulnerable') {
            newNode.icon = (
              <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />
            );
          } else if (dep.security === 'warning') {
            newNode.icon = <WarningOutlined style={{ color: '#fa8c16' }} />;
          } else {
            newNode.icon = <CheckCircleOutlined style={{ color: '#52c41a' }} />;
          }
        } else {
          newNode.icon = <CheckCircleOutlined style={{ color: '#52c41a' }} />;
        }
      }

      if (node.children) {
        newNode.children = addIconsToTreeData(node.children);
      }

      return newNode;
    });
  };

  // 处理搜索
  const handleSearch = (value: string) => {
    setSearchParams({
      ...searchParams,
      keyword: value,
    });
  };

  // 处理安全扫描
  const handleSecurityScan = async () => {
    await scanSecurity();
  };

  const columns = [
    {
      title: '依赖名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
    },
    {
      title: '当前版本',
      dataIndex: 'version',
      key: 'version',
      width: 120,
    },
    {
      title: '最新版本',
      dataIndex: 'latestVersion',
      key: 'latestVersion',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const config = StatusConfig[status as keyof typeof StatusConfig];
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '安全性',
      dataIndex: 'security',
      key: 'security',
      width: 100,
      render: (security: string) => {
        const config = SecurityConfig[security as keyof typeof SecurityConfig];
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '许可证',
      dataIndex: 'license',
      key: 'license',
      width: 120,
    },
    {
      title: '大小',
      dataIndex: 'size',
      key: 'size',
      width: 100,
    },
    {
      title: '使用服务',
      dataIndex: 'usedBy',
      key: 'usedBy',
      width: 200,
      render: (services: string[]) => (
        <span>
          {services.map((service, index) => (
            <Tag key={index}>{service}</Tag>
          ))}
        </span>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: any, record: DependencyItem) => (
        <Button
          type="primary"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => {
            setSelectedNode(record);
            setModalVisible(true);
          }}
        >
          详情
        </Button>
      ),
    },
  ];

  const onSelect = (selectedKeys: React.Key[], info: any) => {
    console.log('selected', selectedKeys, info);
  };

  const onExpand = (expandedKeys: React.Key[]) => {
    setExpandedKeys(expandedKeys);
  };

  // 处理树节点数据，添加图标
  const treeDataWithIcons = addIconsToTreeData(treeData);

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
          {/* 统计卡片 */}
          <Col span={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <NodeIndexOutlined
                  style={{ fontSize: '24px', color: '#1890ff' }}
                />
                <div style={{ marginTop: '8px' }}>
                  <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                    {stats.totalDependencies}
                  </div>
                  <div>总依赖数</div>
                </div>
              </div>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <CheckCircleOutlined
                  style={{ fontSize: '24px', color: '#52c41a' }}
                />
                <div style={{ marginTop: '8px' }}>
                  <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                    {stats.safeDependencies}
                  </div>
                  <div>安全依赖</div>
                </div>
              </div>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <WarningOutlined
                  style={{ fontSize: '24px', color: '#fa8c16' }}
                />
                <div style={{ marginTop: '8px' }}>
                  <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                    {stats.outdatedDependencies}
                  </div>
                  <div>过期依赖</div>
                </div>
              </div>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <div style={{ textAlign: 'center' }}>
                <ExclamationCircleOutlined
                  style={{ fontSize: '24px', color: '#ff4d4f' }}
                />
                <div style={{ marginTop: '8px' }}>
                  <div style={{ fontSize: '20px', fontWeight: 'bold' }}>
                    {stats.vulnerableDependencies}
                  </div>
                  <div>漏洞依赖</div>
                </div>
              </div>
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
          {/* 依赖树 */}
          <Col span={8}>
            <Card title="依赖结构树" style={{ height: '600px' }}>
              <Tree
                showIcon
                defaultExpandedKeys={['0-0']}
                expandedKeys={expandedKeys}
                onExpand={onExpand}
                onSelect={onSelect}
                treeData={treeDataWithIcons}
              />
            </Card>
          </Col>

          {/* 依赖列表 */}
          <Col span={16}>
            <Card
              title="依赖详情列表"
              extra={
                <Space>
                  <Search
                    placeholder="搜索依赖..."
                    style={{ width: 200 }}
                    prefix={<SearchOutlined />}
                    onSearch={handleSearch}
                    allowClear
                  />
                  <Button
                    icon={<SecurityScanOutlined />}
                    onClick={handleSecurityScan}
                    loading={loading}
                  >
                    安全扫描
                  </Button>
                  <Button
                    icon={<ReloadOutlined />}
                    onClick={refreshData}
                    loading={loading}
                  >
                    刷新
                  </Button>
                </Space>
              }
              style={{ height: '600px' }}
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
                dataSource={dependencies}
                columns={columns}
                pagination={{ pageSize: 8 }}
                size="small"
                scroll={{ y: 400 }}
                loading={loading}
              />
            </Card>
          </Col>
        </Row>

        {/* 依赖详情模态框 */}
        <Modal
          title="依赖详情"
          open={modalVisible}
          onCancel={() => setModalVisible(false)}
          width={800}
          footer={[
            <Button key="close" onClick={() => setModalVisible(false)}>
              关闭
            </Button>,
          ]}
        >
          {selectedNode && (
            <div>
              {selectedNode.security === 'vulnerable' && (
                <Alert
                  message="安全警告"
                  description="该依赖存在已知安全漏洞，建议尽快更新到最新版本。"
                  type="error"
                  showIcon
                  style={{ marginBottom: '16px' }}
                />
              )}

              <Descriptions title="基本信息" bordered column={2}>
                <Descriptions.Item label="依赖名称" span={2}>
                  {selectedNode.name}
                </Descriptions.Item>
                <Descriptions.Item label="当前版本">
                  {selectedNode.version}
                </Descriptions.Item>
                <Descriptions.Item label="最新版本">
                  {selectedNode.latestVersion}
                </Descriptions.Item>
                <Descriptions.Item label="状态">
                  <Tag color={StatusConfig[selectedNode.status]?.color}>
                    {StatusConfig[selectedNode.status]?.text}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="安全性">
                  <Tag color={SecurityConfig[selectedNode.security]?.color}>
                    {SecurityConfig[selectedNode.security]?.text}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="许可证">
                  {selectedNode.license}
                </Descriptions.Item>
                <Descriptions.Item label="文件大小">
                  {selectedNode.size}
                </Descriptions.Item>
                <Descriptions.Item label="使用服务" span={2}>
                  {selectedNode.usedBy?.map(
                    (service: string, index: number) => (
                      <Tag key={index}>{service}</Tag>
                    )
                  )}
                </Descriptions.Item>
                {selectedNode.description && (
                  <Descriptions.Item label="描述" span={2}>
                    {selectedNode.description}
                  </Descriptions.Item>
                )}
                {selectedNode.homepage && (
                  <Descriptions.Item label="主页" span={2}>
                    <a
                      href={selectedNode.homepage}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {selectedNode.homepage}
                    </a>
                  </Descriptions.Item>
                )}
                {selectedNode.repository && (
                  <Descriptions.Item label="仓库" span={2}>
                    <a
                      href={selectedNode.repository}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {selectedNode.repository}
                    </a>
                  </Descriptions.Item>
                )}
                {selectedNode.lastUpdate && (
                  <Descriptions.Item label="最后更新">
                    {selectedNode.lastUpdate}
                  </Descriptions.Item>
                )}
              </Descriptions>
            </div>
          )}
        </Modal>
      </Spin>
    </div>
  );
};

export default AppDependencies;
