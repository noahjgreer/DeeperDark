/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class WorldScreenOptionGrid.OptionBuilder {
    private final Text text;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;
    private @Nullable Text tooltip;
    private @Nullable BooleanSupplier toggleable;
    private final int buttonWidth;

    WorldScreenOptionGrid.OptionBuilder(Text text, BooleanSupplier getter, Consumer<Boolean> setter, int buttonWidth) {
        this.text = text;
        this.getter = getter;
        this.setter = setter;
        this.buttonWidth = buttonWidth;
    }

    public WorldScreenOptionGrid.OptionBuilder toggleable(BooleanSupplier toggleable) {
        this.toggleable = toggleable;
        return this;
    }

    public WorldScreenOptionGrid.OptionBuilder tooltip(Text tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    WorldScreenOptionGrid.Option build(WorldScreenOptionGrid.Builder gridBuilder, GridWidget gridWidget, int row) {
        boolean bl;
        gridBuilder.incrementRows();
        TextWidget textWidget = new TextWidget(this.text, MinecraftClient.getInstance().textRenderer);
        gridWidget.add(textWidget, gridBuilder.rows, row, gridWidget.copyPositioner().relative(0.0f, 0.5f).marginLeft(gridBuilder.marginLeft));
        Optional<WorldScreenOptionGrid.TooltipBoxDisplay> optional = gridBuilder.tooltipBoxDisplay;
        CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
        builder.omitKeyText();
        boolean bl2 = bl = this.tooltip != null && optional.isEmpty();
        if (bl) {
            Tooltip tooltip = Tooltip.of(this.tooltip);
            builder.tooltip((T value) -> tooltip);
        }
        if (this.tooltip != null && !bl) {
            builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage(), this.tooltip));
        } else {
            builder.narration(button -> ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage()));
        }
        CyclingButtonWidget<Boolean> cyclingButtonWidget = builder.build(0, 0, this.buttonWidth, 20, Text.empty(), (button, value) -> this.setter.accept((Boolean)value));
        if (this.toggleable != null) {
            cyclingButtonWidget.active = this.toggleable.getAsBoolean();
        }
        gridWidget.add(cyclingButtonWidget, gridBuilder.rows, row + 1, gridWidget.copyPositioner().alignRight());
        if (this.tooltip != null) {
            optional.ifPresent(tooltipBoxDisplay -> {
                MutableText text = this.tooltip.copy().formatted(Formatting.GRAY);
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                MultilineTextWidget multilineTextWidget = new MultilineTextWidget(text, textRenderer);
                multilineTextWidget.setMaxWidth(builder.width - builder.marginLeft - this.buttonWidth);
                multilineTextWidget.setMaxRows(tooltipBoxDisplay.maxInfoRows());
                gridBuilder.incrementRows();
                int j = tooltipBoxDisplay.alwaysMaxHeight ? textRenderer.fontHeight * tooltipBoxDisplay.maxInfoRows - multilineTextWidget.getHeight() : 0;
                gridWidget.add(multilineTextWidget, builder.rows, row, gridWidget.copyPositioner().marginTop(-builder.rowSpacing).marginBottom(j));
            });
        }
        return new WorldScreenOptionGrid.Option(cyclingButtonWidget, this.getter, this.toggleable);
    }
}
