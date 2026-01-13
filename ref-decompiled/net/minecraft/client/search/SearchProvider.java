/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.search.SearchProvider
 *  net.minecraft.client.search.SuffixArray
 */
package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SuffixArray;

/*
 * Exception performing whole class analysis ignored.
 */
@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface SearchProvider<T> {
    public static <T> SearchProvider<T> empty() {
        return string -> List.of();
    }

    public static <T> SearchProvider<T> plainText(List<T> list, Function<T, Stream<String>> function) {
        if (list.isEmpty()) {
            return SearchProvider.empty();
        }
        SuffixArray suffixArray = new SuffixArray();
        for (Object object : list) {
            function.apply(object).forEach(string -> suffixArray.add(object, string.toLowerCase(Locale.ROOT)));
        }
        suffixArray.build();
        return arg_0 -> ((SuffixArray)suffixArray).findAll(arg_0);
    }

    public List<T> findAll(String var1);
}

