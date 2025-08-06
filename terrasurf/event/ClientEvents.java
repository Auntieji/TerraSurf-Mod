package net.add.terrasurf.event;

import com.mojang.math.Axis;
import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.client.hud.EnergyBarHud;
import net.add.terrasurf.entity.TerraSurferEntity;
import net.add.terrasurf.network.Messages;
import net.add.terrasurf.network.PacketActivateBoard;
import net.add.terrasurf.network.PacketDoJump;
import net.add.terrasurf.sounds.LoopingTerraSurferSound;
import net.add.terrasurf.sounds.ModSounds;
import net.add.terrasurf.util.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerraSurfMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    private static LoopingTerraSurferSound currentLoopingSound;
    private static SoundEvent currentSoundEvent;
    private static int flashTicks = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (KeyBinds.ACTIVATE_BOARD_KEY.consumeClick()) {
                    Messages.sendToServer(new PacketActivateBoard());
                }
                if (mc.player.getVehicle() instanceof TerraSurferEntity && mc.options.keyJump.consumeClick()) {
                    Messages.sendToServer(new PacketDoJump());
                }
                handleLoopingSounds(mc);
            }
        }
    }

    private static void handleLoopingSounds(Minecraft mc) {
        if (mc.player.getVehicle() instanceof TerraSurferEntity board) {
            SoundEvent desiredSound = getDesiredLoopingSound(board);

            if (desiredSound != currentSoundEvent) {
                stopCurrentLoopingSound();
                if (desiredSound != null) {
                    currentLoopingSound = new LoopingTerraSurferSound(mc.player, board, desiredSound);
                    mc.getSoundManager().play(currentLoopingSound);
                    currentSoundEvent = desiredSound;
                }
            }
        } else {
            stopCurrentLoopingSound();
        }
    }

    private static SoundEvent getDesiredLoopingSound(TerraSurferEntity board) {
        if (!board.isVehicle() || board.getDeltaMovement().horizontalDistanceSqr() < 0.01D) {
            return null;
        }
        if (board.isClimbing()) return ModSounds.BOARD_LOOP_CLIMBING.get();
        if (board.isInLava()) return ModSounds.BOARD_LOOP_LAVA.get();
        if (board.isInWater()) return ModSounds.BOARD_LOOP_WATER.get();
        if (board.onGround()) {
            if (board.isMovingFast()) {
                return ModSounds.BOARD_LAND_FAST.get();
            } else {
                return ModSounds.BOARD_LAND_SLOW.get();
            }
        }
        return null;
    }

    private static void stopCurrentLoopingSound() {
        if (currentLoopingSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentLoopingSound);
            currentLoopingSound = null;
            currentSoundEvent = null;
        }
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiOverlayEvent.Pre event) {
        // --- FIX: Moved screenWidth and screenHeight outside the 'if' block to widen their scope ---
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        if (flashTicks > 0 && event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            float alphaFraction = (float) flashTicks / 15.0F;
            int alpha = (int) (Mth.clamp(alphaFraction, 0.0F, 1.0F) * 180);
            int color = (alpha << 24) | 0x88CCFF;
            event.getGuiGraphics().fill(0, 0, screenWidth, screenHeight, color);
            flashTicks--;
        }

        if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getVehicle() instanceof TerraSurferEntity board) {
                boolean isFast = board.isMovingFast();
                EnergyBarHud.render(event.getGuiGraphics(), event.getPartialTick(),
                        screenWidth, screenHeight,
                        board.getEnergy(), 120, isFast);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRender(RenderLivingEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getVehicle() instanceof TerraSurferEntity board) {
                float interpolatedClimbAngle = Mth.lerp(event.getPartialTick(), board.prevClimbAngle, board.climbAngle);
                if (interpolatedClimbAngle > 0.0F) {
                    event.getPoseStack().mulPose(Axis.XP.rotationDegrees(-interpolatedClimbAngle));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.getVehicle() instanceof TerraSurferEntity) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof TerraSurferEntity) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof InventoryScreen) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getVehicle() instanceof TerraSurferEntity) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        // --- FIX: Replaced event.isClient() with a check on the entity's level ---
        if (event.getEntity().level().isClientSide() && event.getEntityMounting() == Minecraft.getInstance().player) {
            if (event.getEntityBeingMounted() instanceof TerraSurferEntity) {
                flashTicks = 15;
            }
        }
    }

    private static boolean wasRidingBoardLastTick = false;
    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        // --- FIX: Replaced event.side == Dist.CLIENT with event.side.isClient() ---
        if (event.phase == TickEvent.Phase.END && event.side.isClient()) {
            boolean isRidingBoardThisTick = event.player.getVehicle() instanceof TerraSurferEntity;

            if (wasRidingBoardLastTick && !isRidingBoardThisTick) {
                flashTicks = 15;
            }

            wasRidingBoardLastTick = isRidingBoardThisTick;
        }
    }
}
