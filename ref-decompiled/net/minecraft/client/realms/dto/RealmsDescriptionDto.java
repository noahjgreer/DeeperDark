/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsDescriptionDto
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsDescriptionDto(@SerializedName(value="name") @Nullable String name, @SerializedName(value="description") String description) implements RealmsSerializable
{
    @SerializedName(value="name")
    private final @Nullable String name;
    @SerializedName(value="description")
    private final String description;

    public RealmsDescriptionDto(@Nullable String name, String description) {
        this.name = name;
        this.description = description;
    }

    @SerializedName(value="name")
    public @Nullable String name() {
        return this.name;
    }

    @SerializedName(value="description")
    public String description() {
        return this.description;
    }
}

