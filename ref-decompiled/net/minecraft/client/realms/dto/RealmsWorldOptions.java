package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.StringHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RealmsWorldOptions extends ValueObject implements RealmsSerializable {
   @SerializedName("pvp")
   public boolean pvp = true;
   @SerializedName("spawnMonsters")
   public boolean spawnMonsters = true;
   @SerializedName("spawnProtection")
   public int spawnProtection = 0;
   @SerializedName("commandBlocks")
   public boolean commandBlocks = false;
   @SerializedName("forceGameMode")
   public boolean forceGameMode = false;
   @SerializedName("difficulty")
   public int difficulty = 2;
   @SerializedName("gameMode")
   public int gameMode = 0;
   @SerializedName("slotName")
   private String slotName = "";
   @SerializedName("version")
   public String version = "";
   @SerializedName("compatibility")
   public RealmsServer.Compatibility compatibility;
   @SerializedName("worldTemplateId")
   public long templateId;
   @Nullable
   @SerializedName("worldTemplateImage")
   public String templateImage;
   public boolean empty;

   private RealmsWorldOptions() {
      this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      this.templateId = -1L;
      this.templateImage = null;
   }

   public RealmsWorldOptions(boolean pvp, boolean spawnAnimals, int spawnProtection, boolean commandBlocks, int difficulty, int gameMode, boolean hardcore, String slotName, String version, RealmsServer.Compatibility compatibility) {
      this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      this.templateId = -1L;
      this.templateImage = null;
      this.pvp = pvp;
      this.spawnMonsters = spawnAnimals;
      this.spawnProtection = spawnProtection;
      this.commandBlocks = commandBlocks;
      this.difficulty = difficulty;
      this.gameMode = gameMode;
      this.forceGameMode = hardcore;
      this.slotName = slotName;
      this.version = version;
      this.compatibility = compatibility;
   }

   public static RealmsWorldOptions getDefaults() {
      return new RealmsWorldOptions();
   }

   public static RealmsWorldOptions create(GameMode gameMode, boolean commandBlocks, Difficulty difficulty, boolean hardcore, String version, String slotName) {
      RealmsWorldOptions realmsWorldOptions = getDefaults();
      realmsWorldOptions.commandBlocks = commandBlocks;
      realmsWorldOptions.difficulty = difficulty.getId();
      realmsWorldOptions.gameMode = gameMode.getIndex();
      realmsWorldOptions.slotName = slotName;
      realmsWorldOptions.version = version;
      return realmsWorldOptions;
   }

   public static RealmsWorldOptions create(LevelInfo levelInfo, boolean commandBlocks, String version) {
      return create(levelInfo.getGameMode(), commandBlocks, levelInfo.getDifficulty(), levelInfo.isHardcore(), version, levelInfo.getLevelName());
   }

   public static RealmsWorldOptions getEmptyDefaults() {
      RealmsWorldOptions realmsWorldOptions = getDefaults();
      realmsWorldOptions.setEmpty(true);
      return realmsWorldOptions;
   }

   public void setEmpty(boolean empty) {
      this.empty = empty;
   }

   public static RealmsWorldOptions fromJson(CheckedGson gson, String json) {
      RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)gson.fromJson(json, RealmsWorldOptions.class);
      if (realmsWorldOptions == null) {
         return getDefaults();
      } else {
         replaceNullsWithDefaults(realmsWorldOptions);
         return realmsWorldOptions;
      }
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
      if (StringHelper.isBlank(this.slotName)) {
         return this.empty ? I18n.translate("mco.configure.world.slot.empty") : this.getDefaultSlotName(index);
      } else {
         return this.slotName;
      }
   }

   public String getDefaultSlotName(int index) {
      return I18n.translate("mco.configure.world.slot", index);
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.pvp, this.spawnMonsters, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName, this.version, this.compatibility);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }
}
