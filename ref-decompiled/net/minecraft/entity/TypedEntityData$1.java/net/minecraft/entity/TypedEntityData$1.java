/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;

static class TypedEntityData.1
implements Codec<TypedEntityData<T>> {
    final /* synthetic */ Codec field_61901;

    TypedEntityData.1(Codec codec) {
        this.field_61901 = codec;
    }

    public <V> DataResult<Pair<TypedEntityData<T>, V>> decode(DynamicOps<V> ops, V value) {
        return NbtComponent.COMPOUND_CODEC.decode(ops, value).flatMap(pair -> {
            NbtCompound nbtCompound = ((NbtCompound)pair.getFirst()).copy();
            NbtElement nbtElement = nbtCompound.remove(TypedEntityData.ID_KEY);
            if (nbtElement == null) {
                return DataResult.error(() -> "Expected 'id' field in " + String.valueOf(value));
            }
            return this.field_61901.parse(TypedEntityData.1.toNbtOps(ops), (Object)nbtElement).map(object -> Pair.of(new TypedEntityData<Object>(object, nbtCompound), (Object)pair.getSecond()));
        });
    }

    public <V> DataResult<V> encode(TypedEntityData<T> typedEntityData, DynamicOps<V> dynamicOps, V object) {
        return this.field_61901.encodeStart(TypedEntityData.1.toNbtOps(dynamicOps), typedEntityData.type).flatMap(id -> {
            NbtCompound nbtCompound = typedEntityData.nbt.copy();
            nbtCompound.put(TypedEntityData.ID_KEY, (NbtElement)id);
            return NbtComponent.COMPOUND_CODEC.encode((Object)nbtCompound, dynamicOps, object);
        });
    }

    private static <T> DynamicOps<NbtElement> toNbtOps(DynamicOps<T> ops) {
        if (ops instanceof RegistryOps) {
            RegistryOps registryOps = (RegistryOps)ops;
            return registryOps.withDelegate(NbtOps.INSTANCE);
        }
        return NbtOps.INSTANCE;
    }

    public /* synthetic */ DataResult encode(Object prefix, DynamicOps ops, Object value) {
        return this.encode((TypedEntityData)prefix, ops, value);
    }
}
