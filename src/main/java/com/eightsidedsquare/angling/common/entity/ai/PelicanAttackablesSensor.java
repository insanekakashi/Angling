package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestVisibleLivingEntitySensor;
import net.minecraft.world.entity.animal.Bucketable;

public class PelicanAttackablesSensor extends NearestVisibleLivingEntitySensor {

    @Override
    protected boolean isMatchingEntity(LivingEntity entity, LivingEntity target) {
        return entity instanceof PelicanEntity pelicanEntity &&
                !pelicanEntity.hasEntityInBeak() &&
                !(target instanceof Mob mob && (mob.isPersistenceRequired() || mob.hasCustomName())) &&
                !(target instanceof Bucketable bucketable && bucketable.fromBucket()) &&
                target.distanceTo(entity) < 40 &&
                PelicanBrain.canPutInBeak(target) &&
                !entity.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN);
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
