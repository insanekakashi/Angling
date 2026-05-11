package com.eightsidedsquare.angling.common.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class WaterAdjacentPatchFeature extends RandomPatchFeature {

    public WaterAdjacentPatchFeature(Codec<RandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> ctx) {
        BlockPos pos = ctx.origin();
        WorldGenLevel world = ctx.level();
        Vec3i offset = new Vec3i(5, 5, 5);
        if(BlockPos.betweenClosedStream(pos.subtract(offset), pos.offset(offset)).noneMatch(blockPos -> world.getFluidState(pos).is(FluidTags.WATER)))
            return false;
        return super.place(ctx);
    }
}
