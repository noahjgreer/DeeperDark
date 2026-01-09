package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleDefaultedRegistry extends SimpleRegistry implements DefaultedRegistry {
   private final Identifier defaultId;
   private RegistryEntry.Reference defaultEntry;

   public SimpleDefaultedRegistry(String defaultId, RegistryKey key, Lifecycle lifecycle, boolean intrusive) {
      super(key, lifecycle, intrusive);
      this.defaultId = Identifier.of(defaultId);
   }

   public RegistryEntry.Reference add(RegistryKey key, Object value, RegistryEntryInfo info) {
      RegistryEntry.Reference reference = super.add(key, value, info);
      if (this.defaultId.equals(key.getValue())) {
         this.defaultEntry = reference;
      }

      return reference;
   }

   public int getRawId(@Nullable Object value) {
      int i = super.getRawId(value);
      return i == -1 ? super.getRawId(this.defaultEntry.value()) : i;
   }

   @NotNull
   public Identifier getId(Object value) {
      Identifier identifier = super.getId(value);
      return identifier == null ? this.defaultId : identifier;
   }

   @NotNull
   public Object get(@Nullable Identifier id) {
      Object object = super.get(id);
      return object == null ? this.defaultEntry.value() : object;
   }

   public Optional getOptionalValue(@Nullable Identifier id) {
      return Optional.ofNullable(super.get(id));
   }

   public Optional getDefaultEntry() {
      return Optional.ofNullable(this.defaultEntry);
   }

   @NotNull
   public Object get(int index) {
      Object object = super.get(index);
      return object == null ? this.defaultEntry.value() : object;
   }

   public Optional getRandom(Random random) {
      return super.getRandom(random).or(() -> {
         return Optional.of(this.defaultEntry);
      });
   }

   public Identifier getDefaultId() {
      return this.defaultId;
   }
}
