package io.github.gaming32.fabricspigot.mixin;

import net.minecraft.server.command.HelpCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HelpCommand.class)
public class MixinHelpCommand {
    @ModifyConstant(method = "register", constant = @Constant(stringValue = "help"))
    private static String namespacedHelp(String constant) {
        if (!constant.equalsIgnoreCase("help")) return constant;
        return "fabric:help";
    }
}
