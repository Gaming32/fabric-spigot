package io.github.gaming32.fabricspigot.mm;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;
import java.util.function.Consumer;

public final class MmMapping {
    public static final String ENVIRONMENT = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();

    private final String namespaced;

    public MmMapping(String intermediary, String hashed, String yarn) {
        namespaced = switch (ENVIRONMENT) {
            case "intermediary" -> intermediary;
            case "hashed" -> hashed;
            case "named" -> yarn;
            default -> throw new UnsupportedOperationException("Unsupported mappings environment: " + ENVIRONMENT);
        };
    }

    public void registerClassTransformer(Consumer<ClassNode> consumer) {
        ClassTinkerers.addTransformation("net/minecraft/" + namespaced, consumer);
    }

    public void findMethod(ClassNode clazz, Consumer<MethodNode> consumer) {
        for (final MethodNode node : clazz.methods) {
            if (node.name.equals(namespaced)) {
                consumer.accept(node);
            }
        }
    }

    public boolean matchesClass(String name) {
        if (!name.startsWith("net/minecraft/")) return false;
        return matches(name.substring(14));
    }

    public boolean matches(String name) {
        return name.equals(namespaced);
    }

    public Type type() {
        return Type.getObjectType("net/minecraft/" + namespaced);
    }

    public static ListIterator<AbstractInsnNode> findMethodInsn(ListIterator<AbstractInsnNode> it, MmMapping owner, MmMapping method) {
        while (it.hasNext()) {
            final AbstractInsnNode node = it.next();
            if (!(node instanceof MethodInsnNode methodInsn)) continue;
            if (owner.matchesClass(methodInsn.owner) && method.matches(methodInsn.name)) break;
        }
        return it;
    }

    public static ListIterator<AbstractInsnNode> findMethodInsn(MethodNode methodNode, MmMapping owner, MmMapping method) {
        return findMethodInsn(methodNode.instructions.iterator(), owner, method);
    }
}
