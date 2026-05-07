package com.eightsidedsquare.angling.core;


import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingItemGroups {
    public static ItemGroup ANGLING= Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID,"angling"),
            FabricItemGroup.builder()
                    .displayName(Text.literal("Angling Mod"))
                    .icon(() -> new ItemStack(AnglingItems.ANGLERFISH_BUCKET))
                    .entries(((displayContext, entries) -> {

                        addToItemGroup(AnglingItems.ANGLERFISH_BUCKET, entries);
                        addToItemGroup(AnglingItems.ANOMALOCARIS_BUCKET, entries);
                        addToItemGroup(AnglingItems.BUBBLE_EYE_BUCKET, entries);
                        addToItemGroup(AnglingItems.CATFISH_BUCKET, entries);
                        addToItemGroup(AnglingItems.CRAB_BUCKET, entries);
                        addToItemGroup(AnglingItems.DONGFISH_BUCKET, entries);
                        addToItemGroup(AnglingItems.FRY_BUCKET, entries);
                        addToItemGroup(AnglingItems.MAHI_MAHI_BUCKET, entries);
                        addToItemGroup(AnglingItems.NAUTILUS_BUCKET, entries);
                        addToItemGroup(AnglingItems.SEAHORSE_BUCKET, entries);
                        addToItemGroup(AnglingItems.SEA_SLUG_BUCKET, entries);
                        addToItemGroup(AnglingItems.SUNFISH_BUCKET, entries);
                        addToItemGroup(AnglingItems.URCHIN_BUCKET, entries);

                        addToItemGroup(AnglingItems.PELICAN_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.ANGLERFISH_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.ANOMALOCARIS_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.BUBBLE_EYE_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.CATFISH_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.CRAB_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.DONGFISH_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.FRY_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.MAHI_MAHI_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.NAUTILUS_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.SEAHORSE_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.SEA_SLUG_SPAWN_EGG, entries);
                        addToItemGroup(AnglingItems.SUNFISH_SPAWN_EGG, entries);

                        addToItemGroup(AnglingItems.SEA_SLUG_EGGS, entries);
                        addToItemGroup(AnglingItems.WORM, entries);
                        addToItemGroup(AnglingItems.ROE,  entries);
                        addToItemGroup(AnglingItems.SUNFISH, entries);
                        addToItemGroup(AnglingItems.FRIED_SUNFISH, entries);

                        addToItemGroup(Item.fromBlock(AnglingBlocks.ALGAE), entries);
                        addToItemGroup(Item.fromBlock(AnglingBlocks.ANEMONE), entries);
                        addToItemGroup(Item.fromBlock(AnglingBlocks.CLAM), entries);
                        addToItemGroup(AnglingItems.DUCKWEED, entries);
                        addToItemGroup(Item.fromBlock(AnglingBlocks.OYSTERS), entries);
                        addToItemGroup(Item.fromBlock(AnglingBlocks.PAPYRUS), entries);
                        addToItemGroup(AnglingItems.SARGASSUM, entries);
                        addToItemGroup(Item.fromBlock(AnglingBlocks.STARFISH), entries);
                        addToItemGroup(Item.fromBlock(AnglingBlocks.DEAD_STARFISH), entries);



                    }))
                    .build());

    public static void registerItemGroup() {

    }

    public static void addToItemGroup(Item item, ItemGroup.Entries entries){
        entries.add(item);
    }

}
