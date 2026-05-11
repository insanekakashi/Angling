package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.NautilusEntityModel;
import com.eightsidedsquare.angling.common.entity.NautilusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NautilusEntityRenderer extends BasicEntityRenderer<NautilusEntity> {
    public NautilusEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NautilusEntityModel());
    }

    @Override
    public void render(NautilusEntity entity, float entityYaw, float partialTick, PoseStack matrices, MultiBufferSource bufferSource, int packedLight) {
        matrices.pushPose();
        matrices.scale(0.65f, 0.65f, 0.65f);
        super.render(entity, entityYaw, partialTick, matrices, bufferSource, packedLight);
        matrices.popPose();
    }
}
