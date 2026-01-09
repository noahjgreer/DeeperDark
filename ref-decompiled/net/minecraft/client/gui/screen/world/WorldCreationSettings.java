package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.world.level.WorldGenSettings;

@Environment(EnvType.CLIENT)
public record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
   public WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
      this.worldGenSettings = worldGenSettings;
      this.dataConfiguration = dataConfiguration;
   }

   public WorldGenSettings worldGenSettings() {
      return this.worldGenSettings;
   }

   public DataConfiguration dataConfiguration() {
      return this.dataConfiguration;
   }
}
