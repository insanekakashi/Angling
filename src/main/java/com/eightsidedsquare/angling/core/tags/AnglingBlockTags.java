package com.eightsidedsquare.angling.core.tags;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AnglingBlockTags {

    public static final TagKey<Block> FILTER_FEEDERS = create("filter_feeders");
    public static final TagKey<Block> STARFISH_FOOD = create("starfish_food");
    public static final TagKey<Block> CRAB_SPAWNABLE_ON = create("crab_spawnable_on");

    private static TagKey<Block> create(String id) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, id));
    }

}
