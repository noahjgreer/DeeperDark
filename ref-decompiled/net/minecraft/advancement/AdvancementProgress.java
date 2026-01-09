package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public class AdvancementProgress implements Comparable {
   private static final DateTimeFormatter TIME_FORMATTER;
   private static final Codec TIME_CODEC;
   private static final Codec MAP_CODEC;
   public static final Codec CODEC;
   private final Map criteriaProgresses;
   private AdvancementRequirements requirements;

   private AdvancementProgress(Map criteriaProgresses) {
      this.requirements = AdvancementRequirements.EMPTY;
      this.criteriaProgresses = criteriaProgresses;
   }

   public AdvancementProgress() {
      this.requirements = AdvancementRequirements.EMPTY;
      this.criteriaProgresses = Maps.newHashMap();
   }

   public void init(AdvancementRequirements requirements) {
      Set set = requirements.getNames();
      this.criteriaProgresses.entrySet().removeIf((progress) -> {
         return !set.contains(progress.getKey());
      });
      Iterator var3 = set.iterator();

      while(var3.hasNext()) {
         String string = (String)var3.next();
         this.criteriaProgresses.putIfAbsent(string, new CriterionProgress());
      }

      this.requirements = requirements;
   }

   public boolean isDone() {
      return this.requirements.matches(this::isCriterionObtained);
   }

   public boolean isAnyObtained() {
      Iterator var1 = this.criteriaProgresses.values().iterator();

      CriterionProgress criterionProgress;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         criterionProgress = (CriterionProgress)var1.next();
      } while(!criterionProgress.isObtained());

      return true;
   }

   public boolean obtain(String name) {
      CriterionProgress criterionProgress = (CriterionProgress)this.criteriaProgresses.get(name);
      if (criterionProgress != null && !criterionProgress.isObtained()) {
         criterionProgress.obtain();
         return true;
      } else {
         return false;
      }
   }

   public boolean reset(String name) {
      CriterionProgress criterionProgress = (CriterionProgress)this.criteriaProgresses.get(name);
      if (criterionProgress != null && criterionProgress.isObtained()) {
         criterionProgress.reset();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.criteriaProgresses);
      return "AdvancementProgress{criteria=" + var10000 + ", requirements=" + String.valueOf(this.requirements) + "}";
   }

   public void toPacket(PacketByteBuf buf) {
      buf.writeMap(this.criteriaProgresses, PacketByteBuf::writeString, (bufx, progresses) -> {
         progresses.toPacket(bufx);
      });
   }

   public static AdvancementProgress fromPacket(PacketByteBuf buf) {
      Map map = buf.readMap(PacketByteBuf::readString, CriterionProgress::fromPacket);
      return new AdvancementProgress(map);
   }

   @Nullable
   public CriterionProgress getCriterionProgress(String name) {
      return (CriterionProgress)this.criteriaProgresses.get(name);
   }

   private boolean isCriterionObtained(String name) {
      CriterionProgress criterionProgress = this.getCriterionProgress(name);
      return criterionProgress != null && criterionProgress.isObtained();
   }

   public float getProgressBarPercentage() {
      if (this.criteriaProgresses.isEmpty()) {
         return 0.0F;
      } else {
         float f = (float)this.requirements.getLength();
         float g = (float)this.countObtainedRequirements();
         return g / f;
      }
   }

   @Nullable
   public Text getProgressBarFraction() {
      if (this.criteriaProgresses.isEmpty()) {
         return null;
      } else {
         int i = this.requirements.getLength();
         if (i <= 1) {
            return null;
         } else {
            int j = this.countObtainedRequirements();
            return Text.translatable("advancements.progress", j, i);
         }
      }
   }

   private int countObtainedRequirements() {
      return this.requirements.countMatches(this::isCriterionObtained);
   }

   public Iterable getUnobtainedCriteria() {
      List list = Lists.newArrayList();
      Iterator var2 = this.criteriaProgresses.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         if (!((CriterionProgress)entry.getValue()).isObtained()) {
            list.add((String)entry.getKey());
         }
      }

      return list;
   }

   public Iterable getObtainedCriteria() {
      List list = Lists.newArrayList();
      Iterator var2 = this.criteriaProgresses.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         if (((CriterionProgress)entry.getValue()).isObtained()) {
            list.add((String)entry.getKey());
         }
      }

      return list;
   }

   @Nullable
   public Instant getEarliestProgressObtainDate() {
      return (Instant)this.criteriaProgresses.values().stream().map(CriterionProgress::getObtainedTime).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse((Object)null);
   }

   public int compareTo(AdvancementProgress advancementProgress) {
      Instant instant = this.getEarliestProgressObtainDate();
      Instant instant2 = advancementProgress.getEarliestProgressObtainDate();
      if (instant == null && instant2 != null) {
         return 1;
      } else if (instant != null && instant2 == null) {
         return -1;
      } else {
         return instant == null && instant2 == null ? 0 : instant.compareTo(instant2);
      }
   }

   // $FF: synthetic method
   public int compareTo(final Object other) {
      return this.compareTo((AdvancementProgress)other);
   }

   static {
      TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
      TIME_CODEC = Codecs.formattedTime(TIME_FORMATTER).xmap(Instant::from, (instant) -> {
         return instant.atZone(ZoneId.systemDefault());
      });
      MAP_CODEC = Codec.unboundedMap(Codec.STRING, TIME_CODEC).xmap((map) -> {
         return Util.transformMapValues(map, CriterionProgress::new);
      }, (map) -> {
         return (Map)map.entrySet().stream().filter((entry) -> {
            return ((CriterionProgress)entry.getValue()).isObtained();
         }).collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {
            return (Instant)Objects.requireNonNull(((CriterionProgress)entry.getValue()).getObtainedTime());
         }));
      });
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(MAP_CODEC.optionalFieldOf("criteria", Map.of()).forGetter((advancementProgress) -> {
            return advancementProgress.criteriaProgresses;
         }), Codec.BOOL.fieldOf("done").orElse(true).forGetter(AdvancementProgress::isDone)).apply(instance, (criteriaProgresses, done) -> {
            return new AdvancementProgress(new HashMap(criteriaProgresses));
         });
      });
   }
}
