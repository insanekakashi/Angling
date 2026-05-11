package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class SeahorseEntity extends AbstractFish implements GeoEntity {
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.seahorse.flop");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.seahorse.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    public SeahorseEntity(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MoveToKelpGoal(this));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_SEAHORSE_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_SEAHORSE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_SEAHORSE_DEATH;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.SEAHORSE_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<SeahorseEntity> event) {
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

    static class MoveToKelpGoal extends MoveToBlockGoal {

        public MoveToKelpGoal(SeahorseEntity mob) {
            super(mob, 1f, 6,6);
        }

        @Override
        public double acceptedDistance() {
            return 0d;
        }

        @Override
        protected boolean isValidTarget(LevelReader world, BlockPos pos) {
            BlockState state = world.getBlockState(pos.above());
            return state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT);
        }
    }
}
