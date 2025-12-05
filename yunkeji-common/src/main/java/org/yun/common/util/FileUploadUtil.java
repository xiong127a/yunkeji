package org.yun.common.util;

import java.io.File;

/**
 * 文件上传工具类
 * 用于获取上传文件的基础目录（项目平级目录下的upload目录）
 */
public class FileUploadUtil {
    
    /**
     * 获取上传文件的基础目录
     * 返回项目平级目录下的 upload 目录
     * 例如：项目在 D:/workSpace/yunkeji，则返回 D:/workSpace/upload
     * 
     * @return 上传文件基础目录路径
     */
    public static String getUploadBasePath() {
        // 获取当前工作目录（通常是项目根目录或jar包所在目录）
        String currentDir = System.getProperty("user.dir");
        File currentFile = new File(currentDir);
        
        // 如果是jar包运行，需要特殊处理
        String jarPath = FileUploadUtil.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        
        File baseDir;
        if (jarPath.endsWith(".jar")) {
            // jar包运行模式：jar包在 lib/ 目录下，需要向上两级到项目根目录
            File jarFile = new File(jarPath);
            File libDir = jarFile.getParentFile(); // lib目录
            File appDir = libDir != null ? libDir.getParentFile() : null; // 项目根目录
            baseDir = appDir != null ? appDir.getParentFile() : currentFile.getParentFile();
        } else {
            // 开发模式：直接使用当前目录的父目录
            baseDir = currentFile.getParentFile();
        }
        
        if (baseDir == null) {
            // 如果无法获取父目录，使用当前目录
            baseDir = currentFile;
        }
        
        // 返回项目平级目录下的 upload 目录
        File uploadDir = new File(baseDir, "upload");
        return uploadDir.getAbsolutePath();
    }
    
    /**
     * 确保上传目录存在
     * @param subPath 子路径（如 "real-estate"）
     * @return 完整的上传目录路径
     */
    public static String ensureUploadDir(String subPath) {
        String basePath = getUploadBasePath();
        File uploadDir = new File(basePath, subPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        return uploadDir.getAbsolutePath();
    }
}

