/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;

public static final class RawShapedRecipe.Data
extends Record {
    final Map<Character, Ingredient> key;
    final List<String> pattern;
    private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
        if (pattern.size() > 3) {
            return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
        }
        if (pattern.isEmpty()) {
            return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
        }
        int i = ((String)pattern.getFirst()).length();
        for (String string : pattern) {
            if (string.length() > 3) {
                return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
            }
            if (i == string.length()) continue;
            return DataResult.error(() -> "Invalid pattern: each row must be the same width");
        }
        return DataResult.success((Object)pattern);
    }, Function.identity());
    private static final Codec<Character> KEY_ENTRY_CODEC = Codec.STRING.comapFlatMap(keyEntry -> {
        if (keyEntry.length() != 1) {
            return DataResult.error(() -> "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).");
        }
        if (" ".equals(keyEntry)) {
            return DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.");
        }
        return DataResult.success((Object)Character.valueOf(keyEntry.charAt(0)));
    }, String::valueOf);
    public static final MapCodec<RawShapedRecipe.Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.strictUnboundedMap(KEY_ENTRY_CODEC, Ingredient.CODEC).fieldOf("key").forGetter(data -> data.key), (App)PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.pattern)).apply((Applicative)instance, RawShapedRecipe.Data::new));

    public RawShapedRecipe.Data(Map<Character, Ingredient> key, List<String> pattern) {
        this.key = key;
        this.pattern = pattern;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RawShapedRecipe.Data.class, "key;pattern", "key", "pattern"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RawShapedRecipe.Data.class, "key;pattern", "key", "pattern"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RawShapedRecipe.Data.class, "key;pattern", "key", "pattern"}, this, object);
    }

    public Map<Character, Ingredient> key() {
        return this.key;
    }

    public List<String> pattern() {
        return this.pattern;
    }
}
