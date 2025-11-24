import React from 'react';
import { Inter } from 'next/font/google';
import './styles/globals.css';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import AppHeader from '@/app/components/Header';

const inter = Inter({ subsets: ['latin'] });

export const metadata = {
  title: '云科技不动产查询系统',
  description: '不动产信息查询平台',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ConfigProvider locale={zhCN}>
      <html lang="zh">
        <body className={inter.className}>
          <AppHeader />
          <main>{children}</main>
        </body>
      </html>
    </ConfigProvider>
  );
}