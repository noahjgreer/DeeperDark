/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;

class TextStream.1
implements TextStream {
    TextStream.1() {
    }

    @Override
    public CompletableFuture<FilteredMessage> filterText(String text) {
        return CompletableFuture.completedFuture(FilteredMessage.permitted(text));
    }

    @Override
    public CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
        return CompletableFuture.completedFuture((List)texts.stream().map(FilteredMessage::permitted).collect(ImmutableList.toImmutableList()));
    }
}
