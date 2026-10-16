import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import jdk.internal.org.objectweb.asm.*;
public final class PatchShield {
 public static void main(String[] a) throws Exception {
  String target="com/mchim/Intensify/Utils/SecretChatShield.class";
  int[] changed={0};
  try(JarFile jar=new JarFile(a[0]);JarOutputStream out=new JarOutputStream(Files.newOutputStream(Paths.get(a[2])))) {
   Enumeration<JarEntry> es=jar.entries();
   while(es.hasMoreElements()) {JarEntry e=es.nextElement();byte[] data;
    try(InputStream in=jar.getInputStream(e)){data=in.readAllBytes();}
    if(e.getName().equals(target)) {
     ClassReader cr=new ClassReader(data);ClassWriter cw=new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
     cr.accept(new ClassVisitor(Opcodes.ASM8,cw){
      @Override public MethodVisitor visitMethod(int access,String name,String desc,String sig,String[] ex) {
       if(name.equals("ALLATORIxDEMO") && desc.equals("(Lcom/mchim/Intensify/IntensifyPlugin;)V")) {
        changed[0]++;MethodVisitor mv=super.visitMethod(access,name,desc,sig,ex);
        mv.visitCode();mv.visitVarInsn(Opcodes.ALOAD,0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,"com/mchim/Intensify/Utils/SecretChatShieldCompat","install",desc,false);
        mv.visitInsn(Opcodes.RETURN);mv.visitMaxs(1,1);mv.visitEnd();return null;
       }return super.visitMethod(access,name,desc,sig,ex);
      }
     },0);data=cw.toByteArray();
    }
    out.putNextEntry(new JarEntry(e.getName()));out.write(data);out.closeEntry();
   }
   Path root=Paths.get(a[1]);
   try(java.util.stream.Stream<Path> paths=Files.walk(root)) {
    for(Path p:(Iterable<Path>)paths.filter(p->Files.isRegularFile(p)&&p.toString().endsWith(".class"))::iterator) {
     out.putNextEntry(new JarEntry(root.relativize(p).toString().replace('\\','/')));out.write(Files.readAllBytes(p));out.closeEntry();
    }
   }
  }
  if(changed[0]!=1)throw new IllegalStateException("Patched method count "+changed[0]);
  System.out.println("Replaced shield registration method; retained original packet handlers.");
 }
}