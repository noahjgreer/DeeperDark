package net.minecraft.client.render.entity.equipment;

import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class EquipmentModelLoader extends JsonDataLoader {
   public static final EquipmentModel EMPTY = new EquipmentModel(Map.of());
   private static final ResourceFinder FINDER = ResourceFinder.json("equipment");
   private Map models = Map.of();

   public EquipmentModelLoader() {
      super(EquipmentModel.CODEC, FINDER);
   }

   protected void apply(Map map, ResourceManager resourceManager, Profiler profiler) {
      this.models = (Map)map.entrySet().stream().collect(Collectors.toUnmodifiableMap((entry) -> {
         return RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, (Identifier)entry.getKey());
      }, Map.Entry::getValue));
   }

   public EquipmentModel get(RegistryKey assetKey) {
      return (EquipmentModel)this.models.getOrDefault(assetKey, EMPTY);
   }
}
