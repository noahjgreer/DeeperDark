package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.PlayerSaveHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private NbtCompound userData;

   public IntegratedPlayerManager(IntegratedServer server, CombinedDynamicRegistries registryManager, PlayerSaveHandler saveHandler) {
      super(server, registryManager, saveHandler, 8);
      this.setViewDistance(10);
   }

   protected void savePlayerData(ServerPlayerEntity player) {
      if (this.getServer().isHost(player.getGameProfile())) {
         ErrorReporter.Logging logging = new ErrorReporter.Logging(player.getErrorReporterContext(), LOGGER);

         try {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, player.getRegistryManager());
            player.writeData(nbtWriteView);
            this.userData = nbtWriteView.getNbt();
         } catch (Throwable var6) {
            try {
               logging.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         logging.close();
      }

      super.savePlayerData(player);
   }

   public Text checkCanJoin(SocketAddress address, GameProfile profile) {
      return (Text)(this.getServer().isHost(profile) && this.getPlayer(profile.getName()) != null ? Text.translatable("multiplayer.disconnect.name_taken") : super.checkCanJoin(address, profile));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   @Nullable
   public NbtCompound getUserData() {
      return this.userData;
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getServer();
   }
}
