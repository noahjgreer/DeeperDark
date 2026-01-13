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
    public RealmsOptionsDto(int slotId, RealmsWorldOptions options, boolean hardcore) {
        this(slotId, options.spawnProtection, options.forceGameMode, options.difficulty, options.gameMode, options.getSlotName(slotId), options.version, options.compatibility, options.templateId, options.templateImage, hardcore);
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
}
