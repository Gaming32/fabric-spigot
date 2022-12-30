package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.entity.passive.GolemEntity;
import org.bukkit.entity.Golem;

public class FabricGolem extends FabricCreature implements Golem {
    public FabricGolem(FabricServer server, GolemEntity entity) {
        super(server, entity);
    }

    @Override
    public GolemEntity getHandle() {
        return (GolemEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricGolem";
    }
}
