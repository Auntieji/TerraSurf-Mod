package net.add.terrasurf.enchantment;

import net.add.terrasurf.item.TerraSurferBoardItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class FlowEnchantment extends Enchantment {

    public FlowEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem() instanceof TerraSurferBoardItem;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        // This prevents Flow from being combined with Riptide or any Protection enchantments.
        if (pEnch == Enchantments.RIPTIDE) {
            return false;
        }
        if (pEnch instanceof net.minecraft.world.item.enchantment.ProtectionEnchantment) {
            return false;
        }
        return super.checkCompatibility(pEnch);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
