// 公开模板：先替换三个路径。原执行版本仅修改两个已批准NPC，详见同目录targets.json。
var B=Java.type('org.bukkit.Bukkit'),File=Java.type('java.io.File'),Files=Java.type('java.nio.file.Files');
var root='REPLACE_WITH_SERVER_ROOT',backup='REPLACE_WITH_VERIFIED_BACKUP_DIRECTORY';
if(!new File(backup+'/all-configs-before.zip').isFile())throw 'Required configuration backup missing';
var targets=JSON.parse(String(new (Java.type('java.lang.String'))(Files.readAllBytes(new File('REPLACE_WITH_TARGETS_JSON_PATH').toPath()),'UTF-8')));
if(targets.length!=2)throw 'Target count mismatch';
var cl=B.getPluginManager().getPlugin('Adyeshach').getClass().getClassLoader();
var api=cl.loadClass('ink.ptms.adyeshach.core.Adyeshach').getField('INSTANCE').get(null).api();
var type=cl.loadClass('ink.ptms.adyeshach.core.entity.manager.ManagerType').getField('PERSISTENT').get(null);
var manager=api.getPublicEntityManager(type),checked=[];
for(var n=0;n<targets.length;n++){
 var t=targets[n],e=manager.getEntityByUniqueId(t.uuid);
 if(e==null||String(e.getId())!=t.id||String(e.getName())!=t.before)throw 'Runtime target changed: '+t.id;
 var disk=JSON.parse(String(new (Java.type('java.lang.String'))(Files.readAllBytes(new File(root+'/plugins/Adyeshach/npc/'+t.uuid+'.json').toPath()),'UTF-8')));
 if(disk.gameProfile.name!=t.after)throw 'Stored target changed: '+t.id;
 checked.push({entity:e,target:t});
}
Files.write(new File(backup+'/runtime-targets-before.json').toPath(),new (Java.type('java.lang.String'))(JSON.stringify(targets,null,2)).getBytes('UTF-8'));
var changed=[];
for(var n=0;n<checked.length;n++){var e=checked[n].entity,t=checked[n].target;e.setName(t.after);e.setNameTagVisible(true);changed.push({id:t.id,uuid:t.uuid,before:t.before,after:String(e.getName()),visible:e.isNameTagVisible()})}
JSON.stringify({changed:changed,serverStopped:false,method:'Adyeshach public setName/setNameTagVisible; existing correct stored names preserved'});
