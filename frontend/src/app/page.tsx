'use client';

import React, { useEffect, useState } from 'react';
import { Button, Card, Col, Row, Space, Typography } from 'antd';
import { useRouter } from 'next/navigation';
import { isAuthenticated } from '@/app/utils/auth';
import Image from 'next/image';

const { Title, Paragraph } = Typography;

export default function Home() {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    setIsLoggedIn(isAuthenticated());
  }, []);

  const handleNavigate = (path: string) => {
    router.push(path);
  };

  return (
    <div className="container">
      <Row justify="center" style={{ marginTop: '50px' }}>
        <Col span={24}>
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
        <Col span={24}>
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
        <Col span={24}>
          <Card>
            <Space direction="vertical" style={{ width: '100%' }}>
              {isLoggedIn ? (
                <Space wrap>
                  <Button type="primary" size="large" onClick={() => handleNavigate('/dashboard')}>
                    进入控制台
                  </Button>
                  <Button size="large" onClick={() => handleNavigate('/query')}>
                    开始查询
                  </Button>
                </Space>
              ) : (
                <Space wrap>
                  <Button type="primary" size="large" onClick={() => handleNavigate('/login')}>
                    登录
                  </Button>
                  <Button size="large" onClick={() => handleNavigate('/register')}>
                    注册
                  </Button>
                </Space>
              )}
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
}