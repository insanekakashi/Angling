package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.core.AnglingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import org.jetbrains.annotations.Nullable;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class BasicEntityModel<A extends LivingEntity & GeoEntity> extends GeoModel<A> {

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;
    private final boolean liesOutOfWater;
    private final @Nullable String head;

    public BasicEntityModel(ResourceLocation model, ResourceLocation texture, ResourceLocation animation, boolean liesOutOfWater, @Nullable String head) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.liesOutOfWater = liesOutOfWater;
        this.head = head;
    }

    public BasicEntityModel(String name, boolean liesOutOfWater, @Nullable String head) {
        this(
                new ResourceLocation(MOD_ID,"geo/" + name + ".geo.json"),
                new ResourceLocation(MOD_ID, "textures/entity/" + name + "/" + name + ".png"),
                new ResourceLocation(MOD_ID, "animations/" + name + ".animation.json"), liesOutOfWater, head);

    }

    public BasicEntityModel(String name, boolean liesOutOfWater) {
        this(name, liesOutOfWater, null);
    }

    @Override
    public ResourceLocation getModelResource(A entity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(A entity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(A entity) {
        return animation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setCustomAnimations(A entity, long instanceId, AnimationState<A> event) {
        if(!AnglingUtil.isReloadingResources()) {
            if(liesOutOfWater) {
                CoreGeoBone root = getAnimationProcessor().getBone("root");
                if (!entity.isInWater() && root != null) {
                    root.setRotZ((float) (Math.PI / 2d));
                }
            }
            if(head != null) {
                CoreGeoBone headBone = this.getAnimationProcessor().getBone(head);
                EntityModelData extraData = event.getData(DataTickets.ENTITY_MODEL_DATA);
                if (headBone != null) {
                    headBone.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
                    headBone.setRotY(extraData.netHeadYaw() * ((float) Math.PI / 180F));
                }
            }
        }
    }
}
