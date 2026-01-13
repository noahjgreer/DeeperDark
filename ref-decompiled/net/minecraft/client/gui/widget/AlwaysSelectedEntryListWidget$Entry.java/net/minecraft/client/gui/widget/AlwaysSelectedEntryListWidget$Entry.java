/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static abstract class AlwaysSelectedEntryListWidget.Entry<E extends AlwaysSelectedEntryListWidget.Entry<E>>
extends EntryListWidget.Entry<E>
implements Narratable {
    public abstract Text getNarration();

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return true;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarration());
    }
}
