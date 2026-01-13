/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.OrderedText;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static abstract class EditGameRulesScreen.AbstractRuleWidget
extends ElementListWidget.Entry<EditGameRulesScreen.AbstractRuleWidget> {
    final @Nullable List<OrderedText> description;

    public EditGameRulesScreen.AbstractRuleWidget(@Nullable List<OrderedText> description) {
        this.description = description;
    }
}
