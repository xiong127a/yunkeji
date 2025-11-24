'use client';

import React from 'react';
import { Button, Card, Col, Row, Typography } from 'antd';
import Link from 'next/link';

const { Title } = Typography;

export default function TestPage() {
  return (
    <div className="container">
      <Row justify="center" style={{ marginTop: '50px' }}>
        <Col span={24}>
          <Card>
            <Title level={2} style={{ textAlign: 'center' }}>
              测试页面
            </Title>
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <Link href="/">
                <Button type="primary">返回首页</Button>
              </Link>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}