package com.eightsidedsquare.angling.core.tags;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AnglingEntityTypeTags {

    public static final TagKey<EntityType<?>> SPAWNING_FISH = create("spawning_fish");
    public static final TagKey<EntityType<?>> COMMON_ENTITIES_IN_PELICAN_BEAK = create("common_entities_in_pelican_beak");
    public static final TagKey<EntityType<?>> UNCOMMON_ENTITIES_IN_PELICAN_BEAK = create("uncommon_entities_in_pelican_beak");
    public static final TagKey<EntityType<?>> HUNTED_BY_PELICAN = create("hunted_by_pelican");
    public static final TagKey<EntityType<?>> HUNTED_BY_PELICAN_WHEN_BABY = create("hunted_by_pelican_when_baby");

    private static TagKey<EntityType<?>> create(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MOD_ID, id));
    }
}
