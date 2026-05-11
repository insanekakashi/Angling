package com.eightsidedsquare.angling.common.feature;

import com.eightsidedsquare.angling.common.block.WormyBlock;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WormyBlockFeature extends Feature<NoneFeatureConfiguration> {
    public WormyBlockFeature(Codec<NoneFeatureConfiguration> configCodec) {
        super(configCodec);
    }

    private boolean surroundedByDirt(BlockPos pos, WorldGenLevel world) {
        return Direction.stream().allMatch(d -> world.getBlockState(pos.relative(d)).is(BlockTags.DIRT));
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        RandomSource random = ctx.random();
        WorldGenLevel world = ctx.level();
        BlockPos origin = ctx.origin();
        int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR, origin.getX(), origin.getZ());
        BlockPos wormyBlockPos = new BlockPos(origin.getX(), y - 2, origin.getZ());
        if(surroundedByDirt(wormyBlockPos, world)) {
            int count = random.nextIntBetweenInclusive(1, 3);
            BlockState state = world.getBlockState(wormyBlockPos);
            if(state.is(Blocks.DIRT)) {
                world.setBlock(wormyBlockPos, AnglingBlocks.WORMY_DIRT.defaultBlockState().setValue(WormyBlock.WORMS, count), Block.UPDATE_CLIENTS);
                return true;
            }else if(state.is(Blocks.MUD)) {
                world.setBlock(wormyBlockPos, AnglingBlocks.WORMY_MUD.defaultBlockState().setValue(WormyBlock.WORMS, count), Block.UPDATE_CLIENTS);
                return true;
            }
        }
        return false;
    }
}
