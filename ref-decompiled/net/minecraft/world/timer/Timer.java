package net.minecraft.world.timer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;

public class Timer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String CALLBACK_KEY = "Callback";
   private static final String NAME_KEY = "Name";
   private static final String TRIGGER_TIME_KEY = "TriggerTime";
   private final TimerCallbackSerializer callback;
   private final Queue events;
   private UnsignedLong eventCounter;
   private final Table eventsByName;

   private static Comparator createEventComparator() {
      return Comparator.comparingLong((event) -> {
         return event.triggerTime;
      }).thenComparing((event) -> {
         return event.id;
      });
   }

   public Timer(TimerCallbackSerializer timerCallbackSerializer, Stream nbts) {
      this(timerCallbackSerializer);
      this.events.clear();
      this.eventsByName.clear();
      this.eventCounter = UnsignedLong.ZERO;
      nbts.forEach((nbt) -> {
         NbtElement nbtElement = (NbtElement)nbt.convert(NbtOps.INSTANCE).getValue();
         if (nbtElement instanceof NbtCompound nbtCompound) {
            this.addEvent(nbtCompound);
         } else {
            LOGGER.warn("Invalid format of events: {}", nbtElement);
         }

      });
   }

   public Timer(TimerCallbackSerializer timerCallbackSerializer) {
      this.events = new PriorityQueue(createEventComparator());
      this.eventCounter = UnsignedLong.ZERO;
      this.eventsByName = HashBasedTable.create();
      this.callback = timerCallbackSerializer;
   }

   public void processEvents(Object server, long time) {
      while(true) {
         Event event = (Event)this.events.peek();
         if (event == null || event.triggerTime > time) {
            return;
         }

         this.events.remove();
         this.eventsByName.remove(event.name, time);
         event.callback.call(server, this, time);
      }
   }

   public void setEvent(String name, long triggerTime, TimerCallback callback) {
      if (!this.eventsByName.contains(name, triggerTime)) {
         this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
         Event event = new Event(triggerTime, this.eventCounter, name, callback);
         this.eventsByName.put(name, triggerTime, event);
         this.events.add(event);
      }
   }

   public int remove(String name) {
      Collection collection = this.eventsByName.row(name).values();
      Queue var10001 = this.events;
      Objects.requireNonNull(var10001);
      collection.forEach(var10001::remove);
      int i = collection.size();
      collection.clear();
      return i;
   }

   public Set getEventNames() {
      return Collections.unmodifiableSet(this.eventsByName.rowKeySet());
   }

   private void addEvent(NbtCompound nbt) {
      TimerCallback timerCallback = (TimerCallback)nbt.get("Callback", this.callback.getCodec()).orElse((Object)null);
      if (timerCallback != null) {
         String string = nbt.getString("Name", "");
         long l = nbt.getLong("TriggerTime", 0L);
         this.setEvent(string, l, timerCallback);
      }

   }

   private NbtCompound serialize(Event event) {
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.putString("Name", event.name);
      nbtCompound.putLong("TriggerTime", event.triggerTime);
      nbtCompound.put("Callback", this.callback.getCodec(), event.callback);
      return nbtCompound;
   }

   public NbtList toNbt() {
      NbtList nbtList = new NbtList();
      Stream var10000 = this.events.stream().sorted(createEventComparator()).map(this::serialize);
      Objects.requireNonNull(nbtList);
      var10000.forEach(nbtList::add);
      return nbtList;
   }

   public static class Event {
      public final long triggerTime;
      public final UnsignedLong id;
      public final String name;
      public final TimerCallback callback;

      Event(long triggerTime, UnsignedLong id, String name, TimerCallback callback) {
         this.triggerTime = triggerTime;
         this.id = id;
         this.name = name;
         this.callback = callback;
      }
   }
}
