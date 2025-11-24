'use client';

import React, { useEffect, useState } from 'react';
import { Button, Card, List, message, Row, Col, Typography, Space } from 'antd';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import apiClient from '@/app/api/client';
import { RealEstateQueryRecord } from '@/app/types';
import { isAuthenticated, removeAuthToken } from '@/app/utils/auth';

const { Title, Text } = Typography;

export default function Dashboard() {
  const [records, setRecords] = useState<RealEstateQueryRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push('/login');
      return;
    }
    
    fetchRecords();
  }, []);

  const fetchRecords = async () => {
    try {
      const data = await apiClient.getUserQueryRecords();
      setRecords(data);
    } catch (error) {
      message.error('获取查询记录失败');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    removeAuthToken();
    router.push('/login');
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'SUBMITTED':
        return '已提交';
      case 'PROCESSING':
        return '处理中';
      case 'COMPLETED':
        return '已完成';
      default:
        return status;
    }
  };

  return (
    <div className="container">
      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24}>
          <Card>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <Image 
                    src="/yunkeji-logo.jpg" 
                    alt="云科技Logo" 
                    width={50}
                    height={50}
                    style={{ borderRadius: '4px', marginRight: '10px' }}
                  />
                  <Title level={3} style={{ margin: 0 }}>
                    控制台
                  </Title>
                </div>
              </div>
              <Space>
                <Button type="primary">
                  <Link href="/query">新建查询</Link>
                </Button>
                <Button onClick={handleLogout}>退出登录</Button>
              </Space>
            </div>
          </Card>
        </Col>
      </Row>

      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24}>
          <Card>
            <Title level={4}>我的查询记录</Title>
            <List
              loading={loading}
              dataSource={records}
              renderItem={(record) => (
                <List.Item>
                  <List.Item.Meta
                    title={
                      <Link href={`/query/${record.id}`}>
                        {record.name} ({record.idCard})
                      </Link>
                    }
                    description={
                      <Space>
                        <Text>状态: {getStatusText(record.status)}</Text>
                        <Text>创建时间: {record.createdAt}</Text>
                        {record.requestNo && <Text>请求编号: {record.requestNo}</Text>}
                      </Space>
                    }
                  />
                  <div>
                    <Link href={`/query/${record.id}`}>
                      <Button type="link">查看详情</Button>
                    </Link>
                  </div>
                </List.Item>
              )}
            />
            {records.length === 0 && !loading && (
              <div style={{ textAlign: 'center', padding: '20px' }}>
                <Text>暂无查询记录</Text>
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}