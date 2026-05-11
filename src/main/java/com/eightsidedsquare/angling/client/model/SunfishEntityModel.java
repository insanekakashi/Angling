package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.SunfishEntity;
import com.eightsidedsquare.angling.common.entity.util.SunfishVariant;
import net.minecraft.resources.ResourceLocation;

public class SunfishEntityModel extends BasicEntityModel<SunfishEntity> {
    public SunfishEntityModel() {
        super("sunfish", true);
    }
    @Override
    public ResourceLocation getTextureResource(SunfishEntity entity) {
        SunfishVariant variant = entity.getVariant();
        return variant != null ? variant.texture() : SunfishVariant.BLUEGILL.texture();
    }
}
