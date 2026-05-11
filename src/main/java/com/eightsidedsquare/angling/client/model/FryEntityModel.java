package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.FryEntity;
import net.minecraft.resources.ResourceLocation;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class FryEntityModel extends BasicEntityModel<FryEntity> {
    public FryEntityModel() {
        super("fry", true);
    }
    @Override
    public ResourceLocation getTextureResource(FryEntity object) {
        return new ResourceLocation(MOD_ID, "textures/entity/fry/fry_innards.png");
    }
}
