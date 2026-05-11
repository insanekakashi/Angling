package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import com.eightsidedsquare.angling.common.block.RoeBlock;
import com.eightsidedsquare.angling.common.entity.RoeBlockEntity;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.AnglingUtil;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class FishLayRoeGoal extends MoveToBlockGoal {

    protected final AbstractFish entity;
    protected final Level world;

    public FishLayRoeGoal(WaterAnimal entity) {
        super(entity, 1.25d, 6, 6);
        this.entity = (AbstractFish) entity;
        this.world = entity.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + mob.getRandom().nextInt(20));
    }

    @Override
    public double acceptedDistance() {
        return 0d;
    }

    @Override
    public void tick() {
        super.tick();
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(entity);
        entity.getLookControl().setLookAt(blockPos.getX() + 0.5d, blockPos.getY() + 0.5d, blockPos.getZ() + 0.5d, entity.getHeadRotSpeed(), entity.getMaxHeadXRot());

        if(tryTicks % 5 == 0 && new Vec3(blockPos.getX() + 0.5d, blockPos.getY() + 0.5d, blockPos.getZ() + 0.5d).distanceTo(entity.position()) < 1d && noAlgaeNearby(world, blockPos)) {
            component.setCarryingRoe(false);
            world.setBlock(blockPos.above(), AnglingBlocks.ROE.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_ALL);
            if(world.getBlockEntity(blockPos.above()) instanceof RoeBlockEntity roeBlockEntity && component.getMateData() != null){
                roeBlockEntity.setParentsData(AnglingUtil.entityToNbt(entity, true), component.getMateData().copy());
                roeBlockEntity.setEntityType(entity.getType());
                Tuple<Integer, Integer> colors = RoeBlock.getRoeColor(entity);
                roeBlockEntity.setColors(colors.getA(), colors.getB());
            }
            stop();
        }else if(blockPos.closerToCenterThan(entity.position(), 2)) {
            entity.getMoveControl().setWantedPosition(blockPos.getX() + 0.5d, blockPos.getY(), blockPos.getZ() + 0.5d, speedModifier);
        }
    }

    @Override
    public boolean canUse() {
        FishSpawningComponent component = AnglingEntityComponents.FISH_SPAWNING.get(entity);
        return super.canUse() && component.isCarryingRoe() && component.getMateData() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && AnglingEntityComponents.FISH_SPAWNING.get(entity).isCarryingRoe();
    }

    protected boolean noAlgaeNearby(LevelReader world, BlockPos pos) {
        int r = 3;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(pos.offset(-r, -r, -r), pos.offset(r, r, r));
        for(BlockPos blockPos : iterable) {
            if(world.getBlockState(blockPos).is(AnglingBlocks.ALGAE)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        BlockPos abovePos = pos.above();
        return world.getBlockState(pos).isFaceSturdy(world, pos, Direction.UP)
                && world.getFluidState(abovePos).is(Fluids.WATER)
                && world.getBlockState(abovePos).is(Blocks.WATER)
                && noAlgaeNearby(world, abovePos);
    }
}
