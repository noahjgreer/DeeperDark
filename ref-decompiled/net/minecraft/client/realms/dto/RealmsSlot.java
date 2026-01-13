/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsSettingDto
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.RealmsSlot$OptionsTypeAdapter
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;

@Environment(value=EnvType.CLIENT)
public final class RealmsSlot
implements RealmsSerializable {
    @SerializedName(value="slotId")
    public int slotId;
    @SerializedName(value="options")
    @JsonAdapter(value=OptionsTypeAdapter.class)
    public RealmsWorldOptions options;
    @SerializedName(value="settings")
    public List<RealmsSettingDto> settings;

    public RealmsSlot(int slotId, RealmsWorldOptions options, List<RealmsSettingDto> settings) {
        this.slotId = slotId;
        this.options = options;
        this.settings = settings;
    }

    public static RealmsSlot create(int slotId) {
        return new RealmsSlot(slotId, RealmsWorldOptions.getEmptyDefaults(), List.of(RealmsSettingDto.ofHardcore((boolean)false)));
    }

    public RealmsSlot copy() {
        return new RealmsSlot(this.slotId, this.options.copy(), new ArrayList(this.settings));
    }

    public boolean isHardcore() {
        return RealmsSettingDto.isHardcore((List)this.settings);
    }
}

