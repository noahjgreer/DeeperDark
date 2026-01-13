/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.network.ServerInfo$ResourcePackPolicy
 *  net.minecraft.client.network.ServerInfo$ServerType
 *  net.minecraft.client.network.ServerInfo$Status
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.util.PngMetadata
 *  net.minecraft.util.dynamic.Codecs
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.PngMetadata;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_FAVICON_SIZE = 1024;
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ServerMetadata.Players players;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().protocolVersion();
    public Text version = Text.literal((String)SharedConstants.getGameVersion().name());
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
    private byte @Nullable [] favicon;
    private ServerType serverType;
    private int acceptedCodeOfConduct;
    private Status status = Status.INITIAL;

    public ServerInfo(String name, String address, ServerType serverType) {
        this.name = name;
        this.address = address;
        this.serverType = serverType;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("name", this.name);
        nbtCompound.putString("ip", this.address);
        nbtCompound.putNullable("icon", Codecs.BASE_64, (Object)this.favicon);
        nbtCompound.copyFromCodec(ResourcePackPolicy.CODEC, (Object)this.resourcePackPolicy);
        if (this.acceptedCodeOfConduct != 0) {
            nbtCompound.putInt("acceptedCodeOfConduct", this.acceptedCodeOfConduct);
        }
        return nbtCompound;
    }

    public ResourcePackPolicy getResourcePackPolicy() {
        return this.resourcePackPolicy;
    }

    public void setResourcePackPolicy(ResourcePackPolicy resourcePackPolicy) {
        this.resourcePackPolicy = resourcePackPolicy;
    }

    public static ServerInfo fromNbt(NbtCompound root) {
        ServerInfo serverInfo = new ServerInfo(root.getString("name", ""), root.getString("ip", ""), ServerType.OTHER);
        serverInfo.setFavicon((byte[])root.get("icon", Codecs.BASE_64).orElse(null));
        serverInfo.setResourcePackPolicy(root.decode(ResourcePackPolicy.CODEC).orElse(ResourcePackPolicy.PROMPT));
        serverInfo.acceptedCodeOfConduct = root.getInt("acceptedCodeOfConduct", 0);
        return serverInfo;
    }

    public byte @Nullable [] getFavicon() {
        return this.favicon;
    }

    public void setFavicon(byte @Nullable [] favicon) {
        this.favicon = favicon;
    }

    public boolean isLocal() {
        return this.serverType == ServerType.LAN;
    }

    public boolean isRealm() {
        return this.serverType == ServerType.REALM;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public boolean hasAcceptedCodeOfConduct(String codeOfConductText) {
        return this.acceptedCodeOfConduct == codeOfConductText.hashCode();
    }

    public void setAcceptedCodeOfConduct(String codeOfConductText) {
        this.acceptedCodeOfConduct = codeOfConductText.hashCode();
    }

    public void resetAcceptedCodeOfConduct() {
        this.acceptedCodeOfConduct = 0;
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

    public static byte @Nullable [] validateFavicon(byte @Nullable [] favicon) {
        if (favicon != null) {
            try {
                PngMetadata pngMetadata = PngMetadata.fromBytes((byte[])favicon);
                if (pngMetadata.width() <= 1024 && pngMetadata.height() <= 1024) {
                    return favicon;
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to decode server icon", (Throwable)iOException);
            }
        }
        return null;
    }
}

