<template>
  <div class="contact-float-button">
    <el-popover
      placement="left"
      :width="280"
      trigger="click"
      popper-class="contact-popover"
    >
      <template #reference>
        <div class="float-btn">
          <el-icon class="btn-icon"><Service /></el-icon>
          <span class="btn-text">联系客服</span>
        </div>
      </template>
      
      <div class="contact-popover-content">
        <div class="popover-title">
          <el-icon><Service /></el-icon>
          <span>客服联系方式</span>
        </div>
        
        <div class="contact-items">
          <div v-if="contactInfo.wechat" class="contact-item">
            <div class="item-label">
              <el-icon><ChatDotRound /></el-icon>
              <span>微信</span>
            </div>
            <div class="item-value">
              <span class="wechat-value">{{ contactInfo.wechat }}</span>
              <el-button 
                size="small" 
                type="primary" 
                text 
                @click="copyWechat"
                class="copy-btn"
              >
                复制
              </el-button>
            </div>
          </div>
          
          <div v-if="contactInfo.phone" class="contact-item">
            <div class="item-label">
              <el-icon><Phone /></el-icon>
              <span>电话</span>
            </div>
            <div class="item-value">
              <a :href="`tel:${contactInfo.phone}`" class="phone-link">
                {{ contactInfo.phone }}
              </a>
            </div>
          </div>
          
          <div v-if="contactInfo.workHours" class="work-hours">
            <el-icon><Clock /></el-icon>
            <span>{{ contactInfo.workHours }}</span>
          </div>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Service, ChatDotRound, Phone, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ContactService from '@/services/ContactService'

export default {
  name: 'ContactFloatButton',
  components: {
    Service,
    ChatDotRound,
    Phone,
    Clock
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
        console.log('悬浮按钮 - 客服信息加载:', data)
        // 如果API返回了数据，使用API数据；否则使用默认值
        if (data && (data.wechat || data.phone)) {
          contactInfo.value = data
        }
      } catch (error) {
        console.error('悬浮按钮 - 加载客服信息失败:', error)
        // 使用默认值
      }
    }
    
    const copyWechat = () => {
      if (contactInfo.value?.wechat) {
        navigator.clipboard.writeText(contactInfo.value.wechat).then(() => {
          ElMessage.success('微信号已复制到剪贴板')
        }).catch(() => {
          ElMessage.error('复制失败，请手动复制')
        })
      }
    }
    
    onMounted(() => {
      loadContactInfo()
    })
    
    return {
      contactInfo,
      copyWechat
    }
  }
}
</script>

<style scoped>
.contact-float-button {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 999;
}

.float-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  cursor: pointer;
  transition: all 0.3s;
  color: white;
}

.float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.btn-icon {
  font-size: 28px;
  margin-bottom: 4px;
}

.btn-text {
  font-size: 12px;
  font-weight: 500;
}

.contact-popover-content {
  padding: 10px 0;
}

.popover-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.contact-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.contact-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.item-value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.wechat-value {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  flex: 1;
}

.copy-btn {
  padding: 4px 12px;
}

.phone-link {
  color: #409eff;
  text-decoration: none;
  font-size: 15px;
  font-weight: 600;
  transition: color 0.3s;
}

.phone-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.work-hours {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

@media (max-width: 768px) {
  .contact-float-button {
    right: 20px;
    bottom: 20px;
  }
  
  .float-btn {
    width: 60px;
    height: 60px;
  }
  
  .btn-icon {
    font-size: 24px;
  }
  
  .btn-text {
    font-size: 11px;
  }
}
</style>

<style>
.contact-popover {
  padding: 15px !important;
}
</style>

