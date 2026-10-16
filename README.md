# 神四

2026-10-16 服务端配置检查报告及独立兼容补丁源码。

## 内容

- `docs/`：神四配置检查与修复报告、刷怪点缺失检查记录。
- `src/coupons/`：PlayerPoints模式登录SQL预读的最小修复，以及隔离验证程序。
- `src/shield/`：Intensify聊天保护的ProtocolLib4.4适配器、离线补丁生成器及验证程序。
- `SHA256SUMS.txt`：文件完整性校验清单。

仅包含本次自行编写的补丁源码和用户确认的两份报告。完整服务端、第三方插件JAR、玩家数据、数据库、运行日志、凭证配置及SSH密钥不在本仓库中。

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
