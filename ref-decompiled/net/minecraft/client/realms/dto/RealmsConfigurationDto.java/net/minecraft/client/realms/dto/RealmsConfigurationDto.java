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
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsDescriptionDto;
import net.minecraft.client.realms.dto.RealmsOptionsDto;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsConfigurationDto(@SerializedName(value="options") RealmsOptionsDto options, @SerializedName(value="settings") List<RealmsSettingDto> settings, @SerializedName(value="regionSelectionPreference") @Nullable RealmsRegionSelectionPreference regionSelectionPreference, @SerializedName(value="description") @Nullable RealmsDescriptionDto description) implements RealmsSerializable
{
}
