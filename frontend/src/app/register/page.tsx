'use client';

import React, { useState } from 'react';
import { Button, Card, Form, Input, message, Row, Col, Typography } from 'antd';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import apiClient from '@/app/api/client';
import { setAuthToken } from '@/app/utils/auth';
import { RegisterRequest } from '@/app/types';

const { Title } = Typography;

export default function Register() {
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const onFinish = async (values: RegisterRequest) => {
    setLoading(true);
    try {
      const response = await apiClient.register(values.username, values.email, values.password);
      if (response.success) {
        message.success('注册成功');
        setAuthToken(response.token);
        router.push('/dashboard');
      } else {
        message.error(response.message || '注册失败');
      }
    } catch (error) {
      message.error('注册失败，请检查网络连接');
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
              用户注册
            </Title>
            <Form
              name="register"
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
                label="邮箱"
                name="email"
                rules={[
                  { required: true, message: '请输入邮箱!' },
                  { type: 'email', message: '请输入有效的邮箱地址!' }
                ]}
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

              <Form.Item
                label="确认密码"
                name="confirmPassword"
                dependencies={['password']}
                rules={[
                  { required: true, message: '请确认密码!' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('password') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('两次输入的密码不一致!'));
                    },
                  }),
                ]}
              >
                <Input.Password />
              </Form.Item>

              <Form.Item>
                <Button type="primary" htmlType="submit" loading={loading} block>
                  注册
                </Button>
              </Form.Item>

              <Form.Item>
                <div style={{ textAlign: 'center' }}>
                  已有账号？ <Link href="/login">立即登录</Link>
                </div>
              </Form.Item>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
}