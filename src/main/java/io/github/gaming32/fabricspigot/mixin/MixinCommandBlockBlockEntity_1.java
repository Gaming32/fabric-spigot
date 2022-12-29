package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.api.command.FabricBlockCommandSender;
import io.github.gaming32.fabricspigot.ext.CommandOutputExt;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.block.entity.CommandBlockBlockEntity$1")
public class MixinCommandBlockBlockEntity_1 implements CommandOutputExt {
    @Shadow @Final CommandBlockBlockEntity field_11921;

    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        return new FabricBlockCommandSender(commandSource, field_11921);
    }
}
