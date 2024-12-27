package org.sam.threelives.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.sam.threelives.ThreeLives;
import org.sam.threelives.client.ClientLivesData;

public class PlayerHudOverlay {

    private static final ResourceLocation FILLED_HEART = new ResourceLocation(ThreeLives.MODID, "textures/lives/filled_heart.png");
    private static final ResourceLocation EMPTY_HEART = new ResourceLocation(ThreeLives.MODID, "textures/lives/empty_heart.png");

    public static final IGuiOverlay HUD_HEARTS = ((gui, guiGraphics, partialTick, width, height) -> {
        int x = width / 2; // Center horizontally
        int y = height - 45; // Position above the hotbar, adjust as necessary

        int lives = ClientLivesData.getPlayerLives();

        // Set the shader and render the hearts
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Render the empty hearts (black hearts)
        RenderSystem.setShaderTexture(0, EMPTY_HEART);
        for (int i = 0; i < 3; i++) { // Always render 3 empty hearts
            guiGraphics.blit(EMPTY_HEART, x - 12 + (i * 8), y, 0, 0, 7, 7, 7, 7);
        }

        // Render the filled hearts (yellow hearts) based on client data
        RenderSystem.setShaderTexture(0, FILLED_HEART);
        for (int i = 0; i < lives; i++) { // Render the filled hearts up to the player's lives
            guiGraphics.blit(FILLED_HEART, x - 12 + (i * 8), y, 0, 0, 7, 7, 7, 7);
        }
    });
}