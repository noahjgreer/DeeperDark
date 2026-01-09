package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record JukeboxPlayablePredicate(Optional song) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.JUKEBOX_SONG).optionalFieldOf("song").forGetter(JukeboxPlayablePredicate::song)).apply(instance, JukeboxPlayablePredicate::new);
   });

   public JukeboxPlayablePredicate(Optional optional) {
      this.song = optional;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.JUKEBOX_PLAYABLE;
   }

   public boolean test(JukeboxPlayableComponent jukeboxPlayableComponent) {
      if (!this.song.isPresent()) {
         return true;
      } else {
         boolean bl = false;
         Iterator var3 = ((RegistryEntryList)this.song.get()).iterator();

         while(var3.hasNext()) {
            RegistryEntry registryEntry = (RegistryEntry)var3.next();
            Optional optional = registryEntry.getKey();
            if (!optional.isEmpty() && optional.equals(jukeboxPlayableComponent.song().getKey())) {
               bl = true;
               break;
            }
         }

         return bl;
      }
   }

   public static JukeboxPlayablePredicate empty() {
      return new JukeboxPlayablePredicate(Optional.empty());
   }

   public Optional song() {
      return this.song;
   }
}
