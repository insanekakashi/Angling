package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.common.entity.RoeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class RoeBlockEntityRenderer implements BlockEntityRenderer<RoeBlockEntity> {

    private final BlockRenderDispatcher blockRenderManager;

    public RoeBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        blockRenderManager = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(RoeBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState state = entity.getBlockState();
        if(!entity.isRemoved() && entity.getLevel() != null)
            blockRenderManager.getModelRenderer().tesselateBlock(entity.getLevel(), blockRenderManager.getBlockModel(state), state, entity.getBlockPos(), matrices, vertexConsumers.getBuffer(RenderType.cutout()), false, entity.getLevel().random, 0, 0);
    }
}
