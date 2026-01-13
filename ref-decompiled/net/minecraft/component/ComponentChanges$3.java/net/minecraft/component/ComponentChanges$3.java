/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMaps
 */
package net.minecraft.component;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Optional;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

static class ComponentChanges.3
implements PacketCodec<RegistryByteBuf, ComponentChanges> {
    final /* synthetic */ ComponentChanges.PacketCodecFunction field_58173;

    ComponentChanges.3(ComponentChanges.PacketCodecFunction packetCodecFunction) {
        this.field_58173 = packetCodecFunction;
    }

    @Override
    public ComponentChanges decode(RegistryByteBuf registryByteBuf) {
        ComponentType componentType;
        int l;
        int i = registryByteBuf.readVarInt();
        int j = registryByteBuf.readVarInt();
        if (i == 0 && j == 0) {
            return EMPTY;
        }
        int k = i + j;
        Reference2ObjectArrayMap reference2ObjectMap = new Reference2ObjectArrayMap(Math.min(k, 65536));
        for (l = 0; l < i; ++l) {
            componentType = (ComponentType)ComponentType.PACKET_CODEC.decode(registryByteBuf);
            Object object = this.field_58173.apply(componentType).decode(registryByteBuf);
            reference2ObjectMap.put((Object)componentType, Optional.of(object));
        }
        for (l = 0; l < j; ++l) {
            componentType = (ComponentType)ComponentType.PACKET_CODEC.decode(registryByteBuf);
            reference2ObjectMap.put((Object)componentType, Optional.empty());
        }
        return new ComponentChanges((Reference2ObjectMap<ComponentType<?>, Optional<?>>)reference2ObjectMap);
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, ComponentChanges componentChanges) {
        if (componentChanges.isEmpty()) {
            registryByteBuf.writeVarInt(0);
            registryByteBuf.writeVarInt(0);
            return;
        }
        int i = 0;
        int j = 0;
        for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(componentChanges.changedComponents)) {
            if (((Optional)entry.getValue()).isPresent()) {
                ++i;
                continue;
            }
            ++j;
        }
        registryByteBuf.writeVarInt(i);
        registryByteBuf.writeVarInt(j);
        for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(componentChanges.changedComponents)) {
            Optional optional = (Optional)entry.getValue();
            if (!optional.isPresent()) continue;
            ComponentType componentType = (ComponentType)entry.getKey();
            ComponentType.PACKET_CODEC.encode(registryByteBuf, componentType);
            this.encode(registryByteBuf, componentType, optional.get());
        }
        for (Reference2ObjectMap.Entry entry : Reference2ObjectMaps.fastIterable(componentChanges.changedComponents)) {
            if (!((Optional)entry.getValue()).isEmpty()) continue;
            ComponentType componentType2 = (ComponentType)entry.getKey();
            ComponentType.PACKET_CODEC.encode(registryByteBuf, componentType2);
        }
    }

    private <T> void encode(RegistryByteBuf buf, ComponentType<T> type, Object value) {
        this.field_58173.apply(type).encode(buf, value);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), (ComponentChanges)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
