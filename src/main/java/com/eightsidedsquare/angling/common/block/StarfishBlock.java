package com.eightsidedsquare.angling.common.block;

import com.eightsidedsquare.angling.common.entity.StarfishBlockEntity;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.tags.AnglingBlockTags;
import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class StarfishBlock extends DirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {

    private final boolean dead;

    private static final ImmutableList<Integer> COLORS = ImmutableList.of(
            0xe05a30,
            0xe0b330,
            0x83e030,
            0xe03030,
            0xe03053,
            0x3059e0,
            0xcaceed,
            0x413854
    );
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape DOWN_SHAPE = Block.box(3, 14, 3, 13, 16, 13);
    private static final VoxelShape UP_SHAPE = Block.box(3, 0, 3, 13, 2, 13);
    private static final VoxelShape EAST_SHAPE = Block.box(0, 3, 3, 2, 13, 13);
    private static final VoxelShape WEST_SHAPE = Block.box(14, 3, 3, 16, 13, 13);
    private static final VoxelShape SOUTH_SHAPE = Block.box(3, 3, 0, 13, 13, 2);
    private static final VoxelShape NORTH_SHAPE = Block.box(3, 3, 14, 13, 13, 16);

    public StarfishBlock(Properties settings, boolean dead) {
        super(settings);
        this.dead = dead;
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, true));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(!dead && state.getValue(WATERLOGGED)) {
            Direction.allShuffled(random).stream()
                    .filter(d -> world.getBlockState(pos.relative(d)).is(AnglingBlockTags.STARFISH_FOOD) && world.getFluidState(pos).is(FluidTags.WATER))
                    .findFirst()
                    .ifPresent(d -> {
                        BlockPos childPos = pos.relative(d);
                        world.destroyBlock(childPos, false);
                        createChild(childPos, pos, world, random);
                    });
        }
    }

    private void createChild(BlockPos childPos, BlockPos pos, ServerLevel world, RandomSource random) {
        Direction.allShuffled(random).stream()
                .filter(direction -> canSurvive(defaultBlockState().setValue(FACING, direction.getOpposite()), world, childPos))
                .findFirst().ifPresent(direction -> {
                    world.setBlock(childPos, asBlock().defaultBlockState().setValue(FACING, direction.getOpposite()), Block.UPDATE_ALL);
                    if(random.nextFloat() < 0.1f) {
                        randomize(world, childPos, random);
                    }else if(world.getBlockEntity(pos) instanceof StarfishBlockEntity entity &&
                            world.getBlockEntity(childPos) instanceof StarfishBlockEntity childEntity) {
                        childEntity.setColor(entity.getColor());
                        childEntity.setRainbow(entity.isRainbow());
                    }
                    world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 5, 0.25d, 0.25d, 0.25d, 0);
                });
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        CompoundTag nbt = BlockItem.getBlockEntityData(stack);
        if(!dead && world.getBlockEntity(pos) instanceof StarfishBlockEntity entity){
            if (nbt != null) {
                entity.setColor(nbt.getInt("Color"));
            } else {
                randomize(world, pos, world.getRandom());
            }
        }
    }

    public static void randomize(LevelAccessor world, BlockPos pos, RandomSource random) {
        if(world.getBlockEntity(pos) instanceof StarfishBlockEntity entity) {
            entity.setRandomRotation(random.nextDouble() * 360 - 180);
            if(random.nextFloat() < 0.001) {
                entity.setColor(0xffffff);
                entity.setRainbow(true);
            }else {
                entity.setColor(getRandomColor(random));
            }
        }
    }

    private static int getRandomColor(RandomSource random) {
        return Util.getRandom(COLORS, random);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        randomize(world, pos, world.getRandom());
        if(shouldDie(state, world, pos)) {
            world.scheduleTick(pos, this, 60 + world.getRandom().nextInt(40));
        }
        super.onPlace(state, world, pos, oldState, notify);
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private boolean shouldDie(BlockState state, LevelAccessor world, BlockPos pos) {
        return !dead && !state.getFluidState().is(FluidTags.WATER)
                && Direction.stream().noneMatch(d -> world.getFluidState(pos.relative(d)).is(FluidTags.WATER));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        if(!canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if(shouldDie(state, world, pos)) {
            world.scheduleTick(pos, this, 60 + world.getRandom().nextInt(40));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(!dead && !state.getFluidState().is(FluidTags.WATER)) {
            double rotation = world.random.nextDouble() * 360 - 180;
            if(world.getBlockEntity(pos) instanceof StarfishBlockEntity entity) {
                rotation = entity.getRandomRotation();
            }
            world.setBlockAndUpdate(pos, AnglingBlocks.DEAD_STARFISH.withPropertiesOf(state));
            if(world.getBlockEntity(pos) instanceof StarfishBlockEntity entity) {
                entity.setRandomRotation(rotation);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction d = state.getValue(FACING).getOpposite();
        BlockPos facingPos = pos.relative(d);
        BlockState facingState = world.getBlockState(facingPos);
        return Block.isFaceFull(facingState.getBlockSupportShape(world, facingPos), d.getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
        };
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        boolean bl = fluidState.getType() == Fluids.WATER;
        return defaultBlockState().setValue(WATERLOGGED, bl).setValue(FACING, ctx.getClickedFace());
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StarfishBlockEntity(pos, state);
    }
}
