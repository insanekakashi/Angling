package com.eightsidedsquare.angling.common.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class NoisePatchFeatureConfig implements FeatureConfiguration {

    public final BlockStateProvider blockStateProvider;
    public final int offset;
    public final double octave;
    public final double threshold;
    public final IntProvider radius;

    public static final Codec<NoisePatchFeatureConfig> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    (BlockStateProvider.CODEC.fieldOf("to_place")).forGetter(config -> config.blockStateProvider),
                    (Codec.INT.fieldOf("offset")).orElse(-4).forGetter(config -> config.offset),
                    (Codec.DOUBLE.fieldOf("octave")).orElse(1.1d).forGetter(config -> config.octave),
                    (Codec.DOUBLE.fieldOf("threshold")).orElse(0.5d).forGetter(config -> config.threshold),
                    (IntProvider.CODEC.fieldOf("radius")).orElse(UniformInt.of(10, 20)).forGetter(config -> config.radius)
            ).apply(instance, NoisePatchFeatureConfig::new));


    public NoisePatchFeatureConfig(BlockStateProvider blockStateProvider, int offset, double octave, double threshold, IntProvider radius) {
        this.blockStateProvider = blockStateProvider;
        this.offset = offset;
        this.octave = octave;
        this.threshold = threshold;
        this.radius = radius;
    }

}
