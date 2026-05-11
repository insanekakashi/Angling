package com.eightsidedsquare.angling.core.world;

import com.eightsidedsquare.angling.common.feature.NoisePatchFeatureConfig;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class  AnglingConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_DUCKWEED_CONFIGURED_KEY = registerKey("patch_duckweed");
    public static final ResourceKey<ConfiguredFeature<?,?>> PATCH_SARGASSUM_CONFIGURED_KEY = registerKey("patch_sargasssum");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OYSTER_REEF_CONFIGURED_KEY = registerKey("oyster_reef");
    public static final ResourceKey<ConfiguredFeature<?,?>> CLAMS_CONFIGURED_KEY = registerKey("clams");
    public static final ResourceKey<ConfiguredFeature<?,?>> WORMY_BLOCK_CONFIGURED_KEY = registerKey("wormy_block");
    public static final ResourceKey<ConfiguredFeature<?,?>> PATCH_PAPYRUS_CONFIGURED_KEY = registerKey("patch_papyrus");





    private static final WeightedStateProvider PAPYRUS_BLOCK_STATE_PROVIDER = new WeightedStateProvider(
            SimpleWeightedRandomList.<BlockState>builder()
                    .add(AnglingBlocks.PAPYRUS.defaultBlockState().setValue(BlockStateProperties.AGE_2, 0), 1)
                    .add(AnglingBlocks.PAPYRUS.defaultBlockState().setValue(BlockStateProperties.AGE_2, 1), 2)
                    .add(AnglingBlocks.PAPYRUS.defaultBlockState().setValue(BlockStateProperties.AGE_2, 2), 3)
                    .build()
    );

    private static final WeightedStateProvider CLAMS_BLOCK_STATE_PROVIDER = new WeightedStateProvider(
            SimpleWeightedRandomList.<BlockState>builder()
                    .add(AnglingBlocks.CLAM.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 1)
                    .add(AnglingBlocks.CLAM.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), 1)
                    .add(AnglingBlocks.CLAM.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 1)
                    .add(AnglingBlocks.CLAM.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), 1)
                    .build()
    );

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, PATCH_DUCKWEED_CONFIGURED_KEY, AnglingFeatures.NOISE_PATCH, new NoisePatchFeatureConfig(
                    BlockStateProvider.simple(AnglingBlocks.DUCKWEED),
                    -2,
                    2d,
                    0.35d,
                    UniformInt.of(6, 12)
            ));

        register(context, PATCH_SARGASSUM_CONFIGURED_KEY,AnglingFeatures.NOISE_PATCH, new NoisePatchFeatureConfig(
                    BlockStateProvider.simple(AnglingBlocks.SARGASSUM),
                    -3,
                    2d,
                    0.25d,
                    UniformInt.of(8, 16)
            ));

        register(context, OYSTER_REEF_CONFIGURED_KEY, AnglingFeatures.WATERLOGGABLE_PATCH,
                new SimpleBlockConfiguration(BlockStateProvider.simple(AnglingBlocks.OYSTERS)
                ));

        register(context, CLAMS_CONFIGURED_KEY, AnglingFeatures.WATERLOGGABLE_PATCH,
                new SimpleBlockConfiguration(
                        CLAMS_BLOCK_STATE_PROVIDER
                ));

        register(context, WORMY_BLOCK_CONFIGURED_KEY, AnglingFeatures.WORMY_BLOCK, new NoneFeatureConfiguration());

        register(context, PATCH_PAPYRUS_CONFIGURED_KEY, AnglingFeatures.WATER_ADJACENT_PATCH,
                new RandomPatchConfiguration(
                    64,
                    6,
                    2,
                    PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(PAPYRUS_BLOCK_STATE_PROVIDER))
                ));

    }

//    public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> OYSTER_REEF =
//            register("oyster_reef", AnglingFeatures.WATERLOGGABLE_PATCH,
//                    new SimpleBlockFeatureConfig(BlockStateProvider.of(AnglingBlocks.OYSTERS)));
//
//    public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> CLAMS =
//            register("clams", AnglingFeatures.WATERLOGGABLE_PATCH,
//                    new SimpleBlockFeatureConfig(
//                            new WeightedBlockStateProvider(DataPool.<BlockState>builder()
//                                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH), 1)
//                                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST), 1)
//                                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.SOUTH), 1)
//                                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.WEST), 1)
//                                    .build())));
//
//    public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> WORMY_BLOCK =
//            register("wormy_block", AnglingFeatures.WORMY_BLOCK, new DefaultFeatureConfig());
//
//    public static final RegistryEntry<ConfiguredFeature<NoisePatchFeatureConfig, ?>> PATCH_SARGASSUM =
//            register("patch_sargassum", AnglingFeatures.NOISE_PATCH, new NoisePatchFeatureConfig(
//                    BlockStateProvider.of(AnglingBlocks.SARGASSUM),
//                    -3,
//                    2d,
//                    0.25d,
//                    UniformIntProvider.create(8, 16)
//            ));
//
//    public static final RegistryEntry<ConfiguredFeature<NoisePatchFeatureConfig, ?>> PATCH_DUCKWEED =
//            register("patch_duckweed", AnglingFeatures.NOISE_PATCH, new NoisePatchFeatureConfig(
//                    BlockStateProvider.of(AnglingBlocks.DUCKWEED),
//                    -2,
//                    2d,
//                    0.35d,
//                    UniformIntProvider.create(6, 12)
//            ));
//
//    public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_PAPYRUS =
//            register("patch_papyrus", AnglingFeatures.WATER_ADJACENT_PATCH, new RandomPatchFeatureConfig(
//                    64,
//                    6,
//                    2,
//                    PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(PAPYRUS_BLOCK_STATE_PROVIDER))
//            ));

//    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC config) {
//        return ConfiguredFeatures.register(MOD_ID + ":" + id, feature, config);
//    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID + ":" + name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                   ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
