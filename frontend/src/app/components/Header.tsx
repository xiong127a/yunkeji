'use client';

import React from 'react';
import { Layout, Menu, Button } from 'antd';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { isAuthenticated, removeAuthToken } from '@/app/utils/auth';

const { Header } = Layout;

export default function AppHeader() {
  const router = useRouter();
  const loggedIn = isAuthenticated();

  const handleLogout = () => {
    removeAuthToken();
    router.push('/login');
  };

  return (
    <Header style={{ 
      position: 'sticky', 
      top: 0, 
      zIndex: 1, 
      width: '100%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    }}>
      <div style={{ color: 'white', fontSize: '18px', fontWeight: 'bold', display: 'flex', alignItems: 'center' }}>
        <Image 
          src="/yunkeji-logo.jpg" 
          alt="云科技Logo" 
          width={40}
          height={40}
          style={{ borderRadius: '4px', marginRight: '10px' }}
        />
        <Link href="/">云科技不动产查询系统</Link>
      </div>
      
      {loggedIn ? (
        <div style={{ display: 'flex', alignItems: 'center', flex: 1, justifyContent: 'flex-end' }}>
          <Menu
            theme="dark"
            mode="horizontal"
            items={[
              {
                key: 'dashboard',
                label: <Link href="/dashboard">控制台</Link>,
              },
              {
                key: 'query',
                label: <Link href="/query">查询</Link>,
              },
            ]}
            style={{ 
              minWidth: 0, 
              backgroundColor: 'transparent',
              border: 0,
              flex: 'none'
            }}
            overflowedIndicator={<span>...</span>}
          />
          <Button 
            type="link" 
            onClick={handleLogout}
            style={{ color: 'white', marginLeft: '10px' }}
          >
            退出
          </Button>
        </div>
      ) : (
        <div style={{ display: 'flex', alignItems: 'center', flex: 1, justifyContent: 'flex-end' }}>
          <Menu
            theme="dark"
            mode="horizontal"
            items={[
              {
                key: 'login',
                label: <Link href="/login">登录</Link>,
              },
              {
                key: 'register',
                label: <Link href="/register">注册</Link>,
              },
            ]}
            style={{ 
              minWidth: 0, 
              backgroundColor: 'transparent',
              border: 0,
              flex: 'none'
            }}
            overflowedIndicator={<span>...</span>}
          />
        </div>
      )}
    </Header>
  );
}