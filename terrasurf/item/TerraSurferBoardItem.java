package net.add.terrasurf.item;

import net.add.terrasurf.model.TerraSurferArmorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TerraSurferBoardItem extends ArmorItem {
    public TerraSurferBoardItem() {
        super(
                ArmorMaterials.DIAMOND,
                ArmorItem.Type.HELMET,
                new Item.Properties().stacksTo(1).durability(2031)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        // --- CHANGE: The custom model rendering logic has been re-enabled. ---
        consumer.accept(new IClientItemExtensions() {
            private TerraSurferArmorModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.model == null) {
                    ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(TerraSurferArmorModel.LAYER_LOCATION);
                    this.model = new TerraSurferArmorModel<>(root);
                }
                return this.model;
            }
        });
    }
}
