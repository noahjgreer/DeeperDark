/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static final class ServerInfo.ResourcePackPolicy
extends Enum<ServerInfo.ResourcePackPolicy> {
    public static final /* enum */ ServerInfo.ResourcePackPolicy ENABLED = new ServerInfo.ResourcePackPolicy("enabled");
    public static final /* enum */ ServerInfo.ResourcePackPolicy DISABLED = new ServerInfo.ResourcePackPolicy("disabled");
    public static final /* enum */ ServerInfo.ResourcePackPolicy PROMPT = new ServerInfo.ResourcePackPolicy("prompt");
    public static final MapCodec<ServerInfo.ResourcePackPolicy> CODEC;
    private final Text name;
    private static final /* synthetic */ ServerInfo.ResourcePackPolicy[] RESOURCE_PACK_POLICIES;

    public static ServerInfo.ResourcePackPolicy[] values() {
        return (ServerInfo.ResourcePackPolicy[])RESOURCE_PACK_POLICIES.clone();
    }

    public static ServerInfo.ResourcePackPolicy valueOf(String string) {
        return Enum.valueOf(ServerInfo.ResourcePackPolicy.class, string);
    }

    private ServerInfo.ResourcePackPolicy(String name) {
        this.name = Text.translatable("manageServer.resourcePack." + name);
    }

    public Text getName() {
        return this.name;
    }

    private static /* synthetic */ ServerInfo.ResourcePackPolicy[] method_36896() {
        return new ServerInfo.ResourcePackPolicy[]{ENABLED, DISABLED, PROMPT};
    }

    static {
        RESOURCE_PACK_POLICIES = ServerInfo.ResourcePackPolicy.method_36896();
        CODEC = Codec.BOOL.optionalFieldOf("acceptTextures").xmap(value -> value.map(acceptTextures -> acceptTextures != false ? ENABLED : DISABLED).orElse(PROMPT), value -> switch (value.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> Optional.of(true);
            case 1 -> Optional.of(false);
            case 2 -> Optional.empty();
        });
    }
}
