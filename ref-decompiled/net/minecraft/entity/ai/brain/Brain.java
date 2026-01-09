package net.minecraft.entity.ai.brain;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.annotation.Debug;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Brain {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Supplier codecSupplier;
   private static final int ACTIVITY_REFRESH_COOLDOWN = 20;
   private final Map memories = Maps.newHashMap();
   private final Map sensors = Maps.newLinkedHashMap();
   private final Map tasks = Maps.newTreeMap();
   private Schedule schedule;
   private final Map requiredActivityMemories;
   private final Map forgettingActivityMemories;
   private Set coreActivities;
   private final Set possibleActivities;
   private Activity defaultActivity;
   private long activityStartTime;

   public static Profile createProfile(Collection memoryModules, Collection sensors) {
      return new Profile(memoryModules, sensors);
   }

   public static Codec createBrainCodec(final Collection memoryModules, final Collection sensors) {
      final MutableObject mutableObject = new MutableObject();
      mutableObject.setValue((new MapCodec() {
         public Stream keys(DynamicOps ops) {
            return memoryModules.stream().flatMap((memoryType) -> {
               return memoryType.getCodec().map((codec) -> {
                  return Registries.MEMORY_MODULE_TYPE.getId(memoryType);
               }).stream();
            }).map((id) -> {
               return ops.createString(id.toString());
            });
         }

         public DataResult decode(DynamicOps ops, MapLike map) {
            MutableObject mutableObjectx = new MutableObject(DataResult.success(ImmutableList.builder()));
            map.entries().forEach((pair) -> {
               DataResult dataResult = Registries.MEMORY_MODULE_TYPE.getCodec().parse(ops, pair.getFirst());
               DataResult dataResult2 = dataResult.flatMap((memoryType) -> {
                  return this.parse(memoryType, ops, pair.getSecond());
               });
               mutableObjectx.setValue(((DataResult)mutableObjectx.getValue()).apply2(ImmutableList.Builder::add, dataResult2));
            });
            DataResult var10000 = (DataResult)mutableObjectx.getValue();
            Logger var10001 = Brain.LOGGER;
            Objects.requireNonNull(var10001);
            ImmutableList immutableList = (ImmutableList)var10000.resultOrPartial(var10001::error).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
            Collection var10002 = memoryModules;
            Collection var10003 = sensors;
            MutableObject var10005 = mutableObject;
            Objects.requireNonNull(var10005);
            return DataResult.success(new Brain(var10002, var10003, immutableList, var10005::getValue));
         }

         private DataResult parse(MemoryModuleType memoryType, DynamicOps ops, Object value) {
            return ((DataResult)memoryType.getCodec().map(DataResult::success).orElseGet(() -> {
               return DataResult.error(() -> {
                  return "No codec for memory: " + String.valueOf(memoryType);
               });
            })).flatMap((codec) -> {
               return codec.parse(ops, value);
            }).map((data) -> {
               return new MemoryEntry(memoryType, Optional.of(data));
            });
         }

         public RecordBuilder encode(Brain brain, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
            brain.streamMemories().forEach((entry) -> {
               entry.serialize(dynamicOps, recordBuilder);
            });
            return recordBuilder;
         }

         // $FF: synthetic method
         public RecordBuilder encode(final Object brain, final DynamicOps ops, final RecordBuilder recordBuilder) {
            return this.encode((Brain)brain, ops, recordBuilder);
         }
      }).fieldOf("memories").codec());
      return (Codec)mutableObject.getValue();
   }

   public Brain(Collection memories, Collection sensors, ImmutableList memoryEntries, Supplier codecSupplier) {
      this.schedule = Schedule.EMPTY;
      this.requiredActivityMemories = Maps.newHashMap();
      this.forgettingActivityMemories = Maps.newHashMap();
      this.coreActivities = Sets.newHashSet();
      this.possibleActivities = Sets.newHashSet();
      this.defaultActivity = Activity.IDLE;
      this.activityStartTime = -9999L;
      this.codecSupplier = codecSupplier;
      Iterator var5 = memories.iterator();

      while(var5.hasNext()) {
         MemoryModuleType memoryModuleType = (MemoryModuleType)var5.next();
         this.memories.put(memoryModuleType, Optional.empty());
      }

      var5 = sensors.iterator();

      while(var5.hasNext()) {
         SensorType sensorType = (SensorType)var5.next();
         this.sensors.put(sensorType, sensorType.create());
      }

      var5 = this.sensors.values().iterator();

      while(var5.hasNext()) {
         Sensor sensor = (Sensor)var5.next();
         Iterator var7 = sensor.getOutputMemoryModules().iterator();

         while(var7.hasNext()) {
            MemoryModuleType memoryModuleType2 = (MemoryModuleType)var7.next();
            this.memories.put(memoryModuleType2, Optional.empty());
         }
      }

      UnmodifiableIterator var9 = memoryEntries.iterator();

      while(var9.hasNext()) {
         MemoryEntry memoryEntry = (MemoryEntry)var9.next();
         memoryEntry.apply(this);
      }

   }

   public DataResult encode(DynamicOps ops) {
      return ((Codec)this.codecSupplier.get()).encodeStart(ops, this);
   }

   Stream streamMemories() {
      return this.memories.entrySet().stream().map((entry) -> {
         return Brain.MemoryEntry.of((MemoryModuleType)entry.getKey(), (Optional)entry.getValue());
      });
   }

   public boolean hasMemoryModule(MemoryModuleType type) {
      return this.isMemoryInState(type, MemoryModuleState.VALUE_PRESENT);
   }

   public void forgetAll() {
      this.memories.keySet().forEach((type) -> {
         this.memories.put(type, Optional.empty());
      });
   }

   public void forget(MemoryModuleType type) {
      this.remember(type, Optional.empty());
   }

   public void remember(MemoryModuleType type, @Nullable Object value) {
      this.remember(type, Optional.ofNullable(value));
   }

   public void remember(MemoryModuleType type, Object value, long expiry) {
      this.setMemory(type, Optional.of(Memory.timed(value, expiry)));
   }

   public void remember(MemoryModuleType type, Optional value) {
      this.setMemory(type, value.map(Memory::permanent));
   }

   void setMemory(MemoryModuleType type, Optional memory) {
      if (this.memories.containsKey(type)) {
         if (memory.isPresent() && this.isEmptyCollection(((Memory)memory.get()).getValue())) {
            this.forget(type);
         } else {
            this.memories.put(type, memory);
         }
      }

   }

   public Optional getOptionalRegisteredMemory(MemoryModuleType type) {
      Optional optional = (Optional)this.memories.get(type);
      if (optional == null) {
         throw new IllegalStateException("Unregistered memory fetched: " + String.valueOf(type));
      } else {
         return optional.map(Memory::getValue);
      }
   }

   @Nullable
   public Optional getOptionalMemory(MemoryModuleType type) {
      Optional optional = (Optional)this.memories.get(type);
      return optional == null ? null : optional.map(Memory::getValue);
   }

   public long getMemoryExpiry(MemoryModuleType type) {
      Optional optional = (Optional)this.memories.get(type);
      return (Long)optional.map(Memory::getExpiry).orElse(0L);
   }

   /** @deprecated */
   @Deprecated
   @Debug
   public Map getMemories() {
      return this.memories;
   }

   public boolean hasMemoryModuleWithValue(MemoryModuleType type, Object value) {
      return !this.hasMemoryModule(type) ? false : this.getOptionalRegisteredMemory(type).filter((memoryValue) -> {
         return memoryValue.equals(value);
      }).isPresent();
   }

   public boolean isMemoryInState(MemoryModuleType type, MemoryModuleState state) {
      Optional optional = (Optional)this.memories.get(type);
      if (optional == null) {
         return false;
      } else {
         return state == MemoryModuleState.REGISTERED || state == MemoryModuleState.VALUE_PRESENT && optional.isPresent() || state == MemoryModuleState.VALUE_ABSENT && optional.isEmpty();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule schedule) {
      this.schedule = schedule;
   }

   public void setCoreActivities(Set coreActivities) {
      this.coreActivities = coreActivities;
   }

   /** @deprecated */
   @Deprecated
   @Debug
   public Set getPossibleActivities() {
      return this.possibleActivities;
   }

   /** @deprecated */
   @Deprecated
   @Debug
   public List getRunningTasks() {
      List list = new ObjectArrayList();
      Iterator var2 = this.tasks.values().iterator();

      while(var2.hasNext()) {
         Map map = (Map)var2.next();
         Iterator var4 = map.values().iterator();

         while(var4.hasNext()) {
            Set set = (Set)var4.next();
            Iterator var6 = set.iterator();

            while(var6.hasNext()) {
               Task task = (Task)var6.next();
               if (task.getStatus() == MultiTickTask.Status.RUNNING) {
                  list.add(task);
               }
            }
         }
      }

      return list;
   }

   public void resetPossibleActivities() {
      this.resetPossibleActivities(this.defaultActivity);
   }

   public Optional getFirstPossibleNonCoreActivity() {
      Iterator var1 = this.possibleActivities.iterator();

      Activity activity;
      do {
         if (!var1.hasNext()) {
            return Optional.empty();
         }

         activity = (Activity)var1.next();
      } while(this.coreActivities.contains(activity));

      return Optional.of(activity);
   }

   public void doExclusively(Activity activity) {
      if (this.canDoActivity(activity)) {
         this.resetPossibleActivities(activity);
      } else {
         this.resetPossibleActivities();
      }

   }

   private void resetPossibleActivities(Activity except) {
      if (!this.hasActivity(except)) {
         this.forgetIrrelevantMemories(except);
         this.possibleActivities.clear();
         this.possibleActivities.addAll(this.coreActivities);
         this.possibleActivities.add(except);
      }
   }

   private void forgetIrrelevantMemories(Activity except) {
      Iterator var2 = this.possibleActivities.iterator();

      while(true) {
         Set set;
         do {
            Activity activity;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               activity = (Activity)var2.next();
            } while(activity == except);

            set = (Set)this.forgettingActivityMemories.get(activity);
         } while(set == null);

         Iterator var5 = set.iterator();

         while(var5.hasNext()) {
            MemoryModuleType memoryModuleType = (MemoryModuleType)var5.next();
            this.forget(memoryModuleType);
         }
      }
   }

   public void refreshActivities(long timeOfDay, long time) {
      if (time - this.activityStartTime > 20L) {
         this.activityStartTime = time;
         Activity activity = this.getSchedule().getActivityForTime((int)(timeOfDay % 24000L));
         if (!this.possibleActivities.contains(activity)) {
            this.doExclusively(activity);
         }
      }

   }

   public void resetPossibleActivities(List activities) {
      Iterator var2 = activities.iterator();

      while(var2.hasNext()) {
         Activity activity = (Activity)var2.next();
         if (this.canDoActivity(activity)) {
            this.resetPossibleActivities(activity);
            break;
         }
      }

   }

   public void setDefaultActivity(Activity activity) {
      this.defaultActivity = activity;
   }

   public void setTaskList(Activity activity, int begin, ImmutableList list) {
      this.setTaskList(activity, this.indexTaskList(begin, list));
   }

   public void setTaskList(Activity activity, int begin, ImmutableList tasks, MemoryModuleType memoryType) {
      Set set = ImmutableSet.of(Pair.of(memoryType, MemoryModuleState.VALUE_PRESENT));
      Set set2 = ImmutableSet.of(memoryType);
      this.setTaskList(activity, this.indexTaskList(begin, tasks), set, set2);
   }

   public void setTaskList(Activity activity, ImmutableList indexedTasks) {
      this.setTaskList(activity, indexedTasks, ImmutableSet.of(), Sets.newHashSet());
   }

   public void setTaskList(Activity activity, int begin, ImmutableList tasks, Set requiredMemories) {
      this.setTaskList(activity, this.indexTaskList(begin, tasks), requiredMemories);
   }

   public void setTaskList(Activity activity, ImmutableList indexedTasks, Set requiredMemories) {
      this.setTaskList(activity, indexedTasks, requiredMemories, Sets.newHashSet());
   }

   public void setTaskList(Activity activity, ImmutableList indexedTasks, Set requiredMemories, Set forgettingMemories) {
      this.requiredActivityMemories.put(activity, requiredMemories);
      if (!forgettingMemories.isEmpty()) {
         this.forgettingActivityMemories.put(activity, forgettingMemories);
      }

      UnmodifiableIterator var5 = indexedTasks.iterator();

      while(var5.hasNext()) {
         Pair pair = (Pair)var5.next();
         ((Set)((Map)this.tasks.computeIfAbsent((Integer)pair.getFirst(), (index) -> {
            return Maps.newHashMap();
         })).computeIfAbsent(activity, (activity2) -> {
            return Sets.newLinkedHashSet();
         })).add((Task)pair.getSecond());
      }

   }

   @VisibleForTesting
   public void clear() {
      this.tasks.clear();
   }

   public boolean hasActivity(Activity activity) {
      return this.possibleActivities.contains(activity);
   }

   public Brain copy() {
      Brain brain = new Brain(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codecSupplier);
      Iterator var2 = this.memories.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         MemoryModuleType memoryModuleType = (MemoryModuleType)entry.getKey();
         if (((Optional)entry.getValue()).isPresent()) {
            brain.memories.put(memoryModuleType, (Optional)entry.getValue());
         }
      }

      return brain;
   }

   public void tick(ServerWorld world, LivingEntity entity) {
      this.tickMemories();
      this.tickSensors(world, entity);
      this.startTasks(world, entity);
      this.updateTasks(world, entity);
   }

   private void tickSensors(ServerWorld world, LivingEntity entity) {
      Iterator var3 = this.sensors.values().iterator();

      while(var3.hasNext()) {
         Sensor sensor = (Sensor)var3.next();
         sensor.tick(world, entity);
      }

   }

   private void tickMemories() {
      Iterator var1 = this.memories.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry entry = (Map.Entry)var1.next();
         if (((Optional)entry.getValue()).isPresent()) {
            Memory memory = (Memory)((Optional)entry.getValue()).get();
            if (memory.isExpired()) {
               this.forget((MemoryModuleType)entry.getKey());
            }

            memory.tick();
         }
      }

   }

   public void stopAllTasks(ServerWorld world, LivingEntity entity) {
      long l = entity.getWorld().getTime();
      Iterator var5 = this.getRunningTasks().iterator();

      while(var5.hasNext()) {
         Task task = (Task)var5.next();
         task.stop(world, entity, l);
      }

   }

   private void startTasks(ServerWorld world, LivingEntity entity) {
      long l = world.getTime();
      Iterator var5 = this.tasks.values().iterator();

      label34:
      while(var5.hasNext()) {
         Map map = (Map)var5.next();
         Iterator var7 = map.entrySet().iterator();

         while(true) {
            Map.Entry entry;
            Activity activity;
            do {
               if (!var7.hasNext()) {
                  continue label34;
               }

               entry = (Map.Entry)var7.next();
               activity = (Activity)entry.getKey();
            } while(!this.possibleActivities.contains(activity));

            Set set = (Set)entry.getValue();
            Iterator var11 = set.iterator();

            while(var11.hasNext()) {
               Task task = (Task)var11.next();
               if (task.getStatus() == MultiTickTask.Status.STOPPED) {
                  task.tryStarting(world, entity, l);
               }
            }
         }
      }

   }

   private void updateTasks(ServerWorld world, LivingEntity entity) {
      long l = world.getTime();
      Iterator var5 = this.getRunningTasks().iterator();

      while(var5.hasNext()) {
         Task task = (Task)var5.next();
         task.tick(world, entity, l);
      }

   }

   private boolean canDoActivity(Activity activity) {
      if (!this.requiredActivityMemories.containsKey(activity)) {
         return false;
      } else {
         Iterator var2 = ((Set)this.requiredActivityMemories.get(activity)).iterator();

         MemoryModuleType memoryModuleType;
         MemoryModuleState memoryModuleState;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            Pair pair = (Pair)var2.next();
            memoryModuleType = (MemoryModuleType)pair.getFirst();
            memoryModuleState = (MemoryModuleState)pair.getSecond();
         } while(this.isMemoryInState(memoryModuleType, memoryModuleState));

         return false;
      }
   }

   private boolean isEmptyCollection(Object value) {
      return value instanceof Collection && ((Collection)value).isEmpty();
   }

   ImmutableList indexTaskList(int begin, ImmutableList tasks) {
      int i = begin;
      ImmutableList.Builder builder = ImmutableList.builder();
      UnmodifiableIterator var5 = tasks.iterator();

      while(var5.hasNext()) {
         Task task = (Task)var5.next();
         builder.add(Pair.of(i++, task));
      }

      return builder.build();
   }

   public static final class Profile {
      private final Collection memoryModules;
      private final Collection sensors;
      private final Codec codec;

      Profile(Collection memoryModules, Collection sensors) {
         this.memoryModules = memoryModules;
         this.sensors = sensors;
         this.codec = Brain.createBrainCodec(memoryModules, sensors);
      }

      public Brain deserialize(Dynamic data) {
         DataResult var10000 = this.codec.parse(data);
         Logger var10001 = Brain.LOGGER;
         Objects.requireNonNull(var10001);
         return (Brain)var10000.resultOrPartial(var10001::error).orElseGet(() -> {
            return new Brain(this.memoryModules, this.sensors, ImmutableList.of(), () -> {
               return this.codec;
            });
         });
      }
   }

   static final class MemoryEntry {
      private final MemoryModuleType type;
      private final Optional data;

      static MemoryEntry of(MemoryModuleType type, Optional data) {
         return new MemoryEntry(type, data);
      }

      MemoryEntry(MemoryModuleType type, Optional data) {
         this.type = type;
         this.data = data;
      }

      void apply(Brain brain) {
         brain.setMemory(this.type, this.data);
      }

      public void serialize(DynamicOps ops, RecordBuilder builder) {
         this.type.getCodec().ifPresent((codec) -> {
            this.data.ifPresent((data) -> {
               builder.add(Registries.MEMORY_MODULE_TYPE.getCodec().encodeStart(ops, this.type), codec.encodeStart(ops, data));
            });
         });
      }
   }
}
