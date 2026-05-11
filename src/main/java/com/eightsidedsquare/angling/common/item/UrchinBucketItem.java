package com.eightsidedsquare.angling.common.item;

import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class UrchinBucketItem extends BlockItem {

    public UrchinBucketItem(Properties settings) {
        super(AnglingBlocks.URCHIN, settings);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
        if(player != null && !player.isCreative() && !player.addItem(new ItemStack(Items.BUCKET)))
            player.drop(new ItemStack(Items.BUCKET), true);
        world.scheduleTick(pos, world.getFluidState(pos).getType(), 1);
        if(world.dimensionType().ultraWarm())
            world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.WATERLOGGED, false));
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state) {
        return SoundEvents.BUCKET_EMPTY;
    }
}
