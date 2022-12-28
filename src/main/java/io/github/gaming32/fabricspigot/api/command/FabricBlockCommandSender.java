package io.github.gaming32.fabricspigot.api.command;

import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.jetbrains.annotations.NotNull;

public class FabricBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
    private final Spigot spigot = new Spigot() {
    };
    private final ServerCommandSource source;
    private final BlockEntity entity;

    public FabricBlockCommandSender(ServerCommandSource source, BlockEntity entity) {
        super();
        this.source = source;
        this.entity = entity;
    }

    @NotNull
    @Override
    public Block getBlock() {
        throw new NotImplementedYet();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        for (final String message : messages) {
            sendMessage(message);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of a block");
    }

    public ServerCommandSource getWrapper() {
        return source;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return spigot;
    }
}
