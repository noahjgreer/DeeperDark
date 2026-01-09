package net.minecraft.util;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import net.minecraft.network.encryption.SignatureVerifier;
import org.jetbrains.annotations.Nullable;

public record ApiServices(MinecraftSessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository, UserCache userCache) {
   private static final String USER_CACHE_FILE_NAME = "usercache.json";

   public ApiServices(MinecraftSessionService minecraftSessionService, ServicesKeySet servicesKeySet, GameProfileRepository gameProfileRepository, UserCache userCache) {
      this.sessionService = minecraftSessionService;
      this.servicesKeySet = servicesKeySet;
      this.profileRepository = gameProfileRepository;
      this.userCache = userCache;
   }

   public static ApiServices create(YggdrasilAuthenticationService authenticationService, File rootDirectory) {
      MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
      GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
      UserCache userCache = new UserCache(gameProfileRepository, new File(rootDirectory, "usercache.json"));
      return new ApiServices(minecraftSessionService, authenticationService.getServicesKeySet(), gameProfileRepository, userCache);
   }

   @Nullable
   public SignatureVerifier serviceSignatureVerifier() {
      return SignatureVerifier.create(this.servicesKeySet, ServicesKeyType.PROFILE_KEY);
   }

   public boolean providesProfileKeys() {
      return !this.servicesKeySet.keys(ServicesKeyType.PROFILE_KEY).isEmpty();
   }

   public MinecraftSessionService sessionService() {
      return this.sessionService;
   }

   public ServicesKeySet servicesKeySet() {
      return this.servicesKeySet;
   }

   public GameProfileRepository profileRepository() {
      return this.profileRepository;
   }

   public UserCache userCache() {
      return this.userCache;
   }
}
