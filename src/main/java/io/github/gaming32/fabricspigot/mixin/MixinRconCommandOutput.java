package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.CommandOutputExt;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.rcon.RconCommandOutput;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RconCommandOutput.class)
public class MixinRconCommandOutput implements CommandOutputExt {
    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        throw new NotImplementedYet();
    }
}
