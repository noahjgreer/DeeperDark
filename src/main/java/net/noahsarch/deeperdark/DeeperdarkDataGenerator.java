package net.noahsarch.deeperdark;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.noahsarch.deeperdark.recipe.BlastFurnaceRecipeGenerator;

public class DeeperdarkDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(BlastFurnaceRecipeGenerator::new);
	}
}
