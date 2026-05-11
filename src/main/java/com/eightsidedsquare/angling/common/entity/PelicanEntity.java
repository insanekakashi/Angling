package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.ai.PelicanBrain;
import com.eightsidedsquare.angling.common.entity.util.PelicanBeakEntityInitializer;
import com.eightsidedsquare.angling.core.AnglingCriteria;
import com.eightsidedsquare.angling.core.AnglingSounds;
import com.eightsidedsquare.angling.core.AnglingUtil;
import com.eightsidedsquare.angling.core.ai.AnglingMemoryModuleTypes;
import com.eightsidedsquare.angling.core.ai.AnglingSensorTypes;
import com.eightsidedsquare.angling.core.tags.AnglingEntityTypeTags;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.Optional;

public class PelicanEntity extends Animal implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.pelican.idle");
    private static final RawAnimation SWIMMING = RawAnimation.begin().thenLoop("animation.pelican.swimming");
    private static final RawAnimation FLYING = RawAnimation.begin().thenLoop("animation.pelican.flying");
    private static final RawAnimation FLAPPING = RawAnimation.begin().thenLoop("animation.pelican.flapping");
    private static final RawAnimation DIVING_ANIMATION = RawAnimation.begin().thenLoop("animation.pelican.diving");
    private static final RawAnimation WALKING = RawAnimation.begin().thenLoop("animation.pelican.walking");
    private static final RawAnimation BEAK_OPEN_ANIMATION = RawAnimation.begin().thenLoop("animation.pelican.beak_opened");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);
    protected static final ImmutableList<SensorType<? extends Sensor<? super PelicanEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;

    protected static final EntityDataAccessor<Boolean> BEAK_OPEN;
    protected static final EntityDataAccessor<Boolean> DIVING;
    protected static final EntityDataAccessor<CompoundTag> ENTITY_IN_BEAK;
    protected static final EntityDataAccessor<Integer> TIME_OFF_GROUND;

    public PelicanEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 5, false);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(isBeakOpen() && stack.getItem() instanceof MobBucketItem bucketItem) {
            bucketItem.playEmptySound(player, level(), blockPosition());
            CompoundTag nbt = stack.getOrCreateTag().copy();
            nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(bucketItem.type).toString());
            if(nbt.contains("BucketVariantTag")) {
                nbt.put("Variant", nbt.get("BucketVariantTag"));
                nbt.remove("BucketVariantTag");
            }
            if(!player.getAbilities().instabuild)
                player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
            getEntityInBeak().ifPresent(entity -> {
                Vec3 vec3d = calculateViewVector(getXRot(), getYHeadRot()).scale(0.5d).add(getEyePosition()).subtract(0, entity.getBbHeight(), 0);
                entity.setPosRaw(vec3d.x, vec3d.y, vec3d.z);
                entity.setYBodyRot(getYHeadRot());
                entity.setYRot(getYHeadRot());
                if(entity instanceof Bucketable bucketable)
                    bucketable.setFromBucket(true);
                level().addFreshEntity(entity);
            });
            setEntityInBeak(nbt);
            setBeakOpen(true);
            getBrain().setMemory(AnglingMemoryModuleTypes.HAS_TRADED, Unit.INSTANCE);
            getBrain().eraseMemory(AnglingMemoryModuleTypes.CAN_TRADE);
            if (player instanceof ServerPlayer serverPlayerEntity)
                AnglingCriteria.TRADED_WITH_PELICAN.trigger(serverPlayerEntity);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.FLYING_SPEED, 0.1D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D)
                .add(Attributes.ATTACK_DAMAGE, 1d);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BEAK_OPEN, false);
        entityData.define(DIVING, false);
        entityData.define(ENTITY_IN_BEAK, new CompoundTag());
        entityData.define(TIME_OFF_GROUND, 0);
    }

    public int getTimeOffGround() {
        return entityData.get(TIME_OFF_GROUND);
    }

    public void setTimeOffGround(int timeOffGround) {
        entityData.set(TIME_OFF_GROUND, timeOffGround);
    }

    public boolean isBeakOpen() {
        return entityData.get(BEAK_OPEN);
    }

    public void setBeakOpen(boolean open) {
        entityData.set(BEAK_OPEN, open);
    }

    public boolean isDiving() {
        return entityData.get(DIVING);
    }

    public void setDiving(boolean diving) {
        entityData.set(DIVING, diving);
    }

    public Optional<Entity> getEntityInBeak() {
        return AnglingUtil.entityFromNbt(getEntityInBeakNbt(), level());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions entityDimensions = super.getDimensions(pose);
        return this.isFlying() || this.isInWater() ? EntityDimensions.fixed(entityDimensions.width, 0.75F) : entityDimensions;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.95f * dimensions.height;
    }

    public CompoundTag getEntityInBeakNbt() {
        return entityData.get(ENTITY_IN_BEAK);
    }

    public void setEntityInBeak(CompoundTag nbt) {
        entityData.set(ENTITY_IN_BEAK, nbt);
    }

    public void setEntityInBeak(Entity entity) {
        setEntityInBeak(AnglingUtil.entityToNbt(entity, true));
    }

    public boolean hasEntityInBeak() {
        return getEntityInBeakNbt().contains("id", Tag.TAG_STRING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("BeakOpen", isBeakOpen());
        nbt.putBoolean("Diving", isDiving());
        nbt.putInt("TimeOffGround", getTimeOffGround());
        if(getEntityInBeakNbt() != null)
            nbt.put("EntityInBeak", getEntityInBeakNbt());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setBeakOpen(nbt.getBoolean("BeakOpen"));
        setDiving(nbt.getBoolean("Diving"));
        setTimeOffGround(nbt.getInt("TimeOffGround"));
        if(nbt.contains("EntityInBeak", Tag.TAG_COMPOUND)) {
            setEntityInBeak(nbt.getCompound("EntityInBeak"));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag nbt) {
        getBrain().setMemoryWithExpiry(AnglingMemoryModuleTypes.SOARING_COOLDOWN, Unit.INSTANCE, 100);
        setEntityInBeak(initializeEntityInBeak());
        setBeakOpen(true);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, nbt);
    }

    private CompoundTag initializeEntityInBeak() {
        CompoundTag nbt = new CompoundTag();
        TagKey<EntityType<?>> tag = random.nextInt(5) == 0 ? AnglingEntityTypeTags.UNCOMMON_ENTITIES_IN_PELICAN_BEAK
                : AnglingEntityTypeTags.COMMON_ENTITIES_IN_PELICAN_BEAK;
        EntityType<?> type = AnglingUtil.getRandomTagValue(level(), tag, random);
        nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        nbt.putBoolean("FromBucket", true);
        if(type.is(AnglingEntityTypeTags.HUNTED_BY_PELICAN_WHEN_BABY)) {
            nbt.putInt("Age", -24000);
        }
        return PelicanBeakEntityInitializer.getInitializer(type).initialize(nbt, random, level());
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean bl = super.hurt(source, amount);
        if(bl) {
            setBeakOpen(false);
        }
        return bl;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        super.checkFallDamage(heightDifference, onGround, state, landedPosition);
    }

    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world);
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanFloat(true);
        birdNavigation.setCanPassDoors(true);
        return birdNavigation;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return getBrain().checkMemory(AnglingMemoryModuleTypes.CAN_TRADE, MemoryStatus.VALUE_PRESENT)
                && getBrain().getActiveNonCoreActivity().orElse(Activity.CORE).equals(Activity.IDLE)
                && getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
                ? AnglingSounds.ENTITY_PELICAN_AMBIENT : null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_PELICAN_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_PELICAN_DEATH;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        setTimeOffGround(onGround() ? 0 : getTimeOffGround() + 1);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        refreshDimensions();
    }

    public void travel(Vec3 movementInput) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(this.getSpeed() * 0.75f, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929D));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9100000262260437D));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 4, this::controller));
        registrar.add(new AnimationController<>(this, "beak_controller", 2, this::beakController));
    }

    public boolean isFlying() {
        return getTimeOffGround() > 5;
    }

    private PlayState controller(AnimationState<PelicanEntity> event) {
        if(isDiving() && isFlying()){
            event.getController().setAnimation(DIVING_ANIMATION);
        }else if(isInWater()) {
            event.getController().setAnimation(SWIMMING);
        }else if(isFlying()) {
            if (Math.abs(getDeltaMovement().y) > 0.05d) {
                event.getController().setAnimation(FLYING);
            } else {
                event.getController().setAnimation(FLAPPING);
            }
        }else if(event.isMoving()) {
            event.getController().setAnimation(WALKING);
        }else {
            event.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    private PlayState beakController(AnimationState<PelicanEntity> event) {
        PelicanEntity entity = event.getAnimatable();
        if(entity.isBeakOpen()) {
            event.getController().setAnimation(BEAK_OPEN_ANIMATION);
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected Brain.Provider<PelicanEntity> brainProvider() {
        return Brain.provider(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PelicanBrain.create(brainProvider().makeBrain(dynamic));
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return !(hasCustomName() || isPersistenceRequired());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<PelicanEntity> getBrain() {
        return (Brain<PelicanEntity>) super.getBrain();
    }

    protected void customServerAiStep() {
        this.level().getProfiler().push("pelicanBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("pelicanActivityUpdate");
        PelicanBrain.updateActivities(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    static {
        SENSORS = ImmutableList.of(
                SensorType.NEAREST_LIVING_ENTITIES,
                SensorType.NEAREST_PLAYERS,
                SensorType.HURT_BY,
                AnglingSensorTypes.PELICAN_ATTACKABLES
        );
        MEMORY_MODULES = ImmutableList.of(
                MemoryModuleType.PATH,
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.HURT_BY,
                MemoryModuleType.IS_PANICKING,
                MemoryModuleType.HAS_HUNTING_COOLDOWN,
                MemoryModuleType.NEAREST_ATTACKABLE,
                MemoryModuleType.ATTACK_COOLING_DOWN,
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.NEAREST_PLAYERS,
                MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                AnglingMemoryModuleTypes.SOARING_COOLDOWN,
                AnglingMemoryModuleTypes.CAN_TRADE,
                AnglingMemoryModuleTypes.HAS_TRADED
        );
        TIME_OFF_GROUND = SynchedEntityData.defineId(PelicanEntity.class, EntityDataSerializers.INT);
        BEAK_OPEN = SynchedEntityData.defineId(PelicanEntity.class, EntityDataSerializers.BOOLEAN);
        DIVING = SynchedEntityData.defineId(PelicanEntity.class, EntityDataSerializers.BOOLEAN);
        ENTITY_IN_BEAK = SynchedEntityData.defineId(PelicanEntity.class, EntityDataSerializers.COMPOUND_TAG);
    }

}