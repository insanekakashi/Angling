package com.eightsidedsquare.angling.mixin;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSchoolingFish.class)
public abstract class AbstractSchoolingFishMixin extends AbstractFish {

    public AbstractSchoolingFishMixin(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "inRangeOfLeader", at = @At("HEAD"), cancellable = true)
    public void inRangeOfLeader(CallbackInfoReturnable<Boolean> cir) {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        if(component.isInLove() || component.isCarryingRoe()) {
            cir.setReturnValue(false);
        }
    }

}
