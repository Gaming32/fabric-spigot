package io.github.gaming32.fabricspigot.util;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.Map;

public interface CommandNodeAccess {
    Map<String, CommandNode<?>> getChildren();
    Map<String, LiteralCommandNode<?>> getLiterals();
}
