package org.sam.threelives.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.sam.threelives.ThreeLives;
import org.sam.threelives.player.PlayerHudOverlay;

@Mod.EventBusSubscriber(modid = ThreeLives.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    // Register your custom overlay for hearts
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        // Register the hearts overlay to be rendered above all other overlays
        event.registerAboveAll("hearts_overlay", PlayerHudOverlay.HUD_HEARTS);
    }
}