import React from 'react';
import { Layout, theme } from 'antd';
import { Outlet } from 'react-router-dom';

const { Content } = Layout;

interface MainProps {
  collapsed: boolean;
}

const Main: React.FC<MainProps> = ({ collapsed }) => {
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  return (
    <Layout style={{ marginLeft: collapsed ? 80 : 200 }}>
      <Content
        style={{
          margin: '88px 24px 24px 24px',
          minHeight: 'calc(100vh - 88)',
          background: colorBgContainer,
        }}
      >
        <Outlet />
      </Content>
    </Layout>
  );
};

export default Main;
