package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.vanillaimpl.CommandOutputExt;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandOutput.class)
public interface MixinCommandOutput extends CommandOutputExt {
    @Mixin(targets = "net.minecraft.server.command.CommandOutput$1")
    class Mixin_1 implements CommandOutputExt {
        @Override
        public CommandSender getBukkitSender(ServerCommandSource commandSource) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
