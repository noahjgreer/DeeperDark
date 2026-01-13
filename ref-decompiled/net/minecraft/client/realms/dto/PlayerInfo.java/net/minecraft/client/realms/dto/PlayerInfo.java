/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.ValueObject;

@Environment(value=EnvType.CLIENT)
public class PlayerInfo
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="name")
    public final String name;
    @SerializedName(value="uuid")
    @JsonAdapter(value=UUIDTypeAdapter.class)
    public final UUID uuid;
    @SerializedName(value="operator")
    public boolean operator;
    @SerializedName(value="accepted")
    public final boolean accepted;
    @SerializedName(value="online")
    public final boolean online;

    public PlayerInfo(String string, UUID uUID, boolean bl, boolean bl2, boolean bl3) {
        this.name = string;
        this.uuid = uUID;
        this.operator = bl;
        this.accepted = bl2;
        this.online = bl3;
    }
}
