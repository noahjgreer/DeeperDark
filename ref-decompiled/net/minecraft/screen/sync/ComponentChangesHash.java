package net.minecraft.screen.sync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;

public record ComponentChangesHash(Map addedComponents, Set removedComponents) {
   public static final PacketCodec PACKET_CODEC;

   public ComponentChangesHash(Map map, Set set) {
      this.addedComponents = map;
      this.removedComponents = set;
   }

   public static ComponentChangesHash fromComponents(ComponentChanges changes, ComponentHasher hasher) {
      ComponentChanges.AddedRemovedPair addedRemovedPair = changes.toAddedRemovedPair();
      Map map = new IdentityHashMap(addedRemovedPair.added().size());
      addedRemovedPair.added().forEach((component) -> {
         map.put(component.type(), (Integer)hasher.apply(component));
      });
      return new ComponentChangesHash(map, addedRemovedPair.removed());
   }

   public boolean hashEquals(ComponentChanges changes, ComponentHasher hasher) {
      ComponentChanges.AddedRemovedPair addedRemovedPair = changes.toAddedRemovedPair();
      if (!addedRemovedPair.removed().equals(this.removedComponents)) {
         return false;
      } else if (this.addedComponents.size() != addedRemovedPair.added().size()) {
         return false;
      } else {
         Iterator var4 = addedRemovedPair.added().iterator();

         Integer integer;
         Integer integer2;
         do {
            if (!var4.hasNext()) {
               return true;
            }

            Component component = (Component)var4.next();
            integer = (Integer)this.addedComponents.get(component.type());
            if (integer == null) {
               return false;
            }

            integer2 = (Integer)hasher.apply(component);
         } while(integer2.equals(integer));

         return false;
      }
   }

   public Map addedComponents() {
      return this.addedComponents;
   }

   public Set removedComponents() {
      return this.removedComponents;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.map(HashMap::new, PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE), PacketCodecs.INTEGER, 256), ComponentChangesHash::addedComponents, PacketCodecs.collection(HashSet::new, PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE), 256), ComponentChangesHash::removedComponents, ComponentChangesHash::new);
   }

   @FunctionalInterface
   public interface ComponentHasher extends Function {
   }
}
