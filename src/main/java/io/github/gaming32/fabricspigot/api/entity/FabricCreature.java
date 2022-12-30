package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.entity.mob.PathAwareEntity;
import org.bukkit.entity.Creature;

public class FabricCreature extends FabricMob implements Creature {
    public FabricCreature(FabricServer server, PathAwareEntity entity) {
        super(server, entity);
    }

    @Override
    public PathAwareEntity getHandle() {
        return (PathAwareEntity)entity;
    }

    @Override
    public String toString() {
        return "CraftCreature";
    }
}
