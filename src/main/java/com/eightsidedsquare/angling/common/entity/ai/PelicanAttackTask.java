package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PelicanAttackTask extends Behavior<PelicanEntity> {
    private final long interval;
    private Phase phase;
    private int catchingTicks;

    public PelicanAttackTask(long interval) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
        this.interval = interval;
        phase = Phase.DONE;
    }

    protected LivingEntity getTarget(PelicanEntity entity) {
        return entity.getBrain().getMemoryInternal(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    protected boolean hasValidTarget(PelicanEntity entity) {
        LivingEntity target = getTarget(entity);
        if(target != null) {
            return target.isAlive();
        }
        return false;
    }

    @Override
    protected void start(ServerLevel world, PelicanEntity entity, long time) {
        phase = Phase.MOVE_TO_TARGET;
        catchingTicks = 0;
    }

    @Override
    protected void tick(ServerLevel world, PelicanEntity entity, long time) {
        LivingEntity target = getTarget(entity);
        if(target != null) {
            switch (phase) {
                case MOVE_TO_TARGET -> {
                    if(shouldDive(target, entity)) {
                        entity.push(0, -0.1, 0);
                        entity.setDiving(true);
                    }else {
                        entity.setDiving(false);
                        if(entity.isInWater()) {
                            entity.push(0, 0.15f, 0);
                        }
                    }
                    BehaviorUtils.lookAtEntity(entity, target);
                    if(target.distanceTo(entity) < 1.5f && PelicanBrain.canPutInBeak(target) && !entity.hasEntityInBeak()) {
                        phase = Phase.CATCHING;
                        entity.setBeakOpen(true);
                        target.setDeltaMovement(target.position().vectorTo(entity.position().add(0, 0.5f, 0)).normalize().scale(0.75D));
                    }else if(!PelicanBrain.canPutInBeak(target) && entity.isWithinMeleeAttackRange(target)){
                        attack(target, entity, world, time);
                    }
                }
                case CATCHING -> {
                    if(catchingTicks++ >= 4) {
                        phase = Phase.DONE;
                        entity.setEntityInBeak(target);
                        target.discard();
                        entity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
                    }
                }
                case DONE -> {}
            }
        }
    }

    protected boolean shouldDive(LivingEntity target, PelicanEntity entity) {
        return target.isInWater()
                && entity.isFlying()
                && entity.getY() > target.getY() + 0.5f
                && entity.position().multiply(1, 0, 1).distanceTo(target.position().multiply(1, 0, 1)) < 1.5f;
    }

    protected void attack(LivingEntity target, PelicanEntity entity, ServerLevel world, long time) {
        if(entity.doHurtTarget(target)) {
            entity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
            doStop(world, entity, time);
        }
    }

    @Override
    protected void stop(ServerLevel world, PelicanEntity entity, long time) {
        entity.setDiving(false);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel world, PelicanEntity entity) {
        return hasValidTarget(entity) && !entity.hasEntityInBeak();
    }

    @Override
    protected boolean canStillUse(ServerLevel world, PelicanEntity entity, long time) {
        return phase != Phase.DONE
                && entity.getBrain().checkMemory(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT)
                && !entity.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING)
                && hasValidTarget(entity);
    }

    enum Phase {
        MOVE_TO_TARGET,
        CATCHING,
        DONE
    }
}
