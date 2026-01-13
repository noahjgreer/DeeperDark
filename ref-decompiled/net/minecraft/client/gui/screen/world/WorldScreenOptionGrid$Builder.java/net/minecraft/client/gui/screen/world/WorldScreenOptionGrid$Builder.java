/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class WorldScreenOptionGrid.Builder {
    final int width;
    private final List<WorldScreenOptionGrid.OptionBuilder> options = new ArrayList<WorldScreenOptionGrid.OptionBuilder>();
    int marginLeft;
    int rowSpacing = 4;
    int rows;
    Optional<WorldScreenOptionGrid.TooltipBoxDisplay> tooltipBoxDisplay = Optional.empty();

    public WorldScreenOptionGrid.Builder(int width) {
        this.width = width;
    }

    void incrementRows() {
        ++this.rows;
    }

    public WorldScreenOptionGrid.OptionBuilder add(Text text, BooleanSupplier getter, Consumer<Boolean> setter) {
        WorldScreenOptionGrid.OptionBuilder optionBuilder = new WorldScreenOptionGrid.OptionBuilder(text, getter, setter, 44);
        this.options.add(optionBuilder);
        return optionBuilder;
    }

    public WorldScreenOptionGrid.Builder marginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public WorldScreenOptionGrid.Builder setRowSpacing(int rowSpacing) {
        this.rowSpacing = rowSpacing;
        return this;
    }

    public WorldScreenOptionGrid build() {
        GridWidget gridWidget = new GridWidget().setRowSpacing(this.rowSpacing);
        gridWidget.add(EmptyWidget.ofWidth(this.width - 44), 0, 0);
        gridWidget.add(EmptyWidget.ofWidth(44), 0, 1);
        ArrayList<WorldScreenOptionGrid.Option> list = new ArrayList<WorldScreenOptionGrid.Option>();
        this.rows = 0;
        for (WorldScreenOptionGrid.OptionBuilder optionBuilder : this.options) {
            list.add(optionBuilder.build(this, gridWidget, 0));
        }
        gridWidget.refreshPositions();
        WorldScreenOptionGrid worldScreenOptionGrid = new WorldScreenOptionGrid(list, gridWidget);
        worldScreenOptionGrid.refresh();
        return worldScreenOptionGrid;
    }

    public WorldScreenOptionGrid.Builder withTooltipBox(int maxInfoRows, boolean alwaysMaxHeight) {
        this.tooltipBoxDisplay = Optional.of(new WorldScreenOptionGrid.TooltipBoxDisplay(maxInfoRows, alwaysMaxHeight));
        return this;
    }
}
