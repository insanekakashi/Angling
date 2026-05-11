package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class NautilusEntity extends AbstractFish implements GeoEntity {
    private static final RawAnimation MOVING = RawAnimation.begin().thenLoop("animation.nautilus.moving");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.nautilus.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    public NautilusEntity(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_NAUTILUS_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_NAUTILUS_DEATH;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(AnglingItems.NAUTILUS_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 0, this::controller));
    }

    @Override
    public void aiStep() {
        if(!this.isInWater() && this.onGround() && this.verticalCollision) {
            this.verticalCollision = false;
        }else if(this.isAlive() && this.isInWater() && level().isClientSide && this.getDeltaMovement().length() > 0.025f) {
            level().addParticle(ParticleTypes.BUBBLE, this.getX(), this.getEyeY(), this.getZ(), 0, 0, 0);
        }
        super.aiStep();
    }

    private PlayState controller(AnimationState<NautilusEntity> event) {
        if(event.isMoving() && isInWater()) {
            event.getController().setAnimation(MOVING);
        }else {
            event.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<? extends WaterAnimal> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        int seaLevel = world.getSeaLevel();
        return pos.getY() >= seaLevel - 40 && pos.getY() <= seaLevel - 16 && world.getFluidState(pos.below()).is(FluidTags.WATER) && world.getBlockState(pos.above()).is(Blocks.WATER);
    }
}
