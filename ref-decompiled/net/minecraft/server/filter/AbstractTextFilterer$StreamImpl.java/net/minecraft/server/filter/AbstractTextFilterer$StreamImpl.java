/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.util.Util;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;

protected class AbstractTextFilterer.StreamImpl
implements TextStream {
    protected final GameProfile gameProfile;
    protected final Executor executor;

    protected AbstractTextFilterer.StreamImpl(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        SimpleConsecutiveExecutor simpleConsecutiveExecutor = new SimpleConsecutiveExecutor(AbstractTextFilterer.this.threadPool, "chat stream for " + gameProfile.name());
        this.executor = simpleConsecutiveExecutor::send;
    }

    @Override
    public CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
        List list = (List)texts.stream().map(text -> AbstractTextFilterer.this.filter(this.gameProfile, (String)text, AbstractTextFilterer.this.hashIgnorer, this.executor)).collect(ImmutableList.toImmutableList());
        return Util.combine(list).exceptionally(throwable -> ImmutableList.of());
    }

    @Override
    public CompletableFuture<FilteredMessage> filterText(String text) {
        return AbstractTextFilterer.this.filter(this.gameProfile, text, AbstractTextFilterer.this.hashIgnorer, this.executor);
    }
}
