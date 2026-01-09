package net.minecraft.command;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.Nullable;

public class DataCommandStorage {
   private static final String COMMAND_STORAGE_PREFIX = "command_storage_";
   private final Map storages = new HashMap();
   private final PersistentStateManager stateManager;

   public DataCommandStorage(PersistentStateManager stateManager) {
      this.stateManager = stateManager;
   }

   public NbtCompound get(Identifier id) {
      PersistentState persistentState = this.getStorage(id.getNamespace());
      return persistentState != null ? persistentState.get(id.getPath()) : new NbtCompound();
   }

   @Nullable
   private PersistentState getStorage(String namespace) {
      PersistentState persistentState = (PersistentState)this.storages.get(namespace);
      if (persistentState != null) {
         return persistentState;
      } else {
         PersistentState persistentState2 = (PersistentState)this.stateManager.get(DataCommandStorage.PersistentState.createStateType(namespace));
         if (persistentState2 != null) {
            this.storages.put(namespace, persistentState2);
         }

         return persistentState2;
      }
   }

   private PersistentState getOrCreateStorage(String namespace) {
      PersistentState persistentState = (PersistentState)this.storages.get(namespace);
      if (persistentState != null) {
         return persistentState;
      } else {
         PersistentState persistentState2 = (PersistentState)this.stateManager.getOrCreate(DataCommandStorage.PersistentState.createStateType(namespace));
         this.storages.put(namespace, persistentState2);
         return persistentState2;
      }
   }

   public void set(Identifier id, NbtCompound nbt) {
      this.getOrCreateStorage(id.getNamespace()).set(id.getPath(), nbt);
   }

   public Stream getIds() {
      return this.storages.entrySet().stream().flatMap((entry) -> {
         return ((PersistentState)entry.getValue()).getIds((String)entry.getKey());
      });
   }

   static String getSaveKey(String namespace) {
      return "command_storage_" + namespace;
   }

   private static class PersistentState extends net.minecraft.world.PersistentState {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.unboundedMap(Codecs.IDENTIFIER_PATH, NbtCompound.CODEC).fieldOf("contents").forGetter((state) -> {
            return state.map;
         })).apply(instance, PersistentState::new);
      });
      private final Map map;

      private PersistentState(Map map) {
         this.map = new HashMap(map);
      }

      private PersistentState() {
         this(new HashMap());
      }

      public static PersistentStateType createStateType(String id) {
         return new PersistentStateType(DataCommandStorage.getSaveKey(id), PersistentState::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
      }

      public NbtCompound get(String name) {
         NbtCompound nbtCompound = (NbtCompound)this.map.get(name);
         return nbtCompound != null ? nbtCompound : new NbtCompound();
      }

      public void set(String name, NbtCompound nbt) {
         if (nbt.isEmpty()) {
            this.map.remove(name);
         } else {
            this.map.put(name, nbt);
         }

         this.markDirty();
      }

      public Stream getIds(String namespace) {
         return this.map.keySet().stream().map((key) -> {
            return Identifier.of(namespace, key);
         });
      }
   }
}
