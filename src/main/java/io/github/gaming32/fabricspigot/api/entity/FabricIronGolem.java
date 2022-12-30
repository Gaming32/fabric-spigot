package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.IronGolemEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.jetbrains.annotations.NotNull;

public class FabricIronGolem extends FabricGolem implements IronGolem {
    public FabricIronGolem(FabricServer server, IronGolemEntity entity) {
        super(server, entity);
    }

    @Override
    public IronGolemEntity getHandle() {
        return (IronGolemEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricIronGolem";
    }

    @Override
    public boolean isPlayerCreated() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        throw new NotImplementedYet();
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.IRON_GOLEM;
    }
}
