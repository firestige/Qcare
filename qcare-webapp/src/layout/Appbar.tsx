import React from 'react';
import { Layout, Button, Input, Badge, Dropdown, Avatar, Space } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SearchOutlined,
  BellOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
  ProfileOutlined,
} from '@ant-design/icons';
import { theme } from 'antd';
import type { MenuProps } from 'antd';

const { Header } = Layout;

interface AppbarProps {
  collapsed: boolean;
  onToggle: () => void;
}

const Appbar: React.FC<AppbarProps> = ({ collapsed, onToggle }) => {
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  // 用户下拉菜单项
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <ProfileOutlined />,
      label: '个人资料',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '账户设置',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
    },
  ];

  // 处理搜索
  const handleSearch = (value: string) => {
    console.log('搜索:', value);
  };

  // 处理通知点击
  const handleNotificationClick = () => {
    console.log('查看通知');
  };

  // 处理设置点击
  const handleSettingsClick = () => {
    console.log('打开设置');
  };

  // 处理用户菜单点击
  const handleUserMenuClick: MenuProps['onClick'] = ({ key }) => {
    console.log('用户菜单点击:', key);
    if (key === 'logout') {
      // 处理退出登录逻辑
      console.log('用户退出登录');
    }
  };

  return (
    <Header
      style={{
        padding: '0 24px 0 0',
        background: colorBgContainer,
        position: 'fixed',
        top: 0,
        right: 0,
        left: collapsed ? 80 : 200,
        zIndex: 99,
        borderBottom: '1px solid #f0f0f0',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}
    >
      {/* 左侧折叠按钮 */}
      <Button
        type="text"
        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        onClick={onToggle}
        style={{
          fontSize: '16px',
          width: 64,
          height: 64,
        }}
      />

      {/* 右侧功能区域 */}
      <Space size="middle" align="center">
        {/* 搜索框 */}
        <Input.Search
          placeholder="搜索..."
          allowClear
          onSearch={handleSearch}
          style={{
            width: 250,
            height: 40,
            display: 'flex',
            alignItems: 'center',
          }}
          enterButton={<SearchOutlined />}
        />

        {/* 通知铃铛 */}
        <Badge count={5} size="small">
          <Button
            type="text"
            icon={<BellOutlined />}
            onClick={handleNotificationClick}
            style={{
              fontSize: '16px',
              width: 40,
              height: 40,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          />
        </Badge>

        {/* 设置按钮 */}
        <Button
          type="text"
          icon={<SettingOutlined />}
          onClick={handleSettingsClick}
          style={{
            fontSize: '16px',
            width: 40,
            height: 40,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        />

        {/* 用户头像下拉菜单 */}
        <Dropdown
          menu={{
            items: userMenuItems,
            onClick: handleUserMenuClick,
          }}
          placement="bottomRight"
          arrow
        >
          <Button
            type="text"
            style={{
              height: 40,
              padding: '0 8px',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
            }}
          >
            <Avatar
              size={32}
              icon={<UserOutlined />}
              style={{ backgroundColor: '#1890ff' }}
            />
            <span style={{ fontSize: '14px', color: '#262626' }}>管理员</span>
          </Button>
        </Dropdown>
      </Space>
    </Header>
  );
};

export default Appbar;
