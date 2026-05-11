package com.eightsidedsquare.angling.client;

import com.eightsidedsquare.angling.client.model.CrabEntityModel;
import com.eightsidedsquare.angling.client.model.DongfishEntityModel;
import com.eightsidedsquare.angling.client.model.SunfishEntityModel;
import com.eightsidedsquare.angling.client.particle.AlgaeParticle;
import com.eightsidedsquare.angling.client.particle.WormParticle;
import com.eightsidedsquare.angling.client.renderer.*;
import com.eightsidedsquare.angling.common.entity.RoeBlockEntity;
import com.eightsidedsquare.angling.common.entity.SeaSlugEggsBlockEntity;
import com.eightsidedsquare.angling.common.entity.StarfishBlockEntity;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugColor;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugPattern;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.awt.*;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                AnglingBlocks.ROE, AnglingBlocks.DUCKWEED,
                AnglingBlocks.OYSTERS, AnglingBlocks.CLAM,
                AnglingBlocks.SEA_SLUG_EGGS, AnglingBlocks.PAPYRUS,
                AnglingBlocks.SARGASSUM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), AnglingBlocks.ALGAE);

        EntityRendererRegistry.register(AnglingEntities.FRY, FryEntityRenderer::new);
        EntityRendererRegistry.register(AnglingEntities.SUNFISH, BasicEntityRenderer.create(new SunfishEntityModel()));
        EntityRendererRegistry.register(AnglingEntities.PELICAN, PelicanEntityRenderer::new);
        EntityRendererRegistry.register(AnglingEntities.NAUTILUS, NautilusEntityRenderer::new);
        EntityRendererRegistry.register(AnglingEntities.SEA_SLUG, SeaSlugEntityRenderer::new);
        EntityRendererRegistry.register(AnglingEntities.CRAB, BasicEntityRenderer.create(new CrabEntityModel()));
        EntityRendererRegistry.register(AnglingEntities.DONGFISH, BasicEntityRenderer.create(new DongfishEntityModel()));
        EntityRendererRegistry.register(AnglingEntities.CATFISH, BasicEntityRenderer.create("catfish", true));
        EntityRendererRegistry.register(AnglingEntities.SEAHORSE, BasicEntityRenderer.create("seahorse", true, "head"));
        EntityRendererRegistry.register(AnglingEntities.BUBBLE_EYE, BasicEntityRenderer.create("bubble_eye", true));
        EntityRendererRegistry.register(AnglingEntities.ANOMALOCARIS, BasicEntityRenderer.create("anomalocaris", false, "head"));
        EntityRendererRegistry.register(AnglingEntities.ANGLERFISH, AnglerfishEntityRenderer::new);
        EntityRendererRegistry.register(AnglingEntities.MAHI_MAHI, BasicEntityRenderer.create("mahi_mahi", true));

        BlockEntityRendererRegistry.register(AnglingEntities.STARFISH, StarfishBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(AnglingEntities.ANEMONE, AnemoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(AnglingEntities.URCHIN, UrchinBlockEntityRenderer::new);
        if(FabricLoader.getInstance().isModLoaded("sodium")) {
            BlockEntityRendererRegistry.register(AnglingEntities.ROE, RoeBlockEntityRenderer::new);
            BlockEntityRendererRegistry.register(AnglingEntities.SEA_SLUG_EGGS, SeaSlugEggsBlockEntityRenderer::new);
        }

        ColorProviderRegistry.BLOCK.register(RoeBlockEntity::getColor, AnglingBlocks.ROE);
        ColorProviderRegistry.BLOCK.register(SeaSlugEggsBlockEntity::getColor, AnglingBlocks.SEA_SLUG_EGGS);
        ColorProviderRegistry.BLOCK.register(StarfishBlockEntity::getColor, AnglingBlocks.STARFISH);

        ColorProviderRegistry.ITEM.register(RoeBlockEntity::getItemColor, AnglingItems.ROE);
        ColorProviderRegistry.ITEM.register(SeaSlugEggsBlockEntity::getItemColor, AnglingItems.SEA_SLUG_EGGS);
        ColorProviderRegistry.ITEM.register(StarfishBlockEntity::getItemColor, AnglingBlocks.STARFISH.asItem());
        ColorProviderRegistry.ITEM.register(this::getTropicalFishBucketColor, Items.TROPICAL_FISH_BUCKET);
        ColorProviderRegistry.ITEM.register(this::getFryBucketColor, AnglingItems.FRY_BUCKET);
        ColorProviderRegistry.ITEM.register(this::getSeaSlugBucketColor, AnglingItems.SEA_SLUG_BUCKET);

        ParticleFactoryRegistry.getInstance().register(AnglingParticles.ALGAE, AlgaeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(AnglingParticles.WORM, WormParticle.Factory::new);

        ItemProperties.register(AnglingItems.DONGFISH_BUCKET, new ResourceLocation(MOD_ID, "has_horngus"), this::dongfishBucketItemHasHorngus);

    }

    private float dongfishBucketItemHasHorngus(ItemStack stack, ClientLevel clientWorld, LivingEntity livingEntity, int i) {
        CompoundTag nbt = stack.getOrCreateTag();
        return !nbt.contains("HasHorngus") || nbt.getBoolean("HasHorngus") ? 1 : 0;

    }

    private int getFryBucketColor(ItemStack stack, int i) {
        if(i == 0)
            return 0xffffff;
        CompoundTag nbt = stack.getOrCreateTag();
        if(nbt.contains("Color"))
            return stack.getOrCreateTag().getInt("Color");
        return 0xffffff;
    }

    private int getTropicalFishBucketColor(ItemStack stack, int i) {
        CompoundTag nbt = stack.getOrCreateTag();
        if(i == 0)
            return 0xffffff;
        float[] colorComponents = (i == 1 ? DyeColor.WHITE : DyeColor.ORANGE).getTextureDiffuseColors();
        if(nbt != null && nbt.contains("BucketVariantTag")) {
            int variant = nbt.getInt("BucketVariantTag");
            colorComponents = (i == 1 ? TropicalFish.getPatternColor(variant) : TropicalFish.getBaseColor(variant)).getTextureDiffuseColors();
        }
        return new Color(colorComponents[0], colorComponents[1], colorComponents[2]).getRGB();
    }

    private int getSeaSlugBucketColor(ItemStack stack, int i) {
        CompoundTag nbt = stack.getOrCreateTag();
        if(i == 0)
            return 0xffffff;
        else if(i == 1 || (nbt.contains("Pattern", Tag.TAG_STRING) && SeaSlugPattern.fromId(nbt.getString("Pattern")).equals(SeaSlugPattern.NONE)))
            return nbt.contains("BaseColor", Tag.TAG_STRING) ? SeaSlugColor.fromId(nbt.getString("BaseColor")).color() : 0x6f4e37;
        return nbt.contains("PatternColor", Tag.TAG_STRING) ? SeaSlugColor.fromId(nbt.getString("PatternColor")).color() : 0xff3800;

    }
}
