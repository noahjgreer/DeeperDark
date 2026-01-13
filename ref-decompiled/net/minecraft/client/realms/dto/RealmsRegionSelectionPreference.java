/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  net.minecraft.client.realms.dto.RealmsRegion$RegionTypeAdapter
 *  net.minecraft.client.realms.dto.RealmsRegionSelectionPreference
 *  net.minecraft.client.realms.dto.RegionSelectionMethod
 *  net.minecraft.client.realms.dto.RegionSelectionMethod$SelectionMethodTypeAdapter
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsRegionSelectionPreference
implements RealmsSerializable {
    public static final RealmsRegionSelectionPreference DEFAULT = new RealmsRegionSelectionPreference(RegionSelectionMethod.AUTOMATIC_OWNER, null);
    @SerializedName(value="regionSelectionPreference")
    @JsonAdapter(value=RegionSelectionMethod.SelectionMethodTypeAdapter.class)
    public final RegionSelectionMethod selectionMethod;
    @SerializedName(value="preferredRegion")
    @JsonAdapter(value=RealmsRegion.RegionTypeAdapter.class)
    public @Nullable RealmsRegion preferredRegion;

    public RealmsRegionSelectionPreference(RegionSelectionMethod selectionMethod, @Nullable RealmsRegion preferredRegion) {
        this.selectionMethod = selectionMethod;
        this.preferredRegion = preferredRegion;
    }

    public RealmsRegionSelectionPreference copy() {
        return new RealmsRegionSelectionPreference(this.selectionMethod, this.preferredRegion);
    }
}

