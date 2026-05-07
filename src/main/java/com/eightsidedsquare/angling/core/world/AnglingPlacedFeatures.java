package com.eightsidedsquare.angling.core.world;

import com.eightsidedsquare.angling.core.tags.AnglingBiomeTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingPlacedFeatures {
    public static final RegistryKey<PlacedFeature> PATCH_DUCKWEED_PLACED_KEY =registerKey("patch_duckweed");
    public static final RegistryKey<PlacedFeature> PATCH_SARGASSUM_PLACED_KEY =registerKey("patch_sargasssum");
    public static final RegistryKey<PlacedFeature> OYSTER_REEF_PLACED_KEY =registerKey("oyster_reef");
    public static final RegistryKey<PlacedFeature> CLAMS_PLACED_KEY =registerKey("clams");
    public static final RegistryKey<PlacedFeature> WORMY_BLOCK_PLACED_KEY =registerKey("wormy_block");
    public static final RegistryKey<PlacedFeature> PATCH_PAPYRUS_PLACED_KEY =registerKey("patch_papyrus");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
            register(context, PATCH_DUCKWEED_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.PATCH_DUCKWEED_CONFIGURED_KEY),
                    List.of(RarityFilterPlacementModifier.of(3),
                            SquarePlacementModifier.of(),
                            PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
                            BiomePlacementModifier.of()));

            register(context, PATCH_SARGASSUM_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.PATCH_SARGASSUM_CONFIGURED_KEY),
                    List.of(RarityFilterPlacementModifier.of(70),
                            SquarePlacementModifier.of(),
                            PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
                            BiomePlacementModifier.of())
            );

            register(context, OYSTER_REEF_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.OYSTER_REEF_CONFIGURED_KEY),
                    List.of(RarityFilterPlacementModifier.of(14),
                            SquarePlacementModifier.of(),
                            PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
                            BiomePlacementModifier.of())
                    );

            register(context, CLAMS_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.CLAMS_CONFIGURED_KEY),
                    List.of(RarityFilterPlacementModifier.of(12),
                            SquarePlacementModifier.of(),
                            PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
                            BiomePlacementModifier.of())
                    );

            register(context, WORMY_BLOCK_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.WORMY_BLOCK_CONFIGURED_KEY),
                    List.of(CountPlacementModifier.of(2),
                            SquarePlacementModifier.of(),
                            PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
                            BiomePlacementModifier.of())
            );

            register(context, PATCH_PAPYRUS_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(AnglingConfiguredFeatures.PATCH_PAPYRUS_CONFIGURED_KEY),
                    List.of(CountPlacementModifier.of(2),
                            SquarePlacementModifier.of(),
                            PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
                            BiomePlacementModifier.of())
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

    public static RegistryKey<PlacedFeature> registerKey(String id) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MOD_ID + ":" + id));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static void addFeature(RegistryKey<PlacedFeature> key, GenerationStep.Feature step, TagKey<Biome> tag) {
        BiomeModifications.addFeature(ctx -> ctx.getBiomeRegistryEntry().isIn(tag), step, key);
    }

    public static void init() {
        addFeature(OYSTER_REEF_PLACED_KEY, GenerationStep.Feature.VEGETAL_DECORATION, AnglingBiomeTags.OYSTER_REEF_BIOMES);
        addFeature(CLAMS_PLACED_KEY, GenerationStep.Feature.VEGETAL_DECORATION, AnglingBiomeTags.CLAMS_BIOMES);
        addFeature(PATCH_DUCKWEED_PLACED_KEY, GenerationStep.Feature.VEGETAL_DECORATION, AnglingBiomeTags.DUCKWEED_BIOMES);
        addFeature(PATCH_SARGASSUM_PLACED_KEY, GenerationStep.Feature.VEGETAL_DECORATION, AnglingBiomeTags.SARGASSUM_BIOMES);
        addFeature(PATCH_PAPYRUS_PLACED_KEY, GenerationStep.Feature.VEGETAL_DECORATION, AnglingBiomeTags.PAPYRUS_BIOMES);
        addFeature(WORMY_BLOCK_PLACED_KEY, GenerationStep.Feature.UNDERGROUND_ORES, BiomeTags.IS_OVERWORLD);
    }
}
