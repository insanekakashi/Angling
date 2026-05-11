package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.block.StarfishBlock;
import com.eightsidedsquare.angling.common.entity.StarfishBlockEntity;
import com.eightsidedsquare.angling.core.AnglingUtil;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class StarfishBlockEntityModel extends GeoModel<StarfishBlockEntity> {
    @Override
    public ResourceLocation getModelResource(StarfishBlockEntity object) {
        return new ResourceLocation(MOD_ID, "geo/starfish.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StarfishBlockEntity entity) {
        if(entity != null && !((StarfishBlock) entity.getBlockState().getBlock()).isDead())
            return new ResourceLocation(MOD_ID, "textures/entity/starfish/starfish.png");
        return new ResourceLocation(MOD_ID, "textures/entity/starfish/dead_starfish.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StarfishBlockEntity animatable) {
        return new ResourceLocation(MOD_ID, "animations/starfish.animation.json");
    }

    @Override
    public void setCustomAnimations(StarfishBlockEntity entity, long uniqueID, AnimationState<StarfishBlockEntity> event) {
        if(!AnglingUtil.isReloadingResources()){
            super.setCustomAnimations(entity, uniqueID, event);
            Optional.ofNullable(getAnimationProcessor().getBone("root")).ifPresent(bone -> {
                Vec3i rotation = entity.getRotation();
                bone.setRotX((float) Math.toRadians(rotation.getX()));
                bone.setRotY((float) Math.toRadians(rotation.getY()));
                bone.setRotZ((float) Math.toRadians(rotation.getZ()));
            });
            Optional.ofNullable(getAnimationProcessor().getBone("starfish")).ifPresent(bone ->
                    bone.setRotY((float) entity.getRandomRotation()));
        }
    }
}
