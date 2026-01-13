/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.DrawContext$HoverType
 *  net.minecraft.client.gui.Updatable
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget$Builder
 *  net.minecraft.client.gui.widget.CyclingButtonWidget$IconGetter
 *  net.minecraft.client.gui.widget.CyclingButtonWidget$LabelType
 *  net.minecraft.client.gui.widget.CyclingButtonWidget$UpdateCallback
 *  net.minecraft.client.gui.widget.CyclingButtonWidget$Values
 *  net.minecraft.client.gui.widget.PressableWidget
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.option.SimpleOption$TooltipFactory
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
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

    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Identifier identifier = this.icon.apply(this, this.getValue());
        if (identifier != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            this.drawButton(context);
        }
        if (this.labelType != LabelType.HIDE) {
            this.drawLabel(context.getHoverListener((ClickableWidget)this, DrawContext.HoverType.NONE));
        }
    }

    private void refreshTooltip() {
        this.setTooltip(this.tooltipFactory.apply(this.value));
    }

    public void onPress(AbstractInput input) {
        if (input.hasShift()) {
            this.cycle(-1);
        } else {
            this.cycle(1);
        }
    }

    private void cycle(int amount) {
        List list = this.values.getCurrent();
        this.index = MathHelper.floorMod((int)(this.index + amount), (int)list.size());
        Object object = list.get(this.index);
        this.internalSetValue(object);
        this.callback.onValueChange(this, object);
    }

    private T getValue(int offset) {
        List list = this.values.getCurrent();
        return (T)list.get(MathHelper.floorMod((int)(this.index + offset), (int)list.size()));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0.0) {
            this.cycle(-1);
        } else if (verticalAmount < 0.0) {
            this.cycle(1);
        }
        return true;
    }

    public void setValue(T value) {
        List list = this.values.getCurrent();
        int i = list.indexOf(value);
        if (i != -1) {
            this.index = i;
        }
        this.internalSetValue(value);
    }

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
        return this.labelType == LabelType.VALUE ? (Text)this.valueToText.apply(value) : this.composeGenericOptionText(value);
    }

    private MutableText composeGenericOptionText(T value) {
        return ScreenTexts.composeGenericOptionText((Text)this.optionText, (Text)((Text)this.valueToText.apply(value)));
    }

    public T getValue() {
        return (T)this.value;
    }

    protected MutableText getNarrationMessage() {
        return (MutableText)this.narrationMessageFactory.apply(this);
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            Object object = this.getValue(1);
            Text text = this.composeText(object);
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.cycle_button.usage.focused", (Object[])new Object[]{text}));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.cycle_button.usage.hovered", (Object[])new Object[]{text}));
            }
        }
    }

    public MutableText getGenericNarrationMessage() {
        return CyclingButtonWidget.getNarrationMessage((Text)(this.labelType == LabelType.VALUE ? this.composeGenericOptionText(this.value) : this.getMessage()));
    }

    public static <T> Builder<T> builder(Function<T, Text> valueToText, Supplier<T> valueSupplier) {
        return new Builder(valueToText, valueSupplier);
    }

    public static <T> Builder<T> builder(Function<T, Text> valueToText, T value) {
        return new Builder(valueToText, () -> value);
    }

    public static Builder<Boolean> onOffBuilder(Text on, Text off, boolean defaultValue) {
        return new Builder(value -> value == Boolean.TRUE ? on : off, () -> defaultValue).values((Collection)BOOLEAN_VALUES);
    }

    public static Builder<Boolean> onOffBuilder(boolean defaultValue) {
        return new Builder(value -> value == Boolean.TRUE ? ScreenTexts.ON : ScreenTexts.OFF, () -> defaultValue).values((Collection)BOOLEAN_VALUES);
    }
}

