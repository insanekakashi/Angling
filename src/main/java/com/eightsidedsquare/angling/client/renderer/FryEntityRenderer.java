package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.FryEntityModel;
import com.eightsidedsquare.angling.common.entity.FryEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class FryEntityRenderer extends GeoEntityRenderer<FryEntity> {
    public FryEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FryEntityModel());
        addRenderLayer(new FryEntityLayerRenderer(this));
    }

    @Override
    public RenderType getRenderType(FryEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

     static class FryEntityLayerRenderer extends GeoRenderLayer<FryEntity> {
         private static final ResourceLocation OUTSIDE_LAYER = new ResourceLocation(MOD_ID, "textures/entity/fry/fry.png");
         private static final ResourceLocation INSIDE_LAYER = new ResourceLocation(MOD_ID, "textures/entity/fry/fry_innards.png");
         private static final ResourceLocation MODEL = new ResourceLocation(MOD_ID, "geo/fry.geo.json");

        public FryEntityLayerRenderer(GeoRenderer<FryEntity> entityRendererIn) {
            super(entityRendererIn);
        }

        private void render(ResourceLocation layer, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, int overlay, FryEntity entity, float partialTicks, float r, float g, float b) {
            RenderType renderLayer = RenderType.entityTranslucent(layer);
            matrixStackIn.pushPose();
            this.getRenderer().actuallyRender(matrixStackIn, entity, this.getGeoModel().getBakedModel(getGeoModel().getModelResource(entity)), renderLayer, bufferIn,
                    bufferIn.getBuffer(renderLayer), true, partialTicks, packedLightIn, overlay, r, g, b, 1f);
            matrixStackIn.popPose();
        }

         @Override
         public void render(PoseStack matrixStackIn, FryEntity entity, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferIn, VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
             int color = entity.getColor();
             float r = ((color >> 16) & 0xFF) / 255f;
             float g = ((color >> 8) & 0xFF) / 255f;
             float b = (color & 0xFF) / 255f;
             int overlay = OverlayTexture.pack(0,
                     entity.hurtTime > 0 || entity.deathTime > 0);
             render(INSIDE_LAYER, matrixStackIn, bufferIn, packedLightIn, overlay, entity, partialTicks, r, g, b);
             render(OUTSIDE_LAYER, matrixStackIn, bufferIn, packedLightIn, overlay, entity, partialTicks, 1f, 1f, 1f);
         }
    }
}
