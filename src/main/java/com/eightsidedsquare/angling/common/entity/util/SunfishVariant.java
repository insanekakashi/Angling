package com.eightsidedsquare.angling.common.entity.util;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;


public record SunfishVariant(ResourceLocation texture) {

    private static final Map<SunfishVariant, ResourceLocation> VARIANTS = new LinkedHashMap<>();

    public static final SunfishVariant PUMPKINSEED = create("pumpkinseed");
    public static final SunfishVariant LONGEAR = create("longear");
    public static final SunfishVariant BLUEGILL = create("bluegill");
    public static final SunfishVariant REDBREAST = create("redbreast");
    public static final SunfishVariant GREEN = create("green");
    public static final SunfishVariant WARMOUTH = create("warmouth");
    public static final SunfishVariant BLUEGILL_AND_REDBREAST_HYBRID = create("bluegill_and_redbreast_hybrid");
    public static final SunfishVariant BLUEGILL_AND_PUMPKINSEED_HYBRID = create("bluegill_and_pumpkinseed_hybrid");
    public static final SunfishVariant DIANSUS_DIANSUR = create("diansus_diansur");

    public static final Registry<SunfishVariant> REGISTRY = FabricRegistryBuilder
            .createDefaulted(SunfishVariant.class, new ResourceLocation(MOD_ID, "sunfish_variant"), new ResourceLocation(MOD_ID, "pumpkinseed"))
            .attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final EntityDataSerializer<SunfishVariant> TRACKED_DATA_HANDLER = EntityDataSerializer.simpleId(REGISTRY);

    public static ResourceLocation getId(SunfishVariant variant) {
        return REGISTRY.getKey(variant);
    }

    public static SunfishVariant fromId(String id) {
        return fromId(ResourceLocation.tryParse(id));
    }

    public String getTranslationKey() {
        return "sunfish_variant." + getId(this).getNamespace() + "." + getId(this).getPath();
    }

    public static SunfishVariant fromId(ResourceLocation id) {
        return REGISTRY.get(id);
    }


    private static SunfishVariant create(String name) {
        SunfishVariant variant = new SunfishVariant(new ResourceLocation(MOD_ID, "textures/entity/sunfish/" + name + ".png"));
        VARIANTS.put(variant, new ResourceLocation(MOD_ID, name));
        return variant;
    }

    public static void init() {
        EntityDataSerializers.registerSerializer(TRACKED_DATA_HANDLER);
        VARIANTS.keySet().forEach(variant -> Registry.register(REGISTRY, VARIANTS.get(variant), variant));
    }

    public static class Tag {

        public static final TagKey<SunfishVariant> NATURAL_SUNFISH = of("natural_sunfish");
        public static final TagKey<SunfishVariant> PELICAN_BEAK_VARIANTS = of("pelican_beak_variants");

        private static TagKey<SunfishVariant> of(String id) {
            return of(new ResourceLocation(MOD_ID, id));
        }

        public static TagKey<SunfishVariant> of(ResourceLocation id) {
            return TagKey.create(REGISTRY.key(), id);
        }
    }
}
