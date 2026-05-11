package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.CrabEntity;
import com.eightsidedsquare.angling.core.AnglingUtil;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class CrabEntityModel extends GeoModel<CrabEntity> {

    @Override
    public ResourceLocation getModelResource(CrabEntity entity) {
        return new ResourceLocation(MOD_ID, "geo/crab.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CrabEntity entity) {
        return entity.getVariant().texture();
    }

    @Override
    public ResourceLocation getAnimationResource(CrabEntity entity) {
        return new ResourceLocation(MOD_ID, "animations/crab.animation.json");
    }

    @Override @SuppressWarnings("unchecked")
    public void setCustomAnimations(CrabEntity entity, long uniqueId, AnimationState<CrabEntity> event) {
        if(!AnglingUtil.isReloadingResources()) {
            super.setCustomAnimations(entity, uniqueId, event);
            CoreGeoBone root = getAnimationProcessor().getBone("root");
            CoreGeoBone eyes = getAnimationProcessor().getBone("eyes");
            EntityModelData extraData = event.getData(DataTickets.ENTITY_MODEL_DATA);
            if (eyes != null) {
                eyes.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
            }
            if(extraData.isChild() && root != null) {
                root.setScaleX(0.35f);
                root.setScaleY(0.35f);
                root.setScaleZ(0.35f);
                root.setPosY(-1.75f);
            }
        }
    }
}
