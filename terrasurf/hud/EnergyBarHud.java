package net.add.terrasurf.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.add.terrasurf.TerraSurfMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class EnergyBarHud {
    private static final ResourceLocation ENERGY_BAR_BACKGROUND = new ResourceLocation(TerraSurfMod.MODID, "textures/gui/energy_bar_background.png");
    private static final ResourceLocation ENERGY_BAR_FOREGROUND = new ResourceLocation(TerraSurfMod.MODID, "textures/gui/energy_bar_foreground.png");
    // --- NEW: Resource location for the speed effect ---
    private static final ResourceLocation SPEED_EFFECT = new ResourceLocation(TerraSurfMod.MODID, "textures/gui/speed_effect.png");

    // --- UPDATED: Method now accepts a boolean for the speed effect ---
    public static void render(GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight, int energy, int maxEnergy, boolean showSpeedEffect) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // --- 1. Draw the Energy Bar (same as before) ---
        int bgWidth = 170;
        int bgHeight = 12;
        int x = (screenWidth / 2) - (bgWidth / 2);
        int y = 15;

        guiGraphics.blit(ENERGY_BAR_BACKGROUND, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);

        int fgWidth = 165;
        int fgHeight = 9;
        int fgX = x + 2;
        int fgY = y + 1;

        int filledWidth = (int)(((float)energy / maxEnergy) * fgWidth);

        if (filledWidth > 0) {
            guiGraphics.blit(ENERGY_BAR_FOREGROUND, fgX, fgY, 0, 0, filledWidth, fgHeight, fgWidth, fgHeight);
        }

        // --- 2. NEW: Draw the Speed Effect if needed ---
        if (showSpeedEffect) {
            // Make the effect slightly transparent
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.6F); // 60% opacity

            // Draw the texture covering the whole screen
            guiGraphics.blit(SPEED_EFFECT, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

            // Reset color and blending for other HUD elements
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
        }
    }
}
