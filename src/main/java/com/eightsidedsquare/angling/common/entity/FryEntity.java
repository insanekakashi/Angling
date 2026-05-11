package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FryEntity extends AbstractFish implements GeoEntity {
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.fry.flop");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.fry.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> COLOR;
    private static final EntityDataAccessor<Integer> AGE;
    private static final EntityDataAccessor<CompoundTag> VARIANT;
    private static final EntityDataAccessor<String> GROW_UP_TO;

    public FryEntity(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.MAX_HEALTH, 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COLOR, 0xffffff);
        entityData.define(AGE, -12000);
        entityData.define(VARIANT, new CompoundTag());
        entityData.define(GROW_UP_TO, "minecraft:cod");
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return super.getEyeHeight(pose);
    }

    public void growUp() {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        if(component.canGrowUp()) {
            getGrowUpEntity().ifPresent(entityType -> {
                Entity adult = entityType.create(level());
                if(adult != null) {
                    adult.setPosRaw(getX(), getY(), getZ());
                    if(adult instanceof Mob mob) {
                        mob.setPersistenceRequired();
                        CompoundTag nbt = mob.saveWithoutId(new CompoundTag());
                        getVariant().getAllKeys().forEach(key -> nbt.put(key, getVariant().get(key)));
                        mob.load(nbt);
                        level().addFreshEntity(mob);
                    }else {
                        level().addFreshEntity(adult);
                    }
                    discard();
                }
            });
        }
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("Color", getColor());
        nbt.putInt("Age", getAge());
        nbt.put("Variant", getVariant());
        nbt.putString("GrowUpTo", getGrowUpTo());
    }

    @Override
    public void loadFromBucketTag(CompoundTag nbt) {
        super.loadFromBucketTag(nbt);
        if(nbt.contains("Color")) {
            readAdditionalSaveData(nbt);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if(getAge() < 0) {
            setAge(getAge() + 1);
        }
        if(getAge() >= 0) {
            growUp();
        }
    }

    public Optional<EntityType<?>> getGrowUpEntity() {
        return EntityType.byString(getGrowUpTo());
    }

    public String getGrowUpTo() {
        return entityData.get(GROW_UP_TO);
    }

    public void setGrowUpTo(String type) {
        entityData.set(GROW_UP_TO, type);
    }

    public int getColor() {
        return entityData.get(COLOR);
    }

    public void setColor(int color) {
        entityData.set(COLOR, color);
    }

    public int getAge() {
        return entityData.get(AGE);
    }

    public void setAge(int age) {
        entityData.set(AGE, age);
    }

    public CompoundTag getVariant() {
        return entityData.get(VARIANT);
    }

    public void setVariant(CompoundTag variant) {
        entityData.set(VARIANT, variant);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setColor(nbt.getInt("Color"));
        setAge(nbt.getInt("Age"));
        if(nbt.contains("Variant", Tag.TAG_COMPOUND))
            setVariant(nbt.getCompound("Variant"));
        setGrowUpTo(nbt.getString("GrowUpTo"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Color", getColor());
        nbt.putInt("Age", getAge());
        nbt.put("Variant", getVariant());
        nbt.putString("GrowUpTo", getGrowUpTo());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        if(stack.is(Items.FERMENTED_SPIDER_EYE) && component.canGrowUp()) {
            if(!player.getAbilities().instabuild)
                stack.shrink(1);
            component.setCanGrowUp(false);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }else if(stack.is(AnglingItems.WORM) && component.canGrowUp()) {
            if(!player.getAbilities().instabuild)
                stack.shrink(1);
            component.createGrowUpParticles();
            setAge(getAge() + (int) ((getAge() * -1) * 0.05f));
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_FRY_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_FRY_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_FRY_DEATH;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.FRY_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<FryEntity> event) {
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

    static {
        COLOR = SynchedEntityData.defineId(FryEntity.class, EntityDataSerializers.INT);
        AGE = SynchedEntityData.defineId(FryEntity.class, EntityDataSerializers.INT);
        VARIANT = SynchedEntityData.defineId(FryEntity.class, EntityDataSerializers.COMPOUND_TAG);
        GROW_UP_TO = SynchedEntityData.defineId(FryEntity.class, EntityDataSerializers.STRING);
    }
}
