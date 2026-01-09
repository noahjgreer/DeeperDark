package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.PngMetadata;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerInfo {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_FAVICON_SIZE = 1024;
   public String name;
   public String address;
   public Text playerCountLabel;
   public Text label;
   @Nullable
   public ServerMetadata.Players players;
   public long ping;
   public int protocolVersion = SharedConstants.getGameVersion().protocolVersion();
   public Text version = Text.literal(SharedConstants.getGameVersion().name());
   public List playerListSummary = Collections.emptyList();
   private ResourcePackPolicy resourcePackPolicy;
   @Nullable
   private byte[] favicon;
   private ServerType serverType;
   private Status status;

   public ServerInfo(String name, String address, ServerType serverType) {
      this.resourcePackPolicy = ServerInfo.ResourcePackPolicy.PROMPT;
      this.status = ServerInfo.Status.INITIAL;
      this.name = name;
      this.address = address;
      this.serverType = serverType;
   }

   public NbtCompound toNbt() {
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.putString("name", this.name);
      nbtCompound.putString("ip", this.address);
      nbtCompound.putNullable("icon", Codecs.BASE_64, this.favicon);
      nbtCompound.copyFromCodec(ServerInfo.ResourcePackPolicy.CODEC, this.resourcePackPolicy);
      return nbtCompound;
   }

   public ResourcePackPolicy getResourcePackPolicy() {
      return this.resourcePackPolicy;
   }

   public void setResourcePackPolicy(ResourcePackPolicy resourcePackPolicy) {
      this.resourcePackPolicy = resourcePackPolicy;
   }

   public static ServerInfo fromNbt(NbtCompound root) {
      ServerInfo serverInfo = new ServerInfo(root.getString("name", ""), root.getString("ip", ""), ServerInfo.ServerType.OTHER);
      serverInfo.setFavicon((byte[])root.get("icon", Codecs.BASE_64).orElse((Object)null));
      serverInfo.setResourcePackPolicy((ResourcePackPolicy)root.decode(ServerInfo.ResourcePackPolicy.CODEC).orElse(ServerInfo.ResourcePackPolicy.PROMPT));
      return serverInfo;
   }

   @Nullable
   public byte[] getFavicon() {
      return this.favicon;
   }

   public void setFavicon(@Nullable byte[] favicon) {
      this.favicon = favicon;
   }

   public boolean isLocal() {
      return this.serverType == ServerInfo.ServerType.LAN;
   }

   public boolean isRealm() {
      return this.serverType == ServerInfo.ServerType.REALM;
   }

   public ServerType getServerType() {
      return this.serverType;
   }

   public void copyFrom(ServerInfo serverInfo) {
      this.address = serverInfo.address;
      this.name = serverInfo.name;
      this.favicon = serverInfo.favicon;
   }

   public void copyWithSettingsFrom(ServerInfo serverInfo) {
      this.copyFrom(serverInfo);
      this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
      this.serverType = serverInfo.serverType;
   }

   public Status getStatus() {
      return this.status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   @Nullable
   public static byte[] validateFavicon(@Nullable byte[] favicon) {
      if (favicon != null) {
         try {
            PngMetadata pngMetadata = PngMetadata.fromBytes(favicon);
            if (pngMetadata.width() <= 1024 && pngMetadata.height() <= 1024) {
               return favicon;
            }
         } catch (IOException var2) {
            LOGGER.warn("Failed to decode server icon", var2);
         }
      }

      return null;
   }

   @Environment(EnvType.CLIENT)
   public static enum ResourcePackPolicy {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      public static final MapCodec CODEC = Codec.BOOL.optionalFieldOf("acceptTextures").xmap((value) -> {
         return (ResourcePackPolicy)value.map((acceptTextures) -> {
            return acceptTextures ? ENABLED : DISABLED;
         }).orElse(PROMPT);
      }, (value) -> {
         Optional var10000;
         switch (value.ordinal()) {
            case 0:
               var10000 = Optional.of(true);
               break;
            case 1:
               var10000 = Optional.of(false);
               break;
            case 2:
               var10000 = Optional.empty();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });
      private final Text name;

      private ResourcePackPolicy(final String name) {
         this.name = Text.translatable("addServer.resourcePack." + name);
      }

      public Text getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static ResourcePackPolicy[] method_36896() {
         return new ResourcePackPolicy[]{ENABLED, DISABLED, PROMPT};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum Status {
      INITIAL,
      PINGING,
      UNREACHABLE,
      INCOMPATIBLE,
      SUCCESSFUL;

      // $FF: synthetic method
      private static Status[] method_55826() {
         return new Status[]{INITIAL, PINGING, UNREACHABLE, INCOMPATIBLE, SUCCESSFUL};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum ServerType {
      LAN,
      REALM,
      OTHER;

      // $FF: synthetic method
      private static ServerType[] method_52812() {
         return new ServerType[]{LAN, REALM, OTHER};
      }
   }
}
