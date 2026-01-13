/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList
extends AlwaysSelectedEntryListWidget<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
    public RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList() {
        this(Collections.emptyList());
    }

    public RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(Iterable<WorldTemplate> templates) {
        super(MinecraftClient.getInstance(), RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.layout.getContentHeight(), RealmsSelectWorldTemplateScreen.this.layout.getHeaderHeight(), 46);
        templates.forEach(this::addEntry);
    }

    public void addEntry(WorldTemplate template) {
        this.addEntry(new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry(RealmsSelectWorldTemplateScreen.this, template));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
            ConfirmLinkScreen.open((Screen)RealmsSelectWorldTemplateScreen.this, RealmsSelectWorldTemplateScreen.this.currentLink);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry worldTemplateObjectSelectionListEntry) {
        super.setSelected(worldTemplateObjectSelectionListEntry);
        RealmsSelectWorldTemplateScreen.this.selectedTemplate = worldTemplateObjectSelectionListEntry == null ? null : worldTemplateObjectSelectionListEntry.mTemplate;
        RealmsSelectWorldTemplateScreen.this.updateButtonStates();
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    public boolean isEmpty() {
        return this.getEntryCount() == 0;
    }

    public List<WorldTemplate> getValues() {
        return this.children().stream().map(child -> child.mTemplate).collect(Collectors.toList());
    }
}
