package net.minecraft.world.entity;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class SimpleEntityLookup implements EntityLookup {
   private final EntityIndex index;
   private final SectionedEntityCache cache;

   public SimpleEntityLookup(EntityIndex index, SectionedEntityCache cache) {
      this.index = index;
      this.cache = cache;
   }

   @Nullable
   public EntityLike get(int id) {
      return this.index.get(id);
   }

   @Nullable
   public EntityLike get(UUID uuid) {
      return this.index.get(uuid);
   }

   public Iterable iterate() {
      return this.index.iterate();
   }

   public void forEach(TypeFilter filter, LazyIterationConsumer consumer) {
      this.index.forEach(filter, consumer);
   }

   public void forEachIntersects(Box box, Consumer action) {
      this.cache.forEachIntersects(box, LazyIterationConsumer.forConsumer(action));
   }

   public void forEachIntersects(TypeFilter filter, Box box, LazyIterationConsumer consumer) {
      this.cache.forEachIntersects(filter, box, consumer);
   }
}
