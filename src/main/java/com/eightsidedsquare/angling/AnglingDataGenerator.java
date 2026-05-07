package com.eightsidedsquare.angling;

import com.eightsidedsquare.angling.core.world.AnglingConfiguredFeatures;
import com.eightsidedsquare.angling.core.world.AnglingPlacedFeatures;
import com.eightsidedsquare.angling.datagen.AnglingWorldDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class AnglingDataGenerator implements DataGeneratorEntrypoint{

    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(AnglingWorldDataGenerator::new);
    System.out.println("Running Data Generator");

    }

    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, AnglingConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, AnglingPlacedFeatures::bootstrap);
    }
}
