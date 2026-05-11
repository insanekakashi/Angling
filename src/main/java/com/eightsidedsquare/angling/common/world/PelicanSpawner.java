package com.eightsidedsquare.angling.common.world;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.eightsidedsquare.angling.core.AnglingEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;

public class PelicanSpawner implements CustomSpawner {

    private int cooldown;
    private static final int RADIUS = 32;

    @Override
    public int tick(ServerLevel world, boolean spawnMonsters, boolean spawnAnimals) {
        if(spawnAnimals && world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && --cooldown <= 0) {
            cooldown = 2400;
            RandomSource random = world.random;
            if(world.isDay() && world.dimensionType().hasSkyLight() && random.nextInt(16) == 0) {
                int total = 0;
                List<ServerPlayer> players = world.getPlayers(p -> !p.isSpectator());
                PelicanEntity entity;
                for(ServerPlayer player : players) {
                    BlockPos spawnPos = player.blockPosition().offset(random.nextIntBetweenInclusive(-RADIUS, RADIUS), random.nextIntBetweenInclusive(20, 35), random.nextIntBetweenInclusive(-RADIUS, RADIUS));
                    if(world.canSeeSky(spawnPos) &&
                            NaturalSpawner.isValidEmptySpawnBlock(world, spawnPos, world.getBlockState(spawnPos), world.getFluidState(spawnPos), AnglingEntities.PELICAN) &&
                            playerHasEntityBucketItem(player) &&
                            (entity = AnglingEntities.PELICAN.create(world)) != null) {
                        entity.moveTo(spawnPos, 0, 0);
                        entity.finalizeSpawn(world, world.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, null, null);
                        world.addFreshEntityWithPassengers(entity);
                        total++;
                    }
                }
                return total;
            }
        }
        return 0;
    }

    private boolean playerHasEntityBucketItem(Player player) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if(player.getInventory().getItem(i).getItem() instanceof MobBucketItem)
                return true;
        }
        return false;
    }
}
