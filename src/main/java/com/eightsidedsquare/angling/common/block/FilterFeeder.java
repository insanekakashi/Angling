package com.eightsidedsquare.angling.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface FilterFeeder {

    void onFeed(BlockPos pos, BlockState state, ServerLevel world);

    default void createFedParticles(int count, BlockPos pos, ServerLevel world) {
        world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, count, 0.25d, 0.25d, 0.25d, 0);
    }
}
