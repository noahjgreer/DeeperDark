package net.minecraft.world.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class EntityIndex {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Int2ObjectMap idToEntity = new Int2ObjectLinkedOpenHashMap();
   private final Map uuidToEntity = Maps.newHashMap();

   public void forEach(TypeFilter filter, LazyIterationConsumer consumer) {
      ObjectIterator var3 = this.idToEntity.values().iterator();

      EntityLike entityLike2;
      do {
         if (!var3.hasNext()) {
            return;
         }

         EntityLike entityLike = (EntityLike)var3.next();
         entityLike2 = (EntityLike)filter.downcast(entityLike);
      } while(entityLike2 == null || !consumer.accept(entityLike2).shouldAbort());

   }

   public Iterable iterate() {
      return Iterables.unmodifiableIterable(this.idToEntity.values());
   }

   public void add(EntityLike entity) {
      UUID uUID = entity.getUuid();
      if (this.uuidToEntity.containsKey(uUID)) {
         LOGGER.warn("Duplicate entity UUID {}: {}", uUID, entity);
      } else {
         this.uuidToEntity.put(uUID, entity);
         this.idToEntity.put(entity.getId(), entity);
      }
   }

   public void remove(EntityLike entity) {
      this.uuidToEntity.remove(entity.getUuid());
      this.idToEntity.remove(entity.getId());
   }

   @Nullable
   public EntityLike get(int id) {
      return (EntityLike)this.idToEntity.get(id);
   }

   @Nullable
   public EntityLike get(UUID uuid) {
      return (EntityLike)this.uuidToEntity.get(uuid);
   }

   public int size() {
      return this.uuidToEntity.size();
   }
}
