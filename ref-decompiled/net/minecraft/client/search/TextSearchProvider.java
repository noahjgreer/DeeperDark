/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.search.IdentifierSearchProvider
 *  net.minecraft.client.search.IdentifierSearchableIterator
 *  net.minecraft.client.search.SearchProvider
 *  net.minecraft.client.search.TextSearchProvider
 *  net.minecraft.client.search.TextSearchableIterator
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.IdentifierSearchProvider;
import net.minecraft.client.search.IdentifierSearchableIterator;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.TextSearchableIterator;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TextSearchProvider<T>
extends IdentifierSearchProvider<T> {
    private final SearchProvider<T> textSearcher;

    public TextSearchProvider(Function<T, Stream<String>> textsGetter, Function<T, Stream<Identifier>> identifiersGetter, List<T> values) {
        super(identifiersGetter, values);
        this.textSearcher = SearchProvider.plainText(values, textsGetter);
    }

    protected List<T> search(String text) {
        return this.textSearcher.findAll(text);
    }

    protected List<T> search(String namespace, String path) {
        List list = this.idSearcher.searchNamespace(namespace);
        List list2 = this.idSearcher.searchPath(path);
        List list3 = this.textSearcher.findAll(path);
        TextSearchableIterator iterator = new TextSearchableIterator(list2.iterator(), list3.iterator(), this.lastIndexComparator);
        return ImmutableList.copyOf((Iterator)new IdentifierSearchableIterator(list.iterator(), (Iterator)iterator, this.lastIndexComparator));
    }
}

