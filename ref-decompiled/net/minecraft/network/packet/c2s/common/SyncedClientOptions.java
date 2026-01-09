package net.minecraft.network.packet.c2s.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.util.Arm;

public record SyncedClientOptions(String language, int viewDistance, ChatVisibility chatVisibility, boolean chatColorsEnabled, int playerModelParts, Arm mainArm, boolean filtersText, boolean allowsServerListing, ParticlesMode particleStatus) {
   public static final int MAX_LANGUAGE_CODE_LENGTH = 16;

   public SyncedClientOptions(PacketByteBuf buf) {
      this(buf.readString(16), buf.readByte(), (ChatVisibility)buf.readEnumConstant(ChatVisibility.class), buf.readBoolean(), buf.readUnsignedByte(), (Arm)buf.readEnumConstant(Arm.class), buf.readBoolean(), buf.readBoolean(), (ParticlesMode)buf.readEnumConstant(ParticlesMode.class));
   }

   public SyncedClientOptions(String string, int i, ChatVisibility chatVisibility, boolean bl, int j, Arm arm, boolean bl2, boolean bl3, ParticlesMode particlesMode) {
      this.language = string;
      this.viewDistance = i;
      this.chatVisibility = chatVisibility;
      this.chatColorsEnabled = bl;
      this.playerModelParts = j;
      this.mainArm = arm;
      this.filtersText = bl2;
      this.allowsServerListing = bl3;
      this.particleStatus = particlesMode;
   }

   public void write(PacketByteBuf buf) {
      buf.writeString(this.language);
      buf.writeByte(this.viewDistance);
      buf.writeEnumConstant(this.chatVisibility);
      buf.writeBoolean(this.chatColorsEnabled);
      buf.writeByte(this.playerModelParts);
      buf.writeEnumConstant(this.mainArm);
      buf.writeBoolean(this.filtersText);
      buf.writeBoolean(this.allowsServerListing);
      buf.writeEnumConstant(this.particleStatus);
   }

   public static SyncedClientOptions createDefault() {
      return new SyncedClientOptions("en_us", 2, ChatVisibility.FULL, true, 0, PlayerEntity.DEFAULT_MAIN_ARM, false, false, ParticlesMode.ALL);
   }

   public String language() {
      return this.language;
   }

   public int viewDistance() {
      return this.viewDistance;
   }

   public ChatVisibility chatVisibility() {
      return this.chatVisibility;
   }

   public boolean chatColorsEnabled() {
      return this.chatColorsEnabled;
   }

   public int playerModelParts() {
      return this.playerModelParts;
   }

   public Arm mainArm() {
      return this.mainArm;
   }

   public boolean filtersText() {
      return this.filtersText;
   }

   public boolean allowsServerListing() {
      return this.allowsServerListing;
   }

   public ParticlesMode particleStatus() {
      return this.particleStatus;
   }
}
