package net.add.terrasurf.client.renderer.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.item.TerraSurferBoardItem;
import net.add.terrasurf.model.TerraSurferArmorModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TerraSurferArmorLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final TerraSurferArmorModel<T> model;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TerraSurfMod.MODID, "textures/entity/terrasurfer_armor.png");

    public TerraSurferArmorLayer(RenderLayerParent<T, M> pRenderer, TerraSurferArmorModel<T> pModel) {
        super(pRenderer);
        this.model = pModel;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        // --- CHANGE: Check the HEAD slot ---
        ItemStack itemstack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (itemstack.getItem() instanceof TerraSurferBoardItem) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.armorCutoutNoCull(TEXTURE));
            this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, 0, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
