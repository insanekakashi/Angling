package com.eightsidedsquare.angling.core;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AnglingUtil {

    public static <T> List<T> getTagValues(Level world, TagKey<T> tagKey) {
        return world.registryAccess().registryOrThrow(tagKey.registry()).getTag(tagKey).map(entries -> entries.stream().map(Holder::value).toList()).orElse(List.of());
    }

    public static <T> T getRandomTagValue(Level world, TagKey<T> tagKey, RandomSource random) {
        return Util.getRandom(getTagValues(world, tagKey), random);
    }

    public static CompoundTag entityToNbt(Entity entity, boolean stripData) {
        CompoundTag nbt = entity.saveWithoutId(new CompoundTag());
        nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        if(stripData){
            stripEntityNbt(nbt);
        }
        return nbt;
    }

    public static boolean runningSodium() {
        return FabricLoader.getInstance().isModLoaded("sodium");
    }

    public static void stripEntityNbt(CompoundTag nbt) {
        // Yes, this is cursed. No, I'm not sorry.
        nbt.remove("AbsorptionAmount");
        nbt.remove("Air");
        nbt.remove("ArmorDropChances");
        nbt.remove("ArmorItems");
        nbt.remove("Attributes");
        nbt.remove("CanPickUpLoot");
        nbt.remove("DeathTime");
        nbt.remove("FallDistance");
        nbt.remove("FallFlying");
        nbt.remove("Fire");
        nbt.remove("FromBucket");
        nbt.remove("HandDropChances");
        nbt.remove("HandItems");
        nbt.remove("Health");
        nbt.remove("HurtByTimestamp");
        nbt.remove("HurtTime");
        nbt.remove("Invulnerable");
        nbt.remove("LeftHanded");
        nbt.remove("Motion");
        nbt.remove("OnGround");
        nbt.remove("PersistenceRequired");
        nbt.remove("PortalCooldown");
        nbt.remove("Pos");
        nbt.remove("Rotation");
        nbt.remove("cardinal_components");
        nbt.remove("UUID");
    }

    public static <A, B, C, D> boolean pairsAreEqual(A a1, B a2, C b1, D b2) {
        return (a1.equals(b1) && a2.equals(b2)) || (a1.equals(b2) && a2.equals(b1));
    }

    public static Optional<Entity> entityFromNbt(CompoundTag nbt, Level world) {
        if(!nbt.contains("id", Tag.TAG_STRING))
            return Optional.empty();
        return Optional.ofNullable(EntityType.loadEntityRecursive(nbt, world, Function.identity()));
    }

    public static boolean isReloadingResources() {
        return Minecraft.getInstance().getOverlay() instanceof LoadingOverlay splashOverlay && splashOverlay.fadeIn;
    }

}
