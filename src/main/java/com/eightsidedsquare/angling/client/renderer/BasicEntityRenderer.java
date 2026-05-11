package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BasicEntityRenderer<A extends LivingEntity & GeoEntity> extends GeoEntityRenderer<A> {

    public BasicEntityRenderer(EntityRendererProvider.Context ctx, GeoModel<A> modelProvider) {
        super(ctx, modelProvider);
    }

    public static <A extends LivingEntity & GeoEntity> EntityRendererProvider<A> create(GeoModel<A> model) {
        return ctx -> new BasicEntityRenderer<>(ctx, model);
    }

    public static <A extends LivingEntity & GeoEntity> EntityRendererProvider<A> create(String name, boolean liesOutOfWater) {
        return ctx -> new BasicEntityRenderer<>(ctx, new BasicEntityModel<>(name, liesOutOfWater));
    }

    public static <A extends LivingEntity & GeoEntity> EntityRendererProvider<A> create(String name, boolean liesOutOfWater, String head) {
        return ctx -> new BasicEntityRenderer<>(ctx, new BasicEntityModel<>(name, liesOutOfWater, head));
    }

    @Override
    public RenderType getRenderType(A entity, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}
