package net.add.terrasurf.entity;

import net.add.terrasurf.TerraSurfMod;
import net.add.terrasurf.sounds.ModSounds;
import net.add.terrasurf.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TerraSurferEntity extends Entity {

    // --- Animation State Management ---
    public enum Animation {
        IDLE,
        JUMP,
        FALL,
        ATTACK
    }

    private static final EntityDataAccessor<Byte> ANIMATION_STATE =
            SynchedEntityData.defineId(TerraSurferEntity.class, EntityDataSerializers.BYTE);

    public int attackAnimationTicks;
    public int jumpAnimationTicks;
    private static final int ATTACK_ANIMATION_DURATION = 15;
    private static final int JUMP_ANIMATION_DURATION = 10;

    public float climbAngle;
    public float prevClimbAngle;

    private static final EntityDataAccessor<Integer> ENERGY =
            SynchedEntityData.defineId(TerraSurferEntity.class, EntityDataSerializers.INT);
    private static final int MAX_ENERGY = 120;
    private int jumpGracePeriod = 0;

    private boolean wasInWater;
    private boolean wasOnGround;

    // --- OPTIMIZATION: Cached enchantment levels ---
    private boolean hasFlow;
    private int riptideLevel;
    private boolean hasChanneling;

    public TerraSurferEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ENERGY, MAX_ENERGY);
        this.entityData.define(ANIMATION_STATE, (byte)Animation.IDLE.ordinal());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (ANIMATION_STATE.equals(pKey)) {
            if (this.level().isClientSide()) {
                Animation state = getAnimationState();
                if (state == Animation.ATTACK) {
                    this.attackAnimationTicks = ATTACK_ANIMATION_DURATION;
                } else if (state == Animation.JUMP) {
                    this.jumpAnimationTicks = JUMP_ANIMATION_DURATION;
                }
            }
        }
        super.onSyncedDataUpdated(pKey);
    }


    @Override
    public void tick() {
        super.tick();
        this.updateClimbAngle();

        if (this.level().isClientSide()) {
            if (this.attackAnimationTicks > 0) this.attackAnimationTicks--;
            if (this.jumpAnimationTicks > 0) this.jumpAnimationTicks--;
        }

        Entity passenger = this.getControllingPassenger();

        if (this.tickCount > 1 && this.getPassengers().isEmpty() && !this.level().isClientSide()) {
            this.discard();
            return;
        }

        if (passenger instanceof LivingEntity livingPassenger) {
            // --- OPTIMIZATION: Cache enchantment levels once per tick ---
            this.cacheEnchantmentLevels(livingPassenger);

            if (!livingPassenger.getItemBySlot(EquipmentSlot.HEAD).is(TerraSurfMod.TERRASURFERBOARD.get())) {
                livingPassenger.stopRiding();
                return;
            }

            livingPassenger.setAirSupply(livingPassenger.getMaxAirSupply());
            livingPassenger.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, true));

            if (this.hasChanneling && this.level().isNight()) {
                livingPassenger.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
            }

            if (!this.level().isClientSide()) {
                this.handleEnergy();
                this.handleDurability(livingPassenger);
                this.handleMobBumping();
                this.handleOneShotSounds();
                this.updateAnimationState();
            }

            this.handleMovement(livingPassenger);
        }
    }

    // --- OPTIMIZATION: New method to cache enchantments ---
    private void cacheEnchantmentLevels(LivingEntity passenger) {
        ItemStack board = passenger.getItemBySlot(EquipmentSlot.HEAD);
        this.hasFlow = EnchantmentHelper.getItemEnchantmentLevel(TerraSurfMod.FLOW.get(), board) > 0;
        this.riptideLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.RIPTIDE, board);
        this.hasChanneling = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.CHANNELING, board) > 0;
    }

    private void updateAnimationState() {
        if (this.getAnimationState() == Animation.ATTACK && this.attackAnimationTicks > 0) {
            this.attackAnimationTicks--;
            return;
        }
        if (this.getAnimationState() == Animation.JUMP && this.jumpAnimationTicks > 0) {
            this.jumpAnimationTicks--;
            return;
        }

        if (!this.onGround() && this.getDeltaMovement().y < -0.1) {
            this.setAnimationState(Animation.FALL);
        } else {
            this.setAnimationState(Animation.IDLE);
        }
    }

    public Animation getAnimationState() {
        return Animation.values()[this.entityData.get(ANIMATION_STATE)];
    }

    public void setAnimationState(Animation animation) {
        this.entityData.set(ANIMATION_STATE, (byte)animation.ordinal());
    }

    public void attack() {
        if (this.attackAnimationTicks <= 0) {
            this.setAnimationState(Animation.ATTACK);
            this.attackAnimationTicks = ATTACK_ANIMATION_DURATION;
        }
    }

    public void doJump() {
        if (this.onGround() || this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.55D, 0));
            this.jumpGracePeriod = 10;
            this.setAnimationState(Animation.JUMP);
            this.jumpAnimationTicks = JUMP_ANIMATION_DURATION;
        }
    }

    @Override
    protected void removePassenger(Entity pPassenger) {
        super.removePassenger(pPassenger);
        if (pPassenger instanceof ServerPlayer player) {
            ItemStack boardStack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (boardStack.is(TerraSurfMod.TERRASURFERBOARD.get())) {
                int cooldownDuration = 300;
                player.getCooldowns().addCooldown(boardStack.getItem(), cooldownDuration);
            }
        }
    }

    private void handleMobBumping() {
        // --- OPTIMIZATION: Only search for entities if moving fast enough ---
        if (this.getDeltaMovement().horizontalDistanceSqr() < 0.01D) {
            return;
        }

        Entity passenger = this.getControllingPassenger();
        if (passenger instanceof LivingEntity livingPassenger) {
            List<Entity> nearbyEntities = this.level().getEntities(this, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && !entity.is(passenger)) {
                    this.attack();

                    if (this.hasChanneling && this.level().isThundering()) {
                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(this.level());
                        if (lightning != null) {
                            lightning.moveTo(Vec3.atBottomCenterOf(entity.blockPosition()));
                            this.level().addFreshEntity(lightning);
                        }
                    } else {
                        entity.hurt(this.damageSources().mobAttack(livingPassenger), 1.0F);
                        double knockbackStrength = 0.5D;
                        Vec3 knockbackDirection = this.getDeltaMovement().normalize();
                        entity.push(knockbackDirection.x * knockbackStrength, 0.1D, knockbackDirection.z * knockbackStrength);
                    }
                }
            }
        }
    }

    private void updateClimbAngle() {
        this.prevClimbAngle = this.climbAngle;
        float targetAngle = this.isClimbing() ? 90.0F : 0.0F;
        this.climbAngle = Mth.lerp(0.2F, this.climbAngle, targetAngle);
    }

    private void handleOneShotSounds() {
        if (!this.wasInWater && this.isInWater()) {
            this.level().playSound(null, this.blockPosition(), ModSounds.BOARD_WATER_TOGGLE.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        this.wasInWater = this.isInWater();
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.blockPosition(), ModSounds.BOARD_DISMOUNT.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        super.remove(pReason);
    }

    private void handleEnergy() {
        if (this.riptideLevel == 3 && this.isInWater()) {
            this.setEnergy(Math.min(MAX_ENERGY, this.getEnergy() + 2));
            return;
        }

        float drainInterval = 1.0f;
        float efficiencyMultiplier = 1.0f;

        if (this.hasFlow) {
            drainInterval = 2.0f;
        }
        else if (this.riptideLevel > 0) {
            if (this.riptideLevel >= 2 && this.level().isRaining()) {
                efficiencyMultiplier = 2.0f;
            } else {
                efficiencyMultiplier = 1.5f;
            }
        }

        if (this.onFastSurface()) {
            efficiencyMultiplier *= 3.3f;
        }

        if (this.tickCount % (int)Math.max(1, drainInterval * efficiencyMultiplier) == 0) {
            this.setEnergy(this.getEnergy() - 1);
        }

        if (this.getEnergy() <= 0 && this.getControllingPassenger() != null) {
            this.getControllingPassenger().stopRiding();
        }
    }


    private void handleDurability(LivingEntity passenger) {
        if (passenger instanceof ServerPlayer serverPlayer) {
            if (!this.onFastSurface() && this.tickCount % 20 == 0) {
                ItemStack boardStack = serverPlayer.getItemBySlot(EquipmentSlot.HEAD);
                boardStack.hurtAndBreak(1, serverPlayer, (p) -> p.broadcastBreakEvent(EquipmentSlot.HEAD));
            }
        }
    }

    private void handleMovement(LivingEntity passenger) {
        if (this.riptideLevel > 0 && this.isInWater()) {
            this.setYRot(passenger.getYRot());
            this.setXRot(passenger.getXRot());

            Vec3 lookAngle = passenger.getLookAngle();
            double speed = 0.65D;
            this.setDeltaMovement(lookAngle.scale(speed));
            this.move(MoverType.SELF, this.getDeltaMovement());
            return;
        }

        Vec3 velocity = this.getDeltaMovement();
        double verticalSpeed = velocity.y;

        if (this.onFastSurface()) {
            verticalSpeed *= 0.7D;
            verticalSpeed += 0.02D;
        } else {
            if (!this.onGround()) {
                if (this.jumpGracePeriod > 0) {
                    if (this.hasFlow) {
                        verticalSpeed -= 0.04D;
                    } else {
                        verticalSpeed -= 0.06D;
                    }
                } else {
                    verticalSpeed -= 0.08D;
                }
            }
        }

        if (this.jumpGracePeriod > 0) {
            this.jumpGracePeriod--;
        }

        if (isClimbing()) {
            verticalSpeed = 0.6D;
        }

        Vec3 playerInput = this.getPlayerInput(this.onFastSurface());
        this.setDeltaMovement(playerInput.x, verticalSpeed, playerInput.z);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private Vec3 getPlayerInput(boolean onFastSurface) {
        Entity passenger = this.getControllingPassenger();
        if (passenger instanceof LivingEntity livingPassenger) {
            this.setYRot(livingPassenger.getYRot());

            float forwardInput = livingPassenger.zza;
            float sidewaysInput = livingPassenger.xxa;

            if (forwardInput < 0.0F) {
                forwardInput = 0.0F;
            }

            Vec3 moveDirection = new Vec3(sidewaysInput, 0, forwardInput).yRot(-this.getYRot() * ((float)Math.PI / 180F));

            double speed;
            boolean riptideSurfaceBoost = this.riptideLevel == 3 && onFastSurface && this.isInWater();

            if (onFastSurface) {
                if (this.hasFlow || riptideSurfaceBoost) {
                    speed = 0.65D;
                } else {
                    speed = 0.48D;
                }
            } else {
                speed = this.hasFlow ? 0.55D : 0.43D;
            }

            return moveDirection.scale(speed);
        }
        return Vec3.ZERO;
    }

    public boolean isClimbing() {
        Entity controller = this.getControllingPassenger();
        if (controller instanceof LivingEntity passenger) {
            boolean isMovingForward = passenger.zza > 0;
            if (this.horizontalCollision && isMovingForward && !this.onFastSurface()) {
                BlockState stateInFront = this.level().getBlockState(this.blockPosition().relative(passenger.getDirection()));
                return stateInFront.is(ModTags.Blocks.CLIMBABLE);
            }
        }
        return false;
    }

    public boolean isMovingFast() {
        Entity controller = this.getControllingPassenger();
        if (controller instanceof LivingEntity) {
            boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 0.01D;
            if (!isMoving) {
                return false;
            }

            if (this.riptideLevel > 0 && this.isEyeInFluid(FluidTags.WATER)) {
                return true;
            }

            boolean onWater = this.isEyeInFluid(FluidTags.WATER);
            boolean onIce = this.level().getBlockState(this.blockPosition().below()).is(BlockTags.ICE);
            return this.hasFlow && (this.onGround() || onWater || onIce) && !this.isClimbing();
        }
        return false;
    }

    private boolean onFastSurface() {
        BlockState blockBelow = this.level().getBlockState(this.blockPosition().below());
        return this.isEyeInFluid(FluidTags.WATER) || this.isEyeInFluid(FluidTags.LAVA) || blockBelow.is(BlockTags.ICE);
    }

    public int getEnergy() {
        return this.entityData.get(ENERGY);
    }

    public void setEnergy(int energy) {
        this.entityData.set(ENERGY, Math.max(0, energy));
    }


    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof LivingEntity) {
            return (LivingEntity) passenger;
        }
        return null;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.5D;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setEnergy(pCompound.getInt("Energy"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Energy", this.getEnergy());
    }
}
