/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
}
