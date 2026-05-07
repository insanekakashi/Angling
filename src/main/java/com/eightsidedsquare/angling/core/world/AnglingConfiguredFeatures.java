package com.eightsidedsquare.angling.core.world;

import com.eightsidedsquare.angling.common.feature.NoisePatchFeatureConfig;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class  AnglingConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_DUCKWEED_CONFIGURED_KEY = registerKey("patch_duckweed");
    public static final RegistryKey<ConfiguredFeature<?,?>> PATCH_SARGASSUM_CONFIGURED_KEY = registerKey("patch_sargasssum");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OYSTER_REEF_CONFIGURED_KEY = registerKey("oyster_reef");
    public static final RegistryKey<ConfiguredFeature<?,?>> CLAMS_CONFIGURED_KEY = registerKey("clams");
    public static final RegistryKey<ConfiguredFeature<?,?>> WORMY_BLOCK_CONFIGURED_KEY = registerKey("wormy_block");
    public static final RegistryKey<ConfiguredFeature<?,?>> PATCH_PAPYRUS_CONFIGURED_KEY = registerKey("patch_papyrus");





    private static final WeightedBlockStateProvider PAPYRUS_BLOCK_STATE_PROVIDER = new WeightedBlockStateProvider(
            DataPool.<BlockState>builder()
                    .add(AnglingBlocks.PAPYRUS.getDefaultState().with(Properties.AGE_2, 0), 1)
                    .add(AnglingBlocks.PAPYRUS.getDefaultState().with(Properties.AGE_2, 1), 2)
                    .add(AnglingBlocks.PAPYRUS.getDefaultState().with(Properties.AGE_2, 2), 3)
                    .build()
    );

    private static final WeightedBlockStateProvider CLAMS_BLOCK_STATE_PROVIDER = new WeightedBlockStateProvider(
            DataPool.<BlockState>builder()
                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH), 1)
                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST), 1)
                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.SOUTH), 1)
                    .add(AnglingBlocks.CLAM.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.WEST), 1)
                    .build()
    );

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, PATCH_DUCKWEED_CONFIGURED_KEY, AnglingFeatures.NOISE_PATCH, new NoisePatchFeatureConfig(
                    BlockStateProvider.of(AnglingBlocks.DUCKWEED),
                    -2,
                    2d,
                    0.35d,
                    UniformIntProvider.create(6, 12)
            ));

        register(context, PATCH_SARGASSUM_CONFIGURED_KEY,AnglingFeatures.NOISE_PATCH, new NoisePatchFeatureConfig(
                    BlockStateProvider.of(AnglingBlocks.SARGASSUM),
                    -3,
                    2d,
                    0.25d,
                    UniformIntProvider.create(8, 16)
            ));

        register(context, OYSTER_REEF_CONFIGURED_KEY, AnglingFeatures.WATERLOGGABLE_PATCH,
                new SimpleBlockFeatureConfig(BlockStateProvider.of(AnglingBlocks.OYSTERS)
                ));

        register(context, CLAMS_CONFIGURED_KEY, AnglingFeatures.WATERLOGGABLE_PATCH,
                new SimpleBlockFeatureConfig(
                        CLAMS_BLOCK_STATE_PROVIDER
                ));

        register(context, WORMY_BLOCK_CONFIGURED_KEY, AnglingFeatures.WORMY_BLOCK, new DefaultFeatureConfig());

        register(context, PATCH_PAPYRUS_CONFIGURED_KEY, AnglingFeatures.WATER_ADJACENT_PATCH,
                new RandomPatchFeatureConfig(
                    64,
                    6,
                    2,
                    PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(PAPYRUS_BLOCK_STATE_PROVIDER))
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

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(MOD_ID + ":" + name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
