package com.eightsidedsquare.angling.common.entity.util;

import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingUtil;
import com.eightsidedsquare.angling.core.tags.AnglingEntityTypeTags;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public abstract class PelicanBeakEntityInitializer {
    private static final Map<EntityType<?>, PelicanBeakEntityInitializer> INITIALIZERS = Util.make(new Object2ObjectOpenHashMap<>(), map -> {});

    public abstract CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world);

    public static final PelicanBeakEntityInitializer SIMPLE_INITIALIZER = new PelicanBeakEntityInitializer() {
        @Override
        public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
            return nbt;
        }
    };

    public static void init() {
        registerInitializer(AnglingEntities.DONGFISH, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                nbt.putBoolean("HasHorngus", true);
                return nbt;
            }
        });
        registerInitializer(EntityType.PUFFERFISH, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                nbt.putInt("PuffState", random.nextInt(3));
                return nbt;
            }
        });
        registerInitializer(EntityType.TROPICAL_FISH, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                int variant = random.nextInt(5) == 0 ?
                        (random.nextInt(2) | random.nextInt(6) << 8 | random.nextInt(15) << 16 | random.nextInt(15) << 24)
                        : Util.getRandom(TropicalFish.COMMON_VARIANTS.stream().map(v -> v.getPackedId()).collect(Collectors.toList()), random);
                nbt.putInt("Variant", variant);
                return nbt;
            }
        });
        registerInitializer(EntityType.AXOLOTL, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                nbt.putInt("Variant", Axolotl.Variant.getCommonSpawnVariant(random).getId());
                return nbt;
            }
        });
        registerInitializer(EntityType.FROG, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                ResourceLocation variant = BuiltInRegistries.FROG_VARIANT.getKey(Util.getRandom(BuiltInRegistries.FROG_VARIANT.stream().toList(), random));
                if(variant != null)
                    nbt.putString("variant", variant.toString());
                return nbt;
            }
        });
        registerInitializer(EntityType.RABBIT, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                nbt.putInt("RabbitType", random.nextInt(6));
                return nbt;
            }
        });
        registerInitializer(AnglingEntities.SUNFISH, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                ResourceLocation variant = SunfishVariant.REGISTRY.getKey(
                        AnglingUtil.getRandomTagValue(world, SunfishVariant.Tag.PELICAN_BEAK_VARIANTS, random));
                if(variant != null){
                    nbt.putString("Variant", variant.toString());
                }
                return nbt;
            }
        });
        registerInitializer(AnglingEntities.FRY, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                EntityType<?> growUpTo = AnglingUtil.getRandomTagValue(world, AnglingEntityTypeTags.SPAWNING_FISH, random);
                nbt.putString("GrowUpTo", BuiltInRegistries.ENTITY_TYPE.getKey(growUpTo).toString());
                CompoundTag variant = new CompoundTag();
                if(!growUpTo.equals(AnglingEntities.FRY))
                    getInitializer(growUpTo).initialize(variant, random, world);
                nbt.put("Variant", variant);
                nbt.putInt("Age", -12000);
                SpawnEggItem spawnEggItem = SpawnEggItem.byId(growUpTo);
                if(spawnEggItem != null) {
                    nbt.putInt("Color", spawnEggItem.getColor(0));
                }
                return nbt;
            }
        });
        registerInitializer(AnglingEntities.SEA_SLUG, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                nbt.putString("Pattern", AnglingUtil.getRandomTagValue(world, SeaSlugPattern.Tag.NATURAL_PATTERNS, random).getId().toString());
                nbt.putString("BaseColor", AnglingUtil.getRandomTagValue(world, SeaSlugColor.Tag.BASE_COLORS, random).getId().toString());
                nbt.putString("PatternColor", AnglingUtil.getRandomTagValue(world, SeaSlugColor.Tag.PATTERN_COLORS, random).getId().toString());
                nbt.putBoolean("Bioluminescent", random.nextBoolean());
                return nbt;
            }
        });
        registerInitializer(AnglingEntities.CRAB, new PelicanBeakEntityInitializer() {
            @Override
            public CompoundTag initialize(CompoundTag nbt, RandomSource random, Level world) {
                nbt.putString("Variant", AnglingUtil.getRandomTagValue(world, CrabVariant.Tag.NATURAL_VARIANTS, random).getId().toString());
                return nbt;
            }
        });
    }

    public static PelicanBeakEntityInitializer getInitializer(EntityType<?> type) {
        return INITIALIZERS.getOrDefault(type, SIMPLE_INITIALIZER);
    }

    public static void registerInitializer(@NotNull EntityType<?> type, @NotNull PelicanBeakEntityInitializer initializer) {
        INITIALIZERS.put(type, initializer);
    }
}
