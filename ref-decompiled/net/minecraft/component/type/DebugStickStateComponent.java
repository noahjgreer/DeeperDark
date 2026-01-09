package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

public record DebugStickStateComponent(Map properties) {
   public static final DebugStickStateComponent DEFAULT = new DebugStickStateComponent(Map.of());
   public static final Codec CODEC;

   public DebugStickStateComponent(Map map) {
      this.properties = map;
   }

   public DebugStickStateComponent with(RegistryEntry block, Property property) {
      return new DebugStickStateComponent(Util.mapWith(this.properties, block, property));
   }

   public Map properties() {
      return this.properties;
   }

   static {
      CODEC = Codec.dispatchedMap(Registries.BLOCK.getEntryCodec(), (block) -> {
         return Codec.STRING.comapFlatMap((property) -> {
            Property property2 = ((Block)block.value()).getStateManager().getProperty(property);
            return property2 != null ? DataResult.success(property2) : DataResult.error(() -> {
               String var10000 = block.getIdAsString();
               return "No property on " + var10000 + " with name: " + property;
            });
         }, Property::getName);
      }).xmap(DebugStickStateComponent::new, DebugStickStateComponent::properties);
   }
}
