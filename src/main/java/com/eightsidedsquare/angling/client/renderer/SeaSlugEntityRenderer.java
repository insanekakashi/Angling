package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import com.eightsidedsquare.angling.common.entity.SeaSlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SeaSlugEntityRenderer extends GeoEntityRenderer<SeaSlugEntity> {
    public SeaSlugEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BasicEntityModel<>("sea_slug", false));
        this.addRenderLayer(new SeaSlugLayerRenderer(this));
        this.shadowRadius = 0.1f;
    }

    static class SeaSlugLayerRenderer extends GeoRenderLayer<SeaSlugEntity> {

        public SeaSlugLayerRenderer(GeoRenderer<SeaSlugEntity> entityRendererIn) {
            super(entityRendererIn);
        }

        public void render(int color, ResourceLocation texture, boolean glow, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, SeaSlugEntity entity, float partialTicks) {

            if(texture != null) {
                ResourceLocation model = this.getGeoModel().getModelResource(entity);
                float r = ((color >> 16) & 0xff) / 255f;
                float g = ((color >> 8) & 0xff) / 255f;
                float b = (color & 0xff) / 255f;
                int overlay = OverlayTexture.pack(0,
                        entity.hurtTime > 0 || entity.deathTime > 0);

                this.getRenderer().actuallyRender(matrixStackIn, entity, this.getGeoModel().getBakedModel(getGeoModel().getModelResource(entity)), this.getRenderType(texture), bufferIn,
                        bufferIn.getBuffer(this.getRenderType(texture)), true, partialTicks, packedLightIn, overlay, r, g, b, 1f);
            }
        }

        @Override
        public void render(PoseStack matrixStackIn, SeaSlugEntity entity, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer bufferIn, float partialTicks, int packedLight, int packedOverlay) {
            this.render(entity.getBaseColor().color(), this.getTextureResource(entity), false, matrixStackIn, bufferSource, packedLight, entity, partialTicks);
            this.render(entity.getPatternColor().color(), entity.getPattern().texture(), entity.isBioluminescent(), matrixStackIn, bufferSource, packedLight, entity, partialTicks);
        }

        public RenderType getRenderType(ResourceLocation texture) {
            return RenderType.entityCutoutNoCull(texture);
        }
    }
}
