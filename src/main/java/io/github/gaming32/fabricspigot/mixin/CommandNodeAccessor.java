package io.github.gaming32.fabricspigot.mixin;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.gaming32.fabricspigot.util.CommandNodeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CommandNode.class)
public abstract class CommandNodeAccessor implements CommandNodeAccess {
    @Accessor(remap = false)
    public abstract Map<String, CommandNode<?>> getChildren();

    @Accessor(remap = false)
    public abstract Map<String, LiteralCommandNode<?>> getLiterals();
}
