/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static final class InGameHud.SidebarEntry
extends Record {
    final Text name;
    final Text score;
    final int scoreWidth;

    InGameHud.SidebarEntry(Text name, Text score, int scoreWidth) {
        this.name = name;
        this.score = score;
        this.scoreWidth = scoreWidth;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{InGameHud.SidebarEntry.class, "name;score;scoreWidth", "name", "score", "scoreWidth"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{InGameHud.SidebarEntry.class, "name;score;scoreWidth", "name", "score", "scoreWidth"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{InGameHud.SidebarEntry.class, "name;score;scoreWidth", "name", "score", "scoreWidth"}, this, object);
    }

    public Text name() {
        return this.name;
    }

    public Text score() {
        return this.score;
    }

    public int scoreWidth() {
        return this.scoreWidth;
    }
}
