/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(value=EnvType.CLIENT)
public record RealmsWorldResetDto(@SerializedName(value="seed") String seed, @SerializedName(value="worldTemplateId") long worldTemplateId, @SerializedName(value="levelType") int levelType, @SerializedName(value="generateStructures") boolean generateStructures, @SerializedName(value="experiments") Set<String> experiments) implements RealmsSerializable
{
}
