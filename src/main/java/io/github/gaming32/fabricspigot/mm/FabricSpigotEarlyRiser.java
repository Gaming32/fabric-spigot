package io.github.gaming32.fabricspigot.mm;

import com.google.common.collect.Iterators;
import io.github.gaming32.fabricspigot.mm.MmMappings.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

public final class FabricSpigotEarlyRiser implements Runnable {
    private static final String MM_EVENTS = "io/github/gaming32/fabricspigot/mm/MmEvents";

    @Override
    public void run() {
        PlayerManager.CLASS.registerClassTransformer(clazz -> {
            PlayerManager.onPlayerConnect.findMethod(clazz, method -> {
                final var it = method.instructions.iterator();

                MmMapping.findMethodInsn(it, PlayerManager.CLASS, PlayerManager.broadcast);
                it.remove();
                pop(it); // false
                pop(it); // mutableText.formatted(Formatting.YELLOW)
                pop(it); // this

                MmMapping.findMethodInsn(it, PlayerManager.CLASS, PlayerManager.sendToAll);
                it.remove();
                pop(it); // PlayerListS2CPacket.entryFromPlayer(List.of(player))
                pop(it); // this

                // aload 10
                // aload 2
                // invokevirtual net/minecraft/server/world/ServerWorld.onPlayerConnected(Lnet/minecraft/server/network/ServerPlayerEntity;)V
                removeOps(it, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.INVOKEVIRTUAL);

                MmMapping.findMethodInsn(it, BossBarManager.CLASS, BossBarManager.onPlayerConnect);
                it.remove();
                pop(it); // player
                pop(it); // this.server.getBossBarManager()

                it.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                it.add(new VarInsnNode(Opcodes.ALOAD, 2)); // player
                it.add(new VarInsnNode(Opcodes.ALOAD, 17)); // mutableText
                it.add(new VarInsnNode(Opcodes.ALOAD, 10)); // serverWorld2
                it.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC, MM_EVENTS, "playerConnection",
                    descriptor(ServerWorld.CLASS, PlayerManager.CLASS, ServerPlayerEntity.CLASS, MutableText.CLASS, ServerWorld.CLASS)
                ));
                it.add(new InsnNode(Opcodes.DUP));
                final LabelNode nonNullLabel = new LabelNode();
                it.add(new JumpInsnNode(Opcodes.IFNONNULL, nonNullLabel));
                it.add(new InsnNode(Opcodes.RETURN));
                it.add(nonNullLabel);
                it.add(new VarInsnNode(Opcodes.ASTORE, 10)); // serverWorld2
            });
        });
        ServerPlayerEntity.CLASS.registerClassTransformer(clazz -> {
            LivingEntity.onDeath.findMethod(clazz, method -> {
                final var it = method.instructions.iterator();

                findOp(it, Opcodes.IFEQ);
                final LabelNode target = ((JumpInsnNode)it.previous()).label;
                it.remove();
                it.add(new InsnNode(Opcodes.POP));

                MmMapping.findMethodInsn(it, DamageTracker.CLASS, DamageTracker.getDeathMessage);
                removeOps(it, Opcodes.ASTORE);

                it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                it.add(new VarInsnNode(Opcodes.ILOAD, 2));
                it.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC, MM_EVENTS, "playerDeath",
                    descriptor(Text.CLASS, Text.CLASS, ServerPlayerEntity.CLASS, "Z")
                ));
                it.add(new InsnNode(Opcodes.DUP));
                it.add(new JumpInsnNode(Opcodes.IFNULL, target));
                it.add(new VarInsnNode(Opcodes.ASTORE, 3));

                findNode(it, target);
                it.add(new InsnNode(Opcodes.POP));
            });
        });
    }

    private static void removeOps(ListIterator<AbstractInsnNode> it, int... ops) {
        final var realOps = realOps(it);
        for (final int op : ops) {
            if (!realOps.hasNext()) break;
            if (realOps.next().getOpcode() != op) break;
            it.remove();
        }
    }

    private static Iterator<AbstractInsnNode> realOps(Iterator<AbstractInsnNode> it) {
        return Iterators.filter(it, node -> !(node instanceof LabelNode) && !(node instanceof LineNumberNode));
    }

    private static void pop(ListIterator<AbstractInsnNode> it) {
        it.add(new InsnNode(Opcodes.POP));
    }

    private static String descriptor(Object return_, Object... args) {
        final Type[] argTypes = new Type[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = convertType(args[i]);
        }
        return Type.getMethodDescriptor(convertType(return_), argTypes);
    }

    private static Type convertType(Object type) {
        if (type instanceof Type asm) {
            return asm;
        } else if (type instanceof String descriptor) {
            return Type.getType(descriptor);
        } else if (type instanceof MmMapping mapping) {
            return mapping.type();
        } else {
            throw new IllegalArgumentException("Unsupported type class: " + type.getClass().getName());
        }
    }

    private static void findOp(ListIterator<AbstractInsnNode> it, int op) {
        while (it.hasNext()) {
            if (it.next().getOpcode() == op) break;
        }
    }

    private static void findNode(ListIterator<AbstractInsnNode> it, AbstractInsnNode node) {
        while (it.hasNext()) {
            if (it.next() == node) break;
        }
    }
}
