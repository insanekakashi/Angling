package com.eightsidedsquare.angling.mixin;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void shouldRenderFace(BlockAndTintGetter world, BlockPos pos, FluidState fluidState, BlockState blockState, Direction direction, FluidState neighborFluidState, CallbackInfoReturnable<Boolean> cir) {
        if(fluidState.is(FluidTags.WATER)) {
            if(world.getBlockState(pos.relative(direction)).is(ConventionalBlockTags.GLASS_BLOCKS)) {
                cir.setReturnValue(false);
            }
        }
    }

}
