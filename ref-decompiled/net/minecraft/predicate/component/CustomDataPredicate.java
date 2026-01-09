package net.minecraft.predicate.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.predicate.NbtPredicate;

public record CustomDataPredicate(NbtPredicate value) implements ComponentPredicate {
   public static final Codec CODEC;

   public CustomDataPredicate(NbtPredicate nbtPredicate) {
      this.value = nbtPredicate;
   }

   public boolean test(ComponentsAccess components) {
      return this.value.test(components);
   }

   public static CustomDataPredicate customData(NbtPredicate value) {
      return new CustomDataPredicate(value);
   }

   public NbtPredicate value() {
      return this.value;
   }

   static {
      CODEC = NbtPredicate.CODEC.xmap(CustomDataPredicate::new, CustomDataPredicate::value);
   }
}
