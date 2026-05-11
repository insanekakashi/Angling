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

@SuppressWarnings("unused")
public record SeaSlugColor(int color) {

    private static final Map<SeaSlugColor, ResourceLocation> COLORS = new LinkedHashMap<>();

    public static final SeaSlugColor IVORY = create("ivory", 0xFFFFF0);
    public static final SeaSlugColor ONYX = create("onyx", 0x353839);
    public static final SeaSlugColor PERIWINKLE = create("periwinkle", 0xCCCCFF);
    public static final SeaSlugColor BURGUNDY = create("burgundy", 0x800020);
    public static final SeaSlugColor COQUELICOT = create("coquelicot", 0xFF3800);
    public static final SeaSlugColor GAMBOGE = create("gamboge", 0xE49B0F);
    public static final SeaSlugColor JADE = create("jade", 0x00A86B);
    public static final SeaSlugColor MIDNIGHT = create("midnight", 0x702670);
    public static final SeaSlugColor ULTRAMARINE = create("ultramarine", 0x3F00FF);
    public static final SeaSlugColor COFFEE = create("coffee", 0x6F4E37);
    public static final SeaSlugColor CELESTE = create("celeste", 0xB2FFFF);
    public static final SeaSlugColor EGGPLANT = create("eggplant", 0x614051);
    public static final SeaSlugColor OLIVINE = create("olivine", 0x9AB973);
    public static final SeaSlugColor PEAR = create("pear", 0xD1E231);
    public static final SeaSlugColor CYCLAMEN = create("cyclamen", 0xF56FA1);
    public static final SeaSlugColor AMBER = create("amber", 0xFF7E00);
    public static final SeaSlugColor IRIS = create("iris", 0x5A4FCF);
    public static final SeaSlugColor ORCHID = create("orchid", 0xDA70D6D);
    public static final SeaSlugColor FOLLY = create("folly", 0xFF004A);

    public static final Registry<SeaSlugColor> REGISTRY = FabricRegistryBuilder
            .createDefaulted(SeaSlugColor.class, new ResourceLocation(MOD_ID, "sea_slug_color"), new ResourceLocation(MOD_ID, "ivory"))
            .attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final EntityDataSerializer<SeaSlugColor> TRACKED_DATA_HANDLER = EntityDataSerializer.simpleId(REGISTRY);

    public String getTranslationKey() {
        return "sea_slug_color." + this.getId().getNamespace() + "." + this.getId().getPath();
    }

    public ResourceLocation getId() {
        return REGISTRY.getKey(this);
    }

    public static SeaSlugColor fromId(String id) {
        return fromId(ResourceLocation.tryParse(id));
    }

    public static SeaSlugColor fromId(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    private static SeaSlugColor create(String name, int color) {
        SeaSlugColor seaSlugColor = new SeaSlugColor(color);
        COLORS.put(seaSlugColor, new ResourceLocation(MOD_ID, name));
        return seaSlugColor;
    }

    public static void init() {
        EntityDataSerializers.registerSerializer(TRACKED_DATA_HANDLER);
        COLORS.keySet().forEach(color -> Registry.register(REGISTRY, COLORS.get(color), color));
    }

    public static class Tag {

        public static final TagKey<SeaSlugColor> BASE_COLORS = of("base_colors");
        public static final TagKey<SeaSlugColor> PATTERN_COLORS = of("pattern_colors");

        private static TagKey<SeaSlugColor> of(String id) {
            return of(new ResourceLocation(MOD_ID, id));
        }

        public static TagKey<SeaSlugColor> of(ResourceLocation id) {
            return TagKey.create(REGISTRY.key(), id);
        }
    }

}
