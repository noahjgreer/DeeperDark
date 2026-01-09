package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;

public record ServerMetadata(Text description, Optional players, Optional version, Optional favicon, boolean secureChatEnforced) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TextCodecs.CODEC.lenientOptionalFieldOf("description", ScreenTexts.EMPTY).forGetter(ServerMetadata::description), ServerMetadata.Players.CODEC.lenientOptionalFieldOf("players").forGetter(ServerMetadata::players), ServerMetadata.Version.CODEC.lenientOptionalFieldOf("version").forGetter(ServerMetadata::version), ServerMetadata.Favicon.CODEC.lenientOptionalFieldOf("favicon").forGetter(ServerMetadata::favicon), Codec.BOOL.lenientOptionalFieldOf("enforcesSecureChat", false).forGetter(ServerMetadata::secureChatEnforced)).apply(instance, ServerMetadata::new);
   });

   public ServerMetadata(Text text, Optional optional, Optional optional2, Optional optional3, boolean bl) {
      this.description = text;
      this.players = optional;
      this.version = optional2;
      this.favicon = optional3;
      this.secureChatEnforced = bl;
   }

   public Text description() {
      return this.description;
   }

   public Optional players() {
      return this.players;
   }

   public Optional version() {
      return this.version;
   }

   public Optional favicon() {
      return this.favicon;
   }

   public boolean secureChatEnforced() {
      return this.secureChatEnforced;
   }

   public static record Players(int max, int online, List sample) {
      private static final Codec GAME_PROFILE_CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Uuids.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)).apply(instance, GameProfile::new);
      });
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.INT.fieldOf("max").forGetter(Players::max), Codec.INT.fieldOf("online").forGetter(Players::online), GAME_PROFILE_CODEC.listOf().lenientOptionalFieldOf("sample", List.of()).forGetter(Players::sample)).apply(instance, Players::new);
      });

      public Players(int max, int online, List list) {
         this.max = max;
         this.online = online;
         this.sample = list;
      }

      public int max() {
         return this.max;
      }

      public int online() {
         return this.online;
      }

      public List sample() {
         return this.sample;
      }
   }

   public static record Version(String gameVersion, int protocolVersion) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("name").forGetter(Version::gameVersion), Codec.INT.fieldOf("protocol").forGetter(Version::protocolVersion)).apply(instance, Version::new);
      });

      public Version(String gameVersion, int protocolVersion) {
         this.gameVersion = gameVersion;
         this.protocolVersion = protocolVersion;
      }

      public static Version create() {
         GameVersion gameVersion = SharedConstants.getGameVersion();
         return new Version(gameVersion.name(), gameVersion.protocolVersion());
      }

      public String gameVersion() {
         return this.gameVersion;
      }

      public int protocolVersion() {
         return this.protocolVersion;
      }
   }

   public static record Favicon(byte[] iconBytes) {
      private static final String DATA_URI_PREFIX = "data:image/png;base64,";
      public static final Codec CODEC;

      public Favicon(byte[] bs) {
         this.iconBytes = bs;
      }

      public byte[] iconBytes() {
         return this.iconBytes;
      }

      static {
         CODEC = Codec.STRING.comapFlatMap((uri) -> {
            if (!uri.startsWith("data:image/png;base64,")) {
               return DataResult.error(() -> {
                  return "Unknown format";
               });
            } else {
               try {
                  String string = uri.substring("data:image/png;base64,".length()).replaceAll("\n", "");
                  byte[] bs = Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8));
                  return DataResult.success(new Favicon(bs));
               } catch (IllegalArgumentException var3) {
                  return DataResult.error(() -> {
                     return "Malformed base64 server icon";
                  });
               }
            }
         }, (iconBytes) -> {
            String var10000 = new String(Base64.getEncoder().encode(iconBytes.iconBytes), StandardCharsets.UTF_8);
            return "data:image/png;base64," + var10000;
         });
      }
   }
}
