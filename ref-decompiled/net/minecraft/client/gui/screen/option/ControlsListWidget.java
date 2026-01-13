/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.option.ControlsListWidget
 *  net.minecraft.client.gui.screen.option.ControlsListWidget$CategoryEntry
 *  net.minecraft.client.gui.screen.option.ControlsListWidget$Entry
 *  net.minecraft.client.gui.screen.option.ControlsListWidget$KeyBindingEntry
 *  net.minecraft.client.gui.screen.option.KeybindsScreen
 *  net.minecraft.client.gui.widget.ElementListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.option.KeyBinding$Category
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

@Environment(value=EnvType.CLIENT)
public class ControlsListWidget
extends ElementListWidget<Entry> {
    private static final int field_49533 = 20;
    final KeybindsScreen parent;
    private int maxKeyNameLength;

    public ControlsListWidget(KeybindsScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.layout.getContentHeight(), parent.layout.getHeaderHeight(), 20);
        this.parent = parent;
        Object[] keyBindings = (KeyBinding[])ArrayUtils.clone((Object[])client.options.allKeys);
        Arrays.sort(keyBindings);
        KeyBinding.Category category = null;
        for (Object keyBinding : keyBindings) {
            MutableText text;
            int i;
            KeyBinding.Category category2 = keyBinding.getCategory();
            if (category2 != category) {
                category = category2;
                this.addEntry((EntryListWidget.Entry)new CategoryEntry(this, category2));
            }
            if ((i = client.textRenderer.getWidth((StringVisitable)(text = Text.translatable((String)keyBinding.getId())))) > this.maxKeyNameLength) {
                this.maxKeyNameLength = i;
            }
            this.addEntry((EntryListWidget.Entry)new KeyBindingEntry(this, (KeyBinding)keyBinding, (Text)text));
        }
    }

    public void update() {
        KeyBinding.updateKeysByCode();
        this.updateChildren();
    }

    public void updateChildren() {
        this.children().forEach(Entry::update);
    }

    public int getRowWidth() {
        return 340;
    }

    static /* synthetic */ MinecraftClient method_36885(ControlsListWidget controlsListWidget) {
        return controlsListWidget.client;
    }

    static /* synthetic */ int method_73440(ControlsListWidget controlsListWidget) {
        return controlsListWidget.width;
    }

    static /* synthetic */ int method_75375(ControlsListWidget controlsListWidget) {
        return controlsListWidget.getScrollbarX();
    }

    static /* synthetic */ MinecraftClient method_75376(ControlsListWidget controlsListWidget) {
        return controlsListWidget.client;
    }

    static /* synthetic */ MinecraftClient method_73443(ControlsListWidget controlsListWidget) {
        return controlsListWidget.client;
    }

    static /* synthetic */ MinecraftClient method_20115(ControlsListWidget controlsListWidget) {
        return controlsListWidget.client;
    }
}

