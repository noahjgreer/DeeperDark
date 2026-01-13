/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RegionPingResult
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(value=EnvType.CLIENT)
public record RegionPingResult(@SerializedName(value="regionName") String regionName, @SerializedName(value="ping") int ping) implements RealmsSerializable
{
    @SerializedName(value="regionName")
    private final String regionName;
    @SerializedName(value="ping")
    private final int ping;

    public RegionPingResult(String regionName, int ping) {
        this.regionName = regionName;
        this.ping = ping;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, Float.valueOf(this.ping));
    }

    @SerializedName(value="regionName")
    public String regionName() {
        return this.regionName;
    }

    @SerializedName(value="ping")
    public int ping() {
        return this.ping;
    }
}

