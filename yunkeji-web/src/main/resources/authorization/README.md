# 授权书文件说明

## 文件位置

请将授权书PDF文件放置在此目录下：
- 文件名：`云科技个人信息查询及使用授权书.pdf`
- 完整路径：`yunkeji-web/src/main/resources/authorization/云科技个人信息查询及使用授权书.pdf`

## 使用说明

1. 将授权书PDF文件复制到此目录
2. 确保文件名完全匹配：`云科技个人信息查询及使用授权书.pdf`
3. 重启应用后，用户可以通过 `/api/authorization/download` 接口下载授权书

## 注意事项

- 文件必须放在 `resources/authorization/` 目录下
- 文件名必须完全匹配（包括中文字符）
- 文件会在编译时打包到jar包中









