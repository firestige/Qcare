import React from 'react';
import { Layout, theme } from 'antd';
import { Outlet } from 'react-router-dom';

const { Content } = Layout;

interface MainProps {
  collapsed: boolean;
}

const Main: React.FC<MainProps> = ({ collapsed }) => {
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  return (
    <Layout style={{ marginLeft: collapsed ? 80 : 200 }}>
      <Content
        style={{
          margin: '88px 24px 24px 24px',
          padding: 24,
          minHeight: 280,
          background: colorBgContainer,
          borderRadius: borderRadiusLG,
        }}
      >
        <Outlet />
      </Content>
    </Layout>
  );
};

export default Main;
