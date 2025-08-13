import React from 'react';
import { Layout, Menu, Typography, theme } from 'antd';
import {
  DashboardOutlined,
  DeploymentUnitOutlined,
  BugOutlined,
  CodeOutlined,
  NodeIndexOutlined,
  GlobalOutlined,
  PlayCircleOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import navConfig from '../config/nav.json';
import PulseIcon from '../assets/icons/pulse.svg?react';

const { Sider } = Layout;
const { Title } = Typography;

// 图标映射
const iconMap: Record<string, React.ComponentType> = {
  DashboardOutlined,
  DeploymentUnitOutlined,
  BugOutlined,
  CodeOutlined,
  NodeIndexOutlined,
  GlobalOutlined,
  PlayCircleOutlined,
};

interface NavItem {
  title: string;
  path: string;
  view: string;
  icon: string;
}

interface DrawProps {
  collapsed: boolean;
}

const Draw: React.FC<DrawProps> = ({ collapsed }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { borderRadiusLG },
  } = theme.useToken();

  // 处理导航配置，生成菜单项
  const generateMenuItems = (navItems: NavItem[]) => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const menuItems: any[] = [];
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    let currentGroup: any = null;

    navItems.forEach((item, index) => {
      const IconComponent = iconMap[item.icon];

      // 如果 path 和 view 为空，说明这是一个分组标题
      if (!item.path && !item.view) {
        if (collapsed) {
          // 收起时，用分割线替代分组标题
          menuItems.push({
            key: `divider-${index}`,
            type: 'divider',
          });
          currentGroup = null; // 收起时不创建分组，直接添加菜单项
        } else {
          // 展开时，显示分组标题
          currentGroup = {
            key: `group-${index}`,
            label: item.title,
            type: 'group',
            children: [],
          };
          menuItems.push(currentGroup);
        }
      } else {
        // 普通菜单项
        const menuItem = {
          key: item.path,
          icon: IconComponent ? React.createElement(IconComponent) : null,
          label: collapsed ? '' : item.title, // 收起时不显示文字
          onClick: () => navigate(item.path),
        };

        if (currentGroup && !collapsed) {
          currentGroup.children.push(menuItem);
        } else {
          menuItems.push(menuItem);
        }
      }
    });

    return menuItems;
  };

  const menuItems = generateMenuItems(navConfig);

  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={collapsed}
      style={{
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        zIndex: 100,
      }}
    >
      <div
        style={{
          height: 64,
          margin: 16,
          background: 'rgba(255, 255, 255, 0.3)',
          borderRadius: borderRadiusLG,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        {collapsed ? (
          <PulseIcon style={{ width: 32, height: 32, fill: 'white' }} />
        ) : (
          <>
            <PulseIcon style={{ width: 32, height: 32, fill: 'white' }} />
            <Title
              level={4}
              style={{
                color: 'white',
                margin: 0,
              }}
            >
              iCare
            </Title>
          </>
        )}
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
      />
    </Sider>
  );
};

export default Draw;
