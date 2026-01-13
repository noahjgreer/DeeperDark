/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.client.option.SimpleOption$Callbacks
 *  net.minecraft.client.option.SimpleOption$PotentialValuesBasedCallbacks
 *  net.minecraft.client.option.SimpleOption$TooltipFactory
 *  net.minecraft.client.option.SimpleOption$ValueTextGetter
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class SimpleOption<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final PotentialValuesBasedCallbacks<Boolean> BOOLEAN = new PotentialValuesBasedCallbacks((List)ImmutableList.of((Object)Boolean.TRUE, (Object)Boolean.FALSE), (Codec)Codec.BOOL);
    public static final ValueTextGetter<Boolean> BOOLEAN_TEXT_GETTER = (optionText, value) -> value != false ? ScreenTexts.ON : ScreenTexts.OFF;
    private final TooltipFactory<T> tooltipFactory;
    final Function<T, Text> textGetter;
    private final Callbacks<T> callbacks;
    private final Codec<T> codec;
    private final T defaultValue;
    private final Consumer<T> changeCallback;
    final Text text;
    private T value;

    public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return SimpleOption.ofBoolean((String)key, (TooltipFactory)SimpleOption.emptyTooltip(), (boolean)defaultValue, changeCallback);
    }

    public static SimpleOption<Boolean> ofBoolean(String key, boolean defaultValue) {
        return SimpleOption.ofBoolean((String)key, (TooltipFactory)SimpleOption.emptyTooltip(), (boolean)defaultValue, (T value) -> {});
    }

    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactory<Boolean> tooltipFactory, boolean defaultValue) {
        return SimpleOption.ofBoolean((String)key, tooltipFactory, (boolean)defaultValue, (T value) -> {});
    }

    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactory<Boolean> tooltipFactory, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return SimpleOption.ofBoolean((String)key, tooltipFactory, (ValueTextGetter)BOOLEAN_TEXT_GETTER, (boolean)defaultValue, changeCallback);
    }

    public static SimpleOption<Boolean> ofBoolean(String key, TooltipFactory<Boolean> tooltipFactory, ValueTextGetter<Boolean> valueTextGetter, boolean defaultValue, Consumer<Boolean> changeCallback) {
        return new SimpleOption(key, tooltipFactory, valueTextGetter, (Callbacks)BOOLEAN, (Object)defaultValue, changeCallback);
    }

    public SimpleOption(String key, TooltipFactory<T> tooltipFactory, ValueTextGetter<T> valueTextGetter, Callbacks<T> callbacks, T defaultValue, Consumer<T> changeCallback) {
        this(key, tooltipFactory, valueTextGetter, callbacks, callbacks.codec(), defaultValue, changeCallback);
    }

    public SimpleOption(String key, TooltipFactory<T> tooltipFactory, ValueTextGetter<T> valueTextGetter, Callbacks<T> callbacks, Codec<T> codec, T defaultValue, Consumer<T> changeCallback) {
        this.text = Text.translatable((String)key);
        this.tooltipFactory = tooltipFactory;
        this.textGetter = value -> valueTextGetter.toString(this.text, value);
        this.callbacks = callbacks;
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.changeCallback = changeCallback;
        this.value = this.defaultValue;
    }

    public static <T> TooltipFactory<T> emptyTooltip() {
        return value -> null;
    }

    public static <T> TooltipFactory<T> constantTooltip(Text text) {
        return value -> Tooltip.of((Text)text);
    }

    public ClickableWidget createWidget(GameOptions options) {
        return this.createWidget(options, 0, 0, 150);
    }

    public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
        return this.createWidget(options, x, y, width, value -> {});
    }

    public ClickableWidget createWidget(GameOptions options, int x, int y, int width, Consumer<T> changeCallback) {
        return (ClickableWidget)this.callbacks.getWidgetCreator(this.tooltipFactory, options, x, y, width, changeCallback).apply(this);
    }

    public T getValue() {
        return (T)this.value;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }

    public String toString() {
        return this.text.getString();
    }

    public void setValue(T value) {
        Object object = this.callbacks.validate(value).orElseGet(() -> {
            LOGGER.error("Illegal option value {} for {}", value, (Object)this.text.getString());
            return this.defaultValue;
        });
        if (!MinecraftClient.getInstance().isRunning()) {
            this.value = object;
            return;
        }
        if (!Objects.equals(this.value, object)) {
            this.value = object;
            this.changeCallback.accept(this.value);
        }
    }

    public Callbacks<T> getCallbacks() {
        return this.callbacks;
    }
}

