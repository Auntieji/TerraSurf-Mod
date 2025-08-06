package net.add.terrasurf.event;

import com.mojang.brigadier.CommandDispatcher;
import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.entity.TerraSurferEntity;
import net.add.terrasurf.item.TerraSurferBoardItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerraSurfMod.MODID)
public class ModEvents {

    // --- NEW: Cheat code flag ---
    // This boolean controls whether the anti-dupe logic is active. Defaults to true.
    public static boolean antiDupeEnabled = true;

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getVehicle() instanceof TerraSurferEntity) {
                ItemStack board = player.getItemBySlot(EquipmentSlot.HEAD);
                if (EnchantmentHelper.getItemEnchantmentLevel(TerraSurfMod.FLOW.get(), board) > 0) {
                    event.setCanceled(true);
                } else {
                    event.setDamageMultiplier(0.3f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getVehicle() instanceof TerraSurferEntity) {
                ItemStack board = player.getItemBySlot(EquipmentSlot.HEAD);
                boolean hasFlow = EnchantmentHelper.getItemEnchantmentLevel(TerraSurfMod.FLOW.get(), board) > 0;

                if (event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
                    event.setCanceled(true);
                }

                if (hasFlow && event.getSource().is(DamageTypes.IN_WALL)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // --- CHANGE: The anti-dupe logic is now wrapped in this check ---
        if (!antiDupeEnabled) {
            return; // If the cheat is active, skip the rest of the method.
        }

        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            boolean foundFirstBoard = false;

            for (int i = player.getInventory().getContainerSize() - 1; i >= 0; i--) {
                ItemStack currentStack = player.getInventory().getItem(i);
                if (currentStack.getItem() instanceof TerraSurferBoardItem) {
                    if (!foundFirstBoard) {
                        foundFirstBoard = true;
                    } else {
                        ItemStack duplicateBoard = currentStack.copy();
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                        player.drop(duplicateBoard, false);
                    }
                }
            }
        }
    }

    // --- NEW: Command Registration ---
    // This event handler registers our new command with the game.
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("terrasurf")
                .then(Commands.literal("toggledupe")
                        .requires(source -> source.hasPermission(2)) // Requires op-level 2 (cheats enabled)
                        .executes(context -> {
                            // Toggle the flag
                            antiDupeEnabled = !antiDupeEnabled;
                            // Send feedback to the player who ran the command
                            String status = antiDupeEnabled ? "ENABLED" : "DISABLED";
                            context.getSource().sendSuccess(() -> Component.literal("TerraSurf anti-dupe logic is now " + status), true);
                            return 1; // Return 1 to indicate success
                        })
                )
        );
    }
}
