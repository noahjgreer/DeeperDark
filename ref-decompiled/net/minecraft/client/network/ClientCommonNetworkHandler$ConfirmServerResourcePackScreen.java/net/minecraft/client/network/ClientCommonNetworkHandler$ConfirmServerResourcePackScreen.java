/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ClientCommonNetworkHandler.ConfirmServerResourcePackScreen
extends ConfirmScreen {
    private final List<Pack> packs;
    private final @Nullable Screen parent;

    ClientCommonNetworkHandler.ConfirmServerResourcePackScreen(@Nullable MinecraftClient client, Screen parent, List<Pack> pack, @Nullable boolean required, Text prompt) {
        super(confirmed -> {
            client.setScreen(parent);
            ServerResourcePackLoader serverResourcePackLoader = client.getServerResourcePackProvider();
            if (confirmed) {
                if (clientCommonNetworkHandler.serverInfo != null) {
                    clientCommonNetworkHandler.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
                }
                serverResourcePackLoader.acceptAll();
            } else {
                serverResourcePackLoader.declineAll();
                if (required) {
                    clientCommonNetworkHandler.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                } else if (clientCommonNetworkHandler.serverInfo != null) {
                    clientCommonNetworkHandler.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
                }
            }
            for (Pack pack : pack) {
                serverResourcePackLoader.addResourcePack(pack.id, pack.url, pack.hash);
            }
            if (clientCommonNetworkHandler.serverInfo != null) {
                ServerList.updateServerListEntry(clientCommonNetworkHandler.serverInfo);
            }
        }, required ? Text.translatable("multiplayer.requiredTexturePrompt.line1") : Text.translatable("multiplayer.texturePrompt.line1"), ClientCommonNetworkHandler.getPrompt(required ? Text.translatable("multiplayer.requiredTexturePrompt.line2").formatted(Formatting.YELLOW, Formatting.BOLD) : Text.translatable("multiplayer.texturePrompt.line2"), prompt), required ? ScreenTexts.PROCEED : ScreenTexts.YES, required ? ScreenTexts.DISCONNECT : ScreenTexts.NO);
        this.packs = pack;
        this.parent = parent;
    }

    public ClientCommonNetworkHandler.ConfirmServerResourcePackScreen add(MinecraftClient client, UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
        ImmutableList list = ImmutableList.builderWithExpectedSize((int)(this.packs.size() + 1)).addAll(this.packs).add((Object)new Pack(id, url, hash)).build();
        return new ClientCommonNetworkHandler.ConfirmServerResourcePackScreen(client, this.parent, (List<Pack>)list, required, prompt);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Pack
    extends Record {
        final UUID id;
        final URL url;
        final String hash;

        Pack(UUID id, URL url, String hash) {
            this.id = id;
            this.url = url;
            this.hash = hash;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Pack.class, "id;url;hash", "id", "url", "hash"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Pack.class, "id;url;hash", "id", "url", "hash"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Pack.class, "id;url;hash", "id", "url", "hash"}, this, object);
        }

        public UUID id() {
            return this.id;
        }

        public URL url() {
            return this.url;
        }

        public String hash() {
            return this.hash;
        }
    }
}
