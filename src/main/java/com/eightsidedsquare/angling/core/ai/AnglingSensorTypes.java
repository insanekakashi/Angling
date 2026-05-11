package com.eightsidedsquare.angling.core.ai;

import com.eightsidedsquare.angling.common.entity.ai.PelicanAttackablesSensor;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingSensorTypes {

    public static final SensorType<PelicanAttackablesSensor> PELICAN_ATTACKABLES =  register("pelican_attackables", PelicanAttackablesSensor::new);

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, new ResourceLocation(MOD_ID, id), new SensorType<>(factory));
    }
}
