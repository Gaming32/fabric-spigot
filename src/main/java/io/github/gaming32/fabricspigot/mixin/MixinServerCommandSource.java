package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import io.github.gaming32.fabricspigot.vanillaimpl.BukkitServerCommandSource;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommandSource.class)
public class MixinServerCommandSource implements BukkitServerCommandSource {
    @Shadow @Final private CommandOutput output;

    @Override
    public CommandSender getBukkitSender() {
        throw new NotImplementedYet();
//        return output.getBukkitSender(this);
    }
}
