/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 */
package net.minecraft.state;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.state.property.Property;

public static interface StateManager.Factory<O, S> {
    public S create(O var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
}
