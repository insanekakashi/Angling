package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.eightsidedsquare.angling.core.ai.AnglingMemoryModuleTypes;
import com.eightsidedsquare.angling.core.tags.AnglingEntityTypeTags;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import java.util.Objects;
import java.util.Optional;

public class PelicanBrain {

    public static Brain<PelicanEntity> create(Brain<PelicanEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addFightActivities(brain);
        addSoarActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void addCoreActivities(Brain<PelicanEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8F),
                new AnimalPanic(2.5f),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                StartAttacking.create(entity -> entity.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_ATTACKABLE))
        ));
    }

    private static void addIdleActivities(Brain<PelicanEntity> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, StartAttacking.create(entity -> entity.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(1, new PelicanTradeTask()),
                Pair.of(2, SetEntityLookTargetSometimes.create(8.0F, UniformInt.of(30, 60))),
                Pair.of(3, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(
                        Pair.of(RandomStroll.stroll(1f), 1),
                        Pair.of(SetWalkTargetFromLookTarget.create(1f, 3), 1),
                        Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 2),
                        Pair.of(BehaviorBuilder.triggerIf(PelicanEntity::isFlying, RandomStroll.stroll(1f)), 2)))
        )));
    }

    private static void addFightActivities(Brain<PelicanEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT,0, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1f),
                new PelicanAttackTask(5)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addSoarActivities(Brain<PelicanEntity> brain) {
        brain.addActivityWithConditions(Activity.RIDE, ImmutableList.of(
                        Pair.of(0, StartAttacking.create(entity -> entity.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_ATTACKABLE))),
                        Pair.of(1, new PelicanEatTask()),
                        Pair.of(2, new PelicanSoarTask())
                ), ImmutableSet.of(
                        Pair.of(AnglingMemoryModuleTypes.SOARING_COOLDOWN, MemoryStatus.VALUE_ABSENT),
                        Pair.of(AnglingMemoryModuleTypes.CAN_TRADE, MemoryStatus.VALUE_ABSENT))
        );
    }

    public static void updateActivities(PelicanEntity entity) {
        Activity activityBeforeReset = entity.getBrain().getActiveNonCoreActivity().orElse(null);
        entity.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.RIDE, Activity.IDLE));
        Activity activityAfterReset = entity.getBrain().getActiveNonCoreActivity().orElse(null);
        if(Objects.equals(activityBeforeReset, Activity.FIGHT) && !Objects.equals(activityAfterReset, Activity.FIGHT)) {
            entity.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
            if(!entity.hasEntityInBeak() && entity.isBeakOpen()) {
                entity.setBeakOpen(false);
            }
        }else if(Objects.equals(activityBeforeReset, Activity.RIDE) && !Objects.equals(activityAfterReset, Activity.RIDE)) {
            entity.getBrain().setMemoryWithExpiry(AnglingMemoryModuleTypes.SOARING_COOLDOWN, Unit.INSTANCE, 1000L);
        }
        if(entity.hasEntityInBeak()
                && entity.getBrain().checkMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT)
                && entity.getBrain().checkMemory(AnglingMemoryModuleTypes.CAN_TRADE, MemoryStatus.VALUE_ABSENT)) {
            entity.getBrain().setMemoryWithExpiry(AnglingMemoryModuleTypes.CAN_TRADE, Unit.INSTANCE, entity.getRandom().nextIntBetweenInclusive(600, 1200));
        }
    }

    public static Optional<PositionTracker> getPlayerLookTarget(LivingEntity entity) {
        Optional<Player> optional = entity.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        return optional.map(player -> new EntityTracker(player, true));
    }

    public static boolean canPutInBeak(LivingEntity target) {
        return target.getType().is(AnglingEntityTypeTags.HUNTED_BY_PELICAN) &&
                (!target.getType().is(AnglingEntityTypeTags.HUNTED_BY_PELICAN_WHEN_BABY) || target.isBaby());
    }


}