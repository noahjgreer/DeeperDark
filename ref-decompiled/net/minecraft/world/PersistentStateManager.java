package net.minecraft.world;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PersistentStateManager implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PersistentState.Context context;
   private final Map loadedStates = new HashMap();
   private final DataFixer dataFixer;
   private final RegistryWrapper.WrapperLookup registries;
   private final Path directory;
   private CompletableFuture savingFuture = CompletableFuture.completedFuture((Object)null);

   public PersistentStateManager(PersistentState.Context context, Path directory, DataFixer dataFixer, RegistryWrapper.WrapperLookup registries) {
      this.context = context;
      this.dataFixer = dataFixer;
      this.directory = directory;
      this.registries = registries;
   }

   private Path getFile(String id) {
      return this.directory.resolve(id + ".dat");
   }

   public PersistentState getOrCreate(PersistentStateType type) {
      PersistentState persistentState = this.get(type);
      if (persistentState != null) {
         return persistentState;
      } else {
         PersistentState persistentState2 = (PersistentState)type.constructor().apply(this.context);
         this.set(type, persistentState2);
         return persistentState2;
      }
   }

   @Nullable
   public PersistentState get(PersistentStateType type) {
      Optional optional = (Optional)this.loadedStates.get(type);
      if (optional == null) {
         optional = Optional.ofNullable(this.readFromFile(type));
         this.loadedStates.put(type, optional);
      }

      return (PersistentState)optional.orElse((Object)null);
   }

   @Nullable
   private PersistentState readFromFile(PersistentStateType type) {
      try {
         Path path = this.getFile(type.id());
         if (Files.exists(path, new LinkOption[0])) {
            NbtCompound nbtCompound = this.readNbt(type.id(), type.dataFixType(), SharedConstants.getGameVersion().dataVersion().id());
            RegistryOps registryOps = this.registries.getOps(NbtOps.INSTANCE);
            return (PersistentState)((Codec)type.codec().apply(this.context)).parse(registryOps, nbtCompound.get("data")).resultOrPartial((string) -> {
               LOGGER.error("Failed to parse saved data for '{}': {}", type, string);
            }).orElse((Object)null);
         }
      } catch (Exception var5) {
         LOGGER.error("Error loading saved data: {}", type, var5);
      }

      return null;
   }

   public void set(PersistentStateType type, PersistentState state) {
      this.loadedStates.put(type, Optional.of(state));
      state.markDirty();
   }

   public NbtCompound readNbt(String id, DataFixTypes dataFixTypes, int currentSaveVersion) throws IOException {
      InputStream inputStream = Files.newInputStream(this.getFile(id));

      NbtCompound var8;
      try {
         PushbackInputStream pushbackInputStream = new PushbackInputStream(new FixedBufferInputStream(inputStream), 2);

         try {
            NbtCompound nbtCompound;
            if (this.isCompressed(pushbackInputStream)) {
               nbtCompound = NbtIo.readCompressed((InputStream)pushbackInputStream, NbtSizeTracker.ofUnlimitedBytes());
            } else {
               DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);

               try {
                  nbtCompound = NbtIo.readCompound(dataInputStream);
               } catch (Throwable var13) {
                  try {
                     dataInputStream.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }

                  throw var13;
               }

               dataInputStream.close();
            }

            int i = NbtHelper.getDataVersion((NbtCompound)nbtCompound, 1343);
            var8 = dataFixTypes.update(this.dataFixer, nbtCompound, i, currentSaveVersion);
         } catch (Throwable var14) {
            try {
               pushbackInputStream.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }

            throw var14;
         }

         pushbackInputStream.close();
      } catch (Throwable var15) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var10) {
               var15.addSuppressed(var10);
            }
         }

         throw var15;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return var8;
   }

   private boolean isCompressed(PushbackInputStream stream) throws IOException {
      byte[] bs = new byte[2];
      boolean bl = false;
      int i = stream.read(bs, 0, 2);
      if (i == 2) {
         int j = (bs[1] & 255) << 8 | bs[0] & 255;
         if (j == 35615) {
            bl = true;
         }
      }

      if (i != 0) {
         stream.unread(bs, 0, i);
      }

      return bl;
   }

   public CompletableFuture startSaving() {
      Map map = this.collectStatesToSave();
      if (map.isEmpty()) {
         return CompletableFuture.completedFuture((Object)null);
      } else {
         int i = Util.getAvailableBackgroundThreads();
         int j = map.size();
         if (j > i) {
            this.savingFuture = this.savingFuture.thenCompose((object) -> {
               List list = new ArrayList(i);
               int k = MathHelper.ceilDiv(j, i);
               Iterator var7 = Iterables.partition(map.entrySet(), k).iterator();

               while(var7.hasNext()) {
                  List list2 = (List)var7.next();
                  list.add(CompletableFuture.runAsync(() -> {
                     Iterator var2 = list2.iterator();

                     while(var2.hasNext()) {
                        Map.Entry entry = (Map.Entry)var2.next();
                        this.save((PersistentStateType)entry.getKey(), (NbtCompound)entry.getValue());
                     }

                  }, Util.getIoWorkerExecutor()));
               }

               return CompletableFuture.allOf((CompletableFuture[])list.toArray((ix) -> {
                  return new CompletableFuture[ix];
               }));
            });
         } else {
            this.savingFuture = this.savingFuture.thenCompose((object) -> {
               return CompletableFuture.allOf((CompletableFuture[])map.entrySet().stream().map((entry) -> {
                  return CompletableFuture.runAsync(() -> {
                     this.save((PersistentStateType)entry.getKey(), (NbtCompound)entry.getValue());
                  }, Util.getIoWorkerExecutor());
               }).toArray((i) -> {
                  return new CompletableFuture[i];
               }));
            });
         }

         return this.savingFuture;
      }
   }

   private Map collectStatesToSave() {
      Map map = new Object2ObjectArrayMap();
      RegistryOps registryOps = this.registries.getOps(NbtOps.INSTANCE);
      this.loadedStates.forEach((type, optionalState) -> {
         optionalState.filter(PersistentState::isDirty).ifPresent((state) -> {
            map.put(type, this.encode(type, state, registryOps));
            state.setDirty(false);
         });
      });
      return map;
   }

   private NbtCompound encode(PersistentStateType type, PersistentState state, RegistryOps ops) {
      Codec codec = (Codec)type.codec().apply(this.context);
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.put("data", (NbtElement)codec.encodeStart(ops, state).getOrThrow());
      NbtHelper.putDataVersion(nbtCompound);
      return nbtCompound;
   }

   private void save(PersistentStateType type, NbtCompound nbt) {
      Path path = this.getFile(type.id());

      try {
         NbtIo.writeCompressed(nbt, path);
      } catch (IOException var5) {
         LOGGER.error("Could not save data to {}", path.getFileName(), var5);
      }

   }

   public void save() {
      this.startSaving().join();
   }

   public void close() {
      this.save();
   }
}
