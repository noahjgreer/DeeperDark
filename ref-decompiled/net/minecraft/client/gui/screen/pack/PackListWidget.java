/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.pack.PackListWidget
 *  net.minecraft.client.gui.screen.pack.PackListWidget$Entry
 *  net.minecraft.client.gui.screen.pack.PackListWidget$HeaderEntry
 *  net.minecraft.client.gui.screen.pack.PackListWidget$ResourcePackEntry
 *  net.minecraft.client.gui.screen.pack.PackScreen
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$AbstractPack
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$Pack
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.pack;

import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PackListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    static final Identifier SELECT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"transferable_list/select_highlighted");
    static final Identifier SELECT_TEXTURE = Identifier.ofVanilla((String)"transferable_list/select");
    static final Identifier UNSELECT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"transferable_list/unselect_highlighted");
    static final Identifier UNSELECT_TEXTURE = Identifier.ofVanilla((String)"transferable_list/unselect");
    static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"transferable_list/move_up_highlighted");
    static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla((String)"transferable_list/move_up");
    static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"transferable_list/move_down_highlighted");
    static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla((String)"transferable_list/move_down");
    static final Text INCOMPATIBLE = Text.translatable((String)"pack.incompatible");
    static final Text INCOMPATIBLE_CONFIRM = Text.translatable((String)"pack.incompatible.confirm.title");
    private static final int field_62180 = 2;
    private final Text title;
    final PackScreen screen;

    public PackListWidget(MinecraftClient client, PackScreen screen, int width, int height, Text title) {
        super(client, width, height, 33, 36);
        this.screen = screen;
        this.title = title;
        this.centerListVertically = false;
    }

    public int getRowWidth() {
        return this.width - 4;
    }

    protected int getScrollbarX() {
        return this.getRight() - 6;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.getSelectedOrNull() != null) {
            return ((Entry)this.getSelectedOrNull()).keyPressed(input);
        }
        return super.keyPressed(input);
    }

    public void set(Stream<ResourcePackOrganizer.Pack> packs, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ResourcePackOrganizer.AbstractPack focused) {
        this.clearEntries();
        MutableText text = Text.empty().append(this.title).formatted(new Formatting[]{Formatting.UNDERLINE, Formatting.BOLD});
        HeaderEntry headerEntry = new HeaderEntry(this, this.client.textRenderer, (Text)text);
        Objects.requireNonNull(this.client.textRenderer);
        this.addEntry((EntryListWidget.Entry)headerEntry, (int)(9.0f * 1.5f));
        this.setSelected(null);
        packs.forEach(pack -> {
            ResourcePackEntry resourcePackEntry = new ResourcePackEntry(this, this.client, this, pack);
            this.addEntry((EntryListWidget.Entry)resourcePackEntry);
            if (focused != null && focused.getName().equals(pack.getName())) {
                this.screen.setFocused((Element)this);
                this.setFocused((Element)resourcePackEntry);
            }
        });
        this.refreshScroll();
    }

    static /* synthetic */ boolean method_58491(PackListWidget packListWidget) {
        return packListWidget.overflows();
    }

    static /* synthetic */ void method_76284(PackListWidget packListWidget, DrawContext drawContext) {
        packListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76286(PackListWidget packListWidget, DrawContext drawContext) {
        packListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76288(PackListWidget packListWidget, DrawContext drawContext) {
        packListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76289(PackListWidget packListWidget, DrawContext drawContext) {
        packListWidget.setCursor(drawContext);
    }

    static /* synthetic */ boolean method_76285(PackListWidget packListWidget) {
        return packListWidget.overflows();
    }

    static /* synthetic */ boolean method_76287(PackListWidget packListWidget) {
        return packListWidget.overflows();
    }
}

