/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsServerAddress.RegionData(@SerializedName(value="regionName") @JsonAdapter(value=RealmsRegion.RegionTypeAdapter.class) @Nullable RealmsRegion region, @SerializedName(value="serviceQuality") @JsonAdapter(value=ServiceQuality.ServiceQualityTypeAdapter.class) @Nullable ServiceQuality serviceQuality) implements RealmsSerializable
{
}
