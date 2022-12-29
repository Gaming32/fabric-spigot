package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.CommandOutputExt;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandBlockMinecartEntity.CommandExecutor.class)
public class MixinCommandBlockMinecartEntity_CommandExecutor implements CommandOutputExt {
    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        throw new NotImplementedYet();
    }
}
