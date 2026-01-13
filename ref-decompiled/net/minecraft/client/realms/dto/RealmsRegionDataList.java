/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsRegionDataList
 *  net.minecraft.client.realms.dto.RegionData
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RegionData;

@Environment(value=EnvType.CLIENT)
public record RealmsRegionDataList(@SerializedName(value="regionDataList") List<RegionData> regionData) implements RealmsSerializable
{
    @SerializedName(value="regionDataList")
    private final List<RegionData> regionData;

    public RealmsRegionDataList(List<RegionData> regionData) {
        this.regionData = regionData;
    }

    public static RealmsRegionDataList empty() {
        return new RealmsRegionDataList(List.of());
    }

    @SerializedName(value="regionDataList")
    public List<RegionData> regionData() {
        return this.regionData;
    }
}

