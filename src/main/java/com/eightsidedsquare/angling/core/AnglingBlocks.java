package com.eightsidedsquare.angling.core;

import com.eightsidedsquare.angling.common.block.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingBlocks {
    private static final Map<Block, ResourceLocation> BLOCKS = new LinkedHashMap<>();
    private static final Map<Item, ResourceLocation> ITEMS = new LinkedHashMap<>();

    public static final Block ROE = create("roe", new RoeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).noCollission().noOcclusion().instabreak().sound(SoundType.FROGSPAWN)), null);
    public static final Block SEA_SLUG_EGGS = create("sea_slug_eggs", new SeaSlugEggsBlock(BlockBehaviour.Properties.copy(ROE).dynamicShape()), null);
    public static final Block DUCKWEED = create("duckweed", new WaterFloatingPlant(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instabreak().noOcclusion().noCollission().sound(SoundType.WET_GRASS)), null);
    public static final Block ALGAE = create("algae", new AlgaeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.FROGSPAWN).noCollission().noOcclusion().strength(0.1f).randomTicks()), CreativeModeTabs.NATURAL_BLOCKS);
    public static final Block WORMY_DIRT = create("wormy_dirt", new WormyDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT)), CreativeModeTabs.BUILDING_BLOCKS);
    public static final Block WORMY_MUD = create("wormy_mud", new WormyMudBlock(BlockBehaviour.Properties.copy(Blocks.MUD)), CreativeModeTabs.BUILDING_BLOCKS);
    public static final Block OYSTERS = create("oysters", new OystersBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.5f).noOcclusion().sound(AnglingSounds.SHELL_SOUND_GROUP)), CreativeModeTabs.NATURAL_BLOCKS);
    public static final Block STARFISH = create("starfish", new StarfishBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.1f).noOcclusion().noCollission().sound(AnglingSounds.SHELL_SOUND_GROUP).randomTicks(), false), CreativeModeTabs.NATURAL_BLOCKS);
    public static final Block DEAD_STARFISH = create("dead_starfish", new StarfishBlock(BlockBehaviour.Properties.copy(STARFISH), true), CreativeModeTabs.NATURAL_BLOCKS);
    public static final Block CLAM = create("clam", new ClamBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).sound(AnglingSounds.SHELL_SOUND_GROUP).strength(0.05f).noOcclusion()), CreativeModeTabs.NATURAL_BLOCKS);
    public static final Block ANEMONE = create("anemone", new AnemoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.1f).noOcclusion().noCollission().sound(SoundType.SLIME_BLOCK).randomTicks()), CreativeModeTabs.NATURAL_BLOCKS);
    public static final Block URCHIN = create("urchin", new UrchinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(0.1f).noOcclusion().noCollission().sound(AnglingSounds.SHELL_SOUND_GROUP)), null);
    public static final Block SARGASSUM = create("sargassum", new WaterFloatingPlant(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instabreak().noOcclusion().noCollission().sound(SoundType.WET_GRASS)), null);
    public static final Block PAPYRUS = create("papyrus", new PapyrusBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0).instabreak().noOcclusion().noCollission().sound(SoundType.AZALEA).randomTicks().dynamicShape()), CreativeModeTabs.NATURAL_BLOCKS);

    private static <T extends Block> T create(String name, T block, ResourceKey<CreativeModeTab> itemGroup) {
        ResourceLocation id = new ResourceLocation(MOD_ID, name);
        BLOCKS.put(block, id);
        if (itemGroup != null) {
            BlockItem item = new BlockItem(block, new Item.Properties());
            ITEMS.put(item, id);
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.accept(item));
        }
        return block;
    }


    public static void init() {
        BLOCKS.forEach((block, id) -> Registry.register(BuiltInRegistries.BLOCK, id, block));
        ITEMS.forEach((item, id) -> Registry.register(BuiltInRegistries.ITEM, id, item));
    }
}
