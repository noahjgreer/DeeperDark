/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.integrated;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.PlayerSaveHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerManager
extends PlayerManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private @Nullable NbtCompound userData;

    public IntegratedPlayerManager(IntegratedServer server, CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager, PlayerSaveHandler saveHandler) {
        super(server, registryManager, saveHandler, server.getManagementListener());
        this.setViewDistance(10);
    }

    @Override
    protected void savePlayerData(ServerPlayerEntity player) {
        if (this.getServer().isHost(player.getPlayerConfigEntry())) {
            try (ErrorReporter.Logging logging = new ErrorReporter.Logging(player.getErrorReporterContext(), LOGGER);){
                NbtWriteView nbtWriteView = NbtWriteView.create(logging, player.getRegistryManager());
                player.writeData(nbtWriteView);
                this.userData = nbtWriteView.getNbt();
            }
        }
        super.savePlayerData(player);
    }

    @Override
    public Text checkCanJoin(SocketAddress address, PlayerConfigEntry configEntry) {
        if (this.getServer().isHost(configEntry) && this.getPlayer(configEntry.name()) != null) {
            return Text.translatable("multiplayer.disconnect.name_taken");
        }
        return super.checkCanJoin(address, configEntry);
    }

    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    public @Nullable NbtCompound getUserData() {
        return this.userData;
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}
