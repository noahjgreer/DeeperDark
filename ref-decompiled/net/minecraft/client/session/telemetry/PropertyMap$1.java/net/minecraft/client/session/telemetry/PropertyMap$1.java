/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;

@Environment(value=EnvType.CLIENT)
static class PropertyMap.1
extends MapCodec<PropertyMap> {
    final /* synthetic */ List field_41497;

    PropertyMap.1(List list) {
        this.field_41497 = list;
    }

    public <T> RecordBuilder<T> encode(PropertyMap propertyMap, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
        RecordBuilder<T> recordBuilder2 = recordBuilder;
        for (TelemetryEventProperty telemetryEventProperty : this.field_41497) {
            recordBuilder2 = this.encode(propertyMap, recordBuilder2, telemetryEventProperty);
        }
        return recordBuilder2;
    }

    private <T, V> RecordBuilder<T> encode(PropertyMap map, RecordBuilder<T> builder, TelemetryEventProperty<V> property) {
        V object = map.get(property);
        if (object != null) {
            return builder.add(property.id(), object, property.codec());
        }
        return builder;
    }

    public <T> DataResult<PropertyMap> decode(DynamicOps<T> ops, MapLike<T> map) {
        DataResult<PropertyMap.Builder> dataResult = DataResult.success((Object)new PropertyMap.Builder());
        for (TelemetryEventProperty telemetryEventProperty : this.field_41497) {
            dataResult = this.decode(dataResult, ops, map, telemetryEventProperty);
        }
        return dataResult.map(PropertyMap.Builder::build);
    }

    private <T, V> DataResult<PropertyMap.Builder> decode(DataResult<PropertyMap.Builder> result, DynamicOps<T> ops, MapLike<T> map, TelemetryEventProperty<V> property) {
        Object object = map.get(property.id());
        if (object != null) {
            DataResult dataResult = property.codec().parse(ops, object);
            return result.apply2stable((mapBuilder, value) -> mapBuilder.put(property, value), dataResult);
        }
        return result;
    }

    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return this.field_41497.stream().map(TelemetryEventProperty::id).map(arg_0 -> ops.createString(arg_0));
    }

    public /* synthetic */ RecordBuilder encode(Object map, DynamicOps ops, RecordBuilder builder) {
        return this.encode((PropertyMap)map, ops, builder);
    }
}
