/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.search.IdentifierSearcher
 *  net.minecraft.client.search.SuffixArray
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.search;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public interface IdentifierSearcher<T> {
    public static <T> IdentifierSearcher<T> of() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public static <T> IdentifierSearcher<T> of(List<T> values, Function<T, Stream<Identifier>> identifiersGetter) {
        if (values.isEmpty()) {
            return IdentifierSearcher.of();
        }
        SuffixArray suffixArray = new SuffixArray();
        SuffixArray suffixArray2 = new SuffixArray();
        for (Object object : values) {
            identifiersGetter.apply(object).forEach(id -> {
                suffixArray.add(object, id.getNamespace().toLowerCase(Locale.ROOT));
                suffixArray2.add(object, id.getPath().toLowerCase(Locale.ROOT));
            });
        }
        suffixArray.build();
        suffixArray2.build();
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public List<T> searchNamespace(String var1);

    public List<T> searchPath(String var1);
}

