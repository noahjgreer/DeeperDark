package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class SimpleOption {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final PotentialValuesBasedCallbacks BOOLEAN;
   public static final ValueTextGetter BOOLEAN_TEXT_GETTER;
   private final TooltipFactory tooltipFactory;
   final Function textGetter;
   private final Callbacks callbacks;
   private final Codec codec;
   private final Object defaultValue;
   private final Consumer changeCallback;
   final Text text;
   Object value;

   public static SimpleOption ofBoolean(String key, boolean defaultValue, Consumer changeCallback) {
      return ofBoolean(key, emptyTooltip(), defaultValue, changeCallback);
   }

   public static SimpleOption ofBoolean(String key, boolean defaultValue) {
      return ofBoolean(key, emptyTooltip(), defaultValue, (value) -> {
      });
   }

   public static SimpleOption ofBoolean(String key, TooltipFactory tooltipFactory, boolean defaultValue) {
      return ofBoolean(key, tooltipFactory, defaultValue, (value) -> {
      });
   }

   public static SimpleOption ofBoolean(String key, TooltipFactory tooltipFactory, boolean defaultValue, Consumer changeCallback) {
      return ofBoolean(key, tooltipFactory, BOOLEAN_TEXT_GETTER, defaultValue, changeCallback);
   }

   public static SimpleOption ofBoolean(String key, TooltipFactory tooltipFactory, ValueTextGetter valueTextGetter, boolean defaultValue, Consumer changeCallback) {
      return new SimpleOption(key, tooltipFactory, valueTextGetter, BOOLEAN, defaultValue, changeCallback);
   }

   public SimpleOption(String key, TooltipFactory tooltipFactory, ValueTextGetter valueTextGetter, Callbacks callbacks, Object defaultValue, Consumer changeCallback) {
      this(key, tooltipFactory, valueTextGetter, callbacks, callbacks.codec(), defaultValue, changeCallback);
   }

   public SimpleOption(String key, TooltipFactory tooltipFactory, ValueTextGetter valueTextGetter, Callbacks callbacks, Codec codec, Object defaultValue, Consumer changeCallback) {
      this.text = Text.translatable(key);
      this.tooltipFactory = tooltipFactory;
      this.textGetter = (value) -> {
         return valueTextGetter.toString(this.text, value);
      };
      this.callbacks = callbacks;
      this.codec = codec;
      this.defaultValue = defaultValue;
      this.changeCallback = changeCallback;
      this.value = this.defaultValue;
   }

   public static TooltipFactory emptyTooltip() {
      return (value) -> {
         return null;
      };
   }

   public static TooltipFactory constantTooltip(Text text) {
      return (value) -> {
         return Tooltip.of(text);
      };
   }

   public static ValueTextGetter enumValueText() {
      return (optionText, value) -> {
         return value.getText();
      };
   }

   public ClickableWidget createWidget(GameOptions options) {
      return this.createWidget(options, 0, 0, 150);
   }

   public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
      return this.createWidget(options, x, y, width, (value) -> {
      });
   }

   public ClickableWidget createWidget(GameOptions options, int x, int y, int width, Consumer changeCallback) {
      return (ClickableWidget)this.callbacks.getWidgetCreator(this.tooltipFactory, options, x, y, width, changeCallback).apply(this);
   }

   public Object getValue() {
      return this.value;
   }

   public Codec getCodec() {
      return this.codec;
   }

   public String toString() {
      return this.text.getString();
   }

   public void setValue(Object value) {
      Object object = this.callbacks.validate(value).orElseGet(() -> {
         Logger var10000 = LOGGER;
         String var10001 = String.valueOf(value);
         var10000.error("Illegal option value " + var10001 + " for " + String.valueOf(this.text));
         return this.defaultValue;
      });
      if (!MinecraftClient.getInstance().isRunning()) {
         this.value = object;
      } else {
         if (!Objects.equals(this.value, object)) {
            this.value = object;
            this.changeCallback.accept(this.value);
         }

      }
   }

   public Callbacks getCallbacks() {
      return this.callbacks;
   }

   static {
      BOOLEAN = new PotentialValuesBasedCallbacks(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
      BOOLEAN_TEXT_GETTER = (optionText, value) -> {
         return value ? ScreenTexts.ON : ScreenTexts.OFF;
      };
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface TooltipFactory {
      @Nullable
      Tooltip apply(Object value);
   }

   @Environment(EnvType.CLIENT)
   public interface ValueTextGetter {
      Text toString(Text optionText, Object value);
   }

   @Environment(EnvType.CLIENT)
   public static record PotentialValuesBasedCallbacks(List values, Codec codec) implements CyclingCallbacks {
      public PotentialValuesBasedCallbacks(List list, Codec codec) {
         this.values = list;
         this.codec = codec;
      }

      public Optional validate(Object value) {
         return this.values.contains(value) ? Optional.of(value) : Optional.empty();
      }

      public CyclingButtonWidget.Values getValues() {
         return CyclingButtonWidget.Values.of(this.values);
      }

      public List values() {
         return this.values;
      }

      public Codec codec() {
         return this.codec;
      }
   }

   @Environment(EnvType.CLIENT)
   interface Callbacks {
      Function getWidgetCreator(TooltipFactory tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer changeCallback);

      Optional validate(Object value);

      Codec codec();
   }

   @Environment(EnvType.CLIENT)
   public static enum DoubleSliderCallbacks implements SliderCallbacks {
      INSTANCE;

      public Optional validate(Double double_) {
         return double_ >= 0.0 && double_ <= 1.0 ? Optional.of(double_) : Optional.empty();
      }

      public double toSliderProgress(Double double_) {
         return double_;
      }

      public Double toValue(double d) {
         return d;
      }

      public SliderCallbacks withModifier(final DoubleFunction sliderProgressValueToValue, final ToDoubleFunction valueToSliderProgressValue) {
         return new SliderCallbacks() {
            public Optional validate(Object value) {
               Optional var10000 = DoubleSliderCallbacks.this.validate(valueToSliderProgressValue.applyAsDouble(value));
               DoubleFunction var10001 = sliderProgressValueToValue;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderProgress(Object value) {
               return DoubleSliderCallbacks.this.toSliderProgress(valueToSliderProgressValue.applyAsDouble(value));
            }

            public Object toValue(double sliderProgress) {
               return sliderProgressValueToValue.apply(DoubleSliderCallbacks.this.toValue(sliderProgress));
            }

            public Codec codec() {
               Codec var10000 = DoubleSliderCallbacks.this.codec();
               DoubleFunction var10001 = sliderProgressValueToValue;
               Objects.requireNonNull(var10001);
               Function var1 = var10001::apply;
               ToDoubleFunction var10002 = valueToSliderProgressValue;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1, var10002::applyAsDouble);
            }
         };
      }

      public Codec codec() {
         return Codec.withAlternative(Codec.doubleRange(0.0, 1.0), Codec.BOOL, (value) -> {
            return value ? 1.0 : 0.0;
         });
      }

      // $FF: synthetic method
      public Object toValue(final double sliderProgress) {
         return this.toValue(sliderProgress);
      }

      // $FF: synthetic method
      private static DoubleSliderCallbacks[] method_41767() {
         return new DoubleSliderCallbacks[]{INSTANCE};
      }
   }

   @Environment(EnvType.CLIENT)
   public static record MaxSuppliableIntCallbacks(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements IntSliderCallbacks, TypeChangeableCallbacks {
      public MaxSuppliableIntCallbacks(int i, IntSupplier intSupplier, int j) {
         this.minInclusive = i;
         this.maxSupplier = intSupplier;
         this.encodableMaxInclusive = j;
      }

      public Optional validate(Integer integer) {
         return Optional.of(MathHelper.clamp(integer, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec codec() {
         return Codec.INT.validate((value) -> {
            int i = this.encodableMaxInclusive + 1;
            return value.compareTo(this.minInclusive) >= 0 && value.compareTo(i) <= 0 ? DataResult.success(value) : DataResult.error(() -> {
               return "Value " + value + " outside of range [" + this.minInclusive + ":" + i + "]";
            }, value);
         });
      }

      public boolean isCycling() {
         return true;
      }

      public CyclingButtonWidget.Values getValues() {
         return CyclingButtonWidget.Values.of(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }

      public int minInclusive() {
         return this.minInclusive;
      }

      public IntSupplier maxSupplier() {
         return this.maxSupplier;
      }

      public int encodableMaxInclusive() {
         return this.encodableMaxInclusive;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive, boolean applyValueImmediately) implements IntSliderCallbacks {
      public ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive) {
         this(minInclusive, maxInclusive, true);
      }

      public ValidatingIntSliderCallbacks(int i, int j, boolean bl) {
         this.minInclusive = i;
         this.maxInclusive = j;
         this.applyValueImmediately = bl;
      }

      public Optional validate(Integer integer) {
         return integer.compareTo(this.minInclusive()) >= 0 && integer.compareTo(this.maxInclusive()) <= 0 ? Optional.of(integer) : Optional.empty();
      }

      public Codec codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }

      public int minInclusive() {
         return this.minInclusive;
      }

      public int maxInclusive() {
         return this.maxInclusive;
      }

      public boolean applyValueImmediately() {
         return this.applyValueImmediately;
      }
   }

   @Environment(EnvType.CLIENT)
   interface IntSliderCallbacks extends SliderCallbacks {
      int minInclusive();

      int maxInclusive();

      default double toSliderProgress(Integer integer) {
         if (integer == this.minInclusive()) {
            return 0.0;
         } else {
            return integer == this.maxInclusive() ? 1.0 : MathHelper.map((double)integer + 0.5, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0, 0.0, 1.0);
         }
      }

      default Integer toValue(double d) {
         if (d >= 1.0) {
            d = 0.9999899864196777;
         }

         return MathHelper.floor(MathHelper.map(d, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0));
      }

      default SliderCallbacks withModifier(final IntFunction sliderProgressValueToValue, final ToIntFunction valueToSliderProgressValue) {
         return new SliderCallbacks() {
            public Optional validate(Object value) {
               Optional var10000 = IntSliderCallbacks.this.validate(valueToSliderProgressValue.applyAsInt(value));
               IntFunction var10001 = sliderProgressValueToValue;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderProgress(Object value) {
               return IntSliderCallbacks.this.toSliderProgress(valueToSliderProgressValue.applyAsInt(value));
            }

            public Object toValue(double sliderProgress) {
               return sliderProgressValueToValue.apply(IntSliderCallbacks.this.toValue(sliderProgress));
            }

            public Codec codec() {
               Codec var10000 = IntSliderCallbacks.this.codec();
               IntFunction var10001 = sliderProgressValueToValue;
               Objects.requireNonNull(var10001);
               Function var1 = var10001::apply;
               ToIntFunction var10002 = valueToSliderProgressValue;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1, var10002::applyAsInt);
            }
         };
      }

      // $FF: synthetic method
      default Object toValue(double sliderProgress) {
         return this.toValue(sliderProgress);
      }
   }

   @Environment(EnvType.CLIENT)
   public static final class OptionSliderWidgetImpl extends OptionSliderWidget {
      private final SimpleOption option;
      private final SliderCallbacks callbacks;
      private final TooltipFactory tooltipFactory;
      private final Consumer changeCallback;
      @Nullable
      private Long timeToApply;
      private final boolean shouldApplyImmediately;

      OptionSliderWidgetImpl(GameOptions options, int x, int y, int width, int height, SimpleOption option, SliderCallbacks callbacks, TooltipFactory tooltipFactory, Consumer changeCallback, boolean shouldApplyImmediately) {
         super(options, x, y, width, height, callbacks.toSliderProgress(option.getValue()));
         this.option = option;
         this.callbacks = callbacks;
         this.tooltipFactory = tooltipFactory;
         this.changeCallback = changeCallback;
         this.shouldApplyImmediately = shouldApplyImmediately;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage((Text)this.option.textGetter.apply(this.callbacks.toValue(this.value)));
         this.setTooltip(this.tooltipFactory.apply(this.callbacks.toValue(this.value)));
      }

      protected void applyValue() {
         if (this.shouldApplyImmediately) {
            this.applyPendingValue();
         } else {
            this.timeToApply = Util.getMeasuringTimeMs() + 600L;
         }

      }

      public void applyPendingValue() {
         Object object = this.callbacks.toValue(this.value);
         if (!Objects.equals(object, this.option.getValue())) {
            this.option.setValue(object);
            this.changeCallback.accept(this.option.getValue());
         }

      }

      public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         super.renderWidget(context, mouseX, mouseY, deltaTicks);
         if (this.timeToApply != null && Util.getMeasuringTimeMs() >= this.timeToApply) {
            this.timeToApply = null;
            this.applyPendingValue();
         }

      }
   }

   @Environment(EnvType.CLIENT)
   public static record LazyCyclingCallbacks(Supplier values, Function validateValue, Codec codec) implements CyclingCallbacks {
      public LazyCyclingCallbacks(Supplier supplier, Function function, Codec codec) {
         this.values = supplier;
         this.validateValue = function;
         this.codec = codec;
      }

      public Optional validate(Object value) {
         return (Optional)this.validateValue.apply(value);
      }

      public CyclingButtonWidget.Values getValues() {
         return CyclingButtonWidget.Values.of((Collection)this.values.get());
      }

      public Supplier values() {
         return this.values;
      }

      public Function validateValue() {
         return this.validateValue;
      }

      public Codec codec() {
         return this.codec;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record AlternateValuesSupportingCyclingCallbacks(List values, List altValues, BooleanSupplier altCondition, CyclingCallbacks.ValueSetter valueSetter, Codec codec) implements CyclingCallbacks {
      public AlternateValuesSupportingCyclingCallbacks(List list, List list2, BooleanSupplier booleanSupplier, CyclingCallbacks.ValueSetter valueSetter, Codec codec) {
         this.values = list;
         this.altValues = list2;
         this.altCondition = booleanSupplier;
         this.valueSetter = valueSetter;
         this.codec = codec;
      }

      public CyclingButtonWidget.Values getValues() {
         return CyclingButtonWidget.Values.of(this.altCondition, this.values, this.altValues);
      }

      public Optional validate(Object value) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
      }

      public List values() {
         return this.values;
      }

      public List altValues() {
         return this.altValues;
      }

      public BooleanSupplier altCondition() {
         return this.altCondition;
      }

      public CyclingCallbacks.ValueSetter valueSetter() {
         return this.valueSetter;
      }

      public Codec codec() {
         return this.codec;
      }
   }

   @Environment(EnvType.CLIENT)
   interface TypeChangeableCallbacks extends CyclingCallbacks, SliderCallbacks {
      boolean isCycling();

      default Function getWidgetCreator(TooltipFactory tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer changeCallback) {
         return this.isCycling() ? SimpleOption.CyclingCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback) : SimpleOption.SliderCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback);
      }
   }

   @Environment(EnvType.CLIENT)
   interface CyclingCallbacks extends Callbacks {
      CyclingButtonWidget.Values getValues();

      default ValueSetter valueSetter() {
         return SimpleOption::setValue;
      }

      default Function getWidgetCreator(TooltipFactory tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer changeCallback) {
         return (option) -> {
            return CyclingButtonWidget.builder(option.textGetter).values(this.getValues()).tooltip(tooltipFactory).initially(option.value).build(x, y, width, 20, option.text, (button, value) -> {
               this.valueSetter().set(option, value);
               gameOptions.write();
               changeCallback.accept(value);
            });
         };
      }

      @Environment(EnvType.CLIENT)
      public interface ValueSetter {
         void set(SimpleOption option, Object value);
      }
   }

   @Environment(EnvType.CLIENT)
   interface SliderCallbacks extends Callbacks {
      double toSliderProgress(Object value);

      Object toValue(double sliderProgress);

      default boolean applyValueImmediately() {
         return true;
      }

      default Function getWidgetCreator(TooltipFactory tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer changeCallback) {
         return (option) -> {
            return new OptionSliderWidgetImpl(gameOptions, x, y, width, 20, option, this, tooltipFactory, changeCallback, this.applyValueImmediately());
         };
      }
   }
}
