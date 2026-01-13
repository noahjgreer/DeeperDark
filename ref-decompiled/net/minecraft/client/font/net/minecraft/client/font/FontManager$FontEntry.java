/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class FontManager.FontEntry
extends Record
implements DependencyTracker.Dependencies<Identifier> {
    final Identifier fontId;
    private final List<FontManager.Builder> builders;
    private final Set<Identifier> dependencies;

    public FontManager.FontEntry(Identifier fontId) {
        this(fontId, new ArrayList<FontManager.Builder>(), new HashSet<Identifier>());
    }

    private FontManager.FontEntry(Identifier fontId, List<FontManager.Builder> builders, Set<Identifier> dependencies) {
        this.fontId = fontId;
        this.builders = builders;
        this.dependencies = dependencies;
    }

    public void addReferenceBuilder(FontManager.FontKey key, FontFilterType.FilterMap filters, FontLoader.Reference reference) {
        this.builders.add(new FontManager.Builder(key, filters, (Either<CompletableFuture<Optional<Font>>, Identifier>)Either.right((Object)reference.id())));
        this.dependencies.add(reference.id());
    }

    public void addBuilder(FontManager.FontKey key, FontFilterType.FilterMap filters, CompletableFuture<Optional<Font>> fontFuture) {
        this.builders.add(new FontManager.Builder(key, filters, (Either<CompletableFuture<Optional<Font>>, Identifier>)Either.left(fontFuture)));
    }

    private Stream<CompletableFuture<Optional<Font>>> getImmediateProviders() {
        return this.builders.stream().flatMap(builder -> builder.result.left().stream());
    }

    public Optional<List<Font.FontFilterPair>> getRequiredFontProviders(Function<Identifier, List<Font.FontFilterPair>> fontRetriever) {
        ArrayList list = new ArrayList();
        for (FontManager.Builder builder : this.builders) {
            Optional<List<Font.FontFilterPair>> optional = builder.build(fontRetriever);
            if (optional.isPresent()) {
                list.addAll(optional.get());
                continue;
            }
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public void forDependencies(Consumer<Identifier> callback) {
        this.dependencies.forEach(callback);
    }

    @Override
    public void forOptionalDependencies(Consumer<Identifier> callback) {
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontManager.FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontManager.FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontManager.FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this, object);
    }

    public Identifier fontId() {
        return this.fontId;
    }

    public List<FontManager.Builder> builders() {
        return this.builders;
    }

    public Set<Identifier> dependencies() {
        return this.dependencies;
    }
}
