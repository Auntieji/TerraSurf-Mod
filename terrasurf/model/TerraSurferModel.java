package net.add.terrasurf.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.entity.TerraSurferEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TerraSurferModel<T extends TerraSurferEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TerraSurfMod.MODID, "terrasurfer"), "main");

    private final ModelPart root;
    private final ModelPart TerraSurfer;
    private final ModelPart water2;
    private final ModelPart jaw;
    private final ModelPart tail;

    public TerraSurferModel(ModelPart root) {
        this.root = root;
        this.TerraSurfer = root.getChild("TerraSurfer");
        this.water2 = root.getChild("water2");
        this.jaw = this.TerraSurfer.getChild("jaw");
        this.tail = this.TerraSurfer.getChild("tail");
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        animateIdle(ageInTicks);

        switch (entity.getAnimationState()) {
            case JUMP -> animateJump(entity);
            case FALL -> animateFall(entity);
            case ATTACK -> animateAttack(entity);
        }
    }

    private void animateIdle(float ageInTicks) {
        this.TerraSurfer.y += Mth.sin(ageInTicks * 0.1F) * 0.5F;
        this.jaw.xRot = Mth.sin(ageInTicks * 0.08F) * 0.05F;
    }

    private void animateJump(T entity) {
        float progress = (float)(10 - entity.jumpAnimationTicks) / 10.0F;
        // --- FIX: Replaced Mth.toRadians with manual calculation ---
        this.TerraSurfer.xRot += Mth.sin(progress * Mth.PI) * (-30.0F * Mth.PI / 180.0F);
    }

    private void animateFall(T entity) {
        // --- FIX: Replaced Mth.toRadians with manual calculation ---
        this.TerraSurfer.xRot += (15.0F * Mth.PI / 180.0F);
    }

    private void animateAttack(T entity) {
        float progress = (float)(15 - entity.attackAnimationTicks) / 15.0F;
        this.TerraSurfer.xRot += Mth.lerp(progress, 0, Mth.PI * 2);
        // --- FIX: Replaced Mth.toRadians with manual calculation ---
        this.jaw.xRot += Mth.lerp(progress, 0, (45F * Mth.PI / 180.0F));
    }

    public static LayerDefinition createBodyLayer() {
        // ... (Your massive createBodyLayer method is unchanged)
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition TerraSurfer = partdefinition.addOrReplaceChild("TerraSurfer", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -2.0F));

        PartDefinition jaw = TerraSurfer.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -6.25F));

        jaw.addOrReplaceChild("toungelayer", CubeListBuilder.create().texOffs(0, 58).addBox(2.0F, -2.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0002F))
                .texOffs(0, 39).addBox(1.0F, -2.0F, -9.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0002F))
                .texOffs(0, 29).addBox(-1.0F, -2.0F, -10.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0002F))
                .texOffs(16, 36).addBox(-2.0F, -2.0F, -9.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0002F))
                .texOffs(12, 52).addBox(-2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0002F))
                .texOffs(20, 52).addBox(2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0002F))
                .texOffs(38, 59).addBox(-3.0F, -2.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0002F)), PartPose.offset(0.0F, 1.0F, 6.25F));

        jaw.addOrReplaceChild("jawcurvings", CubeListBuilder.create().texOffs(0, 62).addBox(2.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0001F))
                .texOffs(46, 59).addBox(2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0001F))
                .texOffs(46, 6).addBox(1.0F, -2.0F, -9.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0001F))
                .texOffs(32, 29).addBox(-1.0F, -2.0F, -10.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0001F))
                .texOffs(46, 28).addBox(-2.0F, -2.0F, -9.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0001F))
                .texOffs(60, 42).addBox(-2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0001F))
                .texOffs(62, 12).addBox(-3.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0001F)), PartPose.offset(0.0F, 1.5F, 7.25F));

        jaw.addOrReplaceChild("jawbase", CubeListBuilder.create().texOffs(32, 14).addBox(2.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(22, 48).addBox(2.0F, -2.0F, -6.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 15).addBox(1.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(34, 47).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(62, 18).addBox(-2.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 50).addBox(-2.0F, -2.0F, -6.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 14).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 7.25F));

        PartDefinition teeth = jaw.addOrReplaceChild("teeth", CubeListBuilder.create().texOffs(44, 47).addBox(-3.5F, -2.75F, -11.25F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 1.5F, 10.5F));

        PartDefinition teeth1 = teeth.addOrReplaceChild("teeth1", CubeListBuilder.create().texOffs(12, 39).addBox(-3.0F, -3.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        teeth1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(58, 12).addBox(-3.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(0.0F, -3.9238F, -15.7739F, -0.48F, 0.0F, 0.0F));

        PartDefinition teeth2 = teeth.addOrReplaceChild("teeth2", CubeListBuilder.create().texOffs(60, 4).addBox(-3.0F, -3.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offset(1.25F, 0.0F, 1.5F));

        teeth2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 60).addBox(-3.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(0.0F, -3.9238F, -15.7739F, -0.48F, 0.0F, 0.0F));

        PartDefinition teeth3 = teeth.addOrReplaceChild("teeth3", CubeListBuilder.create().texOffs(62, 49).addBox(-3.0F, -3.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offset(-1.25F, 0.0F, 1.5F));

        teeth3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(36, 63).addBox(-3.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(0.0F, -3.9238F, -15.7739F, -0.48F, 0.0F, 0.0F));

        PartDefinition head = TerraSurfer.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.6F, -6.0F, 0.0F, 0.0F, 3.1416F));

        head.addOrReplaceChild("headmouth", CubeListBuilder.create().texOffs(60, 0).addBox(2.0F, -2.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0002F))
                .texOffs(28, 41).addBox(1.0F, -2.0F, -9.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0002F))
                .texOffs(16, 29).addBox(-1.0F, -2.0F, -10.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0002F))
                .texOffs(40, 41).addBox(-2.0F, -2.0F, -9.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0002F))
                .texOffs(52, 41).addBox(-2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0002F))
                .texOffs(0, 53).addBox(2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0002F))
                .texOffs(8, 60).addBox(-3.0F, -2.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0002F)), PartPose.offset(0.0F, 1.0F, 6.0F));

        head.addOrReplaceChild("headcurving2", CubeListBuilder.create().texOffs(62, 21).addBox(2.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0001F))
                .texOffs(52, 60).addBox(2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0001F))
                .texOffs(46, 33).addBox(1.0F, -2.0F, -9.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0001F))
                .texOffs(32, 35).addBox(-1.0F, -2.0F, -10.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0001F))
                .texOffs(24, 47).addBox(-2.0F, -2.0F, -9.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0001F))
                .texOffs(58, 60).addBox(-2.0F, -2.0F, -8.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0001F))
                .texOffs(24, 62).addBox(-3.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0001F)), PartPose.offset(0.0F, 1.5F, 7.0F));

        head.addOrReplaceChild("headcurving3", CubeListBuilder.create().texOffs(36, 14).addBox(2.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(56, 9).addBox(2.0F, -2.0F, -6.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 62).addBox(0.9F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 48).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(62, 46).addBox(-2.0F, -2.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 36).addBox(-2.0F, -2.0F, -6.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 14).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 7.0F));

        head.addOrReplaceChild("headcurving4", CubeListBuilder.create().texOffs(40, 14).addBox(2.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(2.0F, -2.0F, -5.0F, 0.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 41).addBox(1.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(28, 52).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 41).addBox(-2.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -2.0F, -5.0F, 0.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(42, 14).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.5F, 7.0F));

        PartDefinition teeth4 = head.addOrReplaceChild("teeth4", CubeListBuilder.create(), PartPose.offset(2.5F, 1.5F, 10.25F));

        PartDefinition teeth6 = teeth4.addOrReplaceChild("teeth6", CubeListBuilder.create().texOffs(40, 63).addBox(-3.0F, -3.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offset(1.25F, 0.0F, 3.5F));

        teeth6.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(44, 63).addBox(-3.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(0.0F, -3.9238F, -15.7739F, -0.48F, 0.0F, 0.0F));

        PartDefinition horn = teeth4.addOrReplaceChild("horn", CubeListBuilder.create().texOffs(8, 53).addBox(-0.25F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(-2.25F, -1.75F, -9.0F, 0.1747F, -0.043F, 3.134F));

        horn.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(20, 63).addBox(-3.0F, -3.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(2.75F, -3.4238F, -2.2739F, -0.48F, 0.0F, 0.0F));

        PartDefinition teeth7 = teeth4.addOrReplaceChild("teeth7", CubeListBuilder.create().texOffs(48, 63).addBox(-3.0F, -3.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offset(-1.25F, 0.0F, 3.5F));

        teeth7.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(64, 4).addBox(-3.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0002F)), PartPose.offsetAndRotation(0.0F, -3.9238F, -15.7739F, -0.48F, 0.0F, 0.0F));

        PartDefinition tail = TerraSurfer.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-0.5F, -4.75F, 10.0F));

        tail.addOrReplaceChild("tailbone", CubeListBuilder.create().texOffs(46, 11).addBox(-3.0F, -2.0F, 1.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-4.0F, -2.0F, -1.0F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-3.5F, -2.0F, 0.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0001F))
                .texOffs(54, 24).addBox(-2.5F, -2.0F, 2.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.75F, 1.75F, -0.75F));

        PartDefinition tailfinleft = tail.addOrReplaceChild("tailfinleft", CubeListBuilder.create().texOffs(46, 38).addBox(-0.75F, -2.0F, 3.0F, 4.5F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 6).addBox(1.0F, -2.0F, 5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 54).addBox(0.25F, -2.0F, 4.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.75F, 1.75F, -0.75F));

        tailfinleft.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(56, 27).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0001F)), PartPose.offsetAndRotation(4.5F, 0.0F, 6.75F, 0.0F, -0.2618F, 0.0F));

        PartDefinition tailfinright = tail.addOrReplaceChild("tailfinright", CubeListBuilder.create().texOffs(0, 50).addBox(-0.75F, -2.0F, 3.0F, 4.5F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 33).addBox(1.0F, -2.0F, 5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 57).addBox(0.25F, -2.0F, 4.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -0.25F, -0.75F, 0.0F, -0.0436F, -3.1416F));

        tailfinright.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(56, 30).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0001F)), PartPose.offsetAndRotation(4.5F, 0.0F, 6.75F, 0.0F, -0.2618F, 0.0F));

        PartDefinition thefins = TerraSurfer.addOrReplaceChild("thefins", CubeListBuilder.create(), PartPose.offset(-1.25F, 0.0F, 2.0F));

        PartDefinition finleft = thefins.addOrReplaceChild("finleft", CubeListBuilder.create().texOffs(34, 51).addBox(-0.75F, -2.0F, 3.0F, 4.5F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 57).addBox(1.0F, -2.0F, 5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 9).addBox(0.25F, -2.0F, 4.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.25F, -3.0F, -3.5F));

        finleft.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(18, 57).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0001F)), PartPose.offsetAndRotation(4.5F, 0.0F, 6.75F, 0.0F, -0.2618F, 0.0F));

        PartDefinition finright = thefins.addOrReplaceChild("finright", CubeListBuilder.create().texOffs(46, 51).addBox(-0.75F, -2.0F, 3.0F, 4.5F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 51).addBox(1.0F, -2.0F, 5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 59).addBox(0.25F, -2.0F, 4.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -5.0F, -3.75F, 0.0F, -0.0436F, -3.1416F));

        finright.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(58, 36).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0001F)), PartPose.offsetAndRotation(4.5F, 0.0F, 6.75F, 0.0F, -0.2618F, 0.0F));

        PartDefinition Body = TerraSurfer.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(-1.75F, 0.0F, 2.0F));

        Body.addOrReplaceChild("fronthalf", CubeListBuilder.create().texOffs(24, 42).addBox(2.0F, -6.0F, -4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 14).addBox(-2.0F, -7.0F, -4.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 36).addBox(-3.0F, -6.0F, -4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, -2.0F));

        Body.addOrReplaceChild("fronthalf6", CubeListBuilder.create().texOffs(62, 54).addBox(2.0F, -6.0F, -4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 21).addBox(-2.0F, -7.0F, -4.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 63).addBox(-3.0F, -6.0F, -4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, 9.25F));

        Body.addOrReplaceChild("fronthalf7", CubeListBuilder.create().texOffs(16, 60).addBox(0.5F, -5.0F, -4.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 45).addBox(-3.0F, -6.0F, -4.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 39).addBox(-4.5F, -5.0F, -4.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 9.75F));

        Body.addOrReplaceChild("fronthalf2", CubeListBuilder.create().texOffs(54, 14).addBox(1.0F, -6.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 15).addBox(-3.0F, -7.0F, -4.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 19).addBox(-4.0F, -6.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, -1.0F));

        Body.addOrReplaceChild("fronthalf5", CubeListBuilder.create().texOffs(44, 54).addBox(1.0F, -6.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-3.0F, -7.0F, -4.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 46).addBox(-4.0F, -6.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, 8.5F));

        Body.addOrReplaceChild("fronthalf4", CubeListBuilder.create().texOffs(28, 54).addBox(1.0F, -6.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 22).addBox(-3.0F, -7.0F, -4.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 54).addBox(-4.0F, -6.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, 7.5F));

        Body.addOrReplaceChild("fronthalf3", CubeListBuilder.create().texOffs(12, 42).addBox(0.0F, -6.0F, -4.0F, 4.0F, 4.0F, 1.75F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(-4.0F, -7.0F, -4.0F, 7.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 7).addBox(-5.0F, -6.0F, -4.0F, 4.0F, 4.0F, 2.75F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, -0.25F));

        Body.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -6.0F, 2.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, 0.0F, -2.0F));

        PartDefinition water2 = partdefinition.addOrReplaceChild("water2", CubeListBuilder.create(), PartPose.offset(0.5F, 23.5F, -1.0F));

        PartDefinition cluster4 = water2.addOrReplaceChild("cluster4", CubeListBuilder.create(), PartPose.offset(-6.75F, -0.5F, -5.0F));

        cluster4.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, 2.0F, -0.2032F, -0.0925F, -0.3256F));
        cluster4.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, 2.0F, -0.2727F, 0.4F, -0.6229F));
        cluster4.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, 0.0F, 0.0F, 1.0036F));
        cluster4.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.75F, 0.0F, 0.7854F, 0.0F, 0.0F));
        cluster4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.2618F, -0.3927F, 0.0F));
        cluster4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 1.0F, 0.0F, 0.5236F, 0.0F));

        PartDefinition cluster5 = water2.addOrReplaceChild("cluster5", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 12.0F));

        cluster5.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -2.0F, -0.2032F, -0.0925F, -0.3256F));
        cluster5.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -2.0F, -0.2727F, 0.4F, -0.6229F));
        cluster5.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.0F, 0.0F, 1.0036F));
        cluster5.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.75F, -4.0F, 0.7854F, 0.0F, 0.0F));
        cluster5.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, -0.3927F, 0.0F));
        cluster5.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -3.0F, 0.0F, 0.5236F, 0.0F));

        PartDefinition cluster6 = water2.addOrReplaceChild("cluster6", CubeListBuilder.create(), PartPose.offset(-0.75F, -0.5F, -5.0F));

        cluster6.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -2.0F, -0.2032F, -0.0925F, -0.3256F));
        cluster6.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -2.0F, -0.2727F, 0.4F, -0.6229F));
        cluster6.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.0F, 0.0F, 1.0036F));
        cluster6.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.75F, -4.0F, 0.7854F, 0.0F, 0.0F));
        cluster6.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, -0.3927F, 0.0F));
        cluster6.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -3.0F, 0.0F, 0.5236F, 0.0F));

        PartDefinition cluster2 = water2.addOrReplaceChild("cluster2", CubeListBuilder.create(), PartPose.offset(5.25F, -0.5F, 4.0F));

        cluster2.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -2.0F, -0.2032F, -0.0925F, -0.3256F));
        cluster2.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -2.0F, -0.2727F, 0.4F, -0.6229F));
        cluster2.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.0F, 0.0F, 1.0036F));
        cluster2.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.75F, -4.0F, 0.7854F, 0.0F, 0.0F));
        cluster2.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, -0.3927F, 0.0F));
        cluster2.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -3.0F, 0.0F, 0.5236F, 0.0F));

        PartDefinition cluster1 = water2.addOrReplaceChild("cluster1", CubeListBuilder.create(), PartPose.offset(-5.75F, 0.5F, 6.0F));

        cluster1.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -2.0F, -0.2032F, -0.0925F, -0.3256F));
        cluster1.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -2.0F, -0.2727F, 0.4F, -0.6229F));
        cluster1.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.0F, 0.0F, 1.0036F));
        cluster1.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.75F, -4.0F, 0.7854F, 0.0F, 0.0F));
        cluster1.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, -0.3927F, 0.0F));
        cluster1.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -3.0F, 0.0F, 0.5236F, 0.0F));

        PartDefinition cluster3 = water2.addOrReplaceChild("cluster3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.0F));

        cluster3.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -2.0F, -0.2032F, -0.0925F, -0.3256F));
        cluster3.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -2.0F, -0.2727F, 0.4F, -0.6229F));
        cluster3.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.0F, 0.0F, 1.0036F));
        cluster3.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.75F, -4.0F, 0.7854F, 0.0F, 0.0F));
        cluster3.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, -0.3927F, 0.0F));
        cluster3.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -3.0F, 0.0F, 0.5236F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        TerraSurfer.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        water2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
