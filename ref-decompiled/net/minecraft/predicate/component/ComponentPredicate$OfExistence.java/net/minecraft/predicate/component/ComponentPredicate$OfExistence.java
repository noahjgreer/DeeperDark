/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.predicate.component;

import com.mojang.serialization.MapCodec;
import net.minecraft.component.ComponentType;
import net.minecraft.predicate.component.ComponentExistencePredicate;
import net.minecraft.predicate.component.ComponentPredicate;

public static final class ComponentPredicate.OfExistence
extends ComponentPredicate.TypeImpl<ComponentExistencePredicate> {
    private final ComponentExistencePredicate predicate;

    public ComponentPredicate.OfExistence(ComponentExistencePredicate predicate) {
        super(MapCodec.unitCodec((Object)predicate));
        this.predicate = predicate;
    }

    public ComponentExistencePredicate getPredicate() {
        return this.predicate;
    }

    public ComponentType<?> getComponentType() {
        return this.predicate.type();
    }

    public static ComponentPredicate.OfExistence toPredicateType(ComponentType<?> type) {
        return new ComponentPredicate.OfExistence(new ComponentExistencePredicate(type));
    }
}
