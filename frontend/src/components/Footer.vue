<template>
  <el-footer class="app-footer">
    <div class="footer-content">
      <div class="contact-info">
        <span class="footer-label">客服联系方式：</span>
        <span v-if="contactInfo && contactInfo.wechat" class="contact-item">
          <el-icon><ChatDotRound /></el-icon>
          微信：{{ contactInfo.wechat }}
        </span>
        <span v-if="contactInfo && contactInfo.phone" class="contact-item">
          <el-icon><Phone /></el-icon>
          电话：<a :href="`tel:${contactInfo.phone}`" class="phone-link">{{ contactInfo.phone }}</a>
        </span>
        <span v-if="contactInfo && contactInfo.workHours" class="work-hours">
          工作时间：{{ contactInfo.workHours }}
        </span>
      </div>
      <div class="copyright">
        © 2024 云科技 版权所有
      </div>
    </div>
  </el-footer>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ChatDotRound, Phone } from '@element-plus/icons-vue'
import ContactService from '@/services/ContactService'

export default {
  name: 'AppFooter',
  components: {
    ChatDotRound,
    Phone
  },
  setup() {
    const contactInfo = ref({
      wechat: 'zs_s0501',
      phone: '17600536651',
      workHours: '周一至周五 9:00-18:00'
    })
    
    const loadContactInfo = async () => {
      try {
        const data = await ContactService.getContactInfo()
        console.log('Footer - 客服信息加载:', data)
        // 如果API返回了数据，使用API数据；否则使用默认值
        if (data && (data.wechat || data.phone)) {
          contactInfo.value = data
        }
      } catch (error) {
        console.error('Footer - 加载客服信息失败:', error)
        // 使用默认值
      }
    }
    
    onMounted(() => {
      loadContactInfo()
    })
    
    return {
      contactInfo
    }
  }
}
</script>

<style scoped>
.app-footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: #ecf0f1;
  padding: 20px 0;
  margin-top: 40px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.contact-info {
  margin-bottom: 15px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.footer-label {
  font-weight: 600;
  color: #fff;
}

.contact-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  transition: all 0.3s;
}

.contact-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

.phone-link {
  color: #3498db;
  text-decoration: none;
  transition: color 0.3s;
}

.phone-link:hover {
  color: #5dade2;
  text-decoration: underline;
}

.work-hours {
  color: #bdc3c7;
  font-size: 14px;
}

.copyright {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #95a5a6;
  font-size: 14px;
}

@media (max-width: 768px) {
  .contact-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .contact-item {
    width: 100%;
    justify-content: center;
  }
}
</style>

