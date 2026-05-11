package com.eightsidedsquare.angling.mixin;

import com.eightsidedsquare.angling.common.block.StarfishBlock;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CoralFeature.class)
public abstract class CoralFeatureMixin {

    @Inject(method = "placeCoralBlock", at = @At("RETURN"))
    protected void placeCoralBlock(LevelAccessor world, RandomSource random, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            Direction.stream()
                    .filter(d -> random.nextFloat() < 0.0125d && canPlace(pos.relative(d), world))
                    .forEach(d -> {
                        world.setBlock(pos.relative(d),
                                AnglingBlocks.STARFISH.defaultBlockState().setValue(DirectionalBlock.FACING, d), Block.UPDATE_CLIENTS);
                        StarfishBlock.randomize(world, pos.relative(d), random);
                    });
            if(random.nextFloat() < 0.025f && canPlace(pos.above(), world)) {
                world.setBlock(pos.above(),
                        (random.nextBoolean() ? AnglingBlocks.URCHIN : AnglingBlocks.ANEMONE)
                        .defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_CLIENTS);
            }
        }
    }

    private boolean canPlace(BlockPos pos, LevelAccessor world) {
        BlockState state = world.getBlockState(pos);
        return state.is(BlockTags.CORALS) || state.is(BlockTags.WALL_CORALS) || state.is(Blocks.WATER);
    }

}
