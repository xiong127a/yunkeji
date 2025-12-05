package org.yun.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.*;

/**
 * API签名工具类（官方版本）
 * 来源：https://dhtech-assets.oss-cn-beijing.aliyuncs.com/openApi/ApiSignUtil.java
 * 
 * @author chenxuwei
 * @date 2022/2/24
 */
public class ApiSignUtil {

    /**
     * 生成签名（主入口方法）
     * @param o 请求参数对象（可以是 Map 或其他对象）
     * @param cKey 密钥
     * @return 签名
     */
    @SuppressWarnings("unchecked")
    public static String sign(Object o, String cKey) {
        String paramJson = JSONObject.toJSONString(o);
        Map<String, Object> treeMap = JSON.parseObject(paramJson, TreeMap.class);
        return gzSign(treeMap, cKey);
    }

    /**
     * 生成签名（Map版本，兼容现有调用）
     * @param params 请求参数Map
     * @param secretKey 密钥
     * @return 签名
     */
    public static String sign(Map<String, Object> params, String secretKey) {
        // 过滤掉 signature 字段（如果存在）
        Map<String, Object> filteredParams = new HashMap<>(params);
        filteredParams.remove("signature");
        
        // 转换为 TreeMap 自动排序
        Map<String, Object> treeMap = new TreeMap<>(filteredParams);
        return gzSign(treeMap, secretKey);
    }

    /**
     * 生成签名（核心方法）
     * @param data 已排序的数据
     * @param ckey 密钥
     * @return 签名
     */
    public static String gzSign(Map<String, Object> data, String ckey) {
        String signStr = JSON.toJSONString(gzDataSort(data));
        String sign = SecureUtil.sha256(signStr + ckey);
        return sign;
    }

    /**
     * 对数据进行递归排序
     * @param data 原始数据
     * @return 排序后的数据
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> gzDataSort(Map<String, Object> data) {
        // 排序
        for (int i = 0; i < data.size(); i++) {
            Set<String> keySet = data.keySet();
            String[] keyArray = keySet.toArray(new String[keySet.size()]);
            Object obj = data.get(keyArray[i]);

            // 处理null值
            if (obj == null) {
                continue;
            }

            if (isBaseType(obj)) {
                continue;
            }

            if (isArray(obj)) {
                List<Object> mapList = JSON.parseObject(obj.toString(), List.class);
                if (CollectionUtil.isEmpty(mapList) || isBaseType(mapList.get(0)))
                    continue;

                List<Map<String, Object>> signList = new ArrayList<>();
                for (Object map : mapList) {
                    Map<String, Object> vd = JSON.parseObject(map.toString(), TreeMap.class);
                    signList.add(gzDataSort(vd));
                }

                data.put(keyArray[i], signList);
            } else {
                Map<String, Object> vd = JSON.parseObject(obj.toString(), TreeMap.class);
                data.put(keyArray[i], gzDataSort(vd));
            }
        }

        return data;
    }

    /**
     * 判断object是否为基本类型
     * @param object 对象
     * @return 是否为基本类型
     */
    @SuppressWarnings("rawtypes")
    public static boolean isBaseType(Object object) {
        // 处理null值
        if (object == null) {
            return true; // null值视为基本类型，跳过处理
        }
        Class className = object.getClass();
        if (className.equals(Integer.class) ||
                className.equals(Byte.class) ||
                className.equals(Long.class) ||
                className.equals(Double.class) ||
                className.equals(Float.class) ||
                className.equals(Character.class) ||
                className.equals(Short.class) ||
                className.equals(Boolean.class) ||
                className.equals(String.class) ||
                className.equals(BigDecimal.class)
        ) {
            return true;
        }
        return false;
    }

    /**
     * 判断object是否为数组
     * @param object 对象
     * @return 是否为数组
     */
    @SuppressWarnings("rawtypes")
    public static boolean isArray(Object object) {
        // 处理null值
        if (object == null) {
            return false;
        }
        Class className = object.getClass();
        if (className.equals(JSONArray.class)) {
            return true;
        }
        // 也支持 List 类型
        if (object instanceof List) {
            return true;
        }
        return false;
    }
}
