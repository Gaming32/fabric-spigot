package io.github.gaming32.fabricspigot.mm;

import io.github.gaming32.fabricspigot.api.entity.FabricPlayer;
import io.github.gaming32.fabricspigot.ext.EntityExt;
import io.github.gaming32.fabricspigot.util.ChatMessageConversion;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public final class MmEvents {
    public static ServerWorld playerConnection(PlayerManager playerManager, ServerPlayerEntity player, MutableText joinText, ServerWorld destWorld) {
        String joinMessage = ChatMessageConversion.fromComponent(joinText);

        final FabricPlayer bukkitPlayer = (FabricPlayer)((EntityExt)player).getBukkitEntity();

        // TODO: transferTo

        final PlayerJoinEvent event = new PlayerJoinEvent(bukkitPlayer, joinMessage);
        bukkitPlayer.getServer().getPluginManager().callEvent(event);

        if (!player.networkHandler.connection.isOpen()) {
            return null;
        }

        joinMessage = event.getJoinMessage();

        if (joinMessage != null && joinMessage.length() > 0) {
            for (final Text line : ChatMessageConversion.fromString(joinMessage)) {
                playerManager.broadcast(line, false);
            }
        }

        final PlayerListS2CPacket packet = PlayerListS2CPacket.entryFromPlayer(List.of(player));
        for (final ServerPlayerEntity otherPlayer : playerManager.getPlayerList()) {
            if (((FabricPlayer)((EntityExt)otherPlayer).getBukkitEntity()).canSee(bukkitPlayer)) {
                otherPlayer.networkHandler.sendPacket(packet);
            }

            if (!bukkitPlayer.canSee((FabricPlayer)((EntityExt)otherPlayer).getBukkitEntity())) {
                continue;
            }

            player.networkHandler.sendPacket(PlayerListS2CPacket.entryFromPlayer(List.of(otherPlayer)));
        }
        // player.sentListPacket = true;

//        player.getDataTracker().refresh(player);

        if (player.world == destWorld && !destWorld.getPlayers().contains(player)) {
            destWorld.onPlayerConnected(player);
            playerManager.getServer().getBossBarManager().onPlayerConnect(player);
        }

        return player.getWorld();
    }

    public static Text playerDeath(Text defaultMessage, ServerPlayerEntity player, boolean showDeathMessage) {
        // TODO: Do more than just the death message
        final String stringMessage = ChatMessageConversion.fromComponent(defaultMessage);

        final PlayerDeathEvent event = new PlayerDeathEvent(
            (Player)((EntityExt)player).getBukkitEntity(),
            new ArrayList<>(),
            0, 0,
            stringMessage
        );
        //noinspection DataFlowIssue
        player.getServer().getBukkitServer().getPluginManager().callEvent(event);

        final String newMessage = event.getDeathMessage();

        if (newMessage != null && newMessage.length() > 0 && showDeathMessage) {
            if (newMessage.equals(stringMessage)) {
                return defaultMessage;
            } else {
                return ChatMessageConversion.fromStringOrNull(newMessage);
            }
        }
        return null;
    }
}
