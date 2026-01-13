/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.predicate.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.predicate.component.ComponentPredicate;

public record ComponentPredicate.Typed<T extends ComponentPredicate>(ComponentPredicate.Type<T> type, T predicate) {
    static <T extends ComponentPredicate> MapCodec<ComponentPredicate.Typed<T>> getCodec(ComponentPredicate.Type<T> type, Codec<T> valueCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)valueCodec.fieldOf("value").forGetter(ComponentPredicate.Typed::predicate)).apply((Applicative)instance, predicate -> new ComponentPredicate.Typed<ComponentPredicate>(type, (ComponentPredicate)predicate)));
    }

    private static <T extends ComponentPredicate> ComponentPredicate.Typed<T> fromEntry(Map.Entry<ComponentPredicate.Type<?>, T> entry) {
        return new ComponentPredicate.Typed<ComponentPredicate>(entry.getKey(), (ComponentPredicate)entry.getValue());
    }
}
