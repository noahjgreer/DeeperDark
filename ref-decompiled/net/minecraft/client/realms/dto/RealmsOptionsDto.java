package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class RealmsOptionsDto implements RealmsSerializable {
   @SerializedName("slotId")
   public final int slotId;
   @SerializedName("pvp")
   private final boolean pvp;
   @SerializedName("spawnMonsters")
   private final boolean spawnMonsters;
   @SerializedName("spawnProtection")
   private final int spawnProtection;
   @SerializedName("commandBlocks")
   private final boolean commandBlocks;
   @SerializedName("forceGameMode")
   private final boolean forceGameMode;
   @SerializedName("difficulty")
   private final int difficulty;
   @SerializedName("gameMode")
   private final int gameMode;
   @SerializedName("slotName")
   private final String slotName;
   @SerializedName("version")
   private final String version;
   @SerializedName("compatibility")
   private final RealmsServer.Compatibility compatibility;
   @SerializedName("worldTemplateId")
   private final long worldTemplateId;
   @Nullable
   @SerializedName("worldTemplateImage")
   private final String worldTemplateImage;
   @SerializedName("hardcore")
   private final boolean hardcore;

   public RealmsOptionsDto(int slotId, RealmsWorldOptions options, boolean hardcore) {
      this.slotId = slotId;
      this.pvp = options.pvp;
      this.spawnMonsters = options.spawnMonsters;
      this.spawnProtection = options.spawnProtection;
      this.commandBlocks = options.commandBlocks;
      this.forceGameMode = options.forceGameMode;
      this.difficulty = options.difficulty;
      this.gameMode = options.gameMode;
      this.slotName = options.getSlotName(slotId);
      this.version = options.version;
      this.compatibility = options.compatibility;
      this.worldTemplateId = options.templateId;
      this.worldTemplateImage = options.templateImage;
      this.hardcore = hardcore;
   }
}
