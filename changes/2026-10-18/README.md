# 2026-10-18 神四两次修改

本目录只记录装备等级修复与两个NPC原名称恢复。实际服务器始终运行，没有更换系统架构或修改插件JAR。

## 装备等级

`equipment-level/patches/`是两个AttributePlus文件的最小统一差异补丁。保留“使用等级”读取，将等级源改为`%lgl_default_level%`，新增原生条件兼容“装备等级”。支持半角/全角冒号与等级范围。

在目标配置的副本目录先检查，再应用：

```powershell
git apply --check /path/to/attribute.yml.patch
git apply --check /path/to/format.yml.patch
git apply /path/to/attribute.yml.patch
git apply /path/to/format.yml.patch
```

文件路径应与补丁中的`plugins/AttributePlus/`一致。`deployment-manifest.json`记录实际原/部署字节SHA-256；原部署文件为CRLF，补丁按LF表达逻辑差异，因此应用后需要按实际换行格式理解字节哈希，不能把文本一致与字节一致混为一谈。在线生效使用插件原生`ap reload`，具体操作和缓存回滚注意事项见报告。

## NPC名称

`npc-names/src/restore-original-names.js`是实际恢复脚本的公开模板，只替换了三个内部路径；`targets.json`列出已批准的两个NPC UUID及原/新名称。由既有Adyeshach环境执行，使用公开API，先检查备份、目标与正确存档，再仅刷新两个目标。

磁盘名称原本正确，修复的是运行时和发送到客户端的名字；其他14个NPC及16个NPC存档哈希均未变。不应通过批量重写全部NPC、补customName或重载全部实体来重复执行本次恢复。

## 实测范围

- 70件装备253孔、104种符文通过当前运行插件接口逐项识别。
- GUI样本通过符文镶嵌/拆卸、宝石共存、等级不足/达标和重登验证。
- 两个NPC的正确中文名称与客户端实体生成包关联通过，恢复后观察超过2分钟未重置。
- 用户模组客户端的画面和实际商店交易尚未人工验收。

完整服务端配置、1928文件的私有备份、第三方插件JAR、玩家库存、原始日志、数据库及连接凭据均不在本目录中。
