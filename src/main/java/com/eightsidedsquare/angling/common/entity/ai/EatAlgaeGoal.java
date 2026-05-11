package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.block.AlgaeBlock;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class EatAlgaeGoal extends MoveToBlockGoal {

    private final PathfinderMob entity;
    private boolean finished;

    public EatAlgaeGoal(PathfinderMob mob, double speed, int range) {
        super(mob, speed, range, 5);
        this.entity = mob;
    }

    @Override
    public void start() {
        super.start();
        finished = false;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos.above());
        return state.is(AnglingBlocks.ALGAE) && state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + mob.getRandom().nextInt(20));
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !finished;
    }

    @Override
    public void tick() {
        super.tick();
        BlockState state = entity.level().getBlockState(entity.blockPosition());
        if(!finished && state.is(AnglingBlocks.ALGAE) && state.getFluidState().is(FluidTags.WATER)) {
            AlgaeBlock.deteriorate(entity.blockPosition(), entity.level());
            finished = true;
        }
    }
}
