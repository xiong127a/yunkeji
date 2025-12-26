package org.yun.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yun.common.dto.CreateUserRequest;
import org.yun.common.dto.RechargeBalanceRequest;
import org.yun.common.dto.UpdateUserPriceRequest;
import org.yun.common.dto.UserResponse;
import org.yun.dao.entity.User;
import org.yun.dao.entity.UserBalanceRecord;
import org.yun.dao.mapper.UserBalanceRecordMapper;
import org.yun.dao.mapper.UserMapper;
import org.yun.service.UserService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.mybatisflex.core.query.QueryWrapper;

@Service
public class UserServiceImpl implements UserService {
    
    private static final int MAX_DEPTH = 3;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserBalanceRecordMapper userBalanceRecordMapper;
    
    @Value("${real.estate.cost-price:500.00}")
    private BigDecimal costPrice;
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        // 默认普通用户，查询单价默认为500（可在后台调整）
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getQueryPrice() == null) {
            user.setQueryPrice(BigDecimal.valueOf(500));
        }
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }
        // 去除层级关系，全部视为顶级
        user.setParentId(null);
        user.setDepth(1);
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
        if (user.getKycStatus() == null) {
            // 后台创建的账号默认视为已实名（可按需改成 PENDING）
            user.setKycStatus("APPROVED");
        }
        if (user.getTrusted() == null) {
            user.setTrusted(Boolean.FALSE);
        }
        
        // 校验单价：1) 不能低于成本价 2) 如果有父级，不能低于父级单价
        BigDecimal finalPrice = user.getQueryPrice() != null ? user.getQueryPrice() : BigDecimal.ZERO;
        BigDecimal minCost = costPrice != null ? costPrice : BigDecimal.valueOf(500);
        if (finalPrice.compareTo(minCost) < 0) {
            throw new IllegalArgumentException("用户单价不能低于成本价（成本价为：" + minCost + "）");
        }
        
        userMapper.insert(user);
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
    
    @Override
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userMapper.selectAll();
        return users.stream().map(user -> {
            UserResponse response = new UserResponse();
            BeanUtils.copyProperties(user, response);
            return response;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void deleteUser(Long id) {
        // 存在子级则禁止删除，避免孤儿账号
        long childCount = userMapper.selectCountByQuery(
                QueryWrapper.create().where(org.yun.dao.table.UserTableDef.USER.PARENT_ID.eq(id))
        );
        if (childCount > 0) {
            throw new IllegalStateException("该用户存在子账号，删除前请先处理/转移子级");
        }
        userMapper.deleteById(id);
    }
    
    @Override
    public UserResponse updateUserPrice(Long id, UpdateUserPriceRequest request) {
        User user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }
        BigDecimal price = request.getQueryPrice() != null ? request.getQueryPrice() : BigDecimal.ZERO;

        // 校验1：不能低于成本价
        BigDecimal minCost = costPrice != null ? costPrice : BigDecimal.valueOf(500);
        if (price.compareTo(minCost) < 0) {
            throw new IllegalArgumentException("用户单价不能低于成本价（成本价为：" + minCost + "）");
        }

        // 校验2：如果存在上级用户，校验新价格不能低于上级价格
        if (user.getParentId() != null && user.getParentId() > 0) {
            User parent = userMapper.selectOneById(user.getParentId());
            if (parent != null && parent.getQueryPrice() != null) {
                if (price.compareTo(parent.getQueryPrice()) < 0) {
                    throw new IllegalArgumentException("下级用户单价不能低于上级单价（上级当前单价为：" + parent.getQueryPrice() + "）");
                }
            }
        }

        user.setQueryPrice(price);
        userMapper.update(user);
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    @Override
    public UserResponse updateUserStatus(Long userId, String status) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User child = userMapper.selectOneById(userId);
        if (child == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("状态不能为空");
        }
        String normalized = status.trim().toUpperCase();
        if (!"ACTIVE".equals(normalized) && !"DISABLED".equals(normalized) && !"PENDING".equals(normalized)) {
            throw new IllegalArgumentException("不支持的状态值：" + status);
        }
        child.setStatus(normalized);
        userMapper.update(child);

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(child, response);
        return response;
    }
    
    @Override
    public UserResponse rechargeBalance(Long id, RechargeBalanceRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        User user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }
        BigDecimal before = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal after = before.add(request.getAmount());
        user.setBalance(after);
        userMapper.update(user);
        
        UserBalanceRecord record = new UserBalanceRecord();
        record.setUserId(user.getId());
        record.setChangeAmount(request.getAmount());
        record.setChangeType("RECHARGE");
        record.setRemark(request.getRemark());
        record.setCreatedAt(new Date());
        userBalanceRecordMapper.insert(record);
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    @Override
    public UserResponse transferToChild(Long parentId, Long childId, RechargeBalanceRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        User parent = userMapper.selectOneById(parentId);
        User child = userMapper.selectOneById(childId);
        if (parent == null || child == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!childId.equals(child.getId()) || !parentId.equals(child.getParentId())) {
            throw new IllegalStateException("仅能为直属子级分配余额");
        }
        BigDecimal parentBalance = parent.getBalance() != null ? parent.getBalance() : BigDecimal.ZERO;
        if (parentBalance.compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("父级余额不足，无法分配");
        }
        // 扣父级
        parent.setBalance(parentBalance.subtract(request.getAmount()));
        userMapper.update(parent);
        UserBalanceRecord parentRecord = new UserBalanceRecord();
        parentRecord.setUserId(parentId);
        parentRecord.setChangeAmount(request.getAmount().negate());
        parentRecord.setChangeType("DEDUCT_TRANSFER");
        parentRecord.setRemark("转给子级 " + childId + (request.getRemark() != null ? ("：" + request.getRemark()) : ""));
        parentRecord.setCreatedAt(new Date());
        userBalanceRecordMapper.insert(parentRecord);

        // 加子级
        BigDecimal childBalance = child.getBalance() != null ? child.getBalance() : BigDecimal.ZERO;
        child.setBalance(childBalance.add(request.getAmount()));
        userMapper.update(child);
        UserBalanceRecord childRecord = new UserBalanceRecord();
        childRecord.setUserId(childId);
        childRecord.setChangeAmount(request.getAmount());
        childRecord.setChangeType("RECHARGE_FROM_PARENT");
        childRecord.setRemark("来自父级 " + parentId + (request.getRemark() != null ? ("：" + request.getRemark()) : ""));
        childRecord.setCreatedAt(new Date());
        userBalanceRecordMapper.insert(childRecord);

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(child, response);
        return response;
    }

    @Override
    public UserResponse updateUserTrust(Long userId, Boolean trusted) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setTrusted(Boolean.TRUE.equals(trusted));
        userMapper.update(user);

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
