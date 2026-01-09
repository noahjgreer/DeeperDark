package net.minecraft.client.resource.waypoint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.waypoint.WaypointStyles;

@Environment(EnvType.CLIENT)
public class WaypointStyleAssetManager extends JsonDataLoader {
   private static final ResourceFinder FINDER = ResourceFinder.json("waypoint_style");
   private static final WaypointStyleAsset MISSING = new WaypointStyleAsset(0, 1, List.of(MissingSprite.getMissingSpriteId()));
   private Map registry = Map.of();

   public WaypointStyleAssetManager() {
      super(WaypointStyleAsset.CODEC, FINDER);
   }

   protected void apply(Map map, ResourceManager resourceManager, Profiler profiler) {
      this.registry = (Map)map.entrySet().stream().collect(Collectors.toUnmodifiableMap((entry) -> {
         return RegistryKey.of(WaypointStyles.REGISTRY, (Identifier)entry.getKey());
      }, Map.Entry::getValue));
   }

   public WaypointStyleAsset get(RegistryKey key) {
      return (WaypointStyleAsset)this.registry.getOrDefault(key, MISSING);
   }
}
