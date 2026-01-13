/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.DebugOptionsScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class DebugOptionsScreen.Category
extends DebugOptionsScreen.AbstractEntry {
    final Text label;

    public DebugOptionsScreen.Category(Text label) {
        this.label = label;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawCenteredTextWithShadow(((DebugOptionsScreen)DebugOptionsScreen.this).client.textRenderer, this.label, this.getContentX() + this.getContentWidth() / 2, this.getContentY() + 5, -1);
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of();
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of((Object)new Selectable(){

            @Override
            public Selectable.SelectionType getType() {
                return Selectable.SelectionType.HOVERED;
            }

            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
                builder.put(NarrationPart.TITLE, Category.this.label);
            }
        });
    }

    @Override
    public void init() {
    }
}
