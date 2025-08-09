import React from 'react';
import { Card, Form, Input, Button, Checkbox, Image, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import Banner from '../assets/img/banner.png';

const { Title, Link } = Typography;

interface LoginFormValues {
  username: string;
  password: string;
  remember: boolean;
}

const Login: React.FC = () => {
  const [form] = Form.useForm<LoginFormValues>();

  const onFinish = (values: LoginFormValues) => {
    console.log('登录信息:', values);
    // 这里可以添加登录逻辑
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        height: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: '#f0f2f5',
        overflow: 'hidden',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '1920px', // 限制最大宽度到16:9的1920px
          height: '100vh',
          display: 'flex',
          boxShadow: '0 0 20px rgba(0, 0, 0, 0.1)',
        }}
      >
        {/* 左侧图片区域 - 黄金分割比例约 62% */}
        <div
          style={{
            flex: '0 0 62%',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            position: 'relative',
            overflow: 'hidden',
          }}
        >
          <div
            style={{
              textAlign: 'center',
              color: 'white',
              zIndex: 1,
            }}
          >
            <Image
              src={Banner}
              alt="Banner"
              style={{ width: '70%', height: 'auto' }}
            />
          </div>
          {/* 装饰性背景元素 */}
          <div
            style={{
              position: 'absolute',
              top: '-50%',
              right: '-20%',
              width: '100%',
              height: '200%',
              background: 'rgba(255, 255, 255, 0.1)',
              borderRadius: '50%',
              transform: 'rotate(-15deg)',
            }}
          />
        </div>

        {/* 右侧登录区域 - 黄金分割比例约 38% */}
        <div
          style={{
            flex: '0 0 38%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '2rem',
            background: '#ffffff',
          }}
        >
          <Card
            style={{
              width: '100%',
              maxWidth: '400px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
              borderRadius: '12px',
            }}
          >
            <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
              <Title level={2} style={{ marginBottom: '0.5rem' }}>
                欢迎登录
              </Title>
              <p style={{ color: '#666', fontSize: '14px' }}>
                请输入您的账号信息
              </p>
            </div>

            <Form
              form={form}
              name="login"
              onFinish={onFinish}
              autoComplete="off"
              size="large"
            >
              <Form.Item
                name="username"
                rules={[
                  { required: true, message: '请输入用户名!' },
                  { min: 3, message: '用户名至少3个字符' },
                ]}
              >
                <Input
                  prefix={<UserOutlined style={{ color: '#1890ff' }} />}
                  placeholder="用户名"
                  style={{ borderRadius: '8px' }}
                />
              </Form.Item>

              <Form.Item
                name="password"
                rules={[
                  { required: true, message: '请输入密码!' },
                  { min: 6, message: '密码至少6个字符' },
                ]}
              >
                <Input.Password
                  prefix={<LockOutlined style={{ color: '#1890ff' }} />}
                  placeholder="密码"
                  style={{ borderRadius: '8px' }}
                />
              </Form.Item>

              <Form.Item>
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <Form.Item name="remember" valuePropName="checked" noStyle>
                    <Checkbox>记住我</Checkbox>
                  </Form.Item>
                  <Link href="#" style={{ color: '#1890ff' }}>
                    忘记密码？
                  </Link>
                </div>
              </Form.Item>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  style={{
                    width: '100%',
                    height: '48px',
                    borderRadius: '8px',
                    fontSize: '16px',
                    fontWeight: '500',
                  }}
                >
                  登录
                </Button>
              </Form.Item>

              <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                <span style={{ color: '#666' }}>还没有账号？</span>
                <Link href="#" style={{ color: '#1890ff', marginLeft: '8px' }}>
                  立即注册
                </Link>
              </div>
            </Form>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Login;
