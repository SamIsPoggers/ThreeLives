package org.sam.threelives.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.sam.threelives.Config;
import org.sam.threelives.packets.LivesDataSyncS2CPacket;
import org.sam.threelives.packets.ModMessages;

import java.util.UUID;

public class PlayerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerUUID = player.getUUID();
            Config.PLAYER_LIVES.putIfAbsent(playerUUID, 3);  // Default to 3 lives
            Config.saveConfig();
            sendLivesToClient(player);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSource().getEntity() instanceof ServerPlayer) {
                handlePlayerDeath(player);
            }
        }
    }

    public void handlePlayerDeath(ServerPlayer player) {

        GameProfile playerProfile = player.getGameProfile();
        PlayerList playerList = player.server.getPlayerList();
        UserBanList banList = playerList.getBans();

        UUID playerUUID = player.getUUID();
        int livesRemaining = Config.PLAYER_LIVES.getOrDefault(playerUUID, 3);

        if (livesRemaining > 1) {
            Config.PLAYER_LIVES.put(playerUUID, livesRemaining - 1);
        } else {
            Config.PLAYER_LIVES.put(playerUUID, 0);
            if (isMultiplayerServer()) {

                player.connection.disconnect(Component.literal("You have been banned for losing all lives."));

                UserBanListEntry banEntry = new UserBanListEntry(playerProfile, null, "Server", null, "You have been banned for losing all lives.");
                banList.add(banEntry);

            } else {
                player.setGameMode(GameType.SPECTATOR);
            }
        }

        sendLivesToClient(player);  // Update the client with the new lives
        Config.saveConfig();
    }

    private void sendLivesToClient(ServerPlayer player) {
        int livesRemaining = Config.PLAYER_LIVES.getOrDefault(player.getUUID(), 3);
        LivesDataSyncS2CPacket packet = new LivesDataSyncS2CPacket(livesRemaining);
        // Send the packet to the player
        ModMessages.sendToPlayer(packet, player); // Use public method
    }

    private boolean isMultiplayerServer() {
        return net.minecraftforge.fml.loading.FMLEnvironment.dist.isDedicatedServer();
    }
}