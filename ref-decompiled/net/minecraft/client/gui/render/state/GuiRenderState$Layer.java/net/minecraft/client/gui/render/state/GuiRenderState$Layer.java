/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class GuiRenderState.Layer {
    public final @Nullable GuiRenderState.Layer parent;
    public @Nullable GuiRenderState.Layer up;
    public @Nullable List<SimpleGuiElementRenderState> simpleElementRenderStates;
    public @Nullable List<SimpleGuiElementRenderState> preparedTextElementRenderStates;
    public @Nullable List<ItemGuiElementRenderState> itemElementRenderStates;
    public @Nullable List<TextGuiElementRenderState> textElementRenderStates;
    public @Nullable List<SpecialGuiElementRenderState> specialElementRenderStates;

    GuiRenderState.Layer(@Nullable GuiRenderState.Layer parent) {
        this.parent = parent;
    }

    public void addItem(ItemGuiElementRenderState state) {
        if (this.itemElementRenderStates == null) {
            this.itemElementRenderStates = new ArrayList<ItemGuiElementRenderState>();
        }
        this.itemElementRenderStates.add(state);
    }

    public void addText(TextGuiElementRenderState state) {
        if (this.textElementRenderStates == null) {
            this.textElementRenderStates = new ArrayList<TextGuiElementRenderState>();
        }
        this.textElementRenderStates.add(state);
    }

    public void addSpecialElement(SpecialGuiElementRenderState state) {
        if (this.specialElementRenderStates == null) {
            this.specialElementRenderStates = new ArrayList<SpecialGuiElementRenderState>();
        }
        this.specialElementRenderStates.add(state);
    }

    public void addSimpleElement(SimpleGuiElementRenderState state) {
        if (this.simpleElementRenderStates == null) {
            this.simpleElementRenderStates = new ArrayList<SimpleGuiElementRenderState>();
        }
        this.simpleElementRenderStates.add(state);
    }

    public void addPreparedText(SimpleGuiElementRenderState state) {
        if (this.preparedTextElementRenderStates == null) {
            this.preparedTextElementRenderStates = new ArrayList<SimpleGuiElementRenderState>();
        }
        this.preparedTextElementRenderStates.add(state);
    }
}
