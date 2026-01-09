package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(PlayerListS2CPacket::write, PlayerListS2CPacket::new);
   private final EnumSet actions;
   private final List entries;

   public PlayerListS2CPacket(EnumSet actions, Collection players) {
      this.actions = actions;
      this.entries = players.stream().map(Entry::new).toList();
   }

   public PlayerListS2CPacket(Action action, ServerPlayerEntity player) {
      this.actions = EnumSet.of(action);
      this.entries = List.of(new Entry(player));
   }

   public static PlayerListS2CPacket entryFromPlayer(Collection players) {
      EnumSet enumSet = EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.INITIALIZE_CHAT, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, PlayerListS2CPacket.Action.UPDATE_HAT, PlayerListS2CPacket.Action.UPDATE_LIST_ORDER);
      return new PlayerListS2CPacket(enumSet, players);
   }

   private PlayerListS2CPacket(RegistryByteBuf buf) {
      this.actions = buf.readEnumSet(Action.class);
      this.entries = buf.readList((buf2) -> {
         Serialized serialized = new Serialized(buf2.readUuid());
         Iterator var3 = this.actions.iterator();

         while(var3.hasNext()) {
            Action action = (Action)var3.next();
            action.reader.read(serialized, (RegistryByteBuf)buf2);
         }

         return serialized.toEntry();
      });
   }

   private void write(RegistryByteBuf buf) {
      buf.writeEnumSet(this.actions, Action.class);
      buf.writeCollection(this.entries, (buf2, entry) -> {
         buf2.writeUuid(entry.profileId());
         Iterator var3 = this.actions.iterator();

         while(var3.hasNext()) {
            Action action = (Action)var3.next();
            action.writer.write((RegistryByteBuf)buf2, entry);
         }

      });
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_INFO_UPDATE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onPlayerList(this);
   }

   public EnumSet getActions() {
      return this.actions;
   }

   public List getEntries() {
      return this.entries;
   }

   public List getPlayerAdditionEntries() {
      return this.actions.contains(PlayerListS2CPacket.Action.ADD_PLAYER) ? this.entries : List.of();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
   }

   public static record Entry(UUID profileId, @Nullable GameProfile profile, boolean listed, int latency, GameMode gameMode, @Nullable Text displayName, boolean showHat, int listOrder, @Nullable PublicPlayerSession.Serialized chatSession) {
      final boolean showHat;
      final int listOrder;
      @Nullable
      final PublicPlayerSession.Serialized chatSession;

      Entry(ServerPlayerEntity player) {
         this(player.getUuid(), player.getGameProfile(), true, player.networkHandler.getLatency(), player.getGameMode(), player.getPlayerListName(), player.isPartVisible(PlayerModelPart.HAT), player.getPlayerListOrder(), (PublicPlayerSession.Serialized)Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
      }

      public Entry(UUID uUID, @Nullable GameProfile gameProfile, boolean bl, int i, GameMode gameMode, @Nullable Text text, boolean bl2, int j, @Nullable PublicPlayerSession.Serialized serialized) {
         this.profileId = uUID;
         this.profile = gameProfile;
         this.listed = bl;
         this.latency = i;
         this.gameMode = gameMode;
         this.displayName = text;
         this.showHat = bl2;
         this.listOrder = j;
         this.chatSession = serialized;
      }

      public UUID profileId() {
         return this.profileId;
      }

      @Nullable
      public GameProfile profile() {
         return this.profile;
      }

      public boolean listed() {
         return this.listed;
      }

      public int latency() {
         return this.latency;
      }

      public GameMode gameMode() {
         return this.gameMode;
      }

      @Nullable
      public Text displayName() {
         return this.displayName;
      }

      public boolean showHat() {
         return this.showHat;
      }

      public int listOrder() {
         return this.listOrder;
      }

      @Nullable
      public PublicPlayerSession.Serialized chatSession() {
         return this.chatSession;
      }
   }

   public static enum Action {
      ADD_PLAYER((serialized, buf) -> {
         GameProfile gameProfile = new GameProfile(serialized.profileId, buf.readString(16));
         gameProfile.getProperties().putAll((Multimap)PacketCodecs.PROPERTY_MAP.decode(buf));
         serialized.gameProfile = gameProfile;
      }, (buf, entry) -> {
         GameProfile gameProfile = (GameProfile)Objects.requireNonNull(entry.profile());
         buf.writeString(gameProfile.getName(), 16);
         PacketCodecs.PROPERTY_MAP.encode(buf, gameProfile.getProperties());
      }),
      INITIALIZE_CHAT((serialized, buf) -> {
         serialized.session = (PublicPlayerSession.Serialized)buf.readNullable(PublicPlayerSession.Serialized::fromBuf);
      }, (buf, entry) -> {
         buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write);
      }),
      UPDATE_GAME_MODE((serialized, buf) -> {
         serialized.gameMode = GameMode.byIndex(buf.readVarInt());
      }, (buf, entry) -> {
         buf.writeVarInt(entry.gameMode().getIndex());
      }),
      UPDATE_LISTED((serialized, buf) -> {
         serialized.listed = buf.readBoolean();
      }, (buf, entry) -> {
         buf.writeBoolean(entry.listed());
      }),
      UPDATE_LATENCY((serialized, buf) -> {
         serialized.latency = buf.readVarInt();
      }, (buf, entry) -> {
         buf.writeVarInt(entry.latency());
      }),
      UPDATE_DISPLAY_NAME((serialized, buf) -> {
         serialized.displayName = (Text)PacketByteBuf.readNullable(buf, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
      }, (buf, entry) -> {
         PacketByteBuf.writeNullable(buf, entry.displayName(), TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
      }),
      UPDATE_LIST_ORDER((serialized, buf) -> {
         serialized.listOrder = buf.readVarInt();
      }, (buf, entry) -> {
         buf.writeVarInt(entry.listOrder);
      }),
      UPDATE_HAT((serialized, buf) -> {
         serialized.showHat = buf.readBoolean();
      }, (buf, entry) -> {
         buf.writeBoolean(entry.showHat);
      });

      final Reader reader;
      final Writer writer;

      private Action(final Reader reader, final Writer writer) {
         this.reader = reader;
         this.writer = writer;
      }

      // $FF: synthetic method
      private static Action[] method_36951() {
         return new Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LIST_ORDER, UPDATE_HAT};
      }

      public interface Reader {
         void read(Serialized serialized, RegistryByteBuf buf);
      }

      public interface Writer {
         void write(RegistryByteBuf buf, Entry entry);
      }
   }

   private static class Serialized {
      final UUID profileId;
      @Nullable
      GameProfile gameProfile;
      boolean listed;
      int latency;
      GameMode gameMode;
      @Nullable
      Text displayName;
      boolean showHat;
      int listOrder;
      @Nullable
      PublicPlayerSession.Serialized session;

      Serialized(UUID profileId) {
         this.gameMode = GameMode.DEFAULT;
         this.profileId = profileId;
      }

      Entry toEntry() {
         return new Entry(this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.showHat, this.listOrder, this.session);
      }
   }
}
