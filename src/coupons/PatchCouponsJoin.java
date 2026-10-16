import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import jdk.internal.org.objectweb.asm.*;

/** Offline transformer: preserve SQL mode; skip its login warmup in PlayerPoints mode. */
public final class PatchCouponsJoin {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("input.jar output.jar");
        final String target = "com/mchim/Coupons/Listeners/InventoryListener.class";
        int[] patched = {0};
        Path out = Paths.get(args[1]);
        if (Files.exists(out)) throw new IOException("Refusing to overwrite " + out);
        try (JarFile jar = new JarFile(args[0]);
             JarOutputStream dst = new JarOutputStream(Files.newOutputStream(out))) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry src = entries.nextElement();
                byte[] bytes;
                try (InputStream in = jar.getInputStream(src)) { bytes = in.readAllBytes(); }
                if (src.getName().equals(target)) {
                    ClassReader reader = new ClassReader(bytes);
                    ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
                    reader.accept(new ClassVisitor(Opcodes.ASM8, writer) {
                        @Override public MethodVisitor visitMethod(int access, String name,
                                String descriptor, String signature, String[] exceptions) {
                            MethodVisitor original = super.visitMethod(access, name, descriptor, signature, exceptions);
                            if (!name.equals("join") || !descriptor.equals("(Lorg/bukkit/event/player/PlayerJoinEvent;)V"))
                                return original;
                            patched[0]++;
                            return new MethodVisitor(Opcodes.ASM8, original) {
                                @Override public void visitCode() {
                                    super.visitCode();
                                    Label sqlMode = new Label();
                                    super.visitFieldInsn(Opcodes.GETSTATIC, "com/mchim/Coupons/Config/Settings", "I",
                                            "Lcom/mchim/Coupons/Config/Settings;");
                                    super.visitFieldInsn(Opcodes.GETFIELD, "com/mchim/Coupons/Config/Settings", "usePlayerPoint", "Z");
                                    super.visitJumpInsn(Opcodes.IFEQ, sqlMode);
                                    super.visitInsn(Opcodes.RETURN);
                                    super.visitLabel(sqlMode);
                                    super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                                }
                            };
                        }
                    }, 0);
                    bytes = writer.toByteArray();
                }
                JarEntry copy = new JarEntry(src.getName());
                copy.setTime(src.getTime());
                if (src.getComment() != null) copy.setComment(src.getComment());
                dst.putNextEntry(copy);
                dst.write(bytes);
                dst.closeEntry();
            }
        }
        if (patched[0] != 1) throw new IllegalStateException("Expected one join method, got " + patched[0]);
        System.out.println("Patched join methods: " + patched[0]);
    }
}
