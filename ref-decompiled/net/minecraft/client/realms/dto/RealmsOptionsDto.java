/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsOptionsDto
 *  net.minecraft.client.realms.dto.RealmsServer$Compatibility
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsOptionsDto(@SerializedName(value="slotId") int slotId, @SerializedName(value="spawnProtection") int spawnProtection, @SerializedName(value="forceGameMode") boolean forceGameMode, @SerializedName(value="difficulty") int difficulty, @SerializedName(value="gameMode") int gameMode, @SerializedName(value="slotName") String slotName, @SerializedName(value="version") String version, @SerializedName(value="compatibility") RealmsServer.Compatibility compatibility, @SerializedName(value="worldTemplateId") long worldTemplateId, @SerializedName(value="worldTemplateImage") @Nullable String worldTemplateImage, @SerializedName(value="hardcore") boolean hardcore) implements RealmsSerializable
{
    @SerializedName(value="slotId")
    private final int slotId;
    @SerializedName(value="spawnProtection")
    private final int spawnProtection;
    @SerializedName(value="forceGameMode")
    private final boolean forceGameMode;
    @SerializedName(value="difficulty")
    private final int difficulty;
    @SerializedName(value="gameMode")
    private final int gameMode;
    @SerializedName(value="slotName")
    private final String slotName;
    @SerializedName(value="version")
    private final String version;
    @SerializedName(value="compatibility")
    private final RealmsServer.Compatibility compatibility;
    @SerializedName(value="worldTemplateId")
    private final long worldTemplateId;
    @SerializedName(value="worldTemplateImage")
    private final @Nullable String worldTemplateImage;
    @SerializedName(value="hardcore")
    private final boolean hardcore;

    public RealmsOptionsDto(int slotId, RealmsWorldOptions options, boolean hardcore) {
        this(slotId, options.spawnProtection, options.forceGameMode, options.difficulty, options.gameMode, options.getSlotName(slotId), options.version, options.compatibility, options.templateId, options.templateImage, hardcore);
    }

    public RealmsOptionsDto(int slotId, int spawnProtection, boolean forceGameMode, int difficulty, int gameMode, String slotName, String version, RealmsServer.Compatibility compatibility, long worldTemplateId, @Nullable String worldTemplateImage, boolean hardcore) {
        this.slotId = slotId;
        this.spawnProtection = spawnProtection;
        this.forceGameMode = forceGameMode;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.slotName = slotName;
        this.version = version;
        this.compatibility = compatibility;
        this.worldTemplateId = worldTemplateId;
        this.worldTemplateImage = worldTemplateImage;
        this.hardcore = hardcore;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsOptionsDto.class, "slotId;spawnProtection;forceGameMode;difficulty;gameMode;slotName;version;compatibility;templateId;templateImage;hardcore", "slotId", "spawnProtection", "forceGameMode", "difficulty", "gameMode", "slotName", "version", "compatibility", "worldTemplateId", "worldTemplateImage", "hardcore"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsOptionsDto.class, "slotId;spawnProtection;forceGameMode;difficulty;gameMode;slotName;version;compatibility;templateId;templateImage;hardcore", "slotId", "spawnProtection", "forceGameMode", "difficulty", "gameMode", "slotName", "version", "compatibility", "worldTemplateId", "worldTemplateImage", "hardcore"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsOptionsDto.class, "slotId;spawnProtection;forceGameMode;difficulty;gameMode;slotName;version;compatibility;templateId;templateImage;hardcore", "slotId", "spawnProtection", "forceGameMode", "difficulty", "gameMode", "slotName", "version", "compatibility", "worldTemplateId", "worldTemplateImage", "hardcore"}, this, object);
    }

    @SerializedName(value="slotId")
    public int slotId() {
        return this.slotId;
    }

    @SerializedName(value="spawnProtection")
    public int spawnProtection() {
        return this.spawnProtection;
    }

    @SerializedName(value="forceGameMode")
    public boolean forceGameMode() {
        return this.forceGameMode;
    }

    @SerializedName(value="difficulty")
    public int difficulty() {
        return this.difficulty;
    }

    @SerializedName(value="gameMode")
    public int gameMode() {
        return this.gameMode;
    }

    @SerializedName(value="slotName")
    public String slotName() {
        return this.slotName;
    }

    @SerializedName(value="version")
    public String version() {
        return this.version;
    }

    @SerializedName(value="compatibility")
    public RealmsServer.Compatibility compatibility() {
        return this.compatibility;
    }

    @SerializedName(value="worldTemplateId")
    public long worldTemplateId() {
        return this.worldTemplateId;
    }

    @SerializedName(value="worldTemplateImage")
    public @Nullable String worldTemplateImage() {
        return this.worldTemplateImage;
    }

    @SerializedName(value="hardcore")
    public boolean hardcore() {
        return this.hardcore;
    }
}

