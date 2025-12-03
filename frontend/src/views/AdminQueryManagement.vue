<template>
  <div class="admin-query-page">
    <el-card class="admin-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="查询记录管理" name="records">
          <div class="card-header">
            <div>
              <h2>查询记录管理</h2>
              <p>查看所有用户提交的查询信息，并调整每条查询的费用。</p>
            </div>
            <div class="actions">
              <el-button type="primary" :loading="recordsLoading" @click="loadRecords">刷新数据</el-button>
            </div>
          </div>

          <el-table
            v-loading="recordsLoading"
            :data="records"
            stripe
            border
            class="records-table"
            empty-text="暂无查询记录"
          >
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="name" label="姓名" width="140" />
        <el-table-column prop="idCard" label="证件号" min-width="200" />
        <el-table-column prop="requestNo" label="请求编号" min-width="200" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag type="info">{{ row.status || 'SUBMITTED' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="180">
          <template #default="{ row }">
            {{ row.createdAt || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="反馈结果" min-width="220">
          <template #default="{ row }">
            {{ row.result || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="当前费用 (元)" min-width="180">
          <template #default="{ row }">
            <div v-if="editingRecordId === row.id" class="edit-fee-cell">
              <el-input-number
                v-model="editFee"
                :precision="2"
                :step="10"
                :min="0"
                controls-position="right"
                class="fee-input"
              />
            </div>
            <div v-else>
              {{ formatCurrency(row.queryFee) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div v-if="editingRecordId === row.id">
              <el-button
                size="small"
                type="primary"
                :loading="saving"
                @click="saveFee(row)"
              >
                保存
              </el-button>
              <el-button
                size="small"
                @click="cancelEdit"
                :disabled="saving"
              >
                取消
              </el-button>
            </div>
            <div v-else>
              <el-button size="small" type="primary" @click="startEdit(row)">编辑费用</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
        </el-tab-pane>

        <el-tab-pane label="用户单价管理" name="users">
          <div class="card-header">
            <div>
              <h2>用户单价管理</h2>
              <p>为每个用户设置默认查询单价（后续计费可以按此单价计算）。</p>
            </div>
            <div class="actions">
              <el-button type="primary" :loading="usersLoading" @click="loadUsers">刷新用户</el-button>
            </div>
          </div>

          <el-table
            v-loading="usersLoading"
            :data="users"
            stripe
            border
            class="records-table"
            empty-text="暂无用户"
          >
            <el-table-column prop="id" label="用户ID" width="90" />
            <el-table-column prop="username" label="用户名" min-width="140" />
            <el-table-column prop="email" label="邮箱" min-width="200" />
            <el-table-column prop="role" label="角色" width="120" />
            <el-table-column label="余额 (元)" min-width="160">
              <template #default="{ row }">
                {{ formatCurrency(row.balance) }}
              </template>
            </el-table-column>
            <el-table-column label="查询单价 (元)" min-width="180">
              <template #default="{ row }">
                <div v-if="editingUserId === row.id" class="edit-fee-cell">
                  <el-input-number
                    v-model="editUserPrice"
                    :precision="2"
                    :step="10"
                    :min="0"
                    controls-position="right"
                    class="fee-input"
                  />
                </div>
                <div v-else>
                  {{ formatCurrency(row.queryPrice) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="320" fixed="right">
              <template #default="{ row }">
                <div v-if="editingUserId === row.id">
                  <el-button
                    size="small"
                    type="primary"
                    :loading="savingUser"
                    @click="saveUserPrice(row)"
                  >
                    保存
                  </el-button>
                  <el-button
                    size="small"
                    @click="cancelUserEdit"
                    :disabled="savingUser"
                  >
                    取消
                  </el-button>
                </div>
                <div v-else class="user-actions">
                  <el-button size="small" type="primary" @click="startUserEdit(row)">编辑单价</el-button>
                  <el-button size="small" @click="openRechargeDialog(row)">储值</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="rechargeDialogVisible"
      title="用户储值"
      width="400px"
    >
      <div v-if="rechargeUser">
        <p>为用户 <strong>{{ rechargeUser.username }}</strong> 储值。</p>
        <p>当前余额：{{ formatCurrency(rechargeUser.balance) }}</p>
        <el-form label-width="80px" style="margin-top: 10px;">
          <el-form-item label="金额">
            <el-input-number
              v-model="rechargeAmount"
              :precision="2"
              :step="100"
              :min="0"
              controls-position="right"
              style="width: 220px;"
            />
          </el-form-item>
          <el-form-item label="备注">
            <el-input
              v-model="rechargeRemark"
              type="textarea"
              :rows="3"
              placeholder="可填写转账凭证号等信息"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false" :disabled="rechargeLoading">取 消</el-button>
        <el-button type="primary" :loading="rechargeLoading" @click="confirmRecharge">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AdminService from '@/services/AdminService'
import AdminUserService from '@/services/AdminUserService'

const activeTab = ref('records')

const records = ref([])
const recordsLoading = ref(false)
const saving = ref(false)
const editingRecordId = ref(null)
const editFee = ref(0)

const users = ref([])
const usersLoading = ref(false)
const savingUser = ref(false)
const editingUserId = ref(null)
const editUserPrice = ref(0)
const rechargeDialogVisible = ref(false)
const rechargeUser = ref(null)
const rechargeAmount = ref(0)
const rechargeRemark = ref('')
const rechargeLoading = ref(false)

const formatCurrency = (value) => {
  if (value === null || value === undefined) {
    return '¥0.00'
  }
  const numberValue = typeof value === 'number' ? value : Number(value)
  if (Number.isNaN(numberValue)) {
    return '¥0.00'
  }
  return `¥${numberValue.toFixed(2)}`
}

const loadRecords = async () => {
  recordsLoading.value = true
  try {
    const response = await AdminService.getAllQueryRecords()
    records.value = Array.isArray(response) ? response : []
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '获取查询记录失败')
  } finally {
    recordsLoading.value = false
  }
}

const startEdit = (row) => {
  editingRecordId.value = row.id
  editFee.value = row.queryFee ?? 0
}

const cancelEdit = () => {
  editingRecordId.value = null
  editFee.value = 0
}

const saveFee = async (row) => {
  saving.value = true
  try {
    const updated = await AdminService.updateQueryFee(row.id, editFee.value)
    records.value = records.value.map((record) => (record.id === row.id ? updated : record))
    ElMessage.success('查询费用已更新')
    cancelEdit()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '更新查询费用失败')
  } finally {
    saving.value = false
  }
}

const loadUsers = async () => {
  usersLoading.value = true
  try {
    const response = await AdminUserService.getAllUsers()
    users.value = Array.isArray(response) ? response : []
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '获取用户列表失败')
  } finally {
    usersLoading.value = false
  }
}

const startUserEdit = (row) => {
  editingUserId.value = row.id
  editUserPrice.value = row.queryPrice ?? 0
}

const cancelUserEdit = () => {
  editingUserId.value = null
  editUserPrice.value = 0
}

const saveUserPrice = async (row) => {
  savingUser.value = true
  try {
    const updated = await AdminUserService.updateUserPrice(row.id, editUserPrice.value)
    users.value = users.value.map((user) => (user.id === row.id ? updated : user))
    ElMessage.success('用户查询单价已更新')
    cancelUserEdit()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '更新用户单价失败')
  } finally {
    savingUser.value = false
  }
}

const openRechargeDialog = (row) => {
  rechargeUser.value = row
  rechargeAmount.value = 0
  rechargeRemark.value = ''
  rechargeDialogVisible.value = true
}

const confirmRecharge = async () => {
  if (!rechargeUser.value) return
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    ElMessage.error('请输入大于0的充值金额')
    return
  }
  rechargeLoading.value = true
  try {
    const updated = await AdminUserService.rechargeUser(
      rechargeUser.value.id,
      rechargeAmount.value,
      rechargeRemark.value
    )
    users.value = users.value.map((user) => (user.id === rechargeUser.value.id ? updated : user))
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '充值失败')
  } finally {
    rechargeLoading.value = false
  }
}

onMounted(() => {
  loadRecords()
  loadUsers()
})
</script>

<style scoped>
.admin-query-page {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}

.admin-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
}

.card-header p {
  margin: 4px 0 0 0;
  color: #7f8c8d;
}

.records-table {
  margin-top: 10px;
}

.edit-fee-cell {
  display: flex;
  align-items: center;
}

.fee-input {
  width: 160px;
}

.user-actions {
  display: flex;
  gap: 8px;
}
</style>

