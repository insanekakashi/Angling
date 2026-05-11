package com.eightsidedsquare.angling.common.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoisePatchFeature extends Feature<NoisePatchFeatureConfig> {

    public NoisePatchFeature(Codec<NoisePatchFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean place(FeaturePlaceContext<NoisePatchFeatureConfig> ctx) {
        NoisePatchFeatureConfig config = ctx.config();
        BlockPos pos = ctx.origin();
        WorldGenLevel world = ctx.level();
        RandomSource random = ctx.random();
        NormalNoise sampler = NormalNoise.create(random, config.offset, config.octave);
        BlockStateProvider blockStateProvider = config.blockStateProvider;

        int radius = config.radius.sample(random);
        double threshold = config.threshold;

        for(int x = -radius; x <= radius; x++) {
            for(int z = -radius; z <= radius; z++) {
                if(x * x + z * z >= radius * radius)
                    continue;
                double value = sampler.getValue(pos.getX() + x, pos.getY(), pos.getZ() + z);
                BlockPos blockPos = pos.offset(x, 0, z);
                if(value > threshold) {

                    BlockState state = blockStateProvider.getState(random, blockPos);
                    if(state.canSurvive(world, blockPos))
                        world.setBlock(blockPos, state, Block.UPDATE_CLIENTS);
                }
            }
        }

        return true;
    }
}
