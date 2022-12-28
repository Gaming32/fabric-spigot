package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.api.entity.FabricEntity;
import io.github.gaming32.fabricspigot.vanillaimpl.CommandOutputExt;
import io.github.gaming32.fabricspigot.vanillaimpl.EntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class MixinEntity implements CommandOutputExt, EntityExt {
    @Shadow public World world;
    private FabricEntity fabricSpigot$bukkitEntity;

    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        return getBukkitEntity();
    }

    @Override
    public FabricEntity getBukkitEntity() {
        if (fabricSpigot$bukkitEntity == null) {
            //noinspection DataFlowIssue
            fabricSpigot$bukkitEntity = FabricEntity.getEntity(world.getServer().getBukkitServer(), (Entity)(Object)this);
        }
        return fabricSpigot$bukkitEntity;
    }
}
