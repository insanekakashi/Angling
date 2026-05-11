package com.eightsidedsquare.angling.mixin;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import com.eightsidedsquare.angling.common.entity.ai.FishLayRoeGoal;
import com.eightsidedsquare.angling.common.entity.ai.FishMateGoal;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.tags.AnglingEntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFish.class)
public abstract class AbstractFishMixin extends WaterAnimal {

    public AbstractFishMixin(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep(CallbackInfo ci) {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        component.tick();
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    protected void registerGoals(CallbackInfo ci) {
        if(getType().is(AnglingEntityTypeTags.SPAWNING_FISH)) {
            goalSelector.addGoal(1, new FishLayRoeGoal(this));
            goalSelector.addGoal(3, new FishMateGoal(this));
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    protected void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        if (!component.hasCooldown() && !component.isInLove()
                && stack.is(AnglingItems.WORM)
                && getType().is(AnglingEntityTypeTags.SPAWNING_FISH)) {
            if(!player.getAbilities().instabuild)
                stack.shrink(1);
            component.setLoveTicks(600);
            component.setWasFed(true);
            component.createHeartParticles();
            cir.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide));
        }
    }

    @Mixin(AbstractFish.FishSwimGoal.class)
    public abstract static class RandomSwimmingGoalMixin {

        @Shadow @Final private AbstractFish fish;

        @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
        public void canuse(CallbackInfoReturnable<Boolean> cir) {
            FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(fish);
            if(component.isCarryingRoe())
                cir.setReturnValue(false);
        }
    }

}
