package net.minecraft.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record NumberRangeInputControl(int width, Text label, String labelFormat, RangeInfo rangeInfo) implements InputControl {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(NumberRangeInputControl::width), TextCodecs.CODEC.fieldOf("label").forGetter(NumberRangeInputControl::label), Codec.STRING.optionalFieldOf("label_format", "options.generic_value").forGetter(NumberRangeInputControl::labelFormat), NumberRangeInputControl.RangeInfo.CODEC.forGetter(NumberRangeInputControl::rangeInfo)).apply(instance, NumberRangeInputControl::new);
   });

   public NumberRangeInputControl(int i, Text text, String string, RangeInfo rangeInfo) {
      this.width = i;
      this.label = text;
      this.labelFormat = string;
      this.rangeInfo = rangeInfo;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Text getFormattedLabel(String value) {
      return Text.translatable(this.labelFormat, this.label, value);
   }

   public int width() {
      return this.width;
   }

   public Text label() {
      return this.label;
   }

   public String labelFormat() {
      return this.labelFormat;
   }

   public RangeInfo rangeInfo() {
      return this.rangeInfo;
   }

   public static record RangeInfo(float start, float end, Optional initial, Optional step) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.FLOAT.fieldOf("start").forGetter(RangeInfo::start), Codec.FLOAT.fieldOf("end").forGetter(RangeInfo::end), Codec.FLOAT.optionalFieldOf("initial").forGetter(RangeInfo::initial), Codecs.POSITIVE_FLOAT.optionalFieldOf("step").forGetter(RangeInfo::step)).apply(instance, RangeInfo::new);
      }).validate((rangeInfo) -> {
         if (rangeInfo.initial.isPresent()) {
            double d = (double)(Float)rangeInfo.initial.get();
            double e = (double)Math.min(rangeInfo.start, rangeInfo.end);
            double f = (double)Math.max(rangeInfo.start, rangeInfo.end);
            if (d < e || d > f) {
               return DataResult.error(() -> {
                  return "Initial value " + d + " is outside of range [" + e + ", " + f + "]";
               });
            }
         }

         return DataResult.success(rangeInfo);
      });

      public RangeInfo(float f, float g, Optional optional, Optional optional2) {
         this.start = f;
         this.end = g;
         this.initial = optional;
         this.step = optional2;
      }

      public float sliderProgressToValue(float sliderProgress) {
         float f = MathHelper.lerp(sliderProgress, this.start, this.end);
         if (this.step.isEmpty()) {
            return f;
         } else {
            float g = (Float)this.step.get();
            float h = this.getInitialValue();
            float i = f - h;
            int j = Math.round(i / g);
            float k = h + (float)j * g;
            if (!this.isValueOutOfRange(k)) {
               return k;
            } else {
               int l = j - MathHelper.sign((double)j);
               return h + (float)l * g;
            }
         }
      }

      private boolean isValueOutOfRange(float value) {
         float f = this.valueToSliderProgress(value);
         return (double)f < 0.0 || (double)f > 1.0;
      }

      private float getInitialValue() {
         return this.initial.isPresent() ? (Float)this.initial.get() : (this.start + this.end) / 2.0F;
      }

      public float getInitialSliderProgress() {
         float f = this.getInitialValue();
         return this.valueToSliderProgress(f);
      }

      private float valueToSliderProgress(float value) {
         return this.start == this.end ? 0.5F : MathHelper.getLerpProgress(value, this.start, this.end);
      }

      public float start() {
         return this.start;
      }

      public float end() {
         return this.end;
      }

      public Optional initial() {
         return this.initial;
      }

      public Optional step() {
         return this.step;
      }
   }
}
