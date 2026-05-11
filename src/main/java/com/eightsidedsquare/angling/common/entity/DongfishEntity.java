package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class DongfishEntity extends AbstractFish implements GeoEntity {
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.dongfish.flop");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.dongfish.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Boolean> HAS_HORNGUS = SynchedEntityData.defineId(DongfishEntity.class, EntityDataSerializers.BOOLEAN);

    public DongfishEntity(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(hasHorngus() && stack.is(Items.SHEARS)) {
            playSound(AnglingSounds.ENTITY_DONGFISH_SHEAR, 1, 1);
            setHasHorngus(false);
            hurt(player.damageSources().playerAttack(player), 1);
            if(!player.getAbilities().instabuild)
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_HORNGUS, true);
    }

    public boolean hasHorngus() {
        return entityData.get(HAS_HORNGUS);
    }

    public void setHasHorngus(boolean hasHorngus) {
        entityData.set(HAS_HORNGUS, hasHorngus);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("HasHorngus", hasHorngus());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setHasHorngus(nbt.getBoolean("HasHorngus"));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_DONGFISH_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_DONGFISH_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_DONGFISH_DEATH;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.DONGFISH_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<DongfishEntity> event) {
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

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        stack.getOrCreateTag().putBoolean("HasHorngus", hasHorngus());
    }

    @Override
    public void loadFromBucketTag(CompoundTag nbt) {
        super.loadFromBucketTag(nbt);
        if(nbt.contains("HasHorngus"))
            setHasHorngus(nbt.getBoolean("HasHorngus"));
        else
            setHasHorngus(true);
    }
}
