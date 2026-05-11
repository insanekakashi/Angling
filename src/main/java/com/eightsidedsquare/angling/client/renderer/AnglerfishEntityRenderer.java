package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import com.eightsidedsquare.angling.common.entity.AnglerfishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
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

public class AnglerfishEntityRenderer extends GeoEntityRenderer<AnglerfishEntity> {

    private static final ResourceLocation OVERLAY = new ResourceLocation(MOD_ID, "textures/entity/anglerfish/anglerfish_overlay.png");

    public AnglerfishEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BasicEntityModel<>("anglerfish", true));
        addRenderLayer(new AnglerfishLayerRenderer(this));
    }

    @Override
    public RenderType getRenderType(AnglerfishEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    static class AnglerfishLayerRenderer extends GeoRenderLayer<AnglerfishEntity> {

        public AnglerfishLayerRenderer(GeoRenderer<AnglerfishEntity> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack matrixStackIn, AnglerfishEntity entity, BakedGeoModel bakedModel, RenderType renderType,
                           MultiBufferSource bufferIn, VertexConsumer buffer, float partialTicks,
                           int packedLight, int packedOverlay) {

            int overlay = OverlayTexture.pack(0,
                    entity.hurtTime > 0 || entity.deathTime > 0);

            this.getRenderer().actuallyRender(matrixStackIn, entity, this.getGeoModel().getBakedModel(getGeoModel().getModelResource(entity)), this.getRenderType(OVERLAY), bufferIn,
                    bufferIn.getBuffer(this.getRenderType(OVERLAY)), true, LightTexture.FULL_BRIGHT, overlay, 1, 1, 1, 1, 1);
        }

        public RenderType getRenderType(ResourceLocation texture) {
            return RenderType.entityTranslucent(texture);
        }
    }
}
