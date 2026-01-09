package net.minecraft.client.session;

import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ProfileKeys {
   ProfileKeys MISSING = new ProfileKeys() {
      public CompletableFuture fetchKeyPair() {
         return CompletableFuture.completedFuture(Optional.empty());
      }

      public boolean isExpired() {
         return false;
      }
   };

   static ProfileKeys create(UserApiService userApiService, Session session, Path root) {
      return (ProfileKeys)(session.getAccountType() == Session.AccountType.MSA ? new ProfileKeysImpl(userApiService, session.getUuidOrNull(), root) : MISSING);
   }

   CompletableFuture fetchKeyPair();

   boolean isExpired();
}
