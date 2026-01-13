/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class CyclingButtonWidget.Builder<T> {
    private final Supplier<T> valueSupplier;
    private final Function<T, Text> valueToText;
    private SimpleOption.TooltipFactory<T> tooltipFactory = value -> null;
    private CyclingButtonWidget.IconGetter<T> icon = (button, value) -> null;
    private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
    private CyclingButtonWidget.Values<T> values = CyclingButtonWidget.Values.of(ImmutableList.of());
    private CyclingButtonWidget.LabelType labelType = CyclingButtonWidget.LabelType.NAME_AND_VALUE;

    public CyclingButtonWidget.Builder(Function<T, Text> valueToText, Supplier<T> valueSupplier) {
        this.valueToText = valueToText;
        this.valueSupplier = valueSupplier;
    }

    public CyclingButtonWidget.Builder<T> values(Collection<T> values) {
        return this.values(CyclingButtonWidget.Values.of(values));
    }

    @SafeVarargs
    public final CyclingButtonWidget.Builder<T> values(T ... values) {
        return this.values((Collection<T>)ImmutableList.copyOf((Object[])values));
    }

    public CyclingButtonWidget.Builder<T> values(List<T> defaults, List<T> alternatives) {
        return this.values(CyclingButtonWidget.Values.of(HAS_ALT_DOWN, defaults, alternatives));
    }

    public CyclingButtonWidget.Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
        return this.values(CyclingButtonWidget.Values.of(alternativeToggle, defaults, alternatives));
    }

    public CyclingButtonWidget.Builder<T> values(CyclingButtonWidget.Values<T> values) {
        this.values = values;
        return this;
    }

    public CyclingButtonWidget.Builder<T> tooltip(SimpleOption.TooltipFactory<T> tooltipFactory) {
        this.tooltipFactory = tooltipFactory;
        return this;
    }

    public CyclingButtonWidget.Builder<T> narration(Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
        this.narrationMessageFactory = narrationMessageFactory;
        return this;
    }

    public CyclingButtonWidget.Builder<T> icon(CyclingButtonWidget.IconGetter<T> icon) {
        this.icon = icon;
        return this;
    }

    public CyclingButtonWidget.Builder<T> labelType(CyclingButtonWidget.LabelType labelType) {
        this.labelType = labelType;
        return this;
    }

    public CyclingButtonWidget.Builder<T> omitKeyText() {
        return this.labelType(CyclingButtonWidget.LabelType.VALUE);
    }

    public CyclingButtonWidget<T> build(Text optionText, CyclingButtonWidget.UpdateCallback<T> callback) {
        return this.build(0, 0, 150, 20, optionText, callback);
    }

    public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText) {
        return this.build(x, y, width, height, optionText, (button, value) -> {});
    }

    public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText, CyclingButtonWidget.UpdateCallback<T> callback) {
        List<T> list = this.values.getDefaults();
        if (list.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
        }
        T object = this.valueSupplier.get();
        int i = list.indexOf(object);
        Text text = this.valueToText.apply(object);
        Text text2 = this.labelType == CyclingButtonWidget.LabelType.VALUE ? text : ScreenTexts.composeGenericOptionText(optionText, text);
        return new CyclingButtonWidget<T>(x, y, width, height, text2, optionText, i, object, this.valueSupplier, this.values, this.valueToText, this.narrationMessageFactory, callback, this.tooltipFactory, this.labelType, this.icon);
    }
}
