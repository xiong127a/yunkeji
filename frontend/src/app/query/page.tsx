'use client';

import React, { useState, useEffect } from 'react';
import { 
  Button, 
  Card, 
  Form, 
  Input, 
  Upload, 
  message, 
  Row, 
  Col, 
  Typography,
  Steps,
  Space
} from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { useRouter } from 'next/navigation';
import type { UploadProps } from 'antd';
import apiClient from '@/app/api/client';
import { RealEstateQueryRequest } from '@/app/types';
import { isAuthenticated } from '@/app/utils/auth';

const { Title } = Typography;
const { Step } = Steps;

export default function QueryPage() {
  const [currentStep, setCurrentStep] = useState(0);
  const [form] = Form.useForm();
  const [fileList, setFileList] = useState<UploadProps['fileList']>([]);
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
    if (!isAuthenticated()) {
      router.push('/login');
    }
  }, []);

  if (!isClient) {
    return null;
  }

  const handleFileChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
    setFileList(newFileList);
  };

  const onFinish = async (values: any) => {
    setLoading(true);
    try {
      const formData = new FormData();
      
      // 创建查询请求对象
      const queryRequest: RealEstateQueryRequest = {
        name: values.name,
        idCard: values.idCard,
      };
      
      formData.append('request', new Blob([JSON.stringify(queryRequest)], {
        type: 'application/json'
      }));
      
      // 添加文件
      fileList?.forEach((file) => {
        formData.append('files', file.originFileObj as Blob);
      });
      
      const response = await apiClient.submitRealEstateQueryWithFiles(formData);
      message.success('查询请求提交成功');
      router.push('/dashboard');
    } catch (error) {
      message.error('查询请求提交失败');
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    {
      title: '填写信息',
      content: (
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
        >
          <Form.Item
            label="姓名"
            name="name"
            rules={[{ required: true, message: '请输入姓名!' }]}
          >
            <Input placeholder="请输入被查询人姓名" />
          </Form.Item>

          <Form.Item
            label="身份证号"
            name="idCard"
            rules={[{ required: true, message: '请输入身份证号!' }]}
          >
            <Input placeholder="请输入被查询人身份证号" />
          </Form.Item>

          <Form.Item
            label="相关文件"
            name="files"
          >
            <Upload
              fileList={fileList}
              onChange={handleFileChange}
              multiple
              beforeUpload={() => false} // 不自动上传
            >
              <Button icon={<UploadOutlined />}>选择文件</Button>
            </Upload>
            <div style={{ marginTop: 8 }}>
              <small>支持PDF、图片等文件格式</small>
            </div>
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                提交查询
              </Button>
              <Button onClick={() => router.push('/dashboard')}>
                返回
              </Button>
            </Space>
          </Form.Item>
        </Form>
      ),
    },
  ];

  return (
    <div className="container">
      <Row justify="center" style={{ marginTop: '30px' }}>
        <Col xs={24} sm={20} md={16} lg={12}>
          <Card>
            <Title level={3} style={{ textAlign: 'center' }}>
              不动产信息查询
            </Title>
            
            <Steps current={currentStep} items={steps.map(item => ({ title: item.title }))} />
            
            <div style={{ marginTop: 24 }}>
              {steps[currentStep].content}
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}