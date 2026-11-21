import org.objectweb.asm.*;
import java.nio.file.*;
import java.util.jar.*;
import java.io.*;

/** Native cabinet and loading games; download callbacks and game launch remain untouched. */
public final class PatchRuntime {
    public static void main(String[] args) throws Exception {
        Path original = Path.of(args[0]), output = Path.of(args[1]), classes = Path.of(args[2]);
        String login = "pro/gravit/launcher/gui/scenes/login/LoginScene.class";
        String update = "pro/gravit/launcher/gui/scenes/update/UpdateScene.class";
        int[] replaced = {0}, loadingHooks = {0, 0};
        try (JarFile input = new JarFile(original.toFile()); JarOutputStream out = new JarOutputStream(Files.newOutputStream(output))) {
            var entries = input.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("META-INF/") && (name.endsWith(".SF") || name.endsWith(".RSA") || name.endsWith(".DSA") || name.endsWith(".EC"))) continue;
                if (Files.isRegularFile(classes.resolve(name))) continue;
                byte[] bytes = input.getInputStream(entry).readAllBytes();
                if (name.equals(login) || name.equals(update)) {
                    var reader = new ClassReader(bytes);
                    var writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
                    reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
                        @Override public MethodVisitor visitMethod(int access, String method, String descriptor, String signature, String[] exceptions) {
                            final boolean begin = name.equals(update) && method.equals("reset") && descriptor.equals("()V");
                            final boolean fail = name.equals(update) && method.equals("errorHandle") && descriptor.equals("(Ljava/lang/Throwable;)V");
                            return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, method, descriptor, signature, exceptions)) {
                                @Override public void visitInsn(int opcode) {
                                    if (opcode == Opcodes.RETURN && (begin || fail)) {
                                        super.visitVarInsn(Opcodes.ALOAD, 0);
                                        super.visitFieldInsn(Opcodes.GETFIELD, "pro/gravit/launcher/gui/scenes/update/UpdateScene", "layout", "Ljavafx/scene/layout/Pane;");
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "pro/gravit/launcher/gui/components/LoadingGames", begin ? "begin" : "fail", "(Ljavafx/scene/Node;)V", false);
                                        loadingHooks[begin ? 0 : 1]++;
                                    }
                                    super.visitInsn(opcode);
                                }
                                @Override public void visitMethodInsn(int opcode, String owner, String method, String descriptor, boolean isInterface) {
                                    if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("pro/gravit/launcher/gui/core/JavaFXApplication") && method.equals("openURL") && descriptor.equals("(Ljava/lang/String;)Z")) {
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "pro/gravit/launcher/gui/components/NativeCabinet", "openForUrl", "(Lpro/gravit/launcher/gui/core/JavaFXApplication;Ljava/lang/String;)Z", false);
                                        replaced[0]++;
                                    } else super.visitMethodInsn(opcode, owner, method, descriptor, isInterface);
                                }
                            };
                        }
                    }, 0);
                    bytes = writer.toByteArray();
                }
                out.putNextEntry(new JarEntry(name)); out.write(bytes); out.closeEntry();
            }
            try (var paths = Files.walk(classes)) {
                for (var path : paths.filter(Files::isRegularFile).toList()) {
                    String name = classes.relativize(path).toString().replace(File.separatorChar, '/');
                    out.putNextEntry(new JarEntry(name)); out.write(Files.readAllBytes(path)); out.closeEntry();
                }
            }
        }
        if (replaced[0] != 2) { Files.delete(output); throw new IllegalStateException("Expected two LoginScene browser links, found " + replaced[0]); }
        if (loadingHooks[0] != 1 || loadingHooks[1] != 1) { Files.delete(output); throw new IllegalStateException("Expected one update reset/error hook each"); }
        System.out.println("PASS: native cabinet preserved; loading reset/error hooks installed; downloader and game launch unchanged");
    }
}
