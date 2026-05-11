package com.eightsidedsquare.angling.common.entity.util;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public record SeaSlugPattern(@Nullable ResourceLocation texture) {

    private static final Map<SeaSlugPattern, ResourceLocation> PATTERNS = new LinkedHashMap<>();

    public static final SeaSlugPattern NONE = create("none", true);
    public static final SeaSlugPattern STRIPES = create("stripes", false);
    public static final SeaSlugPattern SQUIGGLES = create("squiggles", false);
    public static final SeaSlugPattern SPOTS = create("spots", false);
    public static final SeaSlugPattern RINGS = create("rings", false);

    public static final Registry<SeaSlugPattern> REGISTRY = FabricRegistryBuilder
            .createDefaulted(SeaSlugPattern.class, new ResourceLocation(MOD_ID, "sea_slug_pattern"), new ResourceLocation(MOD_ID, "none"))
            .attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public String getTranslationKey() {
        return "sea_slug_pattern." + this.getId().getNamespace() + "." + this.getId().getPath();
    }

    public static final EntityDataSerializer<SeaSlugPattern> TRACKED_DATA_HANDLER = EntityDataSerializer.simpleId(REGISTRY);

    public ResourceLocation getId() {
        return REGISTRY.getKey(this);
    }

    public static SeaSlugPattern fromId(String id) {
        return fromId(ResourceLocation.tryParse(id));
    }

    public static SeaSlugPattern fromId(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    private static SeaSlugPattern create(String name, boolean empty) {
        SeaSlugPattern pattern = new SeaSlugPattern(empty ? null : new ResourceLocation(MOD_ID, "textures/entity/sea_slug/" + name + ".png"));
        PATTERNS.put(pattern, new ResourceLocation(MOD_ID, name));
        return pattern;
    }

    public static void init() {
        EntityDataSerializers.registerSerializer(TRACKED_DATA_HANDLER);
        PATTERNS.keySet().forEach(variant -> Registry.register(REGISTRY, PATTERNS.get(variant), variant));
    }

    public static class Tag {

        public static final TagKey<SeaSlugPattern> NATURAL_PATTERNS = of("natural_patterns");

        private static TagKey<SeaSlugPattern> of(String id) {
            return of(new ResourceLocation(MOD_ID, id));
        }

        public static TagKey<SeaSlugPattern> of(ResourceLocation id) {
            return TagKey.create(REGISTRY.key(), id);
        }
    }
}
