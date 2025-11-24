'use client';

import React, { useEffect, useState } from 'react';
import { 
  Button, 
  Card, 
  Descriptions, 
  message, 
  Row, 
  Col, 
  Typography,
  List,
  Tag,
  Space
} from 'antd';
import { useRouter } from 'next/navigation';
import apiClient from '@/app/api/client';
import { RealEstateQueryRecord, RealEstateFile } from '@/app/types';
import { isAuthenticated } from '@/app/utils/auth';

const { Title } = Typography;

export default function QueryDetailPage({ params }: { params: { id: string } }) {
  const [record, setRecord] = useState<RealEstateQueryRecord | null>(null);
  const [files, setFiles] = useState<RealEstateFile[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push('/login');
      return;
    }
    
    fetchRecord();
  }, [params.id]);

  const fetchRecord = async () => {
    try {
      const recordData = await apiClient.getQueryRecordDetail(Number(params.id));
      setRecord(recordData);
      
      const fileData = await apiClient.getQueryRecordFiles(Number(params.id));
      setFiles(fileData);
    } catch (error) {
      message.error('获取查询记录详情失败');
    } finally {
      setLoading(false);
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'SUBMITTED':
        return <Tag color="blue">已提交</Tag>;
      case 'PROCESSING':
        return <Tag color="orange">处理中</Tag>;
      case 'COMPLETED':
        return <Tag color="green">已完成</Tag>;
      default:
        return <Tag>{status}</Tag>;
    }
  };

  const getFileTypeText = (fileType: string) => {
    switch (fileType) {
      case 'PDF':
        return <Tag color="red">PDF</Tag>;
      case 'IMAGE':
        return <Tag color="purple">图片</Tag>;
      default:
        return <Tag color="gray">其他</Tag>;
    }
  };

  if (loading) {
    return <div className="container">加载中...</div>;
  }

  if (!record) {
    return <div className="container">未找到查询记录</div>;
  }

  return (
    <div className="container">
      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24}>
          <Card>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Title level={3} style={{ margin: 0 }}>
                查询详情
              </Title>
              <Space>
                <Button onClick={() => router.push('/dashboard')}>
                  返回
                </Button>
              </Space>
            </div>
          </Card>
        </Col>
      </Row>

      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24}>
          <Card>
            <Title level={4}>基本信息</Title>
            <Descriptions bordered column={1}>
              <Descriptions.Item label="姓名">{record.name}</Descriptions.Item>
              <Descriptions.Item label="身份证号">{record.idCard}</Descriptions.Item>
              <Descriptions.Item label="状态">{getStatusText(record.status)}</Descriptions.Item>
              {record.requestNo && (
                <Descriptions.Item label="请求编号">{record.requestNo}</Descriptions.Item>
              )}
              <Descriptions.Item label="创建时间">{record.createdAt}</Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>

      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col span={24}>
          <Card>
            <Title level={4}>相关文件</Title>
            <List
              dataSource={files}
              renderItem={(file) => (
                <List.Item>
                  <List.Item.Meta
                    title={file.fileName}
                    description={
                      <Space>
                        {getFileTypeText(file.fileType)}
                        <span>大小: {file.fileSize} 字节</span>
                        <span>上传时间: {file.createdAt}</span>
                      </Space>
                    }
                  />
                  <div>
                    <Button type="link">下载</Button>
                  </div>
                </List.Item>
              )}
            />
            {files.length === 0 && (
              <div style={{ textAlign: 'center', padding: '20px' }}>
                暂无相关文件
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}