/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.MultipartModelCondition
 *  net.minecraft.client.render.model.json.MultipartModelConditionBuilder
 *  net.minecraft.client.render.model.json.SimpleMultipartModelSelector
 *  net.minecraft.client.render.model.json.SimpleMultipartModelSelector$Term
 *  net.minecraft.client.render.model.json.SimpleMultipartModelSelector$Terms
 *  net.minecraft.state.property.Property
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public class MultipartModelConditionBuilder {
    private final ImmutableMap.Builder<String, SimpleMultipartModelSelector.Terms> values = ImmutableMap.builder();

    private <T extends Comparable<T>> void putTerms(Property<T> property, SimpleMultipartModelSelector.Terms terms) {
        this.values.put((Object)property.getName(), (Object)terms);
    }

    public final <T extends Comparable<T>> MultipartModelConditionBuilder put(Property<T> property, T value) {
        this.putTerms(property, new SimpleMultipartModelSelector.Terms(List.of(new SimpleMultipartModelSelector.Term(property.name(value), false))));
        return this;
    }

    @SafeVarargs
    public final <T extends Comparable<T>> MultipartModelConditionBuilder put(Property<T> property, T value, T ... values) {
        List<SimpleMultipartModelSelector.Term> list = Stream.concat(Stream.of(value), Stream.of(values)).map(arg_0 -> property.name(arg_0)).sorted().distinct().map(valuex -> new SimpleMultipartModelSelector.Term(valuex, false)).toList();
        this.putTerms(property, new SimpleMultipartModelSelector.Terms(list));
        return this;
    }

    public final <T extends Comparable<T>> MultipartModelConditionBuilder replace(Property<T> property, T value) {
        this.putTerms(property, new SimpleMultipartModelSelector.Terms(List.of(new SimpleMultipartModelSelector.Term(property.name(value), true))));
        return this;
    }

    public MultipartModelCondition build() {
        return new SimpleMultipartModelSelector((Map)this.values.buildOrThrow());
    }
}

