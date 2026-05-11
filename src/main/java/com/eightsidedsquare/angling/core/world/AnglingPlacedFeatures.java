package com.eightsidedsquare.angling.core.world;

import com.eightsidedsquare.angling.core.tags.AnglingBiomeTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import java.util.List;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingPlacedFeatures {
    public static final ResourceKey<PlacedFeature> PATCH_DUCKWEED_PLACED_KEY =registerKey("patch_duckweed");
    public static final ResourceKey<PlacedFeature> PATCH_SARGASSUM_PLACED_KEY =registerKey("patch_sargasssum");
    public static final ResourceKey<PlacedFeature> OYSTER_REEF_PLACED_KEY =registerKey("oyster_reef");
    public static final ResourceKey<PlacedFeature> CLAMS_PLACED_KEY =registerKey("clams");
    public static final ResourceKey<PlacedFeature> WORMY_BLOCK_PLACED_KEY =registerKey("wormy_block");
    public static final ResourceKey<PlacedFeature> PATCH_PAPYRUS_PLACED_KEY =registerKey("patch_papyrus");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.lookup(Registries.CONFIGURED_FEATURE);
            register(context, PATCH_DUCKWEED_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.PATCH_DUCKWEED_CONFIGURED_KEY),
                    List.of(RarityFilter.onAverageOnceEvery(3),
                            InSquarePlacement.spread(),
                            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                            BiomeFilter.biome()));

            register(context, PATCH_SARGASSUM_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.PATCH_SARGASSUM_CONFIGURED_KEY),
                    List.of(RarityFilter.onAverageOnceEvery(70),
                            InSquarePlacement.spread(),
                            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                            BiomeFilter.biome())
            );

            register(context, OYSTER_REEF_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.OYSTER_REEF_CONFIGURED_KEY),
                    List.of(RarityFilter.onAverageOnceEvery(14),
                            InSquarePlacement.spread(),
                            PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                            BiomeFilter.biome())
                    );

            register(context, CLAMS_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.CLAMS_CONFIGURED_KEY),
                    List.of(RarityFilter.onAverageOnceEvery(12),
                            InSquarePlacement.spread(),
                            PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                            BiomeFilter.biome())
                    );

            register(context, WORMY_BLOCK_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.WORMY_BLOCK_CONFIGURED_KEY),
                    List.of(CountPlacement.of(2),
                            InSquarePlacement.spread(),
                            PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                            BiomeFilter.biome())
            );

            register(context, PATCH_PAPYRUS_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.PATCH_PAPYRUS_CONFIGURED_KEY),
                    List.of(CountPlacement.of(2),
                            InSquarePlacement.spread(),
                            PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                            BiomeFilter.biome())
                        );

        //        public static final RegistryKey<PlacedFeature> PATCH_DUCKWEED = register("patch_duckweed",
////            AnglingConfiguredFeatures.PATCH_DUCKWEED,
////            List.of(RarityFilterPlacementModifier.of(3),
////                    SquarePlacementModifier.of(),
////                    PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
////                    BiomePlacementModifier.of())
//        );
//
//        public static final RegistryKey<PlacedFeature> PATCH_SARGASSUM = register("patch_sargassum",
////            AnglingConfiguredFeatures.PATCH_SARGASSUM,
////            List.of(RarityFilterPlacementModifier.of(70),
////                    SquarePlacementModifier.of(),
////                    PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
////                    BiomePlacementModifier.of())
//        );
//
//        public static final RegistryKey<PlacedFeature> OYSTER_REEF = register("oyster_reef",
////                    AnglingConfiguredFeatures.OYSTER_REEF,
////            List.of(RarityFilterPlacementModifier.of(14),
////                    SquarePlacementModifier.of(),
////                    PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
////                    BiomePlacementModifier.of())
//        );
//
//        public static final RegistryKey<PlacedFeature> CLAMS = register("clams",
////            AnglingConfiguredFeatures.CLAMS,
////            List.of(RarityFilterPlacementModifier.of(12),
////                    SquarePlacementModifier.of(),
////                    PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
////                    BiomePlacementModifier.of())
//        );
//
//        public static final RegistryKey<PlacedFeature> WORMY_BLOCK = register("wormy_block",
////            AnglingConfiguredFeatures.WORMY_BLOCK,
////            List.of(CountPlacementModifier.of(2),
////                    SquarePlacementModifier.of(),
////                    PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
////                    BiomePlacementModifier.of())
//        );
//
//        public static final RegistryKey<PlacedFeature> PATCH_PAPYRUS = register("patch_papyrus",
//                AnglingConfiguredFeatures.PATCH_PAPYRUS,
//                List.of(CountPlacementModifier.of(2),
//                        SquarePlacementModifier.of(),
//                        PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
//                        BiomePlacementModifier.of())
//        );
    }

    public static ResourceKey<PlacedFeature> registerKey(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MOD_ID + ":" + id));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static void addFeature(ResourceKey<PlacedFeature> key, GenerationStep.Decoration step, TagKey<Biome> tag) {
        BiomeModifications.addFeature(ctx -> ctx.getBiomeRegistryEntry().is(tag), step, key);
    }

    public static void init() {
        addFeature(OYSTER_REEF_PLACED_KEY, GenerationStep.Decoration.VEGETAL_DECORATION, AnglingBiomeTags.OYSTER_REEF_BIOMES);
        addFeature(CLAMS_PLACED_KEY, GenerationStep.Decoration.VEGETAL_DECORATION, AnglingBiomeTags.CLAMS_BIOMES);
        addFeature(PATCH_DUCKWEED_PLACED_KEY, GenerationStep.Decoration.VEGETAL_DECORATION, AnglingBiomeTags.DUCKWEED_BIOMES);
        addFeature(PATCH_SARGASSUM_PLACED_KEY, GenerationStep.Decoration.VEGETAL_DECORATION, AnglingBiomeTags.SARGASSUM_BIOMES);
        addFeature(PATCH_PAPYRUS_PLACED_KEY, GenerationStep.Decoration.VEGETAL_DECORATION, AnglingBiomeTags.PAPYRUS_BIOMES);
        addFeature(WORMY_BLOCK_PLACED_KEY, GenerationStep.Decoration.UNDERGROUND_ORES, BiomeTags.IS_OVERWORLD);
    }
}
