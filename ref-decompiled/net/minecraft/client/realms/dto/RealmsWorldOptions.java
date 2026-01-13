/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.CheckedGson
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.RealmsServer$Compatibility
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 *  net.minecraft.client.realms.dto.ValueObject
 *  net.minecraft.client.realms.util.DontSerialize
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.StringHelper
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.GameMode
 *  net.minecraft.world.level.LevelInfo
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.DontSerialize;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.StringHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsWorldOptions
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="spawnProtection")
    public int spawnProtection = 0;
    @SerializedName(value="forceGameMode")
    public boolean forceGameMode = false;
    @SerializedName(value="difficulty")
    public int difficulty = 2;
    @SerializedName(value="gameMode")
    public int gameMode = 0;
    @SerializedName(value="slotName")
    private String slotName = "";
    @SerializedName(value="version")
    public String version = "";
    @SerializedName(value="compatibility")
    public RealmsServer.Compatibility compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
    @SerializedName(value="worldTemplateId")
    public long templateId = -1L;
    @SerializedName(value="worldTemplateImage")
    public @Nullable String templateImage = null;
    @DontSerialize
    public boolean empty;

    private RealmsWorldOptions() {
    }

    public RealmsWorldOptions(int spawnProtection, int difficulty, int gameMode, boolean forceGameMode, String slotName, String version, RealmsServer.Compatibility compatibility) {
        this.spawnProtection = spawnProtection;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.forceGameMode = forceGameMode;
        this.slotName = slotName;
        this.version = version;
        this.compatibility = compatibility;
    }

    public static RealmsWorldOptions getDefaults() {
        return new RealmsWorldOptions();
    }

    public static RealmsWorldOptions create(GameMode gameMode, Difficulty difficulty, boolean hardcore, String version, String slotName) {
        RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.getDefaults();
        realmsWorldOptions.difficulty = difficulty.getId();
        realmsWorldOptions.gameMode = gameMode.getIndex();
        realmsWorldOptions.slotName = slotName;
        realmsWorldOptions.version = version;
        return realmsWorldOptions;
    }

    public static RealmsWorldOptions create(LevelInfo levelInfo, String slotName) {
        return RealmsWorldOptions.create((GameMode)levelInfo.getGameMode(), (Difficulty)levelInfo.getDifficulty(), (boolean)levelInfo.isHardcore(), (String)slotName, (String)levelInfo.getLevelName());
    }

    public static RealmsWorldOptions getEmptyDefaults() {
        RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.getDefaults();
        realmsWorldOptions.setEmpty(true);
        return realmsWorldOptions;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static RealmsWorldOptions fromJson(CheckedGson gson, String json) {
        RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)gson.fromJson(json, RealmsWorldOptions.class);
        if (realmsWorldOptions == null) {
            return RealmsWorldOptions.getDefaults();
        }
        RealmsWorldOptions.replaceNullsWithDefaults((RealmsWorldOptions)realmsWorldOptions);
        return realmsWorldOptions;
    }

    private static void replaceNullsWithDefaults(RealmsWorldOptions options) {
        if (options.slotName == null) {
            options.slotName = "";
        }
        if (options.version == null) {
            options.version = "";
        }
        if (options.compatibility == null) {
            options.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
        }
    }

    public String getSlotName(int index) {
        if (StringHelper.isBlank((String)this.slotName)) {
            if (this.empty) {
                return I18n.translate((String)"mco.configure.world.slot.empty", (Object[])new Object[0]);
            }
            return this.getDefaultSlotName(index);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int index) {
        return I18n.translate((String)"mco.configure.world.slot", (Object[])new Object[]{index});
    }

    public RealmsWorldOptions copy() {
        return new RealmsWorldOptions(this.spawnProtection, this.difficulty, this.gameMode, this.forceGameMode, this.slotName, this.version, this.compatibility);
    }
}

