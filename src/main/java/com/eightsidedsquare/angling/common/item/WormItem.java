package com.eightsidedsquare.angling.common.item;

import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WormItem extends Item {
    public WormItem(Properties settings) {
        super(settings);
    }

    private InteractionResult addWorms(BlockState state, BlockPos pos, ItemStack stack, Level world) {
        stack.shrink(1);
        world.setBlockAndUpdate(pos, state);
        world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, AnglingSounds.ITEM_WORM_USE, SoundSource.BLOCKS, 1, 1);
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = ctx.getItemInHand();
        if(state.is(Blocks.DIRT)) {
            return addWorms(AnglingBlocks.WORMY_DIRT.defaultBlockState(), pos, stack, world);
        }else if(state.is(Blocks.MUD)) {
            return addWorms(AnglingBlocks.WORMY_MUD.defaultBlockState(), pos, stack, world);
        }
        return super.useOn(ctx);
    }
}
