import React, { useState } from 'react';
import { Layout } from 'antd';
import Appbar from './Appbar';
import Draw from './Draw';
import Main from './Main';

const BasicLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);

  const handleToggle = () => {
    setCollapsed(!collapsed);
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Draw collapsed={collapsed} />
      <Appbar collapsed={collapsed} onToggle={handleToggle} />
      <Main collapsed={collapsed} />
    </Layout>
  );
};

export default BasicLayout;
