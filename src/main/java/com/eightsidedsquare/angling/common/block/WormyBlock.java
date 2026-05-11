package com.eightsidedsquare.angling.common.block;

import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingParticles;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface WormyBlock {

    IntegerProperty WORMS = IntegerProperty.create("worms", 1, 3);

    BlockState getDefaultBlockState();

    default InteractionResult addOrRemoveWorms(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.getMainHandItem().isEmpty() && stack.isEmpty()) {
            decrementWorms(state, pos, world);
            player.addItem(new ItemStack(AnglingItems.WORM));
            world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, AnglingSounds.ITEM_WORM_USE, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }else if(stack.is(AnglingItems.WORM) && state.getValue(WORMS) < 3) {
            if(!player.getAbilities().instabuild)
                stack.shrink(1);
            incrementWorms(state, pos, world);
            world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, AnglingSounds.ITEM_WORM_USE, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }

    default void incrementWorms(BlockState state, BlockPos pos, Level world) {
        int worms = state.getValue(WORMS);
        if(worms < 3) {
            world.setBlockAndUpdate(pos, state.setValue(WORMS, worms + 1));
        }
    }

    default void decrementWorms(BlockState state, BlockPos pos, Level world) {
        int worms = state.getValue(WORMS);
        if(worms == 1) {
            world.setBlockAndUpdate(pos, getDefaultBlockState());
        }else {
            world.setBlockAndUpdate(pos, state.setValue(WORMS, worms - 1));
        }
    }

    default void appendWormProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WORMS);
    }

    default void tickWorms(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(state.getValue(WORMS) < 3 && random.nextInt(8) == 0) {
            if(Direction.stream().filter(d -> world.getBlockState(pos.relative(d)).is(BlockTags.DIRT)).toList().size() == Direction.values().length) {
                incrementWorms(state, pos, world);
            }
        }
    }

    default void spawnWormParticles(Level world, BlockPos pos, RandomSource random) {
        if(random.nextInt(8) == 0) {
            BlockPos.MutableBlockPos mutable = pos.mutable();
            mutable.move(Direction.UP);
            while (world.getBlockState(mutable).is(BlockTags.DIRT)) {
                mutable.move(Direction.UP);
            }
            if (!world.getBlockState(mutable).isRedstoneConductor(world, pos) &&
                    ((world.isRaining() && world.canSeeSky(mutable.above())) || world.getFluidState(mutable).is(FluidTags.WATER))) {
                double x = mutable.getX() + 0.5d + random.nextGaussian() * 0.3f;
                double y = mutable.getY();
                double z = mutable.getZ() + 0.5d + random.nextGaussian() * 0.3f;
                world.addParticle(AnglingParticles.WORM, x, y, z, 0, 0, 0);
            }
        }
    }
}
