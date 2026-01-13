/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.option.KeyBinding;

@Environment(value=EnvType.CLIENT)
public class ControlsListWidget.CategoryEntry
extends ControlsListWidget.Entry {
    private final NarratedMultilineTextWidget field_62179;

    public ControlsListWidget.CategoryEntry(KeyBinding.Category category) {
        this.field_62179 = NarratedMultilineTextWidget.builder(category.getLabel(), ((ControlsListWidget)ControlsListWidget.this).client.textRenderer).alwaysShowBorders(false).backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering.ON_FOCUS).build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.field_62179.setPosition(ControlsListWidget.this.width / 2 - this.field_62179.getWidth() / 2, this.getContentBottomEnd() - this.field_62179.getHeight());
        this.field_62179.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.field_62179);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of(this.field_62179);
    }

    @Override
    protected void update() {
    }
}
