package com.eightsidedsquare.angling.common.item;

import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.tags.AnglingEntityTypeTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class RoeBlockItem extends BlockItem {

    public RoeBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        CompoundTag nbt = getBlockEntityData(stack);
        if(nbt != null && nbt.contains("EntityType", Tag.TAG_STRING)) {
            String key = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(nbt.getString("EntityType"))).getDescriptionId();
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }



    public void appendStacks(CreativeModeTab group, NonNullList<ItemStack> stacks) {
            BuiltInRegistries.ENTITY_TYPE.stream()
                    .filter(type -> type.is(AnglingEntityTypeTags.SPAWNING_FISH))
                    .map(SpawnEggItem::byId)
                    .filter(Objects::nonNull)
                    .forEach(egg -> {
                        ItemStack stack = new ItemStack(AnglingItems.ROE);
                        CompoundTag nbt = new CompoundTag();
                        nbt.putInt("PrimaryColor", egg.getColor(0));
                        nbt.putInt("SecondaryColor", egg.getColor(0));
                        nbt.putString("EntityType", BuiltInRegistries.ENTITY_TYPE.getKey(egg.getType(null)).toString());
                        setBlockEntityData(stack, AnglingEntities.ROE, nbt);
                        stacks.add(stack);
                    });
    }
}
