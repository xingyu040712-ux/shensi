# 云间神四

神四服务器配套 Minecraft 1.12.2 客户端。以“云间艾尔”客户端为模板，保留原 MOD、龙之核心资源和界面风格，移除启动器音乐，预置神四服务器入口。

**[下载云间神四.zip](https://github.com/xingyu040712-ux/shensi/releases/download/client-2026.10.19/yunjianshensi.zip)** · [发布说明与校验文件](https://github.com/xingyu040712-ux/shensi/releases/tag/client-2026.10.19) · [修复日志](repair-logs/README.md)

## 客户端

| 项目 | 当前版本 |
|---|---|
| 游戏 | Minecraft 1.12.2 |
| 加载器 | Forge 14.23.5.2847 |
| 启动器 | PCL 2.13.1.1 |
| 运行环境 | Windows，建议 64 位 Java 8 |
| 服务器 | `t7.sjcmc.cn:35237` |
| 发布包 | `云间神四.zip`，约 237 MiB |
| 当前状态 | 基础适配版，启动加载通过；真实账号登录及全部界面尚未验收 |

1. 从上方下载客户端完整 ZIP，解压整个“神四客户端”文件夹。
2. 双击“神四启动器.exe”，确认选中“神四-1.12.2”。缺少 Java 8 时按 PCL 提示准备环境。
3. 使用自己的账号方式启动游戏，在“多人游戏”选择“神四”。服内登录由服务器 AuthMe 处理，包内没有内置账号密码。

完整包在 **Releases** 的 Assets 中；GitHub 会过滤中文附件名，因此发布附件使用 `yunjianshensi.zip`，下载后可改名为“云间神四.zip”；字节内容及 SHA-256 不变。GitHub 的 **Code → Download ZIP** 只包含本仓库的配置、文档和补丁，不能替代客户端。

[客户端说明](client/README.md) · [检查记录与已知问题](client/客户端检查记录.md) · [发布文件信息](client/release-manifest.json)

## 本次适配

- 删除启动器 MP3，关闭启动器自动播放、启动时播放，启动器音量设为 0；游戏原有声音资源保留。
- 启动器标题、版本目录和服务器列表改为神四，取消模板强制 LittleSkin 登录。
- 模板的 5 个 MOD 文件、10116 份 DragonCore 资源按字节保留；清理旧存档、截图、日志、皮肤缓存及个人运行数据。
- 独立 Java 8 启动测试中 Forge 成功加载 16 个模组（含内置子模组），声音与材质初始化、持续渲染循环通过。ZIP 内 12045 个文件逐个 SHA-256 核验通过。

模板仍缺少部分神四实体模型、衣橱小框和任务图标；部分扩展有模型加载及外部连接提示。详见[客户端检查记录](client/客户端检查记录.md)。启动成功不代表所有界面和自定义模型显示正常。

## 修复日志

此前上传的服务端检查、装备等级、NPC 名称、符文改造及兼容插件源码，现作为本项目的历史修复日志保留。原目录和已有链接继续可用，历史提交没有重写。

| 入口 | 内容 |
|---|---|
| [修复日志索引](repair-logs/README.md) | 按日期查找修改说明和验收证据 |
| [更新记录](CHANGELOG.md) | 客户端发布及服务端修复摘要 |
| `docs/` | 各次检查、修复报告 |
| `changes/` | 最小配置补丁、兼容源码与验证记录 |
| `src/` | 早期 Coupons、Intensify 兼容补丁源码 |
| [历史项目说明](repair-logs/历史项目说明.md) | 调整项目前的完整首页说明 |

客户端完整包按用户授权单独发布。完整服务端、私有备份、玩家数据、数据库和连接凭据不在公开仓库中。

## 致谢

感谢 PCL（Plain Craft Launcher）提供的启动器及相关文档。

- [PCL 官方网站](https://meloong.com/PCL)
- [PCL 技术支持与问题反馈](https://github.com/Meloong-Git/PCL/issues)
