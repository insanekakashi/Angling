package com.eightsidedsquare.angling.core;

import com.eightsidedsquare.angling.common.entity.*;
import com.eightsidedsquare.angling.core.tags.AnglingBiomeTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingEntities {

    public static final BlockEntityType<RoeBlockEntity> ROE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "roe"),
            FabricBlockEntityTypeBuilder
                    .create(RoeBlockEntity::new)
                    .addBlock(AnglingBlocks.ROE)
                    .build()
    );

    public static final BlockEntityType<StarfishBlockEntity> STARFISH = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "starfish"),
            FabricBlockEntityTypeBuilder
                    .create(StarfishBlockEntity::new)
                    .addBlocks(AnglingBlocks.STARFISH, AnglingBlocks.DEAD_STARFISH)
                    .build()
    );

    public static final BlockEntityType<SeaSlugEggsBlockEntity> SEA_SLUG_EGGS = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "sea_slug_eggs"),
            FabricBlockEntityTypeBuilder
                    .create(SeaSlugEggsBlockEntity::new)
                    .addBlock(AnglingBlocks.SEA_SLUG_EGGS)
                    .build()
    );

    public static final BlockEntityType<AnemoneBlockEntity> ANEMONE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "anemone"),
            FabricBlockEntityTypeBuilder
                    .create(AnemoneBlockEntity::new)
                    .addBlock(AnglingBlocks.ANEMONE)
                    .build()
    );

    public static final BlockEntityType<UrchinBlockEntity> URCHIN = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "urchin"),
            FabricBlockEntityTypeBuilder
                    .create(UrchinBlockEntity::new)
                    .addBlock(AnglingBlocks.URCHIN)
                    .build()
    );

    public static final EntityType<FryEntity> FRY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "fry"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(FryEntity::new)
                    .defaultAttributes(FryEntity::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.15f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<SunfishEntity> SUNFISH = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "sunfish"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(SunfishEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.3f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<PelicanEntity> PELICAN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "pelican"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(PelicanEntity::new)
                    .defaultAttributes(PelicanEntity::createAttributes)
                    .dimensions(EntityDimensions.scalable(0.7f, 1.65f))
                    .spawnGroup(MobCategory.AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
                    .build()
    );

    public static final EntityType<NautilusEntity> NAUTILUS = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "nautilus"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(NautilusEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.4f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NautilusEntity::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<SeaSlugEntity> SEA_SLUG = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "sea_slug"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(SeaSlugEntity::new)
                    .defaultAttributes(SeaSlugEntity::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.3f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<CrabEntity> CRAB = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "crab"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(CrabEntity::new)
                    .defaultAttributes(CrabEntity::createAttributes)
                    .dimensions(EntityDimensions.scalable(0.7f, 0.4f))
                    .spawnGroup(MobCategory.CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrabEntity::canSpawn)
                    .build()
    );

    public static final EntityType<DongfishEntity> DONGFISH = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "dongfish"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(DongfishEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.3f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<CatfishEntity> CATFISH = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "catfish"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(CatfishEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.4f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<SeahorseEntity> SEAHORSE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "seahorse"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(SeahorseEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.35f, 0.6f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<BubbleEyeEntity> BUBBLE_EYE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "bubble_eye"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(BubbleEyeEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.3f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<AnomalocarisEntity> ANOMALOCARIS = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "anomalocaris"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(AnomalocarisEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.8f, 0.3f))
                    .spawnGroup(MobCategory.UNDERGROUND_WATER_CREATURE)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AnomalocarisEntity::canSpawn)
                    .build()
    );

    public static final EntityType<AnglerfishEntity> ANGLERFISH = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "anglerfish"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(AnglerfishEntity::new)
                    .defaultAttributes(AbstractFish::createAttributes)
                    .dimensions(EntityDimensions.fixed(0.8f, 0.5f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NautilusEntity::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static final EntityType<MahiMahiEntity> MAHI_MAHI = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "mahi_mahi"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(MahiMahiEntity::new)
                    .defaultAttributes(MahiMahiEntity::createAttributes)
                    .dimensions(EntityDimensions.fixed(1f, 0.8f))
                    .spawnGroup(MobCategory.WATER_AMBIENT)
                    .spawnRestriction(SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractFish::checkSurfaceWaterAnimalSpawnRules)
                    .build()
    );

    public static void init() {
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.SUNFISH_SPAWN_IN),
                MobCategory.WATER_AMBIENT, SUNFISH, 5, 2, 5
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.SEA_SLUG_SPAWN_IN),
                MobCategory.WATER_AMBIENT, SEA_SLUG, 4, 1, 3
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.NAUTILUS_SPAWN_IN),
                MobCategory.WATER_AMBIENT, NAUTILUS, 4, 1, 3
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.CATFISH_SPAWN_IN),
                MobCategory.WATER_AMBIENT, CATFISH, 1, 1, 2
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.CRAB_SPAWN_IN),
                MobCategory.CREATURE, CRAB, 8, 3, 5
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.SEAHORSE_SPAWN_IN),
                MobCategory.WATER_AMBIENT, SEAHORSE, 4, 3, 8
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.BUBBLE_EYE_SPAWN_IN),
                MobCategory.WATER_AMBIENT, BUBBLE_EYE, 4, 2, 3
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.ANOMALOCARIS_SPAWN_IN),
                MobCategory.UNDERGROUND_WATER_CREATURE, ANOMALOCARIS, 20, 1, 2
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.ANGLERFISH_SPAWN_IN),
                MobCategory.WATER_AMBIENT, ANGLERFISH, 6, 1, 2
        );
        BiomeModifications.addSpawn(
                biome -> biome.getBiomeRegistryEntry().is(AnglingBiomeTags.MAHI_MAHI_SPAWN_IN),
                MobCategory.WATER_AMBIENT, MAHI_MAHI, 4, 1, 2
        );
    }

}
