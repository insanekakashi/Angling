package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.eightsidedsquare.angling.core.ai.AnglingMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PelicanTradeTask extends Behavior<PelicanEntity> {
    public PelicanTradeTask() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT,
                AnglingMemoryModuleTypes.CAN_TRADE, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel world, PelicanEntity entity) {
        return entity.hasEntityInBeak() &&
                entity.getBrain().checkMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT) &&
                entity.getBrain().checkMemory(AnglingMemoryModuleTypes.CAN_TRADE, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    protected void start(ServerLevel world, PelicanEntity entity, long time) {
        entity.setBeakOpen(true);
        PelicanBrain.getPlayerLookTarget(entity).ifPresent(player -> {
            BehaviorUtils.setWalkAndLookTargetMemories(entity, player, 1f, 5);
            if(!entity.onGround() && player.currentPosition().distanceTo(entity.position()) < 5 && canLand(world, entity)) {
                entity.push(0, -0.01, 0);
            }
        });
    }

    private boolean canLand(ServerLevel world, PelicanEntity entity) {
        return Stream.of(entity.blockPosition().below(), entity.blockPosition().below(2), entity.blockPosition().below(3))
                .anyMatch(pos -> world.getBlockState(pos).isRedstoneConductor(world, pos));
    }

    @Override
    protected void tick(ServerLevel world, PelicanEntity entity, long time) {
        start(world, entity, time);
    }

    @Override
    protected void stop(ServerLevel world, PelicanEntity entity, long time) {
        entity.getBrain().eraseMemory(AnglingMemoryModuleTypes.CAN_TRADE);
    }

    @Override
    protected boolean canStillUse(ServerLevel world, PelicanEntity entity, long time) {
        return checkExtraStartConditions(world, entity) && entity.getBrain().checkMemory(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT);
    }
}
