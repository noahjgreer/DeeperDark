/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.predicate.component;

import com.mojang.serialization.Codec;
import net.minecraft.predicate.component.ComponentPredicate;

public static final class ComponentPredicate.OfValue<T extends ComponentPredicate>
extends ComponentPredicate.TypeImpl<T> {
    public ComponentPredicate.OfValue(Codec<T> codec) {
        super(codec);
    }
}
