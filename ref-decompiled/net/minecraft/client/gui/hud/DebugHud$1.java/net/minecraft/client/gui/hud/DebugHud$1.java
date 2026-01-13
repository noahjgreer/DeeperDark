/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class DebugHud.1
implements DebugHudLines {
    final /* synthetic */ List field_61533;
    final /* synthetic */ List field_61534;
    final /* synthetic */ List field_61535;
    final /* synthetic */ Map field_61536;

    DebugHud.1() {
        this.field_61533 = list;
        this.field_61534 = list2;
        this.field_61535 = list3;
        this.field_61536 = map;
    }

    @Override
    public void addPriorityLine(String line) {
        if (this.field_61533.size() > this.field_61534.size()) {
            this.field_61534.add(line);
        } else {
            this.field_61533.add(line);
        }
    }

    @Override
    public void addLine(String line) {
        this.field_61535.add(line);
    }

    @Override
    public void addLinesToSection(Identifier sectionId, Collection<String> lines) {
        this.field_61536.computeIfAbsent(sectionId, sectionLines -> new ArrayList()).addAll(lines);
    }

    @Override
    public void addLineToSection(Identifier sectionId, String line) {
        this.field_61536.computeIfAbsent(sectionId, sectionLines -> new ArrayList()).add(line);
    }
}
