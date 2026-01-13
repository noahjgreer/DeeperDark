/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.Maps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;

@Environment(value=EnvType.CLIENT)
public class ScreenNarrator {
    int currentMessageIndex;
    final Map<PartIndex, Message> narrations = Maps.newTreeMap(Comparator.comparing(partIndex -> partIndex.part).thenComparing(partIndex -> partIndex.depth));

    public void buildNarrations(Consumer<NarrationMessageBuilder> builderConsumer) {
        ++this.currentMessageIndex;
        builderConsumer.accept(new MessageBuilder(0));
    }

    public String buildNarratorText(boolean includeUnchanged) {
        final StringBuilder stringBuilder = new StringBuilder();
        Consumer<String> consumer = new Consumer<String>(this){
            private boolean first = true;

            @Override
            public void accept(String string) {
                if (!this.first) {
                    stringBuilder.append(". ");
                }
                this.first = false;
                stringBuilder.append(string);
            }

            @Override
            public /* synthetic */ void accept(Object sentence) {
                this.accept((String)sentence);
            }
        };
        this.narrations.forEach((partIndex, message) -> {
            if (message.index == this.currentMessageIndex && (includeUnchanged || !message.used)) {
                message.narration.forEachSentence(consumer);
                message.used = true;
            }
        });
        return stringBuilder.toString();
    }

    @Environment(value=EnvType.CLIENT)
    class MessageBuilder
    implements NarrationMessageBuilder {
        private final int depth;

        MessageBuilder(int depth) {
            this.depth = depth;
        }

        @Override
        public void put(NarrationPart part, Narration<?> narration) {
            ScreenNarrator.this.narrations.computeIfAbsent(new PartIndex(part, this.depth), partIndex -> new Message()).setNarration(ScreenNarrator.this.currentMessageIndex, narration);
        }

        @Override
        public NarrationMessageBuilder nextMessage() {
            return new MessageBuilder(this.depth + 1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Message {
        Narration<?> narration = Narration.EMPTY;
        int index = -1;
        boolean used;

        Message() {
        }

        public Message setNarration(int index, Narration<?> narration) {
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

    @Environment(value=EnvType.CLIENT)
    static final class PartIndex
    extends Record {
        final NarrationPart part;
        final int depth;

        PartIndex(NarrationPart part, int depth) {
            this.part = part;
            this.depth = depth;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PartIndex.class, "type;depth", "part", "depth"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PartIndex.class, "type;depth", "part", "depth"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PartIndex.class, "type;depth", "part", "depth"}, this, object);
        }

        public NarrationPart part() {
            return this.part;
        }

        public int depth() {
            return this.depth;
        }
    }
}
