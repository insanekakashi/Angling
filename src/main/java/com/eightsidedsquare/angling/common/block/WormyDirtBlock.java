package com.eightsidedsquare.angling.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("deprecation")
public class WormyDirtBlock extends Block implements WormyBlock {
    public WormyDirtBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(WORMS, 1));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(WORMS) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        tickWorms(state, world, pos, random);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        spawnWormParticles(world, pos, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        appendWormProperties(builder);
    }

    @Override
    public BlockState getDefaultBlockState() {
        return Blocks.DIRT.defaultBlockState();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return addOrRemoveWorms(state, world, pos, player, hand);
    }
}
