package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.eightsidedsquare.angling.core.ai.AnglingMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class PelicanSoarTask extends Behavior<PelicanEntity> {
    public PelicanSoarTask() {
        super(ImmutableMap.of(
                AnglingMemoryModuleTypes.SOARING_COOLDOWN, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel world, PelicanEntity entity) {
        return entity.isFlying();
    }

    @Override
    protected void start(ServerLevel world, PelicanEntity entity, long time) {
        Vec3 pos = entity.pick(16, 0, false).getLocation();
        int topY = world.getHeight(Heightmap.Types.WORLD_SURFACE, (int) pos.x, (int) pos.z);
        boolean isWater = world.getFluidState(BlockPos.containing(pos.x, topY - 1, pos.z)).is(FluidTags.WATER);
        int y = topY + (isWater ? entity.getRandom().nextIntBetweenInclusive(3, 8) : entity.getRandom().nextIntBetweenInclusive(5, 25));
        BlockPos blockPos = BlockPos.containing(pos.x, y, pos.z);
        if(world.getBlockState(blockPos).isAir()) {
            entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, 1.5f, 1));
        }
    }

}
