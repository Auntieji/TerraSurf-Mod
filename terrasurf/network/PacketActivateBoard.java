package net.add.terrasurf.network;

import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.entity.TerraSurferEntity;
import net.minecraft.ChatFormatting; // NEW IMPORT for text formatting
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketActivateBoard {

    public PacketActivateBoard() {
    }

    public PacketActivateBoard(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.serverLevel();

            ItemStack boardStack = player.getItemBySlot(EquipmentSlot.HEAD);
            boolean hasBoardEquipped = boardStack.is(TerraSurfMod.TERRASURFERBOARD.get());

            if (!hasBoardEquipped || player.isPassenger()) {
                return;
            }

            if (!player.onGround()) {
                player.sendSystemMessage(Component.literal("The currents are unstable here. Find solid ground.").withStyle(ChatFormatting.AQUA));
                return;
            }

            if (player.getCooldowns().isOnCooldown(boardStack.getItem())) {
                float remainingPercent = player.getCooldowns().getCooldownPercent(boardStack.getItem(), 0f);
                int totalCooldownTicks = 300; // The cooldown is now always 15 seconds (300 ticks)
                int secondsRemaining = (int) (remainingPercent * totalCooldownTicks / 20f) + 1;

                // --- NEW COOL MESSAGE ---
                player.sendSystemMessage(Component.literal("Your form is unstable... Reforming in ").withStyle(ChatFormatting.DARK_PURPLE)
                        .append(Component.literal(secondsRemaining + "s").withStyle(ChatFormatting.WHITE)));
                return;
            }

            TerraSurferEntity boardEntity = new TerraSurferEntity(TerraSurfMod.TERRASURFER_ENTITY.get(), level);
            boardEntity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            level.addFreshEntity(boardEntity);
            player.startRiding(boardEntity);
        });
        return true;
    }
}
