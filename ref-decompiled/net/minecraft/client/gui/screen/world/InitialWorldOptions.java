package net.minecraft.client.gui.screen.world;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record InitialWorldOptions(WorldCreator.Mode selectedGameMode, Set disabledGameRules, @Nullable RegistryKey flatLevelPreset) {
   public InitialWorldOptions(WorldCreator.Mode mode, Set set, @Nullable RegistryKey registryKey) {
      this.selectedGameMode = mode;
      this.disabledGameRules = set;
      this.flatLevelPreset = registryKey;
   }

   public WorldCreator.Mode selectedGameMode() {
      return this.selectedGameMode;
   }

   public Set disabledGameRules() {
      return this.disabledGameRules;
   }

   @Nullable
   public RegistryKey flatLevelPreset() {
      return this.flatLevelPreset;
   }
}
