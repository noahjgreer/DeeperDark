/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

static class ResourceMetadata.2
implements ResourceMetadata {
    final /* synthetic */ JsonObject field_38689;

    ResourceMetadata.2(JsonObject jsonObject) {
        this.field_38689 = jsonObject;
    }

    @Override
    public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
        String string = serializer.name();
        if (this.field_38689.has(string)) {
            Object object = serializer.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)this.field_38689.get(string)).getOrThrow(JsonParseException::new);
            return Optional.of(object);
        }
        return Optional.empty();
    }
}
