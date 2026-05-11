package com.eightsidedsquare.angling.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class OystersBlock extends Block implements SimpleWaterloggedBlock, FilterFeeder {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final IntegerProperty TIMES_FED = IntegerProperty.create("times_fed", 0, 4);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 7, 16);

    public OystersBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, true).setValue(TIMES_FED, 0));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        if(!canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        boolean bl = fluidState.getType() == Fluids.WATER;
        return defaultBlockState().setValue(WATERLOGGED, bl);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = world.getBlockState(belowPos);
        return Block.isFaceFull(belowState.getBlockSupportShape(world, belowPos), Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, TIMES_FED);
    }

    private void setTimesFed(int timesFed, BlockPos pos, BlockState state, ServerLevel world) {
        world.setBlock(pos, state.setValue(TIMES_FED, timesFed), Block.UPDATE_ALL);
    }

    @Override
    public void onFeed(BlockPos pos, BlockState state, ServerLevel world) {
        int timesFed = state.getValue(TIMES_FED);
        if(timesFed == 4) {
            Direction.Plane.HORIZONTAL.shuffledCopy(world.getRandom()).stream()
                    .filter(d ->
                            world.getFluidState(pos.relative(d)).is(Fluids.WATER) &&
                            world.getBlockState(pos.relative(d)).is(Blocks.WATER) &&
                            canSurvive(world.getBlockState(pos.relative(d)), world, pos.relative(d)))
                    .findFirst()
                    .ifPresent(d -> {
                        world.setBlock(pos.relative(d), asBlock().defaultBlockState(), Block.UPDATE_ALL);
                        setTimesFed(0, pos, state, world);
                        createFedParticles(5, pos, world);
                    });
        }else {
            setTimesFed(timesFed + 1, pos, state, world);
            createFedParticles(1, pos, world);
        }
    }
}
