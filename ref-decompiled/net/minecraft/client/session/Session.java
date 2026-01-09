package net.minecraft.client.session;

import com.mojang.util.UndashedUuid;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class Session {
   private final String username;
   private final UUID uuid;
   private final String accessToken;
   private final Optional xuid;
   private final Optional clientId;
   private final AccountType accountType;

   public Session(String username, UUID uuid, String accessToken, Optional xuid, Optional clientId, AccountType accountType) {
      this.username = username;
      this.uuid = uuid;
      this.accessToken = accessToken;
      this.xuid = xuid;
      this.clientId = clientId;
      this.accountType = accountType;
   }

   public String getSessionId() {
      String var10000 = this.accessToken;
      return "token:" + var10000 + ":" + UndashedUuid.toString(this.uuid);
   }

   public UUID getUuidOrNull() {
      return this.uuid;
   }

   public String getUsername() {
      return this.username;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public Optional getClientId() {
      return this.clientId;
   }

   public Optional getXuid() {
      return this.xuid;
   }

   public AccountType getAccountType() {
      return this.accountType;
   }

   @Environment(EnvType.CLIENT)
   public static enum AccountType {
      LEGACY("legacy"),
      MOJANG("mojang"),
      MSA("msa");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((type) -> {
         return type.name;
      }, Function.identity()));
      private final String name;

      private AccountType(final String name) {
         this.name = name;
      }

      @Nullable
      public static AccountType byName(String name) {
         return (AccountType)BY_NAME.get(name.toLowerCase(Locale.ROOT));
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static AccountType[] method_36868() {
         return new AccountType[]{LEGACY, MOJANG, MSA};
      }
   }
}
