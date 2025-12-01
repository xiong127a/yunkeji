import axios, { AxiosInstance, AxiosRequestConfig, AxiosHeaders } from 'axios';
import Cookies from 'js-cookie';

class ApiClient {
  private axiosInstance: AxiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: process.env.NEXT_PUBLIC_API_URL || '/yunkeji/api', // 使用环境变量或默认值
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      } as AxiosHeaders,
    });

    // 请求拦截器
    this.axiosInstance.interceptors.request.use(
      (config) => {
        const token = Cookies.get('token');  // 这里可能需要不同的调用方式
        if (token) {
          if (!config.headers) {
            config.headers = {} as AxiosHeaders;
          }
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // 响应拦截器
    this.axiosInstance.interceptors.response.use(
      (response) => {
        return response;
      },
      (error) => {
        if (error.response?.status === 401) {
          // 未授权，清除token并跳转到登录页
          Cookies.remove('token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // 登录
  async login(username: string, password: string) {
    const response = await this.axiosInstance.post('/auth/login', { username, password });
    return response.data;
  }

  // 注册
  async register(username: string, email: string, password: string) {
    const response = await this.axiosInstance.post('/auth/register', { username, email, password });
    return response.data;
  }

  // 提交不动产查询请求
  async submitRealEstateQuery(data: any) {
    const response = await this.axiosInstance.post('/user/real-estate/query', data);
    return response.data;
  }

  // 提交不动产查询请求（带文件）
  async submitRealEstateQueryWithFiles(data: FormData) {
    const response = await this.axiosInstance.post('/user/real-estate/query-with-files', data, {
      headers: {
        'Content-Type': 'multipart/form-data',
      } as AxiosHeaders,
    });
    return response.data;
  }

  // 查询不动产结果
  async queryRealEstateResult(requestNo: string) {
    const response = await this.axiosInstance.get(`/user/real-estate/result/${requestNo}`);
    return response.data;
  }

  // 获取用户的查询记录
  async getUserQueryRecords() {
    const response = await this.axiosInstance.get('/user/real-estate/records');
    return response.data;
  }

  // 获取查询记录详情
  async getQueryRecordDetail(recordId: number) {
    const response = await this.axiosInstance.get(`/user/real-estate/records/${recordId}`);
    return response.data;
  }

  // 获取查询记录关联的文件
  async getQueryRecordFiles(recordId: number) {
    const response = await this.axiosInstance.get(`/user/real-estate/records/${recordId}/files`);
    return response.data;
  }
}

export default new ApiClient();