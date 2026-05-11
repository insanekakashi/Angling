package com.eightsidedsquare.angling.common.entity.util;

import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.Level;

public abstract class FishVariantInheritance {

    private static final Map<EntityType<? extends AbstractFish>, FishVariantInheritance> INHERITANCE_TYPES = Util.make(new Object2ObjectOpenHashMap<>(), map -> {});

    public static final FishVariantInheritance SIMPLE_INHERITANCE = new FishVariantInheritance() {
        @Override
        protected CompoundTag getChild(CompoundTag parent, CompoundTag mate, CompoundTag child, Level world) {
            return child;
        }
    };

    public static void init() {
        registerVariantInheritance(EntityType.COD, SIMPLE_INHERITANCE);
        registerVariantInheritance(EntityType.SALMON, SIMPLE_INHERITANCE);
        registerVariantInheritance(EntityType.PUFFERFISH, SIMPLE_INHERITANCE);
        registerVariantInheritance(AnglingEntities.DONGFISH, SIMPLE_INHERITANCE);
        registerVariantInheritance(EntityType.TROPICAL_FISH, new FishVariantInheritance() {
            @Override
            protected CompoundTag getChild(CompoundTag parent, CompoundTag mate, CompoundTag child, Level world) {
                int parentVariant = parent.getInt("Variant");
                int mateVariant = mate.getInt("Variant");
                RandomSource random = world.getRandom();
                int variant = pickRandomTropicalFishVariantValue(parentVariant, mateVariant, random, 0, 0xffff, false) |
                        pickRandomTropicalFishVariantValue(parentVariant, mateVariant, random, 16, 0xff, true) |
                        pickRandomTropicalFishVariantValue(parentVariant, mateVariant, random, 24, 0xff, true);
                child.putInt("Variant", variant);
                return child;
            }

            private int pickRandomTropicalFishVariantValue(int parentVariant, int mateVariant, RandomSource random, int shift, int size, boolean color) {
                if(random.nextInt(16) == 0) {
                    return (color ? random.nextInt(15) : random.nextInt(2) | (random.nextInt(6) << 8)) << shift;
                }
                return ((((random.nextBoolean() ? parentVariant : mateVariant) >> shift) & size) << shift);
            }
        });
        registerVariantInheritance(AnglingEntities.SUNFISH, new FishVariantInheritance() {
            @Override
            protected CompoundTag getChild(CompoundTag parent, CompoundTag mate, CompoundTag child, Level world) {
                SunfishVariant parentVariant = SunfishVariant.fromId(ResourceLocation.tryParse(parent.getString("Variant")));
                SunfishVariant mateVariant = SunfishVariant.fromId(ResourceLocation.tryParse(mate.getString("Variant")));
                SunfishVariant childVariant = world.getRandom().nextBoolean() ? parentVariant : mateVariant;
                if(AnglingUtil.pairsAreEqual(parentVariant, mateVariant, SunfishVariant.BLUEGILL, SunfishVariant.REDBREAST)) {
                    childVariant = SunfishVariant.BLUEGILL_AND_REDBREAST_HYBRID;
                }else if(AnglingUtil.pairsAreEqual(parentVariant, mateVariant, SunfishVariant.BLUEGILL, SunfishVariant.PUMPKINSEED)) {
                    childVariant = SunfishVariant.BLUEGILL_AND_PUMPKINSEED_HYBRID;
                }else if(childVariant.equals(SunfishVariant.DIANSUS_DIANSUR))
                    childVariant = SunfishVariant.GREEN;
                child.putString("Variant", SunfishVariant.getId(childVariant).toString());
                return child;
            }
        });
    }

    public static FishVariantInheritance getVariantInheritance(EntityType<?> entityType) {
        return INHERITANCE_TYPES.getOrDefault(entityType, SIMPLE_INHERITANCE);
    }

    public static <U extends AbstractFish> void registerVariantInheritance(@NotNull EntityType<U> type, @NotNull FishVariantInheritance variantBreeder) {
        INHERITANCE_TYPES.put(type, variantBreeder);
    }

    protected abstract CompoundTag getChild(CompoundTag parent, CompoundTag mate, CompoundTag child, Level world);

    public CompoundTag getChild(CompoundTag parent, CompoundTag mate, Level world) {
        CompoundTag child = new CompoundTag();
        return getChild(parent, mate, child, world);
    }

}
