/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.tag;

import java.util.function.Function;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.registry.tag.TagKey;

class ProvidedTagBuilder.2
implements ProvidedTagBuilder<U, T> {
    final /* synthetic */ ProvidedTagBuilder field_60484;
    final /* synthetic */ Function field_60485;

    ProvidedTagBuilder.2(ProvidedTagBuilder providedTagBuilder, ProvidedTagBuilder providedTagBuilder2, Function function) {
        this.field_60484 = providedTagBuilder2;
        this.field_60485 = function;
    }

    @Override
    public ProvidedTagBuilder<U, T> add(U value) {
        this.field_60484.add(this.field_60485.apply(value));
        return this;
    }

    @Override
    public ProvidedTagBuilder<U, T> addOptional(U value) {
        this.field_60484.add(this.field_60485.apply(value));
        return this;
    }

    @Override
    public ProvidedTagBuilder<U, T> addTag(TagKey<T> tag) {
        this.field_60484.addTag(tag);
        return this;
    }

    @Override
    public ProvidedTagBuilder<U, T> addOptionalTag(TagKey<T> tag) {
        this.field_60484.addOptionalTag(tag);
        return this;
    }
}
