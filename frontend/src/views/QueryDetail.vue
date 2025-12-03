<template>
  <div class="query-detail-container">
    <div class="detail-background"></div>
    <el-card class="detail-card" shadow="always">
      <div class="detail-header">
        <h2>查询详情</h2>
        <el-tag type="success" class="status-tag">已完成</el-tag>
      </div>
      
      <el-divider></el-divider>
      
      <el-row :gutter="20" class="detail-row">
        <el-col :span="16" class="main-col">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>基本信息</span>
              </div>
            </template>
            
            <el-descriptions :column="1" label-class-name="desc-label" content-class-name="desc-content">
              <el-descriptions-item label="查询编号">20230101001</el-descriptions-item>
              <el-descriptions-item label="查询类型">房产查询</el-descriptions-item>
              <el-descriptions-item label="提交时间">2023-01-01 10:00:00</el-descriptions-item>
              <el-descriptions-item label="申请人">张三</el-descriptions-item>
              <el-descriptions-item label="身份证号">110***********1234</el-descriptions-item>
            </el-descriptions>
          </el-card>
          
          <el-card class="result-card" style="margin-top: 20px;" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>查询结果</span>
              </div>
            </template>
            <div class="result-content">
              <p>经核查，该不动产信息属实，产权清晰，无抵押记录。</p>
              <div class="result-details">
                <h4>详细信息：</h4>
                <ul>
                  <li>房产地址：北京市朝阳区某某街道某某小区1号楼101室</li>
                  <li>建筑面积：120平方米</li>
                  <li>产权性质：商品房</li>
                  <li>登记时间：2020-05-15</li>
                </ul>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8" class="sidebar-col">
          <el-card class="files-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>相关文件</span>
              </div>
            </template>
            
            <div class="file-list">
              <div class="file-item" v-for="file in files" :key="file.name">
                <div class="file-info">
                  <i class="el-icon-document file-icon"></i>
                  <div class="file-details">
                    <div class="file-name">{{ file.name }}</div>
                    <div class="file-size">{{ file.size }}</div>
                  </div>
                </div>
                <el-button type="primary" size="small" plain class="download-button">下载</el-button>
              </div>
            </div>
            
            <div class="actions">
              <el-button type="primary" @click="$router.back()" class="back-button">
                返回列表
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'QueryDetailView',
  setup() {
    const files = [
      {
        name: '房产证.pdf',
        size: '1.2MB'
      },
      {
        name: '土地使用证.pdf',
        size: '0.8MB'
      },
      {
        name: '购房合同.pdf',
        size: '2.1MB'
      }
    ]
    
    return {
      files
    }
  }
}
</script>

<style scoped>
.query-detail-container {
  position: relative;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
  overflow: hidden;
}

.detail-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.05;
  z-index: -1;
}

.detail-card {
  border: none;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.detail-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
}

.info-card, .result-card, .files-card {
  border: none;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.info-card:hover, .result-card:hover, .files-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  font-weight: 600;
  font-size: 18px;
  color: #2c3e50;
}

.desc-label {
  font-weight: 500;
  width: 100px;
  color: #606266;
}

.desc-content {
  color: #303133;
  font-weight: 500;
}

.result-content p {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
}

.result-details {
  margin-top: 20px;
  padding: 20px;
  background: linear-gradient(120deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 12px;
}

.result-details h4 {
  margin-top: 0;
  color: #1565c0;
  font-weight: 600;
}

.result-details ul {
  padding-left: 20px;
}

.result-details li {
  margin-bottom: 8px;
  line-height: 1.6;
  color: #1565c0;
}

.file-list {
  margin-bottom: 20px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item:hover {
  background-color: #f8f9fa;
  padding-left: 10px;
}

.file-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.file-icon {
  font-size: 24px;
  color: #667eea;
  margin-right: 12px;
}

.file-details {
  flex: 1;
}

.file-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 4px;
  font-weight: 500;
}

.file-size {
  font-size: 14px;
  color: #909399;
}

.download-button {
  transition: all 0.3s ease;
}

.download-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.actions {
  text-align: center;
}

.back-button {
  padding: 14px 32px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.back-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.6);
}

@media (max-width: 992px) {
  .detail-row {
    flex-direction: column;
  }
  
  .main-col, .sidebar-col {
    width: 100%;
  }
  
  .sidebar-col {
    margin-top: 20px;
  }
}
</style>