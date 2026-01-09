package net.minecraft.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityQueriable;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jetbrains.annotations.Nullable;

public final class LazyEntityReference {
   private static final Codec CODEC;
   private static final PacketCodec PACKET_CODEC;
   private Either value;

   public static Codec createCodec() {
      return CODEC;
   }

   public static PacketCodec createPacketCodec() {
      return PACKET_CODEC;
   }

   public LazyEntityReference(UniquelyIdentifiable value) {
      this.value = Either.right(value);
   }

   public LazyEntityReference(UUID value) {
      this.value = Either.left(value);
   }

   public UUID getUuid() {
      return (UUID)this.value.map((uuid) -> {
         return uuid;
      }, UniquelyIdentifiable::getUuid);
   }

   @Nullable
   public UniquelyIdentifiable resolve(EntityQueriable world, Class type) {
      Optional optional = this.value.right();
      if (optional.isPresent()) {
         UniquelyIdentifiable uniquelyIdentifiable = (UniquelyIdentifiable)optional.get();
         if (!uniquelyIdentifiable.isRemoved()) {
            return uniquelyIdentifiable;
         }

         this.value = Either.left(uniquelyIdentifiable.getUuid());
      }

      Optional optional2 = this.value.left();
      if (optional2.isPresent()) {
         UniquelyIdentifiable uniquelyIdentifiable2 = this.cast(world.getEntity((UUID)optional2.get()), type);
         if (uniquelyIdentifiable2 != null && !uniquelyIdentifiable2.isRemoved()) {
            this.value = Either.right(uniquelyIdentifiable2);
            return uniquelyIdentifiable2;
         }
      }

      return null;
   }

   @Nullable
   private UniquelyIdentifiable cast(@Nullable UniquelyIdentifiable entity, Class clazz) {
      return entity != null && clazz.isAssignableFrom(entity.getClass()) ? (UniquelyIdentifiable)clazz.cast(entity) : null;
   }

   public boolean uuidEquals(UniquelyIdentifiable o) {
      return this.getUuid().equals(o.getUuid());
   }

   public void writeData(WriteView view, String key) {
      view.put(key, Uuids.INT_STREAM_CODEC, this.getUuid());
   }

   public static void writeData(@Nullable LazyEntityReference entityRef, WriteView view, String key) {
      if (entityRef != null) {
         entityRef.writeData(view, key);
      }

   }

   @Nullable
   public static UniquelyIdentifiable resolve(@Nullable LazyEntityReference entity, EntityQueriable world, Class type) {
      return entity != null ? entity.resolve(world, type) : null;
   }

   @Nullable
   public static LazyEntityReference fromData(ReadView view, String key) {
      return (LazyEntityReference)view.read(key, createCodec()).orElse((Object)null);
   }

   @Nullable
   public static LazyEntityReference fromDataOrPlayerName(ReadView view, String key, World world) {
      Optional optional = view.read(key, Uuids.INT_STREAM_CODEC);
      return optional.isPresent() ? new LazyEntityReference((UUID)optional.get()) : (LazyEntityReference)view.getOptionalString(key).map((name) -> {
         return ServerConfigHandler.getPlayerUuidByName(world.getServer(), name);
      }).map(LazyEntityReference::new).orElse((Object)null);
   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else {
         boolean var10000;
         if (object instanceof LazyEntityReference) {
            LazyEntityReference lazyEntityReference = (LazyEntityReference)object;
            if (this.getUuid().equals(lazyEntityReference.getUuid())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.getUuid().hashCode();
   }

   static {
      CODEC = Uuids.INT_STREAM_CODEC.xmap(LazyEntityReference::new, LazyEntityReference::getUuid);
      PACKET_CODEC = Uuids.PACKET_CODEC.xmap(LazyEntityReference::new, LazyEntityReference::getUuid);
   }
}
