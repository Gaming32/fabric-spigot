package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.WanderingTraderEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.jetbrains.annotations.NotNull;

public class FabricWanderingTrader extends FabricAbstractVillager implements WanderingTrader {
    public FabricWanderingTrader(FabricServer server, WanderingTraderEntity entity) {
        super(server, entity);
    }

    @Override
    public WanderingTraderEntity getHandle() {
        return (WanderingTraderEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricWanderingTrader";
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.WANDERING_TRADER;
    }

    @Override
    public int getDespawnDelay() {
        throw new NotImplementedYet();
    }

    @Override
    public void setDespawnDelay(int despawnDelay) {
        throw new NotImplementedYet();
    }
}
