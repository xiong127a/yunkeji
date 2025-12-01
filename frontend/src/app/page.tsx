'use client';

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { isAuthenticated } from '@/app/utils/auth';
import { Layout, Menu, Button, Card, Col, Row, Space, Typography } from 'antd';
import Link from 'next/link';
import Image from 'next/image';

const { Title, Paragraph } = Typography;

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    // 检查用户是否已登录
    if (isAuthenticated()) {
      // 已登录用户重定向到控制台
      router.replace('/dashboard');
    } 
    // 如果未登录，留在首页展示欢迎信息
  }, [router]);

  // 如果用户已登录，不显示任何内容（因为会重定向）
  if (isAuthenticated()) {
    return null;
  }

  // 未登录用户显示欢迎页面
  return (
    <div style={{ padding: '20px' }}>
      <Row justify="center" style={{ marginTop: '50px' }}>
        <Col span={24} style={{ maxWidth: '800px' }}>
          <Card>
            <div style={{ textAlign: 'center', marginBottom: '20px' }}>
              <Image 
                src="/yunkeji-logo.jpg" 
                alt="云科技Logo" 
                width={120}
                height={120}
                style={{ borderRadius: '8px' }}
              />
            </div>
            <Title level={2} style={{ textAlign: 'center' }}>
              云科技不动产查询系统
            </Title>
            <Paragraph style={{ textAlign: 'center', fontSize: '16px' }}>
              便捷的不动产信息查询平台
            </Paragraph>
          </Card>
        </Col>
      </Row>

      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24} style={{ maxWidth: '800px' }}>
          <Card>
            <Title level={3}>功能介绍</Title>
            <ul>
              <li>在线提交不动产查询申请</li>
              <li>上传相关证明文件（PDF、图片等）</li>
              <li>实时查询办理进度</li>
              <li>查看查询结果</li>
            </ul>
          </Card>
        </Col>
      </Row>

      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24} style={{ maxWidth: '800px' }}>
          <Card>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Space wrap style={{ justifyContent: 'center', width: '100%' }}>
                <Button type="primary" size="large" onClick={() => router.push('/login')}>
                  登录
                </Button>
                <Button size="large" onClick={() => router.push('/register')}>
                  注册
                </Button>
              </Space>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
}