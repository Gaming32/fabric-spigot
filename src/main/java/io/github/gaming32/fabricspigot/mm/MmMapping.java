package io.github.gaming32.fabricspigot.mm;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;
import java.util.Set;
import java.util.function.Consumer;

public final class MmMapping {
    private static final String ENVIRONMENT = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();

    private final String intermediary, hashed, yarn;
    private final Set<String> mappings;

    public MmMapping(String intermediary, String hashed, String yarn) {
        this.intermediary = intermediary;
        this.hashed = hashed;
        this.yarn = yarn;
        mappings = Set.of(intermediary, hashed, yarn);
    }

    public void registerClassTransformer(Consumer<ClassNode> consumer) {
        for (final String mapping : mappings) {
            ClassTinkerers.addTransformation("net/minecraft/" + mapping, consumer);
        }
    }

    public void findMethod(ClassNode clazz, Consumer<MethodNode> consumer) {
        for (final MethodNode node : clazz.methods) {
            if (matches(node.name)) {
                consumer.accept(node);
            }
        }
    }

    public boolean matchesClass(String name) {
        if (!name.startsWith("net/minecraft/")) return false;
        return matches(name.substring(14));
    }

    public boolean matches(String name) {
        return mappings.contains(name);
    }

    public Type type() {
        return Type.getObjectType("net/minecraft/" + switch (ENVIRONMENT) {
            case "intermediary" -> intermediary;
            case "hashed" -> hashed;
            case "named" -> yarn;
            default -> throw new UnsupportedOperationException("Unsupported mappings environment: " + ENVIRONMENT);
        });
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
