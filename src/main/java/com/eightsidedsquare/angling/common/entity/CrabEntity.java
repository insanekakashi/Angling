package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.ai.GoToWaterGoal;
import com.eightsidedsquare.angling.common.entity.util.CrabVariant;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import com.eightsidedsquare.angling.core.tags.AnglingBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class CrabEntity extends Animal implements GeoEntity, Bucketable {
    private static final RawAnimation MOVING = RawAnimation.begin().thenLoop("animation.crab.moving");
    private static final RawAnimation ROTATED = RawAnimation.begin().thenLoop("animation.crab.rotated");
    private static final RawAnimation FORWARDS = RawAnimation.begin().thenLoop("animation.crab.forwards");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    private static final EntityDataAccessor<CrabVariant> VARIANT = SynchedEntityData.defineId(CrabEntity.class, CrabVariant.TRACKED_DATA_HANDLER);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.BOOLEAN);

    public CrabEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0);
    }

    @Override
    public float getScale() {
        return isBaby() ? 0.35f : 1f;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25d));
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1, 12));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.of(AnglingItems.WORM), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1d));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        Holder<Biome> biome = world.getBiome(blockPosition());
        for(CrabVariant variant : CrabVariant.REGISTRY) {
            if(biome.is(variant.biomeTag())) {
                setVariant(variant);
                break;
            }
        }
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        CrabEntity child;
        if((child = AnglingEntities.CRAB.create(world)) != null && entity instanceof CrabEntity mate) {
            child.setVariant((random.nextBoolean() ? this : mate).getVariant());
            child.setPersistenceRequired();
            return child;
        }
        return null;
    }

    public void travel(Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01f, movementInput.scale(50));
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9d));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.005d, 0));
            }
        } else {
            super.travel(movementInput);
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, CrabVariant.DUNGENESS);
        entityData.define(FROM_BUCKET, false);
    }

    public CrabVariant getVariant() {
        return entityData.get(VARIANT);
    }

    public void setVariant(CrabVariant variant) {
        entityData.set(VARIANT, variant);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Variant", getVariant().getId().toString());
        nbt.putBoolean("FromBucket", fromBucket());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setVariant(CrabVariant.fromId(nbt.getString("Variant")));
        setFromBucket(nbt.getBoolean("FromBucket"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "rotation_controller", 6, this::rotationController));
        registrar.add(new AnimationController<>(this, "controller", 4, this::controller));
    }


    private PlayState rotationController(AnimationState<CrabEntity> event) {
        if(event.isMoving()) {
            event.getController().setAnimation(ROTATED);
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(FORWARDS);
        return PlayState.CONTINUE;
    }

    private PlayState controller(AnimationState<CrabEntity> event) {
        if(event.isMoving()) {
            event.getController().setAnimation(MOVING);
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(AnglingItems.WORM);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10).add(Attributes.MOVEMENT_SPEED, 0.2d);
    }

    public static boolean canSpawn(EntityType<CrabEntity> entity, LevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(AnglingBlockTags.CRAB_SPAWNABLE_ON) && isBrightEnoughToSpawn(world, pos);
    }

    @Override
    public boolean fromBucket() {
        return entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        entityData.set(FROM_BUCKET, fromBucket);
    }

    @Override @SuppressWarnings("deprecation")
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString("Variant", getVariant().getId().toString());
        nbt.putBoolean("FromBucket", fromBucket());

    }

    @Override @SuppressWarnings("deprecation")
    public void loadFromBucketTag(CompoundTag nbt) {
        Bucketable.loadDefaultDataFromBucketTag(this, nbt);
        if(nbt.contains("Variant", Tag.TAG_STRING)) {
            readAdditionalSaveData(nbt);
        }
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double distanceSquared) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_CRAB_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_CRAB_DEATH;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.CRAB_BUCKET);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }
}
