package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.ai.EatAlgaeGoal;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemStack;
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

public class CatfishEntity extends AbstractFish implements GeoEntity {
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.catfish.flop");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.catfish.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    public CatfishEntity(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EatAlgaeGoal(this, 1.25d, 12));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_CATFISH_FLOP;
    }

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_CATFISH_HURT;
    }

    @Nullable @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_CATFISH_DEATH;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.CATFISH_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<CatfishEntity> event) {
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
