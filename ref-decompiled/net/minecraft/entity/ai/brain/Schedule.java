package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Schedule {
   public static final int WORK_TIME = 2000;
   public static final int field_30693 = 7000;
   public static final Schedule EMPTY;
   public static final Schedule SIMPLE;
   public static final Schedule VILLAGER_BABY;
   public static final Schedule VILLAGER_DEFAULT;
   private final Map scheduleRules = Maps.newHashMap();

   protected static ScheduleBuilder register(String id) {
      Schedule schedule = (Schedule)Registry.register(Registries.SCHEDULE, (String)id, new Schedule());
      return new ScheduleBuilder(schedule);
   }

   protected void addActivity(Activity activity) {
      if (!this.scheduleRules.containsKey(activity)) {
         this.scheduleRules.put(activity, new ScheduleRule());
      }

   }

   protected ScheduleRule getRule(Activity activity) {
      return (ScheduleRule)this.scheduleRules.get(activity);
   }

   protected List getOtherRules(Activity activity) {
      return (List)this.scheduleRules.entrySet().stream().filter((rule) -> {
         return rule.getKey() != activity;
      }).map(Map.Entry::getValue).collect(Collectors.toList());
   }

   public Activity getActivityForTime(int time) {
      return (Activity)this.scheduleRules.entrySet().stream().max(Comparator.comparingDouble((rule) -> {
         return (double)((ScheduleRule)rule.getValue()).getPriority(time);
      })).map(Map.Entry::getKey).orElse(Activity.IDLE);
   }

   static {
      EMPTY = register("empty").withActivity(0, Activity.IDLE).build();
      SIMPLE = register("simple").withActivity(5000, Activity.WORK).withActivity(11000, Activity.REST).build();
      VILLAGER_BABY = register("villager_baby").withActivity(10, Activity.IDLE).withActivity(3000, Activity.PLAY).withActivity(6000, Activity.IDLE).withActivity(10000, Activity.PLAY).withActivity(12000, Activity.REST).build();
      VILLAGER_DEFAULT = register("villager_default").withActivity(10, Activity.IDLE).withActivity(2000, Activity.WORK).withActivity(9000, Activity.MEET).withActivity(11000, Activity.IDLE).withActivity(12000, Activity.REST).build();
   }
}
