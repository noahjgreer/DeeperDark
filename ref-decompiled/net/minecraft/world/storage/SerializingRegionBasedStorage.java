package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SerializingRegionBasedStorage implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String SECTIONS_KEY = "Sections";
   private final ChunkPosKeyedStorage storageAccess;
   private final Long2ObjectMap loadedElements = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet unsavedElements = new LongLinkedOpenHashSet();
   private final Codec codec;
   private final Function serializer;
   private final BiFunction deserializer;
   private final Function factory;
   private final DynamicRegistryManager registryManager;
   private final ChunkErrorHandler errorHandler;
   protected final HeightLimitView world;
   private final LongSet loadedChunks = new LongOpenHashSet();
   private final Long2ObjectMap pendingLoads = new Long2ObjectOpenHashMap();
   private final Object lock = new Object();

   public SerializingRegionBasedStorage(ChunkPosKeyedStorage storageAccess, Codec codec, Function serializer, BiFunction deserializer, Function factory, DynamicRegistryManager registryManager, ChunkErrorHandler errorHandler, HeightLimitView world) {
      this.storageAccess = storageAccess;
      this.codec = codec;
      this.serializer = serializer;
      this.deserializer = deserializer;
      this.factory = factory;
      this.registryManager = registryManager;
      this.errorHandler = errorHandler;
      this.world = world;
   }

   protected void tick(BooleanSupplier shouldKeepTicking) {
      LongIterator longIterator = this.unsavedElements.iterator();

      while(longIterator.hasNext() && shouldKeepTicking.getAsBoolean()) {
         ChunkPos chunkPos = new ChunkPos(longIterator.nextLong());
         longIterator.remove();
         this.save(chunkPos);
      }

      this.tickPendingLoads();
   }

   private void tickPendingLoads() {
      synchronized(this.lock) {
         Iterator iterator = Long2ObjectMaps.fastIterator(this.pendingLoads);

         while(iterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            Optional optional = (Optional)((CompletableFuture)entry.getValue()).getNow((Object)null);
            if (optional != null) {
               long l = entry.getLongKey();
               this.onLoad(new ChunkPos(l), (LoadResult)optional.orElse((Object)null));
               iterator.remove();
               this.loadedChunks.add(l);
            }
         }

      }
   }

   public void save() {
      if (!this.unsavedElements.isEmpty()) {
         this.unsavedElements.forEach((chunkPos) -> {
            this.save(new ChunkPos(chunkPos));
         });
         this.unsavedElements.clear();
      }

   }

   public boolean hasUnsavedElements() {
      return !this.unsavedElements.isEmpty();
   }

   @Nullable
   protected Optional getIfLoaded(long pos) {
      return (Optional)this.loadedElements.get(pos);
   }

   protected Optional get(long pos) {
      if (this.isPosInvalid(pos)) {
         return Optional.empty();
      } else {
         Optional optional = this.getIfLoaded(pos);
         if (optional != null) {
            return optional;
         } else {
            this.loadAndWait(ChunkSectionPos.from(pos).toChunkPos());
            optional = this.getIfLoaded(pos);
            if (optional == null) {
               throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException());
            } else {
               return optional;
            }
         }
      }
   }

   protected boolean isPosInvalid(long pos) {
      int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(pos));
      return this.world.isOutOfHeightLimit(i);
   }

   protected Object getOrCreate(long pos) {
      if (this.isPosInvalid(pos)) {
         throw (IllegalArgumentException)Util.getFatalOrPause(new IllegalArgumentException("sectionPos out of bounds"));
      } else {
         Optional optional = this.get(pos);
         if (optional.isPresent()) {
            return optional.get();
         } else {
            Object object = this.factory.apply(() -> {
               this.onUpdate(pos);
            });
            this.loadedElements.put(pos, Optional.of(object));
            return object;
         }
      }
   }

   public CompletableFuture load(ChunkPos chunkPos) {
      synchronized(this.lock) {
         long l = chunkPos.toLong();
         return this.loadedChunks.contains(l) ? CompletableFuture.completedFuture((Object)null) : (CompletableFuture)this.pendingLoads.computeIfAbsent(l, (pos) -> {
            return this.loadNbt(chunkPos);
         });
      }
   }

   private void loadAndWait(ChunkPos chunkPos) {
      long l = chunkPos.toLong();
      CompletableFuture completableFuture;
      synchronized(this.lock) {
         if (!this.loadedChunks.add(l)) {
            return;
         }

         completableFuture = (CompletableFuture)this.pendingLoads.computeIfAbsent(l, (pos) -> {
            return this.loadNbt(chunkPos);
         });
      }

      this.onLoad(chunkPos, (LoadResult)((Optional)completableFuture.join()).orElse((Object)null));
      synchronized(this.lock) {
         this.pendingLoads.remove(l);
      }
   }

   private CompletableFuture loadNbt(ChunkPos chunkPos) {
      RegistryOps registryOps = this.registryManager.getOps(NbtOps.INSTANCE);
      return this.storageAccess.read(chunkPos).thenApplyAsync((chunkNbt) -> {
         return chunkNbt.map((nbt) -> {
            return SerializingRegionBasedStorage.LoadResult.fromNbt(this.codec, registryOps, nbt, this.storageAccess, this.world);
         });
      }, Util.getMainWorkerExecutor().named("parseSection")).exceptionally((throwable) -> {
         if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
         }

         if (throwable instanceof IOException iOException) {
            LOGGER.error("Error reading chunk {} data from disk", chunkPos, iOException);
            this.errorHandler.onChunkLoadFailure(iOException, this.storageAccess.getStorageKey(), chunkPos);
            return Optional.empty();
         } else {
            throw new CompletionException(throwable);
         }
      });
   }

   private void onLoad(ChunkPos chunkPos, @Nullable LoadResult result) {
      if (result == null) {
         for(int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
            this.loadedElements.put(chunkSectionPosAsLong(chunkPos, i), Optional.empty());
         }
      } else {
         boolean bl = result.versionChanged();

         for(int j = this.world.getBottomSectionCoord(); j <= this.world.getTopSectionCoord(); ++j) {
            long l = chunkSectionPosAsLong(chunkPos, j);
            Optional optional = Optional.ofNullable(result.sectionsByY.get(j)).map((section) -> {
               return this.deserializer.apply(section, () -> {
                  this.onUpdate(l);
               });
            });
            this.loadedElements.put(l, optional);
            optional.ifPresent((object) -> {
               this.onLoad(l);
               if (bl) {
                  this.onUpdate(l);
               }

            });
         }
      }

   }

   private void save(ChunkPos pos) {
      RegistryOps registryOps = this.registryManager.getOps(NbtOps.INSTANCE);
      Dynamic dynamic = this.serialize(pos, registryOps);
      NbtElement nbtElement = (NbtElement)dynamic.getValue();
      if (nbtElement instanceof NbtCompound) {
         this.storageAccess.set(pos, (NbtCompound)nbtElement).exceptionally((throwable) -> {
            this.errorHandler.onChunkSaveFailure(throwable, this.storageAccess.getStorageKey(), pos);
            return null;
         });
      } else {
         LOGGER.error("Expected compound tag, got {}", nbtElement);
      }

   }

   private Dynamic serialize(ChunkPos chunkPos, DynamicOps ops) {
      Map map = Maps.newHashMap();

      for(int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
         long l = chunkSectionPosAsLong(chunkPos, i);
         Optional optional = (Optional)this.loadedElements.get(l);
         if (optional != null && !optional.isEmpty()) {
            DataResult dataResult = this.codec.encodeStart(ops, this.serializer.apply(optional.get()));
            String string = Integer.toString(i);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            dataResult.resultOrPartial(var10001::error).ifPresent((value) -> {
               map.put(ops.createString(string), value);
            });
         }
      }

      return new Dynamic(ops, ops.createMap(ImmutableMap.of(ops.createString("Sections"), ops.createMap(map), ops.createString("DataVersion"), ops.createInt(SharedConstants.getGameVersion().dataVersion().id()))));
   }

   private static long chunkSectionPosAsLong(ChunkPos chunkPos, int y) {
      return ChunkSectionPos.asLong(chunkPos.x, y, chunkPos.z);
   }

   protected void onLoad(long pos) {
   }

   protected void onUpdate(long pos) {
      Optional optional = (Optional)this.loadedElements.get(pos);
      if (optional != null && !optional.isEmpty()) {
         this.unsavedElements.add(ChunkPos.toLong(ChunkSectionPos.unpackX(pos), ChunkSectionPos.unpackZ(pos)));
      } else {
         LOGGER.warn("No data for position: {}", ChunkSectionPos.from(pos));
      }
   }

   static int getDataVersion(Dynamic dynamic) {
      return dynamic.get("DataVersion").asInt(1945);
   }

   public void saveChunk(ChunkPos pos) {
      if (this.unsavedElements.remove(pos.toLong())) {
         this.save(pos);
      }

   }

   public void close() throws IOException {
      this.storageAccess.close();
   }

   private static record LoadResult(Int2ObjectMap sectionsByY, boolean versionChanged) {
      final Int2ObjectMap sectionsByY;

      private LoadResult(Int2ObjectMap int2ObjectMap, boolean bl) {
         this.sectionsByY = int2ObjectMap;
         this.versionChanged = bl;
      }

      public static LoadResult fromNbt(Codec sectionCodec, DynamicOps ops, NbtElement nbt, ChunkPosKeyedStorage storage, HeightLimitView world) {
         Dynamic dynamic = new Dynamic(ops, nbt);
         int i = SerializingRegionBasedStorage.getDataVersion(dynamic);
         int j = SharedConstants.getGameVersion().dataVersion().id();
         boolean bl = i != j;
         Dynamic dynamic2 = storage.update(dynamic, i);
         OptionalDynamic optionalDynamic = dynamic2.get("Sections");
         Int2ObjectMap int2ObjectMap = new Int2ObjectOpenHashMap();

         for(int k = world.getBottomSectionCoord(); k <= world.getTopSectionCoord(); ++k) {
            Optional optional = optionalDynamic.get(Integer.toString(k)).result().flatMap((section) -> {
               DataResult var10000 = sectionCodec.parse(section);
               Logger var10001 = SerializingRegionBasedStorage.LOGGER;
               Objects.requireNonNull(var10001);
               return var10000.resultOrPartial(var10001::error);
            });
            if (optional.isPresent()) {
               int2ObjectMap.put(k, optional.get());
            }
         }

         return new LoadResult(int2ObjectMap, bl);
      }

      public Int2ObjectMap sectionsByY() {
         return this.sectionsByY;
      }

      public boolean versionChanged() {
         return this.versionChanged;
      }
   }
}
