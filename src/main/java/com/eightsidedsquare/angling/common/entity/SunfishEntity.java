package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.util.SunfishVariant;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import com.eightsidedsquare.angling.core.AnglingUtil;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class SunfishEntity extends AbstractSchoolingFish implements GeoEntity {
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.sunfish.flop");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sunfish.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    private static final EntityDataAccessor<SunfishVariant> VARIANT;

    public SunfishEntity(EntityType<? extends AbstractSchoolingFish> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, AnglingUtil.getRandomTagValue(level(), SunfishVariant.Tag.NATURAL_SUNFISH, random));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_SUNFISH_FLOP;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.SUNFISH_BUCKET);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_SUNFISH_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_SUNFISH_DEATH;
    }

    @Nullable
    public SunfishVariant getVariant() {
        return entityData.get(VARIANT);
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CompoundTag nbtCompound = stack.getOrCreateTag();
        nbtCompound.putString("BucketVariantTag", SunfishVariant.getId(getVariant()).toString());
    }

    @Override
    public void tick() {
        super.tick();
        if(hasCustomName() && Objects.requireNonNull(getCustomName()).getString().equalsIgnoreCase("diansu")) {
            setVariant(SunfishVariant.DIANSUS_DIANSUR);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        if (spawnReason == MobSpawnType.BUCKET && entityNbt != null && entityNbt.contains("BucketVariantTag", Tag.TAG_STRING)) {
            this.setVariant(SunfishVariant.fromId(entityNbt.getString("BucketVariantTag")));
        }
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public void setVariant(SunfishVariant variant) {
        entityData.set(VARIANT, variant);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Variant", SunfishVariant.getId(entityData.get(VARIANT)).toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(VARIANT, SunfishVariant.fromId(nbt.getString("Variant")));
    }

    static {
        VARIANT = SynchedEntityData.defineId(SunfishEntity.class, SunfishVariant.TRACKED_DATA_HANDLER);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<SunfishEntity> event) {
        if(!wasTouchingWater) {
            event.getController().setAnimation(FLOP);
        } else {
            event.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
