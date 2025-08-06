package net.add.terrasurf.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.entity.TerraSurferEntity;
import net.add.terrasurf.model.TerraSurferModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TerraSurferEntityRenderer extends EntityRenderer<TerraSurferEntity> {

    private final TerraSurferModel<TerraSurferEntity> model;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TerraSurfMod.MODID, "textures/entity/terrasurfer.png");

    public TerraSurferEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new TerraSurferModel<>(pContext.bakeLayer(TerraSurferModel.LAYER_LOCATION));
    }

    @Override
    public void render(TerraSurferEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        pPoseStack.translate(0, 1.5F, 0);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntityYaw));

        // --- CLIMBING AND PITCH LOGIC ---
        // 1. Get the player's normal up/down view pitch.
        float pitch = pEntity.getControllingPassenger() != null ? pEntity.getControllingPassenger().getXRot() : pEntity.getXRot();

        // 2. Get the smooth, interpolated climb angle from our entity.
        float interpolatedClimbAngle = Mth.lerp(pPartialTicks, pEntity.prevClimbAngle, pEntity.climbAngle);

        // 3. Apply the pitch and the INVERTED climb angle to the model.
        // --- FIX: Changed '+' to '-' to invert the model's climbing tilt. ---
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch - interpolatedClimbAngle));
        // --- END OF FIX ---

        this.model.setupAnim(pEntity, 0, 0, pEntity.tickCount + pPartialTicks, 0, 0);
        this.model.renderToBuffer(pPoseStack, pBuffer.getBuffer(this.model.renderType(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TerraSurferEntity pEntity) {
        return TEXTURE;
    }
}
