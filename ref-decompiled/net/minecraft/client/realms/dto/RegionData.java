/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.ServiceQuality
 *  net.minecraft.client.realms.ServiceQuality$ServiceQualityTypeAdapter
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  net.minecraft.client.realms.dto.RealmsRegion$RegionTypeAdapter
 *  net.minecraft.client.realms.dto.RegionData
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;

@Environment(value=EnvType.CLIENT)
public record RegionData(@SerializedName(value="regionName") @JsonAdapter(value=RealmsRegion.RegionTypeAdapter.class) RealmsRegion region, @SerializedName(value="serviceQuality") @JsonAdapter(value=ServiceQuality.ServiceQualityTypeAdapter.class) ServiceQuality serviceQuality) implements RealmsSerializable
{
    @SerializedName(value="regionName")
    @JsonAdapter(value=RealmsRegion.RegionTypeAdapter.class)
    private final RealmsRegion region;
    @SerializedName(value="serviceQuality")
    @JsonAdapter(value=ServiceQuality.ServiceQualityTypeAdapter.class)
    private final ServiceQuality serviceQuality;

    public RegionData(RealmsRegion region, ServiceQuality serviceQuality) {
        this.region = region;
        this.serviceQuality = serviceQuality;
    }

    @SerializedName(value="regionName")
    public RealmsRegion region() {
        return this.region;
    }

    @SerializedName(value="serviceQuality")
    public ServiceQuality serviceQuality() {
        return this.serviceQuality;
    }
}

