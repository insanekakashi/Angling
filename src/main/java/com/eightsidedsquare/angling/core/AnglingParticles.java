package com.eightsidedsquare.angling.core;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingParticles {

    public static SimpleParticleType ALGAE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "algae"), FabricParticleTypes.simple(true));
    public static SimpleParticleType WORM = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "worm"), FabricParticleTypes.simple(true));

    public static void init() {

    }

}
