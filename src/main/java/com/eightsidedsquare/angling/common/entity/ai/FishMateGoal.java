package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import com.eightsidedsquare.angling.core.AnglingUtil;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class FishMateGoal extends Goal {

    private final AbstractFish entity;
    @Nullable
    private AbstractFish mate;
    private final Level world;
    private static final TargetingConditions VALID_MATE_PREDICATE = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();

    public FishMateGoal(WaterAnimal entity) {
        this.entity = (AbstractFish) entity;
        this.world = entity.level();
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void tick() {
        if(mate != null) {
            entity.getLookControl().setLookAt(mate, entity.getHeadRotSpeed(), entity.getMaxHeadXRot());
//            double d = 0.005;
//            double min = 0.05;
//            double dX = (mate.getX() - entity.getX()) * d;
//            dX = Math.min(min, Math.abs(dX)) * (dX > 0 ? 1 : -1);
//            double dY = (mate.getY() - entity.getY()) * d;
//            dY = Math.min(min, Math.abs(dY)) * (dY > 0 ? 1 : -1);
//            double dZ = (mate.getZ() - entity.getZ()) * d;
//            dZ = Math.min(min, Math.abs(dZ)) * (dZ > 0 ? 1 : -1);
//            entity.addVelocity(dX, dY, dZ);
            entity.getNavigation().moveTo(mate.getX(), mate.getY(), mate.getZ(), 2d);
            if(entity.distanceTo(mate) < 1.5d) {
                FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(entity);
                FishSpawningComponent mateComponent = AnglingEntityComponents.FISH_SPAWNING.get(mate);
                component.setLoveTicks(0);
                mateComponent.setLoveTicks(0);
                component.setLoveCooldown(3000);
                mateComponent.setLoveCooldown(3000);
                component.createHeartParticles();
                component.setCarryingRoe(true);
                component.setMateData(AnglingUtil.entityToNbt(mate, true));
                if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    world.addFreshEntity(new ExperienceOrb(world, entity.getX(), entity.getY(), entity.getZ(), entity.getRandom().nextInt(7) + 1));
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if(mate == null || !mate.isAlive()) {
            return false;
        }
        return entity.isInWater() && mate.isInWater() && canBeBred(entity) && canBeBred(mate);
    }

    @Override
    public boolean canUse() {
        if(!canBeBred(entity) || !entity.isInWater()) {
            return false;
        }
        mate = findMate();
        return mate != null;
    }

    private boolean canBeBred(AbstractFish fishEntity) {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(fishEntity);
        return component.isInLove() && !component.isCarryingRoe() && !component.hasCooldown();
    }

    @Nullable
    private AbstractFish findMate() {

        List<? extends AbstractFish> list = world.getNearbyEntities(entity.getClass(), VALID_MATE_PREDICATE, entity, entity.getBoundingBox().inflate(16.0D));
        double d = 16;
        AbstractFish fishEntity = null;

        for (AbstractFish testFishEntity : list) {
            if (canBeBred(testFishEntity) && entity.distanceTo(testFishEntity) < d && testFishEntity.getType().equals(entity.getType())) {
                fishEntity = testFishEntity;
                d = entity.distanceTo(testFishEntity);
            }
        }

        return fishEntity;
    }
}
