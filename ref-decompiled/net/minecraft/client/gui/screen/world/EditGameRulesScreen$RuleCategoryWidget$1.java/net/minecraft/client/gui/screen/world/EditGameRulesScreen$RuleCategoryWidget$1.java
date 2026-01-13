/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;

@Environment(value=EnvType.CLIENT)
class EditGameRulesScreen.RuleCategoryWidget.1
implements Selectable {
    EditGameRulesScreen.RuleCategoryWidget.1() {
    }

    @Override
    public Selectable.SelectionType getType() {
        return Selectable.SelectionType.HOVERED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, RuleCategoryWidget.this.name);
    }
}
