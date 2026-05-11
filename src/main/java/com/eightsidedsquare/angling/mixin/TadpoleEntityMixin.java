package com.eightsidedsquare.angling.mixin;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tadpole.class)
public abstract class TadpoleEntityMixin extends AbstractFish {

    public TadpoleEntityMixin(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "ageUp*", at = @At("HEAD"), cancellable = true)
    private void ageUp(CallbackInfo ci) {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        if(!component.canGrowUp()) {
            ci.cancel();
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(this);
        if(stack.is(Items.FERMENTED_SPIDER_EYE) && component.canGrowUp()) {
            if(!player.getAbilities().instabuild)
                stack.shrink(1);
            component.setCanGrowUp(false);
            cir.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide));
        }
    }

}
