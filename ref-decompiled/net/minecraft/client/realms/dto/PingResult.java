/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.PingResult
 *  net.minecraft.client.realms.dto.RegionPingResult
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RegionPingResult;

@Environment(value=EnvType.CLIENT)
public record PingResult(@SerializedName(value="pingResults") List<RegionPingResult> pingResults, @SerializedName(value="worldIds") List<Long> worldIds) implements RealmsSerializable
{
    @SerializedName(value="pingResults")
    private final List<RegionPingResult> pingResults;
    @SerializedName(value="worldIds")
    private final List<Long> worldIds;

    public PingResult(List<RegionPingResult> pingResults, List<Long> worldIds) {
        this.pingResults = pingResults;
        this.worldIds = worldIds;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PingResult.class, "pingResults;realmIds", "pingResults", "worldIds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PingResult.class, "pingResults;realmIds", "pingResults", "worldIds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PingResult.class, "pingResults;realmIds", "pingResults", "worldIds"}, this, object);
    }

    @SerializedName(value="pingResults")
    public List<RegionPingResult> pingResults() {
        return this.pingResults;
    }

    @SerializedName(value="worldIds")
    public List<Long> worldIds() {
        return this.worldIds;
    }
}

