package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.UrchinBlockEntityModel;
import com.eightsidedsquare.angling.common.entity.UrchinBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class UrchinBlockEntityRenderer extends GeoBlockRenderer<UrchinBlockEntity> {

    private UrchinBlockEntity entity;
    private MultiBufferSource vertexConsumerProvider;

    public UrchinBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new UrchinBlockEntityModel());
    }

    @Override
    public RenderType getRenderType(UrchinBlockEntity entity, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }

    @Override
    public void preRender(PoseStack poseStack, UrchinBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.entity = animatable;
        this.vertexConsumerProvider = bufferSource;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack stack, UrchinBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(bone.getName().equals("root")) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            stack.pushPose();
            stack.mulPose(new Quaternionf().rotateZYX(bone.getRotX(), bone.getRotY(), bone.getRotZ()));
            stack.translate(0f, 0.625f, 0f);
            stack.scale(0.5f, 0.5f, 0.5f);
            itemRenderer.renderStatic(entity.getHat(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, stack, vertexConsumerProvider, animatable.getLevel(), 0);
            stack.popPose();
            buffer = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        }
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
