package io.github.gaming32.fabricspigot.mm;

import com.google.common.collect.Iterators;
import io.github.gaming32.fabricspigot.mm.MmMappings.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

public final class FabricSpigotEarlyRiser implements Runnable {
    @Override
    public void run() {
        PlayerManager.CLASS.registerClassTransformer(clazz -> {
            PlayerManager.ON_PLAYER_CONNECT.findMethod(clazz, method -> {
                final var it = MmMapping.findMethodInsn(method, PlayerManager.CLASS, PlayerManager.BROADCAST);
                it.remove();
                pop(it); // false
                pop(it); // mutableText.formatted(Formatting.YELLOW)
                pop(it); // this

                MmMapping.findMethodInsn(it, PlayerManager.CLASS, PlayerManager.SEND_TO_ALL);
                it.remove();
                pop(it); // PlayerListS2CPacket.entryFromPlayer(List.of(player))
                pop(it); // this

                // aload 10
                // aload 2
                // invokevirtual net/minecraft/server/world/ServerWorld.onPlayerConnected(Lnet/minecraft/server/network/ServerPlayerEntity;)V
                removeOps(it, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.INVOKEVIRTUAL);

                MmMapping.findMethodInsn(it, BossBarManager.CLASS, BossBarManager.ON_PLAYER_CONNECT);
                it.remove();
                pop(it); // player
                pop(it); // this.server.getBossBarManager()

                it.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                it.add(new VarInsnNode(Opcodes.ALOAD, 2)); // player
                it.add(new VarInsnNode(Opcodes.ALOAD, 17)); // mutableText
                it.add(new VarInsnNode(Opcodes.ALOAD, 10)); // serverWorld2
                it.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "io/github/gaming32/fabricspigot/mm/MmEvents",
                    "playerConnection",
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

    private static String descriptor(MmMapping return_, MmMapping... args) {
        final Type[] argTypes = new Type[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].type();
        }
        return Type.getMethodDescriptor(return_.type(), argTypes);
    }
}
