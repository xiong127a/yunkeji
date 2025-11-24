'use client';

import React, { useState } from 'react';
import { Button, Card, Form, Input, message, Row, Col, Typography } from 'antd';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import apiClient from '@/app/api/client';
import { setAuthToken } from '@/app/utils/auth';
import { LoginRequest } from '@/app/types';

const { Title } = Typography;

export default function Login() {
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const onFinish = async (values: LoginRequest) => {
    setLoading(true);
    try {
      const response = await apiClient.login(values.username, values.password);
      if (response.success) {
        message.success('登录成功');
        setAuthToken(response.token);
        router.push('/dashboard');
      } else {
        message.error(response.message || '登录失败');
      }
    } catch (error) {
      message.error('登录失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <Row justify="center" style={{ marginTop: '50px' }}>
        <Col xs={24} sm={20} md={16} lg={12} xl={10}>
          <Card>
            <div style={{ textAlign: 'center', marginBottom: '20px' }}>
              <Image 
                src="/yunkeji-logo.jpg" 
                alt="云科技Logo" 
                width={100}
                height={100}
                style={{ borderRadius: '8px' }}
              />
            </div>
            <Title level={2} style={{ textAlign: 'center' }}>
              用户登录
            </Title>
            <Form
              name="login"
              onFinish={onFinish}
              autoComplete="off"
              layout="vertical"
            >
              <Form.Item
                label="用户名"
                name="username"
                rules={[{ required: true, message: '请输入用户名!' }]}
              >
                <Input />
              </Form.Item>

              <Form.Item
                label="密码"
                name="password"
                rules={[{ required: true, message: '请输入密码!' }]}
              >
                <Input.Password />
              </Form.Item>

              <Form.Item>
                <Button type="primary" htmlType="submit" loading={loading} block>
                  登录
                </Button>
              </Form.Item>

              <Form.Item>
                <div style={{ textAlign: 'center' }}>
                  还没有账号？ <Link href="/register">立即注册</Link>
                </div>
              </Form.Item>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
}