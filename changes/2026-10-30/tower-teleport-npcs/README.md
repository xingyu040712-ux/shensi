# 魔塔一至五层传送 NPC

Adyeshach 持久 NPC → Trigrex 右键事件 → ChestCommands 菜单 → Multiverse-Core 原世界出生点。

| NPC | 魔塔镇 world 坐标 | 普通线路 |
|---|---|---|
| 一层传送npc | -14.056, 33, -62.047 | F1 |
| 二层传送npc | -14.007, 53, -62.056 | F2、F22x |
| 三层传送npc | -13.746, 66, -73.900 | F3、F32x |
| 四层传送npc | -19.447, 86, -67.787 | F4、F42x |
| 五层传送npc | -13.741, 99, -62.577 | F5、F52x、F53x、F54x |

五层旧单层菜单少列普通四线，但普通副本总菜单及世界已有 F54x，本次独立菜单完整列入。现有旧菜单和商人未覆盖。

## 文件与部署

`files/plugins/` 是本次 5 个 NPC、5 个独立触发器和 5 个菜单的实际保存配置。先备份目标端，检查相同 ID、UUID 和文件是否已有，再部署；不要重复导入。

新 NPC JSON 可在正常停服时放入对应目录，正常启动后从持久文件自动加载。在线新增需通过 Adyeshach 持久管理器加载、设置管理器并调用 spawn(Location) 后保存；仅复制 JSON 不会自动在运行中出现。避免全局 reload 或 PlugMan 重载 NPC 插件。

菜单/触发器使用现有 cc reload 和 trigrex reload 加载。控制台为点击者打开菜单，普通玩家无需菜单管理权限；线路沿用 op:mv tp 世界名，测试前后角色均非 OP。

## 新楼层模板

例如 `python generate-template.py templates/floor5.json`，在 generated/ 下生成对应三份配置，只生成本地文件，不自动部署。仅依赖 Python 标准库。

复制模板时修改 npc_id、npc_name、location、menu_file、trigger_file、menu_title 和 routes；不同 NPC 使用不同 ID 和文件名。二至五层按内部 ID 自动生成 UUID；复制一层模板建立新 NPC 时删除旧 npc_uuid，避免复用原 UUID。

## 验收

[重启后完整报告](../../../docs/2026-10-30/神四重启后NPC交互验收报告.md) · [11 条线路结果](verification/acceptance-results.json) · [重启后自动加载](verification/after-start-runtime.json)。

实际正常重启并重新登录后，5 个 NPC 均自动恢复，无重新创建、手动加载或刷新；11 条普通线路逐条右键打开菜单并传送通过。测试角色已清理，115 个相关原文件与重启前哈希一致。
