/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.narration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.Narration;

@Environment(value=EnvType.CLIENT)
static class ScreenNarrator.Message {
    Narration<?> narration = Narration.EMPTY;
    int index = -1;
    boolean used;

    ScreenNarrator.Message() {
    }

    public ScreenNarrator.Message setNarration(int index, Narration<?> narration) {
        if (!this.narration.equals(narration)) {
            this.narration = narration;
            this.used = false;
        } else if (this.index + 1 != index) {
            this.used = false;
        }
        this.index = index;
        return this;
    }
}
