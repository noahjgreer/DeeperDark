package net.minecraft.world.entity;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public interface EntityLookup {
   @Nullable
   EntityLike get(int id);

   @Nullable
   EntityLike get(UUID uuid);

   Iterable iterate();

   void forEach(TypeFilter filter, LazyIterationConsumer consumer);

   void forEachIntersects(Box box, Consumer action);

   void forEachIntersects(TypeFilter filter, Box box, LazyIterationConsumer consumer);
}
