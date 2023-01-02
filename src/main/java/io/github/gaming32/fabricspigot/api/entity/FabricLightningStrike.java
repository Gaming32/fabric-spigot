package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.entity.LightningEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.jetbrains.annotations.NotNull;

public class FabricLightningStrike extends FabricEntity implements LightningStrike {
    private final LightningStrike.Spigot spigot = new LightningStrike.Spigot() {
    };

    public FabricLightningStrike(FabricServer server, LightningEntity entity) {
        super(server, entity);
    }

    @Override
    public boolean isEffect() {
        return getHandle().cosmetic;
    }

    @Override
    public LightningEntity getHandle() {
        return (LightningEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricLightningStrike";
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.LIGHTNING;
    }

    @NotNull
    @Override
    public LightningStrike.Spigot spigot() {
        return spigot;
    }
}
