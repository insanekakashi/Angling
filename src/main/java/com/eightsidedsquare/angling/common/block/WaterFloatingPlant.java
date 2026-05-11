package com.eightsidedsquare.angling.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class WaterFloatingPlant extends BushBlock implements BonemealableBlock {
    protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public WaterFloatingPlant(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        FluidState fluidState2 = world.getFluidState(pos.above());
        return fluidState.getType() == Fluids.WATER && fluidState2.getType() == Fluids.EMPTY;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        for(Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos offsetPos = pos.relative(d).below();
            if(mayPlaceOn(world.getBlockState(offsetPos), world, offsetPos) && world.getBlockState(offsetPos.above()).isAir()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        Direction.Plane.HORIZONTAL.shuffledCopy(random).stream().filter(d -> {
                    BlockPos offsetPos = pos.relative(d).below();
                    return mayPlaceOn(world.getBlockState(offsetPos), world, offsetPos) && world.getBlockState(offsetPos.above()).isAir();
                })
                .findFirst().ifPresent(d -> {
                    BlockPos offsetPos = pos.relative(d);
                    world.setBlock(offsetPos, asBlock().defaultBlockState(), Block.UPDATE_ALL);
        });
    }
}
