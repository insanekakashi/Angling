package com.eightsidedsquare.angling.common.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.material.Fluids;

public class WaterloggablePatchFeature extends Feature<SimpleBlockConfiguration> {
    public WaterloggablePatchFeature(Codec<SimpleBlockConfiguration> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> ctx) {
        boolean bl = false;
        RandomSource random = ctx.random();
        WorldGenLevel structureWorldAccess = ctx.level();
        BlockPos blockPos = ctx.origin();
        int count = random.nextIntBetweenInclusive(16, 32);
        int range = 6;
        for(int i = 0; i < count; i++) {
            int dx = random.nextInt(range) - random.nextInt(range);
            int dz = random.nextInt(range) - random.nextInt(range);
            int y = structureWorldAccess.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX() + dx, blockPos.getZ() + dz);
            BlockPos blockPos2 = new BlockPos(blockPos.getX() + dx, y, blockPos.getZ() + dz);
            BlockState state = ctx.config().toPlace().getState(random, blockPos2);
            if (state.canSurvive(structureWorldAccess, blockPos2) &&
                    !structureWorldAccess.getBlockState(blockPos2.above()).is(Blocks.TALL_SEAGRASS) &&
                    !structureWorldAccess.getBlockState(blockPos2.below()).is(BlockTags.ICE)) {
                if(state.hasProperty(BlockStateProperties.WATERLOGGED))
                    state = state.setValue(BlockStateProperties.WATERLOGGED, structureWorldAccess.getFluidState(blockPos2).is(Fluids.WATER));
                structureWorldAccess.setBlock(blockPos2, state, Block.UPDATE_CLIENTS);

                bl = true;
            }
        }

        return bl;
    }
}
