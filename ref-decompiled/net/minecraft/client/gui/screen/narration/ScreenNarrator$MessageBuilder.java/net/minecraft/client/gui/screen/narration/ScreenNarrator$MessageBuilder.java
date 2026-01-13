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
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;

@Environment(value=EnvType.CLIENT)
class ScreenNarrator.MessageBuilder
implements NarrationMessageBuilder {
    private final int depth;

    ScreenNarrator.MessageBuilder(int depth) {
        this.depth = depth;
    }

    @Override
    public void put(NarrationPart part, Narration<?> narration) {
        ScreenNarrator.this.narrations.computeIfAbsent(new ScreenNarrator.PartIndex(part, this.depth), partIndex -> new ScreenNarrator.Message()).setNarration(ScreenNarrator.this.currentMessageIndex, narration);
    }

    @Override
    public NarrationMessageBuilder nextMessage() {
        return new ScreenNarrator.MessageBuilder(this.depth + 1);
    }
}
