# 神四

2026-10-16 服务端兼容补丁，以及2026-10-18装备等级、NPC名称与符文配置修复记录。

## 2026-10-18 最新符文修复

- [符文名称与镶嵌后横向显示报告](docs/2026-10-18/神四符文名称与横排修改操作报告.md)
- [符文属性迁移与 lore 清理报告](docs/2026-10-18/神四符文lore清理与实测报告.md)
- [8 份最小补丁、部署哈希与验收记录](changes/2026-10-18/rune-horizontal/README.md)

104 种符文统一名称格式，70 条装备的新生成物品在镶嵌、拆卸、复装及重登后保持横排。已有玩家装备未迁移，服务器未关闭，完整配置与私有备份未公开。

## 2026-10-18 两次修改

- [装备等级修复与全量符文检查](docs/2026-10-18/神四装备等级与全量符文检查报告.md)
- [NPC名称显示维护手册](docs/2026-10-18/神四NPC名称显示维护操作手册.md)
- [最小配置补丁、恢复源码与验证记录](changes/2026-10-18/README.md)

两次操作均保持神四运行。NPC只恢复已批准的药品食物商人、装备材料商人；其他14个NPC不变。装备等级使用LyGalaxyLevel，原属性和符文孔数保留。新操作的具体使用及回滚方式见上述说明；下文原JAR兼容补丁属于2026-10-16批次。

## 内容

- `docs/`：神四配置检查与修复报告、刷怪点缺失检查记录。
- `src/coupons/`：PlayerPoints模式登录SQL预读的最小修复，以及隔离验证程序。
- `src/shield/`：Intensify聊天保护的ProtocolLib4.4适配器、离线补丁生成器及验证程序。
- `SHA256SUMS.txt`：文件完整性校验清单。

包含用户确认的修复报告、自行编写的补丁/恢复源码、最小配置差异和筛选后的验证记录。完整服务端、第三方插件JAR、玩家数据、数据库、运行日志、凭证配置及SSH密钥不在本仓库中。

## 使用边界

补丁针对本次检查的既有插件版本，不是完整Coupons或Intensify项目源码，也不是通用升级包。原版第三方依赖需自行准备，勿把补丁直接用于未核对版本的生产服。

原始JAR SHA-256：

- Coupons（上次已补依赖声明的版本）：见补丁前配置修复备份及本地校验记录，使用前核对对应文件。
- Intensify：`42CBC643F921BE97BEABAA3976CF3EA6A542E51D9A542CA853A8F467D3CDB026`。

`PatchCouponsJoin`仅增加PlayerPoints模式guard，保留SQL模式；CDKEY仍依赖原SQL表。
`PatchShield`仅替换SecretChatShield注册入口并增加兼容class；原过滤方法、强化玩法和授权相关class保留。

## 构建说明

需要JDK21、原版目标JAR、ProtocolLib4.4及Paper1.12.2 API。第三方依赖不随此仓库分发。所有路径请替换成自己的本地路径。

Coupons离线生成器：

```powershell
javac --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED -d build/coupons src/coupons/PatchCouponsJoin.java
java --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED -cp build/coupons PatchCouponsJoin input-coupons.jar patched-coupons.jar
```

Intensify适配器编译为Java8 class；生成器本身使用JDK21内置ASM：

```powershell
javac --release 8 -encoding UTF-8 -classpath "input-intensify.jar;protocollib-4.4.jar;paper-1.12.2-api.jar" -d build/shield-adapter src/shield/com/mchim/Intensify/Utils/SecretChatShieldCompat.java
javac --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED -d build/shield-tool src/shield/PatchShield.java
java --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED -cp build/shield-tool PatchShield input-intensify.jar build/shield-adapter patched-intensify.jar
```

先备份并关闭目标服，再对照补丁检查JAR成员差异、Java8类版本、原/新哈希及实际运行日志。验证程序需要对应的插件及Bukkit/ProtocolLib运行类路径；其结果不能替代真实服务器登录验收。

## 已完成的验证

2026-10-16 实际服务器启动、注册登录、普通聊天、宝石及符文菜单通过。最后已正常保存关闭。剩余MM内容缺失和旧插件兼容问题见报告，不能把本仓库理解为“所有问题已经解决”。
