package com.eightsidedsquare.angling;

import com.eightsidedsquare.angling.core.world.AnglingConfiguredFeatures;
import com.eightsidedsquare.angling.core.world.AnglingPlacedFeatures;
import com.eightsidedsquare.angling.datagen.AnglingWorldDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class AnglingDataGenerator implements DataGeneratorEntrypoint{

    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(AnglingWorldDataGenerator::new);
    System.out.println("Running Data Generator");

    }

    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, AnglingConfiguredFeatures::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, AnglingPlacedFeatures::bootstrap);
    }
}
