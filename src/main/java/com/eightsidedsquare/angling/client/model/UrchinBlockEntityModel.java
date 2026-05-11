package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.UrchinBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class UrchinBlockEntityModel extends GeoModel<UrchinBlockEntity> {

    @Override
    public ResourceLocation getModelResource(UrchinBlockEntity object) {
        return new ResourceLocation(MOD_ID, "geo/urchin.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UrchinBlockEntity entity) {
        return new ResourceLocation(MOD_ID, "textures/entity/urchin/urchin.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UrchinBlockEntity animatable) {
        return new ResourceLocation(MOD_ID, "animations/urchin.animation.json");
    }

}
