/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
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
    public  @Nullable ServerMetadata.Players players;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().protocolVersion();
    public Text version = Text.literal(SharedConstants.getGameVersion().name());
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
        nbtCompound.putNullable("icon", Codecs.BASE_64, this.favicon);
        nbtCompound.copyFromCodec(ResourcePackPolicy.CODEC, this.resourcePackPolicy);
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
        serverInfo.setFavicon(root.get("icon", Codecs.BASE_64).orElse(null));
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
                PngMetadata pngMetadata = PngMetadata.fromBytes(favicon);
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

    @Environment(value=EnvType.CLIENT)
    public static final class ResourcePackPolicy
    extends Enum<ResourcePackPolicy> {
        public static final /* enum */ ResourcePackPolicy ENABLED = new ResourcePackPolicy("enabled");
        public static final /* enum */ ResourcePackPolicy DISABLED = new ResourcePackPolicy("disabled");
        public static final /* enum */ ResourcePackPolicy PROMPT = new ResourcePackPolicy("prompt");
        public static final MapCodec<ResourcePackPolicy> CODEC;
        private final Text name;
        private static final /* synthetic */ ResourcePackPolicy[] RESOURCE_PACK_POLICIES;

        public static ResourcePackPolicy[] values() {
            return (ResourcePackPolicy[])RESOURCE_PACK_POLICIES.clone();
        }

        public static ResourcePackPolicy valueOf(String string) {
            return Enum.valueOf(ResourcePackPolicy.class, string);
        }

        private ResourcePackPolicy(String name) {
            this.name = Text.translatable("manageServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }

        private static /* synthetic */ ResourcePackPolicy[] method_36896() {
            return new ResourcePackPolicy[]{ENABLED, DISABLED, PROMPT};
        }

        static {
            RESOURCE_PACK_POLICIES = ResourcePackPolicy.method_36896();
            CODEC = Codec.BOOL.optionalFieldOf("acceptTextures").xmap(value -> value.map(acceptTextures -> acceptTextures != false ? ENABLED : DISABLED).orElse(PROMPT), value -> switch (value.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> Optional.of(true);
                case 1 -> Optional.of(false);
                case 2 -> Optional.empty();
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status INITIAL = new Status();
        public static final /* enum */ Status PINGING = new Status();
        public static final /* enum */ Status UNREACHABLE = new Status();
        public static final /* enum */ Status INCOMPATIBLE = new Status();
        public static final /* enum */ Status SUCCESSFUL = new Status();
        private static final /* synthetic */ Status[] field_47885;

        public static Status[] values() {
            return (Status[])field_47885.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static /* synthetic */ Status[] method_55826() {
            return new Status[]{INITIAL, PINGING, UNREACHABLE, INCOMPATIBLE, SUCCESSFUL};
        }

        static {
            field_47885 = Status.method_55826();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ServerType
    extends Enum<ServerType> {
        public static final /* enum */ ServerType LAN = new ServerType();
        public static final /* enum */ ServerType REALM = new ServerType();
        public static final /* enum */ ServerType OTHER = new ServerType();
        private static final /* synthetic */ ServerType[] field_45612;

        public static ServerType[] values() {
            return (ServerType[])field_45612.clone();
        }

        public static ServerType valueOf(String string) {
            return Enum.valueOf(ServerType.class, string);
        }

        private static /* synthetic */ ServerType[] method_52812() {
            return new ServerType[]{LAN, REALM, OTHER};
        }

        static {
            field_45612 = ServerType.method_52812();
        }
    }
}
