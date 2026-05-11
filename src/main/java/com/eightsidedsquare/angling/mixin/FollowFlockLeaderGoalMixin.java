package com.eightsidedsquare.angling.mixin;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FollowFlockLeaderGoal.class)
public abstract class FollowFlockLeaderGoalMixin {

    @Shadow @Final private AbstractSchoolingFish mob;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void canUse(CallbackInfoReturnable<Boolean> cir) {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(mob);
        if(component.isInLove() || component.isCarryingRoe()) {
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    public void canContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(mob);
        if(component.isInLove() || component.isCarryingRoe()) {
            cir.setReturnValue(false);
        }
    }

}
