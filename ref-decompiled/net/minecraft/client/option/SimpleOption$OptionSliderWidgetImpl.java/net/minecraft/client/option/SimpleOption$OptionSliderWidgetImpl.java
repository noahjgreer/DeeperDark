/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.option;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Updatable;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class SimpleOption.OptionSliderWidgetImpl<N>
extends OptionSliderWidget
implements Updatable {
    private final SimpleOption<N> option;
    private final SimpleOption.SliderCallbacks<N> callbacks;
    private final SimpleOption.TooltipFactory<N> tooltipFactory;
    private final Consumer<N> changeCallback;
    private @Nullable Long timeToApply;
    private final boolean shouldApplyImmediately;

    SimpleOption.OptionSliderWidgetImpl(GameOptions options, int x, int y, int width, int height, SimpleOption<N> option, SimpleOption.SliderCallbacks<N> callbacks, SimpleOption.TooltipFactory<N> tooltipFactory, Consumer<N> changeCallback, boolean shouldApplyImmediately) {
        super(options, x, y, width, height, callbacks.toSliderProgress(option.getValue()));
        this.option = option;
        this.callbacks = callbacks;
        this.tooltipFactory = tooltipFactory;
        this.changeCallback = changeCallback;
        this.shouldApplyImmediately = shouldApplyImmediately;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.option.textGetter.apply(this.callbacks.toValue(this.value)));
        this.setTooltip(this.tooltipFactory.apply(this.callbacks.toValue(this.value)));
    }

    @Override
    protected void applyValue() {
        if (this.shouldApplyImmediately) {
            this.applyPendingValue();
        } else {
            this.timeToApply = Util.getMeasuringTimeMs() + 600L;
        }
    }

    public void applyPendingValue() {
        N object = this.callbacks.toValue(this.value);
        if (!Objects.equals(object, this.option.getValue())) {
            this.option.setValue(object);
            this.changeCallback.accept(this.option.getValue());
        }
    }

    @Override
    public void update() {
        if (this.value != this.callbacks.toSliderProgress(this.option.getValue())) {
            this.value = this.callbacks.toSliderProgress(this.option.getValue());
            this.timeToApply = null;
            this.updateMessage();
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        if (this.timeToApply != null && Util.getMeasuringTimeMs() >= this.timeToApply) {
            this.timeToApply = null;
            this.applyPendingValue();
            this.update();
        }
    }

    @Override
    public void onRelease(Click click) {
        super.onRelease(click);
        if (this.shouldApplyImmediately) {
            this.update();
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace()) {
            this.sliderFocused = !this.sliderFocused;
            return true;
        }
        if (this.sliderFocused) {
            Optional<N> optional;
            boolean bl = input.isLeft();
            boolean bl2 = input.isRight();
            if (bl && (optional = this.callbacks.getPrevious(this.callbacks.toValue(this.value))).isPresent()) {
                this.setValue(this.callbacks.toSliderProgress(optional.get()));
                return true;
            }
            if (bl2 && (optional = this.callbacks.getNext(this.callbacks.toValue(this.value))).isPresent()) {
                this.setValue(this.callbacks.toSliderProgress(optional.get()));
                return true;
            }
            if (bl || bl2) {
                float f = bl ? -1.0f : 1.0f;
                this.setValue(this.value + (double)(f / (float)(this.width - 8)));
                return true;
            }
        }
        return false;
    }
}
