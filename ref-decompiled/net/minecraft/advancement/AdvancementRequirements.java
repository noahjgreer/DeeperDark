package net.minecraft.advancement;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;

public record AdvancementRequirements(List requirements) {
   public static final Codec CODEC;
   public static final AdvancementRequirements EMPTY;

   public AdvancementRequirements(PacketByteBuf buf) {
      this(buf.readList((bufx) -> {
         return bufx.readList(PacketByteBuf::readString);
      }));
   }

   public AdvancementRequirements(List list) {
      this.requirements = list;
   }

   public void writeRequirements(PacketByteBuf buf) {
      buf.writeCollection(this.requirements, (bufx, requirements) -> {
         bufx.writeCollection(requirements, PacketByteBuf::writeString);
      });
   }

   public static AdvancementRequirements allOf(Collection requirements) {
      return new AdvancementRequirements(requirements.stream().map(List::of).toList());
   }

   public static AdvancementRequirements anyOf(Collection requirements) {
      return new AdvancementRequirements(List.of(List.copyOf(requirements)));
   }

   public int getLength() {
      return this.requirements.size();
   }

   public boolean matches(Predicate predicate) {
      if (this.requirements.isEmpty()) {
         return false;
      } else {
         Iterator var2 = this.requirements.iterator();

         List list;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            list = (List)var2.next();
         } while(anyMatch(list, predicate));

         return false;
      }
   }

   public int countMatches(Predicate predicate) {
      int i = 0;
      Iterator var3 = this.requirements.iterator();

      while(var3.hasNext()) {
         List list = (List)var3.next();
         if (anyMatch(list, predicate)) {
            ++i;
         }
      }

      return i;
   }

   private static boolean anyMatch(List requirements, Predicate predicate) {
      Iterator var2 = requirements.iterator();

      String string;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         string = (String)var2.next();
      } while(!predicate.test(string));

      return true;
   }

   public DataResult validate(Set requirements) {
      Set set = new ObjectOpenHashSet();
      Iterator var3 = this.requirements.iterator();

      while(var3.hasNext()) {
         List list = (List)var3.next();
         if (list.isEmpty() && requirements.isEmpty()) {
            return DataResult.error(() -> {
               return "Requirement entry cannot be empty";
            });
         }

         set.addAll(list);
      }

      if (!requirements.equals(set)) {
         Set set2 = Sets.difference(requirements, set);
         Set set3 = Sets.difference(set, requirements);
         return DataResult.error(() -> {
            String var10000 = String.valueOf(set2);
            return "Advancement completion requirements did not exactly match specified criteria. Missing: " + var10000 + ". Unknown: " + String.valueOf(set3);
         });
      } else {
         return DataResult.success(this);
      }
   }

   public boolean isEmpty() {
      return this.requirements.isEmpty();
   }

   public String toString() {
      return this.requirements.toString();
   }

   public Set getNames() {
      Set set = new ObjectOpenHashSet();
      Iterator var2 = this.requirements.iterator();

      while(var2.hasNext()) {
         List list = (List)var2.next();
         set.addAll(list);
      }

      return set;
   }

   public List requirements() {
      return this.requirements;
   }

   static {
      CODEC = Codec.STRING.listOf().listOf().xmap(AdvancementRequirements::new, AdvancementRequirements::requirements);
      EMPTY = new AdvancementRequirements(List.of());
   }

   public interface CriterionMerger {
      CriterionMerger AND = AdvancementRequirements::allOf;
      CriterionMerger OR = AdvancementRequirements::anyOf;

      AdvancementRequirements create(Collection requirements);
   }
}
