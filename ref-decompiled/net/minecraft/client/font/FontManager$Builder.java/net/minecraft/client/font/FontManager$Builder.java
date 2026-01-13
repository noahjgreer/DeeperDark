/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontManager;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class FontManager.Builder
extends Record {
    private final FontManager.FontKey id;
    private final FontFilterType.FilterMap filter;
    final Either<CompletableFuture<Optional<Font>>, Identifier> result;

    FontManager.Builder(FontManager.FontKey id, FontFilterType.FilterMap filter, Either<CompletableFuture<Optional<Font>>, Identifier> result) {
        this.id = id;
        this.filter = filter;
        this.result = result;
    }

    public Optional<List<Font.FontFilterPair>> build(Function<Identifier, @Nullable List<Font.FontFilterPair>> fontRetriever) {
        return (Optional)this.result.map(future -> ((Optional)future.join()).map(font -> List.of(new Font.FontFilterPair((Font)font, this.filter))), referee -> {
            List list = (List)fontRetriever.apply((Identifier)referee);
            if (list == null) {
                LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", referee, (Object)this.id);
                return Optional.empty();
            }
            return Optional.of(list.stream().map(this::applyFilter).toList());
        });
    }

    private Font.FontFilterPair applyFilter(Font.FontFilterPair font) {
        return new Font.FontFilterPair(font.provider(), this.filter.apply(font.filter()));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontManager.Builder.class, "id;filter;result", "id", "filter", "result"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontManager.Builder.class, "id;filter;result", "id", "filter", "result"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontManager.Builder.class, "id;filter;result", "id", "filter", "result"}, this, object);
    }

    public FontManager.FontKey id() {
        return this.id;
    }

    public FontFilterType.FilterMap filter() {
        return this.filter;
    }

    public Either<CompletableFuture<Optional<Font>>, Identifier> result() {
        return this.result;
    }
}
