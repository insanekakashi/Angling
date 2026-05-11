package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.ai.*;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugColor;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugPattern;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import com.eightsidedsquare.angling.core.AnglingUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class SeaSlugEntity extends WaterAnimal implements GeoEntity, Bucketable {
    private static final RawAnimation AMBIENT = RawAnimation.begin().thenLoop("animation.sea_slug.ambient");
    private static final RawAnimation MOVING = RawAnimation.begin().thenLoop("animation.sea_slug.moving");

    private static final EntityDataAccessor<SeaSlugPattern> PATTERN = SynchedEntityData.defineId(SeaSlugEntity.class, SeaSlugPattern.TRACKED_DATA_HANDLER);
    private static final EntityDataAccessor<SeaSlugColor> BASE_COLOR = SynchedEntityData.defineId(SeaSlugEntity.class, SeaSlugColor.TRACKED_DATA_HANDLER);
    private static final EntityDataAccessor<SeaSlugColor> PATTERN_COLOR = SynchedEntityData.defineId(SeaSlugEntity.class, SeaSlugColor.TRACKED_DATA_HANDLER);
    private static final EntityDataAccessor<Boolean> BIOLUMINESCENT = SynchedEntityData.defineId(SeaSlugEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SeaSlugEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGGS = SynchedEntityData.defineId(SeaSlugEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<CompoundTag> MATE_DATA = SynchedEntityData.defineId(SeaSlugEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Integer> LOVE_TICKS = SynchedEntityData.defineId(SeaSlugEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LOVE_COOLDOWN = SynchedEntityData.defineId(SeaSlugEntity.class, EntityDataSerializers.INT);

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    public SeaSlugEntity(EntityType<? extends WaterAnimal> entityType, Level world) {
        super(entityType, world);
        setPathfindingMalus(BlockPathTypes.WATER, 0);
        getNavigation().setCanFloat(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3).add(Attributes.MOVEMENT_SPEED, 0.05d);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GoToWaterGoal(this, 2, 6));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25d));
        this.goalSelector.addGoal(2, new SeaSlugMateGoal(this));
        this.goalSelector.addGoal(3, new SeaSlugLayEggsGoal(this));
        this.goalSelector.addGoal(4, new WanderAroundWaterGoal(this, 1));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new EatAlgaeGoal(this, 1.25d, 12));
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

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_SEA_SLUG_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_SEA_SLUG_DEATH;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        setPattern(AnglingUtil.getRandomTagValue(level(), SeaSlugPattern.Tag.NATURAL_PATTERNS, random));
        setBaseColor(AnglingUtil.getRandomTagValue(level(), SeaSlugColor.Tag.BASE_COLORS, random));
        setPatternColor(AnglingUtil.getRandomTagValue(level(), SeaSlugColor.Tag.PATTERN_COLORS, random));
        setBioluminescent(random.nextBoolean());
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double distanceSquared) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(PATTERN, SeaSlugPattern.NONE);
        entityData.define(BASE_COLOR, SeaSlugColor.IVORY);
        entityData.define(PATTERN_COLOR, SeaSlugColor.IVORY);
        entityData.define(BIOLUMINESCENT, true);
        entityData.define(FROM_BUCKET, false);
        entityData.define(HAS_EGGS, false);
        entityData.define(MATE_DATA, new CompoundTag());
        entityData.define(LOVE_TICKS, 0);
        entityData.define(LOVE_COOLDOWN, 0);
    }

    public void setBaseColor(SeaSlugColor color) {
        entityData.set(BASE_COLOR, color);
    }

    public SeaSlugColor getBaseColor() {
        return entityData.get(BASE_COLOR);
    }

    public void setPatternColor(SeaSlugColor color) {
        entityData.set(PATTERN_COLOR, color);
    }

    public SeaSlugColor getPatternColor() {
        return entityData.get(PATTERN_COLOR);
    }

    public void setPattern(SeaSlugPattern pattern) {
        entityData.set(PATTERN, pattern);
    }

    public SeaSlugPattern getPattern() {
        return entityData.get(PATTERN);
    }

    public void setBioluminescent(boolean bioluminescent) {
        entityData.set(BIOLUMINESCENT, bioluminescent);
    }

    public boolean isBioluminescent() {
        return entityData.get(BIOLUMINESCENT);
    }

    public void setHasEggs(boolean hasEggs) {
        entityData.set(HAS_EGGS, hasEggs);
    }

    public boolean hasEggs() {
        return entityData.get(HAS_EGGS);
    }

    public void setMateData(CompoundTag mateData) {
        entityData.set(MATE_DATA, mateData);
    }

    public CompoundTag getMateData() {
        return entityData.get(MATE_DATA);
    }

    public void setLoveTicks(int ticks) {
        entityData.set(LOVE_TICKS, ticks);
    }

    public int getLoveTicks() {
        return entityData.get(LOVE_TICKS);
    }

    public void setLoveCooldown(int cooldown) {
        entityData.set(LOVE_COOLDOWN, cooldown);
    }

    public int getLoveCooldown() {
        return entityData.get(LOVE_COOLDOWN);
    }

    public boolean isInLove() {
        return getLoveTicks() > 0;
    }

    public boolean hasLoveCooldown() {
        return getLoveCooldown() > 0;
    }

    public CompoundTag writeMateData(CompoundTag nbt) {
        nbt.putString("Pattern", getPattern().getId().toString());
        nbt.putString("BaseColor", getBaseColor().getId().toString());
        nbt.putString("PatternColor", getPatternColor().getId().toString());
        nbt.putBoolean("Bioluminescent", isBioluminescent());
        return nbt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        writeMateData(nbt);
        nbt.putBoolean("FromBucket", fromBucket());
        nbt.putBoolean("HasEggs", hasEggs());
        nbt.put("MateData", getMateData());
        nbt.putInt("LoveTicks", getLoveTicks());
        nbt.putInt("LoveCooldown", getLoveCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setPattern(SeaSlugPattern.fromId(nbt.getString("Pattern")));
        setBaseColor(SeaSlugColor.fromId(nbt.getString("BaseColor")));
        setPatternColor(SeaSlugColor.fromId(nbt.getString("PatternColor")));
        setBioluminescent(nbt.getBoolean("Bioluminescent"));
        setFromBucket(nbt.getBoolean("FromBucket"));
        setHasEggs(nbt.getBoolean("HasEggs"));
        setLoveTicks(nbt.getInt("LoveTicks"));
        setLoveCooldown(nbt.getInt("LoveCooldown"));
        if(nbt.contains("MateData", Tag.TAG_COMPOUND))
            setMateData(nbt.getCompound("MateData"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "ambient_controller", 0, this::ambientController));
        animationData.add(new AnimationController<>(this, "controller", 0, this::controller));
    }

    private PlayState ambientController(AnimationState<SeaSlugEntity> event) {
        event.getController().setAnimation(AMBIENT);
        return PlayState.CONTINUE;
    }

    private PlayState controller(AnimationState<SeaSlugEntity> event) {
        if(new Vec3(getDeltaMovement().x(), 0, getDeltaMovement().z()).length() > 0.005d) {
            event.getController().setAnimation(MOVING);
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(AnglingBlocks.ALGAE.asItem()) && !isInLove() && !hasLoveCooldown() && !hasEggs()) {
            setLoveTicks(600);
            createHeartParticles();
            if(!player.getAbilities().instabuild)
                stack.shrink(1);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    public void createHeartParticles() {
        if(!level().isClientSide) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.HEART, getRandomX(1), getRandomY() + 0.5d, getRandomZ(1), 7, 0.25d, 0.25d, 0.25d, 0);
        }
    }

    @Override
    protected void customServerAiStep() {
        if(hasLoveCooldown()) {
            setLoveCooldown(getLoveCooldown() - 1);
        }
        if(isInLove()) {
            setLoveTicks(getLoveTicks() - 1);
        }
        super.customServerAiStep();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
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
        writeMateData(nbt);
        nbt.putBoolean("HasEggs", hasEggs());
        nbt.put("MateData", getMateData());
        nbt.putInt("LoveTicks", getLoveTicks());
        nbt.putInt("LoveCooldown", getLoveCooldown());
    }

    @Override @SuppressWarnings("deprecation")
    public void loadFromBucketTag(CompoundTag nbt) {
        Bucketable.loadDefaultDataFromBucketTag(this, nbt);
        if(nbt.contains("Pattern", Tag.TAG_STRING)) {
            readAdditionalSaveData(nbt);
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.SEA_SLUG_BUCKET);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }
}
