package net.minecraft.entity.data;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.collection.Class2IntMap;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DataTracker {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_DATA_VALUE_ID = 254;
   static final Class2IntMap CLASS_TO_LAST_ID = new Class2IntMap();
   private final DataTracked trackedEntity;
   private final Entry[] entries;
   private boolean dirty;

   DataTracker(DataTracked trackedEntity, Entry[] entries) {
      this.trackedEntity = trackedEntity;
      this.entries = entries;
   }

   public static TrackedData registerData(Class entityClass, TrackedDataHandler dataHandler) {
      if (LOGGER.isDebugEnabled()) {
         try {
            Class class_ = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!class_.equals(entityClass)) {
               LOGGER.debug("defineId called for: {} from {}", new Object[]{entityClass, class_, new RuntimeException()});
            }
         } catch (ClassNotFoundException var3) {
         }
      }

      int i = CLASS_TO_LAST_ID.put(entityClass);
      if (i > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
      } else {
         return dataHandler.create(i);
      }
   }

   private Entry getEntry(TrackedData key) {
      return this.entries[key.id()];
   }

   public Object get(TrackedData data) {
      return this.getEntry(data).get();
   }

   public void set(TrackedData key, Object value) {
      this.set(key, value, false);
   }

   public void set(TrackedData key, Object value, boolean force) {
      Entry entry = this.getEntry(key);
      if (force || ObjectUtils.notEqual(value, entry.get())) {
         entry.set(value);
         this.trackedEntity.onTrackedDataSet(key);
         entry.setDirty(true);
         this.dirty = true;
      }

   }

   public boolean isDirty() {
      return this.dirty;
   }

   @Nullable
   public List getDirtyEntries() {
      if (!this.dirty) {
         return null;
      } else {
         this.dirty = false;
         List list = new ArrayList();
         Entry[] var2 = this.entries;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Entry entry = var2[var4];
            if (entry.isDirty()) {
               entry.setDirty(false);
               list.add(entry.toSerialized());
            }
         }

         return list;
      }
   }

   @Nullable
   public List getChangedEntries() {
      List list = null;
      Entry[] var2 = this.entries;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Entry entry = var2[var4];
         if (!entry.isUnchanged()) {
            if (list == null) {
               list = new ArrayList();
            }

            list.add(entry.toSerialized());
         }
      }

      return list;
   }

   public void writeUpdatedEntries(List entries) {
      Iterator var2 = entries.iterator();

      while(var2.hasNext()) {
         SerializedEntry serializedEntry = (SerializedEntry)var2.next();
         Entry entry = this.entries[serializedEntry.id];
         this.copyToFrom(entry, serializedEntry);
         this.trackedEntity.onTrackedDataSet(entry.getData());
      }

      this.trackedEntity.onDataTrackerUpdate(entries);
   }

   private void copyToFrom(Entry to, SerializedEntry from) {
      if (!Objects.equals(from.handler(), to.data.dataType())) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", to.data.id(), this.trackedEntity, to.value, to.value.getClass(), from.value, from.value.getClass()));
      } else {
         to.set(from.value);
      }
   }

   public static class Entry {
      final TrackedData data;
      Object value;
      private final Object initialValue;
      private boolean dirty;

      public Entry(TrackedData data, Object value) {
         this.data = data;
         this.initialValue = value;
         this.value = value;
      }

      public TrackedData getData() {
         return this.data;
      }

      public void set(Object value) {
         this.value = value;
      }

      public Object get() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean dirty) {
         this.dirty = dirty;
      }

      public boolean isUnchanged() {
         return this.initialValue.equals(this.value);
      }

      public SerializedEntry toSerialized() {
         return DataTracker.SerializedEntry.of(this.data, this.value);
      }
   }

   public static record SerializedEntry(int id, TrackedDataHandler handler, Object value) {
      final int id;
      final Object value;

      public SerializedEntry(int i, TrackedDataHandler trackedDataHandler, Object object) {
         this.id = i;
         this.handler = trackedDataHandler;
         this.value = object;
      }

      public static SerializedEntry of(TrackedData data, Object value) {
         TrackedDataHandler trackedDataHandler = data.dataType();
         return new SerializedEntry(data.id(), trackedDataHandler, trackedDataHandler.copy(value));
      }

      public void write(RegistryByteBuf buf) {
         int i = TrackedDataHandlerRegistry.getId(this.handler);
         if (i < 0) {
            throw new EncoderException("Unknown serializer type " + String.valueOf(this.handler));
         } else {
            buf.writeByte(this.id);
            buf.writeVarInt(i);
            this.handler.codec().encode(buf, this.value);
         }
      }

      public static SerializedEntry fromBuf(RegistryByteBuf buf, int id) {
         int i = buf.readVarInt();
         TrackedDataHandler trackedDataHandler = TrackedDataHandlerRegistry.get(i);
         if (trackedDataHandler == null) {
            throw new DecoderException("Unknown serializer type " + i);
         } else {
            return fromBuf(buf, id, trackedDataHandler);
         }
      }

      private static SerializedEntry fromBuf(RegistryByteBuf buf, int id, TrackedDataHandler handler) {
         return new SerializedEntry(id, handler, handler.codec().decode(buf));
      }

      public int id() {
         return this.id;
      }

      public TrackedDataHandler handler() {
         return this.handler;
      }

      public Object value() {
         return this.value;
      }
   }

   public static class Builder {
      private final DataTracked entity;
      private final Entry[] entries;

      public Builder(DataTracked entity) {
         this.entity = entity;
         this.entries = new Entry[DataTracker.CLASS_TO_LAST_ID.getNext(entity.getClass())];
      }

      public Builder add(TrackedData data, Object value) {
         int i = data.id();
         if (i > this.entries.length) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + this.entries.length + ")");
         } else if (this.entries[i] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
         } else if (TrackedDataHandlerRegistry.getId(data.dataType()) < 0) {
            String var10002 = String.valueOf(data.dataType());
            throw new IllegalArgumentException("Unregistered serializer " + var10002 + " for " + i + "!");
         } else {
            this.entries[data.id()] = new Entry(data, value);
            return this;
         }
      }

      public DataTracker build() {
         for(int i = 0; i < this.entries.length; ++i) {
            if (this.entries[i] == null) {
               String var10002 = String.valueOf(this.entity.getClass());
               throw new IllegalStateException("Entity " + var10002 + " has not defined synched data value " + i);
            }
         }

         return new DataTracker(this.entity, this.entries);
      }
   }
}
