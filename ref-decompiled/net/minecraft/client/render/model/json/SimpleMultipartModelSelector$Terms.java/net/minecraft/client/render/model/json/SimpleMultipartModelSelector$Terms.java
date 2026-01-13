/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public record SimpleMultipartModelSelector.Terms(List<SimpleMultipartModelSelector.Term> entries) {
    private static final char DELIMITER = '|';
    private static final Joiner JOINER = Joiner.on((char)'|');
    private static final Splitter SPLITTER = Splitter.on((char)'|');
    private static final Codec<String> CODEC = Codec.either((Codec)Codec.INT, (Codec)Codec.BOOL).flatComapMap(either -> (String)either.map(String::valueOf, String::valueOf), v -> DataResult.error(() -> "This codec can't be used for encoding"));
    public static final Codec<SimpleMultipartModelSelector.Terms> VALUE_CODEC = Codec.withAlternative((Codec)Codec.STRING, CODEC).comapFlatMap(SimpleMultipartModelSelector.Terms::tryParse, SimpleMultipartModelSelector.Terms::toString);

    public SimpleMultipartModelSelector.Terms {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Empty value for property");
        }
    }

    public static DataResult<SimpleMultipartModelSelector.Terms> tryParse(String terms) {
        List<SimpleMultipartModelSelector.Term> list = SPLITTER.splitToStream((CharSequence)terms).map(SimpleMultipartModelSelector.Term::parse).toList();
        if (list.isEmpty()) {
            return DataResult.error(() -> "Empty value for property");
        }
        for (SimpleMultipartModelSelector.Term term : list) {
            if (!term.value.isEmpty()) continue;
            return DataResult.error(() -> "Empty term in value '" + terms + "'");
        }
        return DataResult.success((Object)new SimpleMultipartModelSelector.Terms(list));
    }

    @Override
    public String toString() {
        return JOINER.join(this.entries);
    }

    public <O, S extends State<O, S>, T extends Comparable<T>> Predicate<S> instantiate(O object, Property<T> property) {
        ArrayList list2;
        boolean bl;
        Predicate predicate = Util.anyOf(Lists.transform(this.entries, term -> this.instantiate(object, property, (SimpleMultipartModelSelector.Term)term)));
        ArrayList list = new ArrayList(property.getValues());
        int i = list.size();
        list.removeIf(predicate.negate());
        int j = list.size();
        if (j == 0) {
            LOGGER.warn("Condition {} for property {} on {} is always false", new Object[]{this, property.getName(), object});
            return state -> false;
        }
        int k = i - j;
        if (k == 0) {
            LOGGER.warn("Condition {} for property {} on {} is always true", new Object[]{this, property.getName(), object});
            return state -> true;
        }
        if (j <= k) {
            bl = false;
            list2 = list;
        } else {
            bl = true;
            ArrayList<T> list3 = new ArrayList<T>(property.getValues());
            list3.removeIf(predicate);
            list2 = list3;
        }
        if (list2.size() == 1) {
            Comparable comparable = (Comparable)list2.getFirst();
            return state -> {
                Object comparable2 = state.get(property);
                return comparable.equals(comparable2) ^ bl;
            };
        }
        return state -> {
            Object comparable = state.get(property);
            return list2.contains(comparable) ^ bl;
        };
    }

    private <T extends Comparable<T>> T parseValue(Object object, Property<T> property, String value) {
        Optional<T> optional = property.parse(value);
        if (optional.isEmpty()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", value, property, object, this));
        }
        return (T)((Comparable)optional.get());
    }

    private <T extends Comparable<T>> Predicate<T> instantiate(Object object, Property<T> property, SimpleMultipartModelSelector.Term term) {
        Object comparable = this.parseValue(object, property, term.value);
        if (term.negated) {
            return value -> !value.equals(comparable);
        }
        return value -> value.equals(comparable);
    }
}
