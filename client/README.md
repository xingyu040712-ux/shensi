# 云间神四客户端

[下载完整客户端](https://github.com/xingyu040712-ux/shensi/releases/download/client-2026.10.19/yunjianshensi.zip) · [发布页](https://github.com/xingyu040712-ux/shensi/releases/tag/client-2026.10.19) · [返回项目首页](../README.md)

客户端主体以 Releases 附件 `yunjianshensi.zip` 分发（中文包名“云间神四.zip”被 GitHub 文件名过滤规则转换，下载后可改回中文名，字节不变）。本目录保存启动配置快照、使用说明、检查记录和完整性信息，不将约 237 MiB 的整合包放入 Git 历史。

解压整个“神四客户端”目录后运行“神四启动器.exe”，选择“神四-1.12.2”。Windows 建议使用 64 位 Java 8；首次缺少运行环境时按 PCL 提示下载。服务器列表已配置 `t7.sjcmc.cn:35237`，取消模板强制 LittleSkin 验证，使用自己的账号登录，服内由 AuthMe 处理。

| 文件 | 用途 |
|---|---|
| [使用说明](使用说明.md) | 运行方法和本次调整 |
| [客户端检查记录](客户端检查记录.md) | 启动测试、素材缺口及未验收边界 |
| [发布清单](release-manifest.json) | ZIP 名称、体积、SHA-256、版本及已知限制 |
| [校验值](SHA256SUMS.txt) | 下载后核对 ZIP 完整性 |
| `configs/` | 随包发布的 PCL 和版本启动配置快照 |

```powershell
Get-FileHash -LiteralPath .\yunjianshensi.zip -Algorithm SHA256
```

预期 SHA-256：`f6eb7ed291927c764ca834edd2ba65c9e74c811dfd314156b67ecd8a45fb9208`。

全部 5 个模板 MOD 文件与 10116 份 DragonCore 资源保持原始字节；扩展告警、缺少的部分模型/图标和实际登录尚未验收的边界已写入检查记录。压缩包内部 12045 个文件与制作目录逐个哈希一致。

## 致谢

感谢 PCL（Plain Craft Launcher）提供的启动器及相关文档。

- [PCL 官方网站](https://meloong.com/PCL)
- [PCL 技术支持与问题反馈](https://github.com/Meloong-Git/PCL/issues)
