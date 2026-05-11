package com.eightsidedsquare.angling.common.block;

import com.eightsidedsquare.angling.common.entity.UrchinBlockEntity;
import com.eightsidedsquare.angling.core.AnglingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class UrchinBlock extends BushBlock implements EntityBlock, SimpleWaterloggedBlock {

    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 8, 13);
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public UrchinBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, true));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(Items.WATER_BUCKET)) {
            stack.shrink(1);
            player.addItem(new ItemStack(AnglingItems.URCHIN_BUCKET));
            world.playSound(null, pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1, 1);
            world.setBlock(pos, (state.getValue(WATERLOGGED) ? Blocks.WATER : Blocks.AIR).defaultBlockState(), Block.UPDATE_ALL);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }else if(world.getBlockEntity(pos) instanceof UrchinBlockEntity entity) {
            if(entity.getHat().isEmpty() && !stack.isEmpty()) {
                ItemStack hatStack = stack.copy();
                hatStack.setCount(1);
                entity.setHat(hatStack);
                if (!player.isCreative())
                    stack.shrink(1);
                player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1, 1);
                entity.update();
                return InteractionResult.sidedSuccess(world.isClientSide);
            }else if(stack.isEmpty() && !entity.getHat().isEmpty()) {
                player.addItem(entity.getHat().copy());
                entity.setHat(ItemStack.EMPTY);
                player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1, 1);
                entity.update();
                return InteractionResult.sidedSuccess(world.isClientSide);
            }

        }
        return super.use(state, world, pos, player, hand, hit);
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

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {

        if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof UrchinBlockEntity entity) {
            Containers.dropItemStack(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, entity.getHat().copy());
        }

        super.onRemove(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(WATERLOGGED, !ctx.getLevel().dimensionType().ultraWarm());
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return !floor.getCollisionShape(world, pos).getFaceShape(Direction.UP).isEmpty() || floor.isFaceSturdy(world, pos, Direction.UP);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(AnglingItems.URCHIN_BUCKET);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UrchinBlockEntity(pos, state);
    }
}
