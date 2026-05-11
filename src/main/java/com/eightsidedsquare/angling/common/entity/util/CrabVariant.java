package com.eightsidedsquare.angling.common.entity.util;

import com.eightsidedsquare.angling.core.tags.AnglingBiomeTags;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public record CrabVariant(ResourceLocation texture, TagKey<Biome> biomeTag) {

    private static final Map<CrabVariant, ResourceLocation> VARIANTS = new LinkedHashMap<>();

    public static final CrabVariant DUNGENESS = create("dungeness", AnglingBiomeTags.DUNGENESS_CRAB_BIOMES);
    public static final CrabVariant GHOST = create("ghost", AnglingBiomeTags.GHOST_CRAB_BIOMES);
    public static final CrabVariant BLUE_CLAW = create("blue_claw", AnglingBiomeTags.BLUE_CLAW_CRAB_BIOMES);

    public static final Registry<CrabVariant> REGISTRY = FabricRegistryBuilder
            .createDefaulted(CrabVariant.class, new ResourceLocation(MOD_ID, "crab_variant"), new ResourceLocation(MOD_ID, "dungeness"))
            .attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public String getTranslationKey() {
        return "crab_variant." + this.getId().getNamespace() + "." + this.getId().getPath();
    }

    public static final EntityDataSerializer<CrabVariant> TRACKED_DATA_HANDLER = EntityDataSerializer.simpleId(REGISTRY);

    public ResourceLocation getId() {
        return REGISTRY.getKey(this);
    }

    public static CrabVariant fromId(String id) {
        return fromId(ResourceLocation.tryParse(id));
    }

    public static CrabVariant fromId(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    private static CrabVariant create(String name, TagKey<Biome> biomeTag) {
        CrabVariant pattern = new CrabVariant(new ResourceLocation(MOD_ID, "textures/entity/crab/" + name + ".png"), biomeTag);
        VARIANTS.put(pattern, new ResourceLocation(MOD_ID, name));
        return pattern;
    }

    public static void init() {
        EntityDataSerializers.registerSerializer(TRACKED_DATA_HANDLER);
        VARIANTS.keySet().forEach(variant -> Registry.register(REGISTRY, VARIANTS.get(variant), variant));
    }

    public static class Tag {

        public static final TagKey<CrabVariant> NATURAL_VARIANTS = of("natural_variants");

        private static TagKey<CrabVariant> of(String id) {
            return of(new ResourceLocation(MOD_ID, id));
        }

        public static TagKey<CrabVariant> of(ResourceLocation id) {
            return TagKey.create(REGISTRY.key(), id);
        }
    }

}
