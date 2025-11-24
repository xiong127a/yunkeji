// 用户相关类型
export interface User {
  id: number;
  username: string;
  email: string;
}

// 登录请求
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应
export interface LoginResponse {
  success: boolean;
  message: string;
  token?: string;
  user?: User;
}

// 注册请求
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

// 不动产查询记录
export interface RealEstateQueryRecord {
  id: number;
  userId: number;
  name: string;
  idCard: string;
  requestNo?: string;
  status: string;
  result?: string;
  createdAt: string;
  files?: RealEstateFile[];
}

// 不动产文件
export interface RealEstateFile {
  id: number;
  queryRecordId: number;
  fileType: string;
  fileName: string;
  filePath: string;
  fileSize: number;
  createdAt: string;
}

// 不动产查询请求
export interface RealEstateQueryRequest {
  name: string;
  idCard: string;
  callBackUrl?: string;
}

// 不动产查询响应
export interface RealEstateQueryResponse {
  success: boolean;
  fail: boolean;
  message: string;
  code: number;
  result: any;
  timestamp: number;
}