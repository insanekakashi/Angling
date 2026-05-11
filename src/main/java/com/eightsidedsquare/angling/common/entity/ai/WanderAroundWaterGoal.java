package com.eightsidedsquare.angling.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WanderAroundWaterGoal extends RandomStrollGoal {

    private final PathfinderMob entity;

    public WanderAroundWaterGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
        this.entity = mob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && entity.isInWaterOrBubble();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 target = super.getPosition();
        if (target != null && entity.level().getFluidState(new BlockPos((int) target.x, (int) target.y, (int) target.z)).is(FluidTags.WATER)) {
            return target;
        }
        return null;
    }
}
