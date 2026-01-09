package net.minecraft.client.render.item.property.select;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class LocalTimeProperty implements SelectProperty {
   public static final String DEFAULT_FORMATTED_TIME = "";
   private static final long MILLIS_PER_SECOND;
   public static final Codec VALUE_CODEC;
   private static final Codec TIME_ZONE_CODEC;
   private static final MapCodec DATA_CODEC;
   public static final SelectProperty.Type TYPE;
   private final Data data;
   private final DateFormat dateFormat;
   private long nextUpdateTime;
   private String currentTimeFormatted = "";

   private LocalTimeProperty(Data data, DateFormat dateFormat) {
      this.data = data;
      this.dateFormat = dateFormat;
   }

   public static LocalTimeProperty create(String pattern, String locale, Optional timeZone) {
      return (LocalTimeProperty)validate(new Data(pattern, locale, timeZone)).getOrThrow((format) -> {
         return new IllegalStateException("Failed to validate format: " + format);
      });
   }

   private static DataResult validate(Data data) {
      ULocale uLocale = new ULocale(data.localeId);
      Calendar calendar = (Calendar)data.timeZone.map((timeZone) -> {
         return Calendar.getInstance(timeZone, uLocale);
      }).orElseGet(() -> {
         return Calendar.getInstance(uLocale);
      });
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(data.format, uLocale);
      simpleDateFormat.setCalendar(calendar);

      try {
         simpleDateFormat.format(new Date());
      } catch (Exception var5) {
         return DataResult.error(() -> {
            String var10000 = String.valueOf(simpleDateFormat);
            return "Invalid time format '" + var10000 + "': " + var5.getMessage();
         });
      }

      return DataResult.success(new LocalTimeProperty(data, simpleDateFormat));
   }

   @Nullable
   public String getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
      long l = Util.getMeasuringTimeMs();
      if (l > this.nextUpdateTime) {
         this.currentTimeFormatted = this.formatCurrentTime();
         this.nextUpdateTime = l + MILLIS_PER_SECOND;
      }

      return this.currentTimeFormatted;
   }

   private String formatCurrentTime() {
      return this.dateFormat.format(new Date());
   }

   public SelectProperty.Type getType() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }

   // $FF: synthetic method
   @Nullable
   public Object getValue(final ItemStack stack, @Nullable final ClientWorld world, @Nullable final LivingEntity user, final int seed, final ItemDisplayContext displayContext) {
      return this.getValue(stack, world, user, seed, displayContext);
   }

   static {
      MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1L);
      VALUE_CODEC = Codec.STRING;
      TIME_ZONE_CODEC = VALUE_CODEC.comapFlatMap((timeZone) -> {
         TimeZone timeZone2 = TimeZone.getTimeZone(timeZone);
         return timeZone2.equals(TimeZone.UNKNOWN_ZONE) ? DataResult.error(() -> {
            return "Unknown timezone: " + timeZone;
         }) : DataResult.success(timeZone2);
      }, TimeZone::getID);
      DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.fieldOf("pattern").forGetter((data) -> {
            return data.format;
         }), Codec.STRING.optionalFieldOf("locale", "").forGetter((data) -> {
            return data.localeId;
         }), TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter((data) -> {
            return data.timeZone;
         })).apply(instance, Data::new);
      });
      TYPE = SelectProperty.Type.create(DATA_CODEC.flatXmap(LocalTimeProperty::validate, (property) -> {
         return DataResult.success(property.data);
      }), VALUE_CODEC);
   }

   @Environment(EnvType.CLIENT)
   private static record Data(String format, String localeId, Optional timeZone) {
      final String format;
      final String localeId;
      final Optional timeZone;

      Data(String string, String string2, Optional optional) {
         this.format = string;
         this.localeId = string2;
         this.timeZone = optional;
      }

      public String format() {
         return this.format;
      }

      public String localeId() {
         return this.localeId;
      }

      public Optional timeZone() {
         return this.timeZone;
      }
   }
}
