package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.StarfishBlockEntityModel;
import com.eightsidedsquare.angling.common.entity.StarfishBlockEntity;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StarfishBlockEntityRenderer extends GeoBlockRenderer<StarfishBlockEntity> {

    public StarfishBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new StarfishBlockEntityModel());
    }

    @Override
    public RenderType getRenderType(StarfishBlockEntity entity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }

    @Override
    public Color getRenderColor(StarfishBlockEntity entity, float tickDelta, int packedLight) {
        return entity.getBlockState().is(AnglingBlocks.DEAD_STARFISH) ? Color.WHITE : Color.ofOpaque(entity.isRainbow() ? StarfishBlockEntity.getRainbowColor() : entity.getColor());
    }

    @Override
    public void preRender(PoseStack poseStack, StarfishBlockEntity entity, BakedGeoModel model, MultiBufferSource vertexConsumers, VertexConsumer buffer, boolean isReRender, float tickDelta, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!entity.isRemoved()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.01, 0.5);

            super.preRender(poseStack, entity, model, vertexConsumers, buffer, isReRender, tickDelta, light, overlay, red, green, blue, alpha);

            poseStack.popPose();
        }
    }
}
