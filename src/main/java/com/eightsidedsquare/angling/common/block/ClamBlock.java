package com.eightsidedsquare.angling.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ClamBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, FilterFeeder {

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape X_SHAPE = Block.box(5.5, 0, 4, 10.5, 2, 12);
    private static final VoxelShape Z_SHAPE = Block.box(4, 0, 5.5, 12, 2, 10.5);

    public ClamBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, true));
    }

    @Override
    public float getMaxHorizontalOffset() {
        return super.getMaxHorizontalOffset();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        boolean bl = fluidState.getType() == Fluids.WATER;
        return defaultBlockState().setValue(WATERLOGGED, bl).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
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
        Vec3 offset = state.getOffset(world, pos);
        return (state.getValue(FACING).getAxis().equals(Direction.Axis.X) ? X_SHAPE : Z_SHAPE).move(offset.x(), offset.y(), offset.z());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public void onFeed(BlockPos pos, BlockState state, ServerLevel world) {
        if(state.getValue(WATERLOGGED) && world.getRandom().nextFloat() < 0.25f) {
            Direction.Plane.HORIZONTAL.shuffledCopy(world.getRandom()).stream()
                    .filter(d -> world.getFluidState(pos.relative(d)).is(FluidTags.WATER)
                            && world.getBlockState(pos.relative(d)).is(Blocks.WATER)
                            && canSurvive(world.getBlockState(pos.relative(d)), world, pos.relative(d)))
                    .findFirst()
                    .ifPresent(d -> {
                        world.setBlockAndUpdate(pos.relative(d), defaultBlockState().setValue(FACING, Direction.Plane.HORIZONTAL.getRandomDirection(world.getRandom())));
                        createFedParticles(5, pos, world);
                    });
        }
    }
}
