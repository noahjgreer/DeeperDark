/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.ScreenNarrator
 *  net.minecraft.client.gui.screen.narration.ScreenNarrator$1
 *  net.minecraft.client.gui.screen.narration.ScreenNarrator$Message
 *  net.minecraft.client.gui.screen.narration.ScreenNarrator$MessageBuilder
 *  net.minecraft.client.gui.screen.narration.ScreenNarrator$PartIndex
 */
package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;

@Environment(value=EnvType.CLIENT)
public class ScreenNarrator {
    int currentMessageIndex;
    final Map<PartIndex, Message> narrations = Maps.newTreeMap(Comparator.comparing(partIndex -> partIndex.part).thenComparing(partIndex -> partIndex.depth));

    public void buildNarrations(Consumer<NarrationMessageBuilder> builderConsumer) {
        ++this.currentMessageIndex;
        builderConsumer.accept((NarrationMessageBuilder)new MessageBuilder(this, 0));
    }

    public String buildNarratorText(boolean includeUnchanged) {
        StringBuilder stringBuilder = new StringBuilder();
        1 consumer = new /* Unavailable Anonymous Inner Class!! */;
        this.narrations.forEach((arg_0, arg_1) -> this.method_37046(includeUnchanged, (Consumer)consumer, arg_0, arg_1));
        return stringBuilder.toString();
    }

    private /* synthetic */ void method_37046(boolean bl, Consumer consumer, PartIndex partIndex, Message message) {
        if (message.index == this.currentMessageIndex && (bl || !message.used)) {
            message.narration.forEachSentence(consumer);
            message.used = true;
        }
    }
}

