package net.add.terrasurf.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.add.terrasurf.TerraSurfMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

// --- REVAMPED ARMOR MODEL ---
// This class is now correctly set up to work as an armor model in your mod.
public class TerraSurferArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    // This layer location is now correctly named for your mod.
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TerraSurfMod.MODID, "terrasurfer_armor"), "main");
    private final ModelPart waterdroplets;

    public TerraSurferArmorModel(ModelPart root) {
        // We call super(root) because we are extending HumanoidModel.
        super(root);
        // The model part is found as a direct child of the root, as defined in createBodyLayer.
        this.waterdroplets = root.getChild("waterdroplets");
    }

    public static LayerDefinition createBodyLayer() {
        // --- FIX: Start with the default HumanoidModel mesh to prevent crashing ---
        // This ensures that parts like "head" and "body" exist, which the parent class requires.
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // NOTE: The model parts from your file have 0 dimensions, so this model will be invisible.
        // This is perfectly fine if you intend to add effects or change it later.
        PartDefinition waterdroplets = partdefinition.addOrReplaceChild("waterdroplets", CubeListBuilder.create().texOffs(4, 2).addBox(-9.0F, -5.0F, 5.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(4, 2).addBox(-9.0F, -5.0F, 5.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(4, 2).addBox(-9.0F, -5.0F, 5.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(4, 2).addBox(-9.0F, -5.0F, 5.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(4, 2).addBox(-9.0F, -5.0F, 5.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5908F, -0.1096F, 0.4232F));

        waterdroplets.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 2).addBox(-8.0F, -6.0F, 4.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // This is crucial for armor models. It makes our model parts follow the player's animations.
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // We only render our custom part.
        waterdroplets.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
