package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import io.github.gaming32.fabricspigot.vanillaimpl.CommandOutputExt;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class MixinEntity implements CommandOutputExt {
    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        throw new NotImplementedYet();
    }
}
