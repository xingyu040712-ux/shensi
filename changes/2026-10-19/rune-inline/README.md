# 神四符文前缀与全部孔位同一行

2026-10-19 已完成并实测：71 条模板定义（70 个有效 ID）将符文前缀和全部孔位合为一条 Lore，保留孔数、属性、等级、宝石栏和符文 NBT。旧装备使用符文页面时自动整理，首次发生变化前保存完整 ItemStack。详细效果与验收见[报告](../../../docs/2026-10-19/神四装备符文同一行改造与验收.md)。

## 实现与文件

- YeeGem 1.0.1 继续负责符文、孔位、镶嵌、拆卸和属性。
- 自编 CxsRuneCompat 内部版本 1.2 负责 Lore 合行和旧装备自动整理；沿用线上文件名 `CxsRuneCompat-1.0.jar`。源码位于 `src/codex/InlineRuneCompat.java`，插件声明在 `plugin.yml`。
- `patches/` 为 6 份 MythicMobs 装备文件和 1 份 YeeGem 符文页面的最小配置差异；同时撤去上一批次成功事件中的旧排版动作。
- `deployment-manifest.json` 为 8 份实际部署文件的原始/最终 SHA-256。JAR 仅记录哈希，不发布二进制。源码与原部署版本一致。
- `verification/` 为筛选后的逐件 API、GUI、旧格式模型、旧装备重登、等级限制和最终验收记录。

## 补丁基线与使用

本批次接续 2026-10-18 横排修复。应用前须与 manifest 的 before 字节哈希核对，不适用于未知基线；已上线此批次的服务器无需重复套用。补丁使用 LF，原始文件可能含 BOM 或 CRLF；在配置副本中统一为无 BOM 的 UTF-8/LF 后执行 `git apply --check <patch>`，再执行 `git apply <patch>`。7 份补丁均已在该标准化的原始副本上应用，并与最终部署文本逐字核对。原始文件字节备份保持原样。

`CXS4RuneLayout: inline-20261019` 和原 `CXS4RuneSlots` 保留在模板 NBT 中。GUI 名称 `§3§l符文页面`、装备槽位 40 与本插件对应。插件处理点击、拖动、关闭、退出事件；关闭后刷新 YeeGem 属性。物品备份写入插件数据目录下 `inline-backups/`，备份校验失败则保留原物品。

上线时须成套更新模板、GUI 和兼容插件，避免重复加载两个 CxsRuneCompat。由实际维护者采用已核对的加载流程；本次实际操作窄范围重载既有插件及配置，未重启服务。不要将旧版主类或已废弃的拆行逻辑混用。未改写 YeeGem 第三方 JAR。

## 构建自编插件

使用 JDK 8 与自行准备的 Bukkit/Paper 1.12.2 API（或本服可编译的 patched JAR）。在本目录的 PowerShell 中执行，替换类路径为自己的依赖路径：

```powershell
New-Item -ItemType Directory -Path build -Force | Out-Null
javac -encoding UTF-8 -source 8 -target 8 -cp "<Paper-1.12.2-API.jar>" -d build src/codex/InlineRuneCompat.java
if ($LASTEXITCODE -ne 0) { throw 'Compile failed' }
Copy-Item -LiteralPath plugin.yml -Destination build/plugin.yml
jar cf CxsRuneCompat-1.2.jar -C build .
if ($LASTEXITCODE -ne 0) { throw 'Package failed' }
```

不需要在编译类路径加入 YeeGem：属性刷新通过已存在的 API 反射调用。重新打包的压缩时间等元数据会影响 JAR 字节哈希；manifest 的 after 是本次实际部署产物哈希。

## 验证边界

模板 71/71、有效 ID 的 API 拆装 70/70、真实 GUI 70/70、旧格式模型 70/70、符文识别 104/104 通过。真实旧装备含三枚符文，界面整理后重登仍保持符文及非 Lore NBT；24 级屏蔽、25 级恢复属性通过。GUI 最终 140/140 阶段通过，原始 143 次尝试含两次客户端时序失败。未穷举 70×104 所有装备和符文组合或全部战斗场景。

按用户要求未执行回退和后续回退测试；`restoreEqualsOriginal` 仅表示私有备份反序列化校验相等。本目录不含完整服务器、第三方 JAR、私有备份、远程脚本、玩家物品、数据库或连接凭据。旧版 2026-10-18 目录保留为历史记录，其“旧装备不处理”的描述仅适用于当时版本。
