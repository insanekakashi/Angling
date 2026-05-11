package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.SeaSlugEggsBlockEntity;
import com.eightsidedsquare.angling.common.entity.SeaSlugEntity;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class SeaSlugLayEggsGoal extends MoveToBlockGoal {

    protected final SeaSlugEntity entity;
    protected final Level world;

    public SeaSlugLayEggsGoal(SeaSlugEntity entity) {
        super(entity, 1.25d, 6, 6);
        this.entity = entity;
        this.world = entity.level();
    }

    @Override
    public void tick() {
        super.tick();
        entity.getLookControl().setLookAt(blockPos.getX() + 0.5d, blockPos.getY() + 0.5d, blockPos.getZ() + 0.5d, entity.getHeadRotSpeed(), entity.getMaxHeadXRot());

        if(new Vec3(blockPos.getX() + 0.5d, blockPos.getY() + 1.5d, blockPos.getZ() + 0.5d).distanceTo(entity.position()) < 1d) {
            entity.setHasEggs(false);
            world.setBlock(blockPos.above(), AnglingBlocks.SEA_SLUG_EGGS.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_ALL);
            if(world.getBlockEntity(blockPos.above()) instanceof SeaSlugEggsBlockEntity eggsBlockEntity && entity.getMateData() != null){
                eggsBlockEntity.setParentsData(entity.writeMateData(new CompoundTag()), entity.getMateData().copy());
                eggsBlockEntity.setColor(entity.getBaseColor());
            }
        }
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + mob.getRandom().nextInt(20));
    }

    @Override
    public double acceptedDistance() {
        return 0d;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && entity.hasEggs() && entity.getMateData() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && entity.hasEggs();
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = world.getBlockState(abovePos);
        BlockState state = world.getBlockState(pos);
        return state.is(BlockTags.CORAL_BLOCKS) && aboveState.is(Blocks.WATER) && aboveState.getFluidState().is(Fluids.WATER);
    }
}
