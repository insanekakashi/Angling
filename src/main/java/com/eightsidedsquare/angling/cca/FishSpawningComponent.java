package com.eightsidedsquare.angling.cca;

import com.eightsidedsquare.angling.core.AnglingBlocks;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class FishSpawningComponent implements AutoSyncedComponent {

    private final AbstractFish entity;
    private int loveTicks;
    private int loveCooldown;
    @Nullable
    private CompoundTag mateData;
    private boolean carryingRoe;
    private boolean canGrowUp;
    private boolean wasFed;

    public FishSpawningComponent(AbstractFish entity) {
        this.entity = entity;
        this.canGrowUp = true;
    }

    @Nullable
    public CompoundTag getMateData() {
        return mateData;
    }

    public void setMateData(@Nullable CompoundTag mateData) {
        this.mateData = mateData;
    }

    public boolean isCarryingRoe() {
        return carryingRoe;
    }

    public boolean isInLove() {
        return loveTicks > 0;
    }

    public void setWasFed(boolean wasFed) {
        this.wasFed = wasFed;
    }

    public boolean wasFed() {
        return wasFed;
    }

    public void tick() {
        if(loveCooldown > 0) {
            loveCooldown--;
        }
        if(loveTicks > 0) {
            loveTicks--;
        }
        BlockState state = entity.getFeetBlockState();
        if(wasFed() && !entity.level().isClientSide && entity.getRandom().nextIntBetweenInclusive(0, 400) == 0
                && state.is(Blocks.WATER)
                && state.getFluidState().is(Fluids.WATER)) {
            Util.toShuffledList(Direction.stream(), entity.getRandom()).stream().filter(this::canPlaceAlgaeAt).findFirst().ifPresent(d -> {
                entity.level().setBlock(entity.blockPosition(), AnglingBlocks.ALGAE.defaultBlockState().setValue(MultifaceBlock.getFaceProperty(d), true), Block.UPDATE_ALL);
                setWasFed(false);
            });
        }
    }

    private boolean canPlaceAlgaeAt(Direction d) {
        BlockPos pos = entity.blockPosition().relative(d);
        BlockState state = entity.level().getBlockState(pos);
        return MultifaceBlock.canAttachTo(entity.level(), d, pos, state);
    }

    public boolean canGrowUp() {
        return canGrowUp;
    }

    public void setCanGrowUp(boolean canGrowUp) {
        this.canGrowUp = canGrowUp;
    }

    public boolean hasCooldown() {
        return loveCooldown > 0;
    }

    public void setCarryingRoe(boolean bl) {
        carryingRoe = bl;
    }

    public void setLoveTicks(int ticks) {
        loveTicks = ticks;
    }

    public void setLoveCooldown(int ticks) {
        loveCooldown = ticks;
    }

    public void createHeartParticles() {
        if(!entity.level().isClientSide) {
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.HEART, entity.getRandomX(1), entity.getRandomY() + 0.5d, entity.getRandomZ(1), 7, 0.25d, 0.25d, 0.25d, 0);
        }
    }

    public void createGrowUpParticles() {
        if(!entity.level().isClientSide) {
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, entity.getRandomX(1), entity.getRandomY() + 0.25d, entity.getRandomZ(1), 1, 0.1d, 0.1d, 0.1d, 0);
        }
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        loveTicks = tag.getInt("LoveTicks");
        loveCooldown = tag.getInt("LoveCooldown");
        carryingRoe = tag.getBoolean("CarryingRoe");
        canGrowUp = tag.getBoolean("CanGrowUp");
        if(tag.contains("MateData", Tag.TAG_COMPOUND)) {
            mateData = tag.getCompound("MateData");
        }
        wasFed = tag.getBoolean("WasFed");
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        tag.putInt("LoveTicks", loveTicks);
        tag.putInt("LoveCooldown", loveCooldown);
        tag.putBoolean("CarryingRoe", carryingRoe);
        tag.putBoolean("CanGrowUp", canGrowUp);
        tag.put("MateData", Objects.requireNonNullElseGet(mateData, CompoundTag::new));
        tag.putBoolean("WasFed", wasFed);
    }
}
