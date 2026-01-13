/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.CheckedGson
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsServerAddress
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record RealmsServerAddress(@SerializedName(value="address") @Nullable String address, @SerializedName(value="resourcePackUrl") @Nullable String resourcePackUrl, @SerializedName(value="resourcePackHash") @Nullable String resourcePackHash, @SerializedName(value="sessionRegionData") // Could not load outer class - annotation placement on inner may be incorrect
@Nullable RealmsServerAddress.RegionData regionData) implements RealmsSerializable
{
    @SerializedName(value="address")
    private final @Nullable String address;
    @SerializedName(value="resourcePackUrl")
    private final @Nullable String resourcePackUrl;
    @SerializedName(value="resourcePackHash")
    private final @Nullable String resourcePackHash;
    @SerializedName(value="sessionRegionData")
    private final // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable RealmsServerAddress.RegionData regionData;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RealmsServerAddress NULL = new RealmsServerAddress(null, null, null, null);

    public RealmsServerAddress(@Nullable String address, @Nullable String resourcePackUrl, @Nullable String resourcePackHash, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable RealmsServerAddress.RegionData regionData) {
        this.address = address;
        this.resourcePackUrl = resourcePackUrl;
        this.resourcePackHash = resourcePackHash;
        this.regionData = regionData;
    }

    public static RealmsServerAddress parse(CheckedGson gson, String json) {
        try {
            RealmsServerAddress realmsServerAddress = (RealmsServerAddress)gson.fromJson(json, RealmsServerAddress.class);
            if (realmsServerAddress == null) {
                LOGGER.error("Could not parse RealmsServerAddress: {}", (Object)json);
                return NULL;
            }
            return realmsServerAddress;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerAddress", (Throwable)exception);
            return NULL;
        }
    }

    @SerializedName(value="address")
    public @Nullable String address() {
        return this.address;
    }

    @SerializedName(value="resourcePackUrl")
    public @Nullable String resourcePackUrl() {
        return this.resourcePackUrl;
    }

    @SerializedName(value="resourcePackHash")
    public @Nullable String resourcePackHash() {
        return this.resourcePackHash;
    }

    @SerializedName(value="sessionRegionData")
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable RealmsServerAddress.RegionData regionData() {
        return this.regionData;
    }
}

