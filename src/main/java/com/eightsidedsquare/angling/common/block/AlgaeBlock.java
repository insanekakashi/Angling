package com.eightsidedsquare.angling.common.block;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.AnglingParticles;
import com.eightsidedsquare.angling.core.tags.AnglingBlockTags;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class AlgaeBlock extends MultifaceBlock implements SimpleWaterloggedBlock, BonemealableBlock {

    private static final BooleanProperty WATERLOGGED;
    private final MultifaceSpreader grower;

    public AlgaeBlock(Properties settings) {
        super(settings);
        registerDefaultState(super.defaultBlockState().setValue(WATERLOGGED, true));
        grower = new MultifaceSpreader(new GrowChecker(this));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.getItemInHand().is(Items.GLOW_LICHEN) || super.canBeReplaced(state, context);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    }

    public boolean canGrow(BlockGetter world, BlockPos pos, BlockState state) {
        return state.getValue(WATERLOGGED) && Direction.stream().anyMatch((direction) -> this.grower.canSpreadInAnyDirection(state, world, pos, direction.getOpposite()));
    }

    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        while (canGrow(world, pos, state))
            this.grower.spreadFromRandomFaceTowardRandomDirection(state, world, pos, random);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state != null) {
            return state.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType().equals(Fluids.WATER));
        }
        return null;
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean isTranslucent(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return grower;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (canGrow(world, pos, state)) {
            world.getEntities((Entity) null,
                    new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(5),
                    entity -> entity instanceof AbstractFish fish && AnglingEntityComponents.FISH_SPAWNING.get(fish).wasFed()
            ).stream().findFirst().ifPresent(entity -> {
                AnglingEntityComponents.FISH_SPAWNING.get(entity).setWasFed(false);
                performBonemeal(world, random, pos, state);
            });
        }
        if (state.getValue(WATERLOGGED)) {
            int attempts = random.nextIntBetweenInclusive(10, 30);
            int range = 6;
            for (int i = 0; i < attempts; i++) {
                BlockPos testPos = new BlockPos(
                        pos.getX() + (int) (random.nextGaussian() * range),
                        pos.getY() + (int) (random.nextGaussian() * range),
                        pos.getZ() + (int) (random.nextGaussian() * range)
                );
                if (world.getBlockState(testPos).is(AnglingBlockTags.FILTER_FEEDERS) && world.getFluidState(testPos).is(FluidTags.WATER)) {
                    if (world.getBlockState(testPos).getBlock() instanceof FilterFeeder filterFeeder) {
                        filterFeeder.onFeed(testPos, world.getBlockState(testPos), world);
                    }
                    deteriorate(pos, world);
                }
            }
        }
    }

    public static void deteriorate(BlockPos pos, Level world) {
        BlockState state = world.getBlockState(pos);
        if (state.is(AnglingBlocks.ALGAE)) {
            List<Direction> faces = Util.toShuffledList(MultifaceBlock.availableFaces(state).stream(), world.random);
            if (!faces.isEmpty())
                faces.remove(0);
            if (!faces.isEmpty()) {
                BlockState newState = state.getBlock().defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                for (Direction d : faces) {
                    newState = newState.setValue(MultifaceBlock.getFaceProperty(d), true);
                }
                world.setBlockAndUpdate(pos, newState);
            } else {
                world.setBlockAndUpdate(pos, (state.getValue(WATERLOGGED) ? Blocks.WATER : Blocks.AIR).defaultBlockState());
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (state.getValue(WATERLOGGED) && random.nextIntBetweenInclusive(0, 5) == 0) {
            double x = random.nextGaussian() + pos.getX();
            double y = random.nextGaussian() + pos.getY();
            double z = random.nextGaussian() + pos.getZ();
            if (world.getBlockState(new BlockPos((int) x, (int) y, (int) z)).getFluidState().is(FluidTags.WATER)) {
                double velocityX = random.nextGaussian() * 0.01d;
                double velocityY = random.nextGaussian() * 0.01d;
                double velocityZ = random.nextGaussian() * 0.01d;
                world.addParticle(AnglingParticles.ALGAE, x, y, z, velocityX, velocityY, velocityZ);
            }
        }
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    static class GrowChecker extends MultifaceSpreader.DefaultSpreaderConfig {

        public GrowChecker(MultifaceBlock lichen) {
            super(lichen);
        }

        @Override
        protected boolean stateCanBeReplaced(BlockGetter world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
            return state.is(this.block) || state.is(Blocks.WATER) && state.getFluidState().isSource();
        }
    }
}
