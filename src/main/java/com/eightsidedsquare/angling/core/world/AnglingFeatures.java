package com.eightsidedsquare.angling.core.world;

import com.eightsidedsquare.angling.common.feature.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingFeatures {

    public static final Feature<SimpleBlockConfiguration> WATERLOGGABLE_PATCH = register("waterloggable_patch", new WaterloggablePatchFeature(SimpleBlockConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> WORMY_BLOCK = register("wormy_block", new WormyBlockFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoisePatchFeatureConfig> NOISE_PATCH = register("noise_patch", new NoisePatchFeature(NoisePatchFeatureConfig.CODEC));
    public static final Feature<RandomPatchConfiguration> WATER_ADJACENT_PATCH = register("water_adjacent_patch", new WaterAdjacentPatchFeature(RandomPatchConfiguration.CODEC));

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(BuiltInRegistries.FEATURE, new ResourceLocation(MOD_ID, name), feature);
    }

    public static void init() {}
}
