package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class BossBarS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(BossBarS2CPacket::write, BossBarS2CPacket::new);
   private static final int DARKEN_SKY_MASK = 1;
   private static final int DRAGON_MUSIC_MASK = 2;
   private static final int THICKEN_FOG_MASK = 4;
   private final UUID uuid;
   private final Action action;
   static final Action REMOVE_ACTION = new Action() {
      public Type getType() {
         return BossBarS2CPacket.Type.REMOVE;
      }

      public void accept(UUID uuid, Consumer consumer) {
         consumer.remove(uuid);
      }

      public void toPacket(RegistryByteBuf buf) {
      }
   };

   private BossBarS2CPacket(UUID uuid, Action action) {
      this.uuid = uuid;
      this.action = action;
   }

   private BossBarS2CPacket(RegistryByteBuf buf) {
      this.uuid = buf.readUuid();
      Type type = (Type)buf.readEnumConstant(Type.class);
      this.action = (Action)type.parser.decode(buf);
   }

   public static BossBarS2CPacket add(BossBar bar) {
      return new BossBarS2CPacket(bar.getUuid(), new AddAction(bar));
   }

   public static BossBarS2CPacket remove(UUID uuid) {
      return new BossBarS2CPacket(uuid, REMOVE_ACTION);
   }

   public static BossBarS2CPacket updateProgress(BossBar bar) {
      return new BossBarS2CPacket(bar.getUuid(), new UpdateProgressAction(bar.getPercent()));
   }

   public static BossBarS2CPacket updateName(BossBar bar) {
      return new BossBarS2CPacket(bar.getUuid(), new UpdateNameAction(bar.getName()));
   }

   public static BossBarS2CPacket updateStyle(BossBar bar) {
      return new BossBarS2CPacket(bar.getUuid(), new UpdateStyleAction(bar.getColor(), bar.getStyle()));
   }

   public static BossBarS2CPacket updateProperties(BossBar bar) {
      return new BossBarS2CPacket(bar.getUuid(), new UpdatePropertiesAction(bar.shouldDarkenSky(), bar.hasDragonMusic(), bar.shouldThickenFog()));
   }

   private void write(RegistryByteBuf buf) {
      buf.writeUuid(this.uuid);
      buf.writeEnumConstant(this.action.getType());
      this.action.toPacket(buf);
   }

   static int maskProperties(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
      int i = 0;
      if (darkenSky) {
         i |= 1;
      }

      if (dragonMusic) {
         i |= 2;
      }

      if (thickenFog) {
         i |= 4;
      }

      return i;
   }

   public PacketType getPacketType() {
      return PlayPackets.BOSS_EVENT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onBossBar(this);
   }

   public void accept(Consumer consumer) {
      this.action.accept(this.uuid, consumer);
   }

   private interface Action {
      Type getType();

      void accept(UUID uuid, Consumer consumer);

      void toPacket(RegistryByteBuf buf);
   }

   static enum Type {
      ADD(AddAction::new),
      REMOVE((buf) -> {
         return BossBarS2CPacket.REMOVE_ACTION;
      }),
      UPDATE_PROGRESS(UpdateProgressAction::new),
      UPDATE_NAME(UpdateNameAction::new),
      UPDATE_STYLE(UpdateStyleAction::new),
      UPDATE_PROPERTIES(UpdatePropertiesAction::new);

      final PacketDecoder parser;

      private Type(final PacketDecoder parser) {
         this.parser = parser;
      }

      // $FF: synthetic method
      private static Type[] method_36948() {
         return new Type[]{ADD, REMOVE, UPDATE_PROGRESS, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES};
      }
   }

   private static class AddAction implements Action {
      private final Text name;
      private final float percent;
      private final BossBar.Color color;
      private final BossBar.Style style;
      private final boolean darkenSky;
      private final boolean dragonMusic;
      private final boolean thickenFog;

      AddAction(BossBar bar) {
         this.name = bar.getName();
         this.percent = bar.getPercent();
         this.color = bar.getColor();
         this.style = bar.getStyle();
         this.darkenSky = bar.shouldDarkenSky();
         this.dragonMusic = bar.hasDragonMusic();
         this.thickenFog = bar.shouldThickenFog();
      }

      private AddAction(RegistryByteBuf buf) {
         this.name = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
         this.percent = buf.readFloat();
         this.color = (BossBar.Color)buf.readEnumConstant(BossBar.Color.class);
         this.style = (BossBar.Style)buf.readEnumConstant(BossBar.Style.class);
         int i = buf.readUnsignedByte();
         this.darkenSky = (i & 1) > 0;
         this.dragonMusic = (i & 2) > 0;
         this.thickenFog = (i & 4) > 0;
      }

      public Type getType() {
         return BossBarS2CPacket.Type.ADD;
      }

      public void accept(UUID uuid, Consumer consumer) {
         consumer.add(uuid, this.name, this.percent, this.color, this.style, this.darkenSky, this.dragonMusic, this.thickenFog);
      }

      public void toPacket(RegistryByteBuf buf) {
         TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.name);
         buf.writeFloat(this.percent);
         buf.writeEnumConstant(this.color);
         buf.writeEnumConstant(this.style);
         buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
      }
   }

   private static record UpdateProgressAction(float progress) implements Action {
      private UpdateProgressAction(RegistryByteBuf buf) {
         this(buf.readFloat());
      }

      UpdateProgressAction(float percent) {
         this.progress = percent;
      }

      public Type getType() {
         return BossBarS2CPacket.Type.UPDATE_PROGRESS;
      }

      public void accept(UUID uuid, Consumer consumer) {
         consumer.updateProgress(uuid, this.progress);
      }

      public void toPacket(RegistryByteBuf buf) {
         buf.writeFloat(this.progress);
      }

      public float progress() {
         return this.progress;
      }
   }

   private static record UpdateNameAction(Text name) implements Action {
      private UpdateNameAction(RegistryByteBuf buf) {
         this((Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf));
      }

      UpdateNameAction(Text name) {
         this.name = name;
      }

      public Type getType() {
         return BossBarS2CPacket.Type.UPDATE_NAME;
      }

      public void accept(UUID uuid, Consumer consumer) {
         consumer.updateName(uuid, this.name);
      }

      public void toPacket(RegistryByteBuf buf) {
         TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.name);
      }

      public Text name() {
         return this.name;
      }
   }

   private static class UpdateStyleAction implements Action {
      private final BossBar.Color color;
      private final BossBar.Style style;

      UpdateStyleAction(BossBar.Color color, BossBar.Style style) {
         this.color = color;
         this.style = style;
      }

      private UpdateStyleAction(RegistryByteBuf buf) {
         this.color = (BossBar.Color)buf.readEnumConstant(BossBar.Color.class);
         this.style = (BossBar.Style)buf.readEnumConstant(BossBar.Style.class);
      }

      public Type getType() {
         return BossBarS2CPacket.Type.UPDATE_STYLE;
      }

      public void accept(UUID uuid, Consumer consumer) {
         consumer.updateStyle(uuid, this.color, this.style);
      }

      public void toPacket(RegistryByteBuf buf) {
         buf.writeEnumConstant(this.color);
         buf.writeEnumConstant(this.style);
      }
   }

   private static class UpdatePropertiesAction implements Action {
      private final boolean darkenSky;
      private final boolean dragonMusic;
      private final boolean thickenFog;

      UpdatePropertiesAction(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
         this.darkenSky = darkenSky;
         this.dragonMusic = dragonMusic;
         this.thickenFog = thickenFog;
      }

      private UpdatePropertiesAction(RegistryByteBuf buf) {
         int i = buf.readUnsignedByte();
         this.darkenSky = (i & 1) > 0;
         this.dragonMusic = (i & 2) > 0;
         this.thickenFog = (i & 4) > 0;
      }

      public Type getType() {
         return BossBarS2CPacket.Type.UPDATE_PROPERTIES;
      }

      public void accept(UUID uuid, Consumer consumer) {
         consumer.updateProperties(uuid, this.darkenSky, this.dragonMusic, this.thickenFog);
      }

      public void toPacket(RegistryByteBuf buf) {
         buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
      }
   }

   public interface Consumer {
      default void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
      }

      default void remove(UUID uuid) {
      }

      default void updateProgress(UUID uuid, float percent) {
      }

      default void updateName(UUID uuid, Text name) {
      }

      default void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
      }

      default void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
      }
   }
}
