/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.ExclusionStrategy
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.CheckedGson
 *  net.minecraft.client.realms.RealmsSerializable
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CheckedGson {
    ExclusionStrategy EXCLUSION_STRATEGY = new /* Unavailable Anonymous Inner Class!! */;
    private final Gson GSON = new GsonBuilder().addSerializationExclusionStrategy(this.EXCLUSION_STRATEGY).addDeserializationExclusionStrategy(this.EXCLUSION_STRATEGY).create();

    public String toJson(RealmsSerializable serializable) {
        return this.GSON.toJson((Object)serializable);
    }

    public String toJson(JsonElement json) {
        return this.GSON.toJson(json);
    }

    public <T extends RealmsSerializable> @Nullable T fromJson(String json, Class<T> type) {
        return (T)((RealmsSerializable)this.GSON.fromJson(json, type));
    }
}

