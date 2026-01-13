/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.search.IdentifierSearchProvider
 *  net.minecraft.client.search.IdentifierSearchableIterator
 *  net.minecraft.client.search.IdentifierSearcher
 *  net.minecraft.client.search.SearchProvider
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 */
package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.IdentifierSearchableIterator;
import net.minecraft.client.search.IdentifierSearcher;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class IdentifierSearchProvider<T>
implements SearchProvider<T> {
    protected final Comparator<T> lastIndexComparator;
    protected final IdentifierSearcher<T> idSearcher;

    public IdentifierSearchProvider(Function<T, Stream<Identifier>> identifiersGetter, List<T> values) {
        ToIntFunction toIntFunction = Util.lastIndexGetter(values);
        this.lastIndexComparator = Comparator.comparingInt(toIntFunction);
        this.idSearcher = IdentifierSearcher.of(values, identifiersGetter);
    }

    public List<T> findAll(String text) {
        int i = text.indexOf(58);
        if (i == -1) {
            return this.search(text);
        }
        return this.search(text.substring(0, i).trim(), text.substring(i + 1).trim());
    }

    protected List<T> search(String text) {
        return this.idSearcher.searchPath(text);
    }

    protected List<T> search(String namespace, String path) {
        List list = this.idSearcher.searchNamespace(namespace);
        List list2 = this.idSearcher.searchPath(path);
        return ImmutableList.copyOf((Iterator)new IdentifierSearchableIterator(list.iterator(), list2.iterator(), this.lastIndexComparator));
    }
}

