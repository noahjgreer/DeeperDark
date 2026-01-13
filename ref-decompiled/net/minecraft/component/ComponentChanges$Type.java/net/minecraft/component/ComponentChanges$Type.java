/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

record ComponentChanges.Type(ComponentType<?> type, boolean removed) {
    public static final Codec<ComponentChanges.Type> CODEC = Codec.STRING.flatXmap(id -> {
        Identifier identifier;
        ComponentType<?> componentType;
        boolean bl = id.startsWith(ComponentChanges.REMOVE_PREFIX);
        if (bl) {
            id = id.substring(ComponentChanges.REMOVE_PREFIX.length());
        }
        if ((componentType = Registries.DATA_COMPONENT_TYPE.get(identifier = Identifier.tryParse(id))) == null) {
            return DataResult.error(() -> "No component with type: '" + String.valueOf(identifier) + "'");
        }
        if (componentType.shouldSkipSerialization()) {
            return DataResult.error(() -> "'" + String.valueOf(identifier) + "' is not a persistent component");
        }
        return DataResult.success((Object)new ComponentChanges.Type(componentType, bl));
    }, type -> {
        ComponentType<?> componentType = type.type();
        Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(componentType);
        if (identifier == null) {
            return DataResult.error(() -> "Unregistered component: " + String.valueOf(componentType));
        }
        return DataResult.success((Object)(type.removed() ? ComponentChanges.REMOVE_PREFIX + String.valueOf(identifier) : identifier.toString()));
    });

    public Codec<?> getValueCodec() {
        return this.removed ? Codec.EMPTY.codec() : this.type.getCodecOrThrow();
    }
}
