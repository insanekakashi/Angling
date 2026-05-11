package com.eightsidedsquare.angling.mixin.integration;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidRenderer.class, remap = false)
public abstract class SodiumFluidRendererMixin {

    @Shadow @Final private BlockPos.MutableBlockPos scratchPos;

    @Inject(method = "isFluidOccluded", at = @At("RETURN"), cancellable = true)
    private void isFluidOccluded(BlockAndTintGetter world, int x, int y, int z, Direction dir, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) {
            BlockState state = world.getBlockState(new BlockPos(scratchPos).relative(dir.getOpposite()));
            BlockState sideState = world.getBlockState(scratchPos);
            if(state.getFluidState().is(FluidTags.WATER) && sideState.is(ConventionalBlockTags.GLASS_BLOCKS)) {
                cir.setReturnValue(true);
            }
        }
    }

}
