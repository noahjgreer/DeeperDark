/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsWorldResetDto
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
    @SerializedName(value="seed")
    private final String seed;
    @SerializedName(value="worldTemplateId")
    private final long worldTemplateId;
    @SerializedName(value="levelType")
    private final int levelType;
    @SerializedName(value="generateStructures")
    private final boolean generateStructures;
    @SerializedName(value="experiments")
    private final Set<String> experiments;

    public RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures, Set<String> experiments) {
        this.seed = seed;
        this.worldTemplateId = worldTemplateId;
        this.levelType = levelType;
        this.generateStructures = generateStructures;
        this.experiments = experiments;
    }

    @SerializedName(value="seed")
    public String seed() {
        return this.seed;
    }

    @SerializedName(value="worldTemplateId")
    public long worldTemplateId() {
        return this.worldTemplateId;
    }

    @SerializedName(value="levelType")
    public int levelType() {
        return this.levelType;
    }

    @SerializedName(value="generateStructures")
    public boolean generateStructures() {
        return this.generateStructures;
    }

    @SerializedName(value="experiments")
    public Set<String> experiments() {
        return this.experiments;
    }
}

