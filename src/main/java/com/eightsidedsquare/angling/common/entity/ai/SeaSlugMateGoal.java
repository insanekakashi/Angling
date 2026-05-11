package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.SeaSlugEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class SeaSlugMateGoal extends Goal {

    protected final SeaSlugEntity entity;
    protected final Level world;
    @Nullable
    protected SeaSlugEntity mate;
    private static final TargetingConditions VALID_MATE_PREDICATE = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();

    public SeaSlugMateGoal(SeaSlugEntity entity) {
        this.entity = entity;
        this.world = entity.level();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void tick() {
        if(mate != null) {
            entity.getLookControl().setLookAt(mate, entity.getHeadRotSpeed(), entity.getMaxHeadXRot());
            entity.getNavigation().moveTo(mate.getX(), mate.getY(), mate.getZ(), 2d);
            if(entity.distanceTo(mate) < 1.5d) {
                entity.setLoveTicks(0);
                mate.setLoveTicks(0);
                entity.setLoveCooldown(3000);
                mate.setLoveCooldown(3000);
                entity.createHeartParticles();
                entity.setHasEggs(true);
                mate.setHasEggs(true);
                entity.setMateData(mate.writeMateData(new CompoundTag()));
                mate.setMateData(entity.writeMateData(new CompoundTag()));
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
        return canBeBred(entity) && canBeBred(mate);
    }

    @Override
    public boolean canUse() {
        if (!canBeBred(entity)) {
            return false;
        } else {
            this.mate = this.findMate();
            return this.mate != null;
        }
    }

    private boolean canBeBred(SeaSlugEntity entity) {
        return !entity.hasEggs() && entity.isInLove() && entity.isInWater();
    }

    @Nullable
    private SeaSlugEntity findMate() {

        List<? extends SeaSlugEntity> list = world.getNearbyEntities(entity.getClass(), VALID_MATE_PREDICATE, entity, entity.getBoundingBox().inflate(16.0D));
        double d = 16;
        SeaSlugEntity mate = null;

        for (SeaSlugEntity testMate : list) {
            if (canBeBred(testMate) && entity.distanceTo(testMate) < d && testMate.getType().equals(entity.getType())) {
                mate = testMate;
                d = entity.distanceTo(testMate);
            }
        }

        return mate;
    }
}
