/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagPacketSerializer;

public static final class RegistryLoader.ElementsAndTags
extends Record {
    final List<SerializableRegistries.SerializedRegistryEntry> elements;
    final TagPacketSerializer.Serialized tags;

    public RegistryLoader.ElementsAndTags(List<SerializableRegistries.SerializedRegistryEntry> elements, TagPacketSerializer.Serialized tags) {
        this.elements = elements;
        this.tags = tags;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryLoader.ElementsAndTags.class, "elements;tags", "elements", "tags"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryLoader.ElementsAndTags.class, "elements;tags", "elements", "tags"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryLoader.ElementsAndTags.class, "elements;tags", "elements", "tags"}, this, object);
    }

    public List<SerializableRegistries.SerializedRegistryEntry> elements() {
        return this.elements;
    }

    public TagPacketSerializer.Serialized tags() {
        return this.tags;
    }
}
