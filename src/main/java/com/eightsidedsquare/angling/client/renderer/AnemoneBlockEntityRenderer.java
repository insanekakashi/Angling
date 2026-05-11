package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.AnemoneBlockEntityModel;
import com.eightsidedsquare.angling.common.entity.AnemoneBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AnemoneBlockEntityRenderer extends GeoBlockRenderer<AnemoneBlockEntity> {

    public AnemoneBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new AnemoneBlockEntityModel());
    }

    @Override
    public RenderType getRenderType(AnemoneBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
