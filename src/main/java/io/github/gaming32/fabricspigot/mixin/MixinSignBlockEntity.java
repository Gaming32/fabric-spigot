package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.api.command.FabricBlockCommandSender;
import io.github.gaming32.fabricspigot.vanillaimpl.CommandOutputExt;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.bukkit.command.CommandSender;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignBlockEntity.class)
public class MixinSignBlockEntity implements CommandOutput, CommandOutputExt {
    @Redirect(
        method = "getCommandSource",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/command/CommandOutput;DUMMY:Lnet/minecraft/server/command/CommandOutput;",
            opcode = Opcodes.GETSTATIC
        )
    )
    private CommandOutput heyImThatNow() {
        return this;
    }

    @Override
    public void sendMessage(Text message) {
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return false;
    }

    @Override
    public boolean shouldTrackOutput() {
        return false;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        return commandSource.getEntity() != null ? commandSource.getEntity().getBukkitSender(commandSource) : new FabricBlockCommandSender(commandSource, (SignBlockEntity)(Object)this);
    }
}
