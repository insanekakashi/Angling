package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtils;

public class PelicanEntityRenderer extends BasicEntityRenderer<PelicanEntity> {

    private final EntityRenderDispatcher entityRenderDispatcher;
    @Nullable
    private PelicanEntity pelicanEntity;
    private MultiBufferSource vertexConsumerProvider;

    public PelicanEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BasicEntityModel<>("pelican", false, "head_joint"));
        entityRenderDispatcher = ctx.getEntityRenderDispatcher();
        this.shadowRadius = 0.35f;
    }

    @Override
    public void preRender(PoseStack poseStack, PelicanEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.vertexConsumerProvider = bufferSource;
        this.pelicanEntity = animatable;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack stack, PelicanEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(bone.getName().equals("beak_bottom") && pelicanEntity != null && pelicanEntity.isBeakOpen() && pelicanEntity.getEntityInBeak().isPresent()) {
            Entity entityInBeak = pelicanEntity.getEntityInBeak().get();
            Vector3d pos = bone.getPositionVector();
            stack.pushPose();
            GeoBone parent = bone;
            while(parent != null) {
                RenderUtils.translateMatrixToBone(stack, bone);
                if(parent.getName().equals("root")) {
                    if(pelicanEntity.isInWater()) {
                        stack.translate(0, 0.5d, 0.25d);
                    }else if(pelicanEntity.isFlying()) {
                        stack.translate(0, 0.6d, 0.5d);
                    }
                }
                RenderUtils.translateToPivotPoint(stack, parent);
                RenderUtils.rotateMatrixAroundBone(stack, parent);
                if(parent.getName().equals("head_joint")) {
                    stack.mulPose(Axis.XP.rotationDegrees(pelicanEntity.getXRot()));
                    stack.mulPose(Axis.YP.rotationDegrees(pelicanEntity.getYHeadRot() - pelicanEntity.getVisualRotationYInDegrees()));
                }
                RenderUtils.scaleMatrixForBone(stack, parent);
                RenderUtils.translateAwayFromPivotPoint(stack, parent);
                parent = parent.getParent();
            }
            stack.translate(0, 0.75f, -1.35f);
            stack.scale(0.5f, 0.5f, 0.5f);
            stack.mulPose(Axis.YP.rotationDegrees(180));
            entityRenderDispatcher.render(entityInBeak, pos.x, pos.y, pos.z, 0, 0, stack, vertexConsumerProvider, packedLight);
            stack.popPose();
            bufferIn = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(getTextureLocation(pelicanEntity)));
        }
        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, bufferIn, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }


}
