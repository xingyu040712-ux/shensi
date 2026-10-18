> 公开存档版：保留实际操作与验证记录；连接信息、原始配置备份、玩家数据和远程执行辅助脚本不公开。

# 神四NPC名称显示维护操作手册

日期：2026-10-18。目标：`D:\11月四区新端创作中\神四`，端口35237。

## 范围和当前检查结果

仅修复当前名称异常的NPC，保留其他NPC、坐标、皮肤、菜单及点击指令。全过程不关闭、不重启Minecraft服务器，也不整体重载NPC管理器。

当前16个活动NPC属于Adyeshach，全部为PLAYER类型；Citizens没有活动NPC存档。磁盘中的`isNameTagVisible`及运行时开关全为true。没有发现Adyeshach VisualTeam或在线玩家Bukkit记分板隐藏这些名字。

已确认异常：

| NPC ID | UUID | 存档正确名称 | 客户端收到的错误名称 |
|---|---|---|---|
| 药品食物商人 | f0c9a7b1-ee2c-4df4-9d8e-14de3bd09bc9 | §d药品食物商人 | Adyeshach |
| 装备材料商人 | 744ee227-e1be-4741-9e7d-ef813fbfb6bd | §4装备材料商人 | Adyeshach |

其他14个活动NPC的磁盘名称与运行时名称一致。Minecraft协议已捕获上述两个实体的生成包和默认名称，证明问题不只存在于检查工具的字符串输出中。尚未使用用户自己的模组客户端进行画面验收。

## 1. 连接和确认目标

使用内部运维文档中已批准的连接方式确认神四目标、游戏端口及两个NPC UUID。本公开版本移除了SSH地址、账户、私钥路径和连接命令。

原始维护脚本与完整证据保存在本次本地工作目录，文中的本机路径属于执行记录，不能理解为本仓库的一键部署工具。公开恢复源码位于 `changes/2026-10-18/npc-names/src/restore-original-names.js`，使用前须填写三个路径并核验同目录的两个目标。

## 2. 配置备份

已完成1928份YAML/CFG/CONF/PROPERTIES及NPC JSON的ZIP备份，排除了玩家数据和缓存。

```text
D:\11月四区新端创作中\神四\_codex_backup\npc_names_20261018_150710\all-configs-before.zip
```

大小1887059字节；SHA-256：

```text
BEB2030E3BAC26318372D1A3B343725DE1D22D3922077AD7C575B60133C7E634
```

备份中包含相关NPC原存档，完整保留了原名称、坐标、皮肤和交互配置。它可能含服务端私有配置，只供管理员保管，不应作为公开源码包上传。

再次执行备份脚本会生成新时间目录，不覆盖原备份：

```powershell
& .\lycharge-audit-20261013\read-remote.ps1 `
  -ScriptPath .\npc-name-repair-20261018\backup.ps1
```

## 3. 审核具体变更

查看具体目标和实现：

```powershell
& .\npc-name-repair-20261018\restore-names.ps1
```

不带开关只显示方案。`targets.json`列出两个UUID和前后名称；`apply-approved.js`会先核对备份存在、ID、UUID、当前错误值及磁盘正确名称。任何一项不符都会停止，避免覆盖其他人员刚做的改动。

本次采用Adyeshach公开API `setName(原名称)`、`setNameTagVisible(true)`，只刷新两个目标给当前观察者。名称文件本身已经正确，无需批量修改JSON或追加不适用于PLAYER的customName字段。

## 4. 审批后在线恢复

仅在批准方案后，于本机项目目录执行：

```powershell
& .\npc-name-repair-20261018\restore-names.ps1 -ApplyApproved
```

结果写入`apply-result.json`，应恰好包含两项更改：

- `Adyeshach` → `§d药品食物商人`。
- `Adyeshach` → `§4装备材料商人`。

不执行stop、不重启Java、不使用`ady reload`，也不执行会刷新附近全部实体的`laf refresh`。API刷新时，这两个目标可能短暂重新生成，其他NPC不变。

## 5. 验收

检查三个层面：

1. 服务器API：两个NPC名称为原存档值、nameTagVisible=true。
2. 协议：测试客户端的player_info和named_entity_spawn正确关联两个中文名称，没有新的隐藏队伍包。
3. 实际游戏：在world地图接近两个商人，分别确认名称颜色、显示距离和原点击菜单。模组客户端的画面及商店购买属于需要人工确认的部分，后台协议通过不能代替完整画面和交易验收。

两个NPC原位置：

```text
药品食物商人：world，x=-4.8484，y=32，z=0.0391
装备材料商人：world，x=-19.8414，y=32，z=3.2594
```

观察一段时间并重新进入附近范围，确认名称未再次回到Adyeshach。不要为了验证而改动普通玩家背包、余额或NPC购买数据。

## 6. 出现异常时处理

- 提示目标变化：停止，重新读取当前NPC和存档；不要取消检查强行写入。
- 名称再次变成Adyeshach：检查近期NPC编辑指令或调用方。当前实现收到空name值时会回到默认名称，但尚未证明具体是谁触发。
- 后台名称和协议正确、自己的客户端仍不显示：重点核对客户端名称显示选项、模组或资源包；先记录具体NPC和画面，不批量改其他NPC。
- 点击菜单异常：对照备份检查NPC ID/UUID及点击绑定。名称恢复本身没有修改这些配置。
- 需要回滚：先查看`runtime-targets-before.json`和ZIP内原文件；本次磁盘名称原本正确，通常无需恢复整个配置包。不要将整包解压覆盖在线服务端。若有其他人员新增配置，先逐文件对比，再按具体需要恢复。

## 实施状态

用户已批准“仅恢复这两个NPC原名”。2026-10-18 15:09:49左右完成在线恢复。

- 运行时仅两个名称改变，其他14个NPC的读取字段均一致。
- 客户端实际收到了两个正确中文名称及对应实体生成包。
- 两个磁盘NPC JSON的SHA-256与操作前一致，原正确存档保持完整。
- Java PID始终9556，35237持续监听，没有关闭或重启服务器。
- 测试账号没有OP，装备测试物品已清理、等级恢复0，测试客户端会在捕获结束后退出。

结果文件：`apply-result.json`、`verification-summary.json`、`runtime-before.json`、`runtime-after.json`、`client-before.json`、`client-after.json`、`postflight.json`、`final-verification.json`。

15:12:04再次核验，恢复后观察超过2分钟，两个名称没有被重置。16个活动NPC的JSON文件哈希全部与操作前一致；服务端仍为PID9556。测试客户端已退出，当前只剩正常玩家在线，测试账号OP为0。

后台状态与协议验证已经完成；用户自己的模组客户端画面和商店实际交易未代替用户操作验收。
