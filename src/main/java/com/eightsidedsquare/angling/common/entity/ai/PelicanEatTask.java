package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.eightsidedsquare.angling.core.ai.AnglingMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PelicanEatTask extends Behavior<PelicanEntity> {
    public PelicanEatTask() {
        super(ImmutableMap.of(AnglingMemoryModuleTypes.HAS_TRADED, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel world, PelicanEntity entity) {
        return entity.hasEntityInBeak();
    }

    @Override
    protected void start(ServerLevel world, PelicanEntity entity, long time) {
        entity.setEntityInBeak(new CompoundTag());
        entity.setBeakOpen(false);
        entity.getBrain().eraseMemory(AnglingMemoryModuleTypes.HAS_TRADED);
    }
}
