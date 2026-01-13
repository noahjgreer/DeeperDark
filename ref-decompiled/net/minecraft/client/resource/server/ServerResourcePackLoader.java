/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.HashCode
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.RunArgs$Network
 *  net.minecraft.client.resource.server.DownloadQueuer
 *  net.minecraft.client.resource.server.PackStateChangeCallback
 *  net.minecraft.client.resource.server.ReloadScheduler
 *  net.minecraft.client.resource.server.ReloadScheduler$PackInfo
 *  net.minecraft.client.resource.server.ReloadScheduler$ReloadContext
 *  net.minecraft.client.resource.server.ServerResourcePackLoader
 *  net.minecraft.client.resource.server.ServerResourcePackLoader$8
 *  net.minecraft.client.resource.server.ServerResourcePackManager
 *  net.minecraft.client.resource.server.ServerResourcePackManager$AcceptanceStatus
 *  net.minecraft.client.session.Session
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.resource.PackVersion
 *  net.minecraft.resource.ResourcePackInfo
 *  net.minecraft.resource.ResourcePackPosition
 *  net.minecraft.resource.ResourcePackProfile
 *  net.minecraft.resource.ResourcePackProfile$InsertionPosition
 *  net.minecraft.resource.ResourcePackProfile$Metadata
 *  net.minecraft.resource.ResourcePackProfile$PackFactory
 *  net.minecraft.resource.ResourcePackProvider
 *  net.minecraft.resource.ResourcePackSource
 *  net.minecraft.resource.ResourceType
 *  net.minecraft.resource.ZipResourcePack$ZipBackedFactory
 *  net.minecraft.text.Text
 *  net.minecraft.util.Downloader
 *  net.minecraft.util.NetworkUtils$DownloadListener
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource.server;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.server.DownloadQueuer;
import net.minecraft.client.resource.server.PackStateChangeCallback;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.client.session.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.text.Text;
import net.minecraft.util.Downloader;
import net.minecraft.util.NetworkUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ServerResourcePackLoader
implements AutoCloseable {
    private static final Text SERVER_NAME_TEXT = Text.translatable((String)"resourcePack.server.name");
    private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourcePackProvider NOOP_PROVIDER = profileAdder -> {};
    private static final ResourcePackPosition POSITION = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true);
    private static final PackStateChangeCallback DEBUG_PACK_STATE_CHANGE_CALLBACK = new /* Unavailable Anonymous Inner Class!! */;
    final MinecraftClient client;
    private ResourcePackProvider packProvider = NOOP_PROVIDER;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ReloadScheduler.ReloadContext reloadContext;
    final ServerResourcePackManager manager;
    private final Downloader downloader;
    private ResourcePackSource packSource = ResourcePackSource.SERVER;
    PackStateChangeCallback packStateChangeCallback = DEBUG_PACK_STATE_CHANGE_CALLBACK;
    private int packIndex;

    public ServerResourcePackLoader(MinecraftClient client, Path downloadsDirectory, RunArgs.Network runArgs) {
        this.client = client;
        try {
            this.downloader = new Downloader(downloadsDirectory);
        }
        catch (IOException iOException) {
            throw new UncheckedIOException("Failed to open download queue in directory " + String.valueOf(downloadsDirectory), iOException);
        }
        Executor executor = arg_0 -> ((MinecraftClient)client).send(arg_0);
        this.manager = new ServerResourcePackManager(this.createDownloadQueuer(this.downloader, executor, runArgs.session, runArgs.netProxy), (PackStateChangeCallback)new /* Unavailable Anonymous Inner Class!! */, this.getReloadScheduler(), this.createPackChangeCallback(executor), ServerResourcePackManager.AcceptanceStatus.PENDING);
    }

    NetworkUtils.DownloadListener createListener(int entryCount) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    private DownloadQueuer createDownloadQueuer(Downloader downloader, Executor executor, Session session, Proxy proxy) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    private Runnable createPackChangeCallback(Executor executor) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    private ReloadScheduler getReloadScheduler() {
        return arg_0 -> this.reload(arg_0);
    }

    private @Nullable List<ResourcePackProfile> toProfiles(List<ReloadScheduler.PackInfo> packs) {
        ArrayList<ResourcePackProfile> list = new ArrayList<ResourcePackProfile>(packs.size());
        for (ReloadScheduler.PackInfo packInfo : Lists.reverse(packs)) {
            PackVersion packVersion;
            ZipResourcePack.ZipBackedFactory packFactory;
            String string = String.format(Locale.ROOT, "server/%08X/%s", this.packIndex++, packInfo.id());
            Path path = packInfo.path();
            ResourcePackInfo resourcePackInfo = new ResourcePackInfo(string, SERVER_NAME_TEXT, this.packSource, Optional.empty());
            ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata((ResourcePackInfo)resourcePackInfo, (ResourcePackProfile.PackFactory)(packFactory = new ZipResourcePack.ZipBackedFactory(path)), (PackVersion)(packVersion = SharedConstants.getGameVersion().packVersion(ResourceType.CLIENT_RESOURCES)), (ResourceType)ResourceType.CLIENT_RESOURCES);
            if (metadata == null) {
                LOGGER.warn("Invalid pack metadata in {}, ignoring all", (Object)path);
                return null;
            }
            list.add(new ResourcePackProfile(resourcePackInfo, (ResourcePackProfile.PackFactory)packFactory, metadata, POSITION));
        }
        return list;
    }

    public ResourcePackProvider getPassthroughPackProvider() {
        return packAdder -> this.packProvider.register(packAdder);
    }

    private static ResourcePackProvider getPackProvider(List<ResourcePackProfile> serverPacks) {
        if (serverPacks.isEmpty()) {
            return NOOP_PROVIDER;
        }
        return serverPacks::forEach;
    }

    private void reload(ReloadScheduler.ReloadContext context) {
        this.reloadContext = context;
        List list = context.getPacks();
        List list2 = this.toProfiles(list);
        if (list2 == null) {
            context.onFailure(false);
            List list3 = context.getPacks();
            list2 = this.toProfiles(list3);
            if (list2 == null) {
                LOGGER.warn("Double failure in loading server packs");
                list2 = List.of();
            }
        }
        this.packProvider = ServerResourcePackLoader.getPackProvider((List)list2);
        this.client.reloadResources();
    }

    public void onReloadFailure() {
        if (this.reloadContext != null) {
            this.reloadContext.onFailure(false);
            List list = this.toProfiles(this.reloadContext.getPacks());
            if (list == null) {
                LOGGER.warn("Double failure in loading server packs");
                list = List.of();
            }
            this.packProvider = ServerResourcePackLoader.getPackProvider((List)list);
        }
    }

    public void onForcedReloadFailure() {
        if (this.reloadContext != null) {
            this.reloadContext.onFailure(true);
            this.reloadContext = null;
            this.packProvider = NOOP_PROVIDER;
        }
    }

    public void onReloadSuccess() {
        if (this.reloadContext != null) {
            this.reloadContext.onSuccess();
            this.reloadContext = null;
        }
    }

    private static @Nullable HashCode toHashCode(@Nullable String hash) {
        if (hash != null && SHA1_PATTERN.matcher(hash).matches()) {
            return HashCode.fromString((String)hash.toLowerCase(Locale.ROOT));
        }
        return null;
    }

    public void addResourcePack(UUID id, URL url, @Nullable String hash) {
        HashCode hashCode = ServerResourcePackLoader.toHashCode((String)hash);
        this.manager.addResourcePack(id, url, hashCode);
    }

    public void addResourcePack(UUID id, Path path) {
        this.manager.addResourcePack(id, path);
    }

    public void remove(UUID id) {
        this.manager.remove(id);
    }

    public void removeAll() {
        this.manager.removeAll();
    }

    private static PackStateChangeCallback getStateChangeCallback(ClientConnection connection) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public void init(ClientConnection connection, ServerResourcePackManager.AcceptanceStatus acceptanceStatus) {
        this.packSource = ResourcePackSource.SERVER;
        this.packStateChangeCallback = ServerResourcePackLoader.getStateChangeCallback((ClientConnection)connection);
        switch (8.field_47620[acceptanceStatus.ordinal()]) {
            case 1: {
                this.manager.acceptAll();
                break;
            }
            case 2: {
                this.manager.declineAll();
                break;
            }
            case 3: {
                this.manager.resetAcceptanceStatus();
            }
        }
    }

    public void initWorldPack() {
        this.packSource = ResourcePackSource.WORLD;
        this.packStateChangeCallback = DEBUG_PACK_STATE_CHANGE_CALLBACK;
        this.manager.acceptAll();
    }

    public void acceptAll() {
        this.manager.acceptAll();
    }

    public void declineAll() {
        this.manager.declineAll();
    }

    public CompletableFuture<Void> getPackLoadFuture(UUID id) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        PackStateChangeCallback packStateChangeCallback = this.packStateChangeCallback;
        this.packStateChangeCallback = new /* Unavailable Anonymous Inner Class!! */;
        return completableFuture;
    }

    public void clear() {
        this.manager.removeAll();
        this.packStateChangeCallback = DEBUG_PACK_STATE_CHANGE_CALLBACK;
        this.manager.resetAcceptanceStatus();
    }

    @Override
    public void close() throws IOException {
        this.downloader.close();
    }
}

