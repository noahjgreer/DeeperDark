/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Updatable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CyclingButtonWidget<T>
extends PressableWidget
implements Updatable {
    public static final BooleanSupplier HAS_ALT_DOWN = () -> MinecraftClient.getInstance().isAltPressed();
    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of((Object)Boolean.TRUE, (Object)Boolean.FALSE);
    private final Supplier<T> valueSupplier;
    private final Text optionText;
    private int index;
    private T value;
    private final Values<T> values;
    private final Function<T, Text> valueToText;
    private final Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory;
    private final UpdateCallback<T> callback;
    private final LabelType labelType;
    private final SimpleOption.TooltipFactory<T> tooltipFactory;
    private final IconGetter<T> icon;

    CyclingButtonWidget(int x, int y, int width, int height, Text message, Text optionText, int index, T value, Supplier<T> valueSupplier, Values<T> values, Function<T, Text> valueToText, Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory, UpdateCallback<T> callback, SimpleOption.TooltipFactory<T> tooltipFactory, LabelType labelType, IconGetter<T> icon) {
        super(x, y, width, height, message);
        this.optionText = optionText;
        this.index = index;
        this.valueSupplier = valueSupplier;
        this.value = value;
        this.values = values;
        this.valueToText = valueToText;
        this.narrationMessageFactory = narrationMessageFactory;
        this.callback = callback;
        this.labelType = labelType;
        this.tooltipFactory = tooltipFactory;
        this.icon = icon;
        this.refreshTooltip();
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Identifier identifier = this.icon.apply(this, this.getValue());
        if (identifier != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            this.drawButton(context);
        }
        if (this.labelType != LabelType.HIDE) {
            this.drawLabel(context.getHoverListener(this, DrawContext.HoverType.NONE));
        }
    }

    private void refreshTooltip() {
        this.setTooltip(this.tooltipFactory.apply(this.value));
    }

    @Override
    public void onPress(AbstractInput input) {
        if (input.hasShift()) {
            this.cycle(-1);
        } else {
            this.cycle(1);
        }
    }

    private void cycle(int amount) {
        List<T> list = this.values.getCurrent();
        this.index = MathHelper.floorMod(this.index + amount, list.size());
        T object = list.get(this.index);
        this.internalSetValue(object);
        this.callback.onValueChange(this, object);
    }

    private T getValue(int offset) {
        List<T> list = this.values.getCurrent();
        return list.get(MathHelper.floorMod(this.index + offset, list.size()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0.0) {
            this.cycle(-1);
        } else if (verticalAmount < 0.0) {
            this.cycle(1);
        }
        return true;
    }

    public void setValue(T value) {
        List<T> list = this.values.getCurrent();
        int i = list.indexOf(value);
        if (i != -1) {
            this.index = i;
        }
        this.internalSetValue(value);
    }

    @Override
    public void update() {
        this.setValue(this.valueSupplier.get());
    }

    private void internalSetValue(T value) {
        Text text = this.composeText(value);
        this.setMessage(text);
        this.value = value;
        this.refreshTooltip();
    }

    private Text composeText(T value) {
        return this.labelType == LabelType.VALUE ? this.valueToText.apply(value) : this.composeGenericOptionText(value);
    }

    private MutableText composeGenericOptionText(T value) {
        return ScreenTexts.composeGenericOptionText(this.optionText, this.valueToText.apply(value));
    }

    public T getValue() {
        return this.value;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return this.narrationMessageFactory.apply(this);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            T object = this.getValue(1);
            Text text = this.composeText(object);
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.cycle_button.usage.focused", text));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.cycle_button.usage.hovered", text));
            }
        }
    }

    public MutableText getGenericNarrationMessage() {
        return CyclingButtonWidget.getNarrationMessage(this.labelType == LabelType.VALUE ? this.composeGenericOptionText(this.value) : this.getMessage());
    }

    public static <T> Builder<T> builder(Function<T, Text> valueToText, Supplier<T> valueSupplier) {
        return new Builder<T>(valueToText, valueSupplier);
    }

    public static <T> Builder<T> builder(Function<T, Text> valueToText, T value) {
        return new Builder<Object>(valueToText, () -> value);
    }

    public static Builder<Boolean> onOffBuilder(Text on, Text off, boolean defaultValue) {
        return new Builder<Boolean>(value -> value == Boolean.TRUE ? on : off, () -> defaultValue).values((Collection<Boolean>)BOOLEAN_VALUES);
    }

    public static Builder<Boolean> onOffBuilder(boolean defaultValue) {
        return new Builder<Boolean>(value -> value == Boolean.TRUE ? ScreenTexts.ON : ScreenTexts.OFF, () -> defaultValue).values((Collection<Boolean>)BOOLEAN_VALUES);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Values<T> {
        public List<T> getCurrent();

        public List<T> getDefaults();

        public static <T> Values<T> of(Collection<T> values) {
            ImmutableList list = ImmutableList.copyOf(values);
            return new Values<T>((List)list){
                final /* synthetic */ List field_27979;
                {
                    this.field_27979 = list;
                }

                @Override
                public List<T> getCurrent() {
                    return this.field_27979;
                }

                @Override
                public List<T> getDefaults() {
                    return this.field_27979;
                }
            };
        }

        public static <T> Values<T> of(final BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            ImmutableList list = ImmutableList.copyOf(defaults);
            ImmutableList list2 = ImmutableList.copyOf(alternatives);
            return new Values<T>((List)list2, (List)list){
                final /* synthetic */ List field_27981;
                final /* synthetic */ List field_27982;
                {
                    this.field_27981 = list;
                    this.field_27982 = list2;
                }

                @Override
                public List<T> getCurrent() {
                    return alternativeToggle.getAsBoolean() ? this.field_27981 : this.field_27982;
                }

                @Override
                public List<T> getDefaults() {
                    return this.field_27982;
                }
            };
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface UpdateCallback<T> {
        public void onValueChange(CyclingButtonWidget<T> var1, T var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class LabelType
    extends Enum<LabelType> {
        public static final /* enum */ LabelType NAME_AND_VALUE = new LabelType();
        public static final /* enum */ LabelType VALUE = new LabelType();
        public static final /* enum */ LabelType HIDE = new LabelType();
        private static final /* synthetic */ LabelType[] field_64542;

        public static LabelType[] values() {
            return (LabelType[])field_64542.clone();
        }

        public static LabelType valueOf(String string) {
            return Enum.valueOf(LabelType.class, string);
        }

        private static /* synthetic */ LabelType[] method_76617() {
            return new LabelType[]{NAME_AND_VALUE, VALUE, HIDE};
        }

        static {
            field_64542 = LabelType.method_76617();
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface IconGetter<T> {
        public @Nullable Identifier apply(CyclingButtonWidget<T> var1, T var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder<T> {
        private final Supplier<T> valueSupplier;
        private final Function<T, Text> valueToText;
        private SimpleOption.TooltipFactory<T> tooltipFactory = value -> null;
        private IconGetter<T> icon = (button, value) -> null;
        private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
        private Values<T> values = Values.of(ImmutableList.of());
        private LabelType labelType = LabelType.NAME_AND_VALUE;

        public Builder(Function<T, Text> valueToText, Supplier<T> valueSupplier) {
            this.valueToText = valueToText;
            this.valueSupplier = valueSupplier;
        }

        public Builder<T> values(Collection<T> values) {
            return this.values(Values.of(values));
        }

        @SafeVarargs
        public final Builder<T> values(T ... values) {
            return this.values((Collection<T>)ImmutableList.copyOf((Object[])values));
        }

        public Builder<T> values(List<T> defaults, List<T> alternatives) {
            return this.values(Values.of(HAS_ALT_DOWN, defaults, alternatives));
        }

        public Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            return this.values(Values.of(alternativeToggle, defaults, alternatives));
        }

        public Builder<T> values(Values<T> values) {
            this.values = values;
            return this;
        }

        public Builder<T> tooltip(SimpleOption.TooltipFactory<T> tooltipFactory) {
            this.tooltipFactory = tooltipFactory;
            return this;
        }

        public Builder<T> narration(Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
            this.narrationMessageFactory = narrationMessageFactory;
            return this;
        }

        public Builder<T> icon(IconGetter<T> icon) {
            this.icon = icon;
            return this;
        }

        public Builder<T> labelType(LabelType labelType) {
            this.labelType = labelType;
            return this;
        }

        public Builder<T> omitKeyText() {
            return this.labelType(LabelType.VALUE);
        }

        public CyclingButtonWidget<T> build(Text optionText, UpdateCallback<T> callback) {
            return this.build(0, 0, 150, 20, optionText, callback);
        }

        public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText) {
            return this.build(x, y, width, height, optionText, (button, value) -> {});
        }

        public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText, UpdateCallback<T> callback) {
            List<T> list = this.values.getDefaults();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            }
            T object = this.valueSupplier.get();
            int i = list.indexOf(object);
            Text text = this.valueToText.apply(object);
            Text text2 = this.labelType == LabelType.VALUE ? text : ScreenTexts.composeGenericOptionText(optionText, text);
            return new CyclingButtonWidget<T>(x, y, width, height, text2, optionText, i, object, this.valueSupplier, this.values, this.valueToText, this.narrationMessageFactory, callback, this.tooltipFactory, this.labelType, this.icon);
        }
    }
}
