package com.eightsidedsquare.angling.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class GoToWaterGoal extends MoveToBlockGoal {

    private final PathfinderMob entity;

    public GoToWaterGoal(PathfinderMob mob, double speed, int range) {
        super(mob, speed, range);
        this.entity = mob;
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + mob.getRandom().nextInt(20));
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !entity.isInWaterOrBubble();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !entity.isInWaterOrBubble();
    }

    @Override
    public double acceptedDistance() {
        return 0;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        return world.getFluidState(pos.above()).is(FluidTags.WATER);
    }
}
