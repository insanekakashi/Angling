package com.eightsidedsquare.angling.common.block;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import com.eightsidedsquare.angling.common.entity.RoeBlockEntity;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class RoeBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    private static final BooleanProperty WATERLOGGED;
    private static final VoxelShape SHAPE;

    public RoeBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return AnglingUtil.runningSodium() ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        return Block.isFaceFull(belowState.getBlockSupportShape(world, pos.below()), Direction.UP);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        boolean bl = fluidState.getType() == Fluids.WATER;
        return defaultBlockState().setValue(WATERLOGGED, bl);
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public int getHatchTime(RandomSource random) {
        return random.nextIntBetweenInclusive(3600, 7200);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if(state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, this, getHatchTime(world.getRandom()));
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClientSide) {
            world.getBlockEntity(pos, AnglingEntities.ROE).ifPresent(entity -> entity.readFrom(itemStack));
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(state.getValue(WATERLOGGED)) {
            world.getBlockEntity(pos, AnglingEntities.ROE).ifPresent(entity -> entity.hatch(world));
        }
    }

    public static Tuple<Integer, Integer> getRoeColor(AbstractFish entity) {
        SpawnEggItem eggItem = SpawnEggItem.byId(entity.getType());
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(entity);

        if (entity instanceof TropicalFish tropicalFishEntity) {
            int parentVariant = getVariantId(tropicalFishEntity.getVariant());
            int parentColor = TropicalFish.getBaseColor(parentVariant).getTextColor();

            int mateVariant = component.getMateData() != null ? component.getMateData().getInt("Variant") : parentVariant;
            int mateColor = TropicalFish.getBaseColor(mateVariant).getTextColor();

            return new Tuple<>(parentColor, mateColor);
        }

        if (eggItem != null) {
            return new Tuple<>(eggItem.getColor(0), eggItem.getColor(0));
        }

        return new Tuple<>(0xffffff, 0xffffff);
    }

    private static int getVariantId(TropicalFish.Pattern variety) {
        return switch (variety) {
            case KOB -> 0;
            case SUNSTREAK -> 1;
            case SNOOPER -> 2;
            case DASHER -> 3;
            case BRINELY -> 4;
            case SPOTTY -> 5;
            case FLOPPER -> 6;
            case STRIPEY -> 7;
            case GLITTER -> 8;
            case BLOCKFISH -> 9;
            case BETTY -> 10;
            case CLAYFISH -> 11;
        };
    }



    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(0, 0, 0, 16, 0.75, 16);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoeBlockEntity(pos, state);
    }
}
