/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientCommandSource
 *  net.minecraft.client.network.ClientCommandSource$1
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.command.CommandSource
 *  net.minecraft.command.CommandSource$RelativePosition
 *  net.minecraft.command.CommandSource$SuggestedIdType
 *  net.minecraft.command.permission.PermissionPredicate
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket
 *  net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket$Action
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryWrapper
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ClientCommandSource
implements CommandSource {
    private final ClientPlayNetworkHandler networkHandler;
    private final MinecraftClient client;
    private int completionId = -1;
    private @Nullable CompletableFuture<Suggestions> pendingCommandCompletion;
    private final Set<String> chatSuggestions = new HashSet();
    private final PermissionPredicate permissionPredicate;

    public ClientCommandSource(ClientPlayNetworkHandler networkHandler, MinecraftClient client, PermissionPredicate permissionPredicate) {
        this.networkHandler = networkHandler;
        this.client = client;
        this.permissionPredicate = permissionPredicate;
    }

    public Collection<String> getPlayerNames() {
        ArrayList list = Lists.newArrayList();
        for (PlayerListEntry playerListEntry : this.networkHandler.getPlayerList()) {
            list.add(playerListEntry.getProfile().name());
        }
        return list;
    }

    public Collection<String> getChatSuggestions() {
        if (this.chatSuggestions.isEmpty()) {
            return this.getPlayerNames();
        }
        HashSet<String> set = new HashSet<String>(this.getPlayerNames());
        set.addAll(this.chatSuggestions);
        return set;
    }

    public Collection<String> getEntitySuggestions() {
        if (this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            return Collections.singleton(((EntityHitResult)this.client.crosshairTarget).getEntity().getUuidAsString());
        }
        return Collections.emptyList();
    }

    public Collection<String> getTeamNames() {
        return this.networkHandler.getScoreboard().getTeamNames();
    }

    public Stream<Identifier> getSoundIds() {
        return this.client.getSoundManager().getKeys().stream();
    }

    public PermissionPredicate getPermissions() {
        return this.permissionPredicate;
    }

    public CompletableFuture<Suggestions> listIdSuggestions(RegistryKey<? extends Registry<?>> registryRef, CommandSource.SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context) {
        return this.getRegistryManager().getOptional(registryRef).map(registry -> {
            this.suggestIdentifiers((RegistryWrapper)registry, suggestedIdType, builder);
            return builder.buildFuture();
        }).orElseGet(() -> this.getCompletions(context));
    }

    public CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
        if (this.pendingCommandCompletion != null) {
            this.pendingCommandCompletion.cancel(false);
        }
        this.pendingCommandCompletion = new CompletableFuture();
        int i = ++this.completionId;
        this.networkHandler.sendPacket((Packet)new RequestCommandCompletionsC2SPacket(i, context.getInput()));
        return this.pendingCommandCompletion;
    }

    private static String format(double d) {
        return String.format(Locale.ROOT, "%.2f", d);
    }

    private static String format(int i) {
        return Integer.toString(i);
    }

    public Collection<CommandSource.RelativePosition> getBlockPositionSuggestions() {
        HitResult hitResult = this.client.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return super.getBlockPositionSuggestions();
        }
        BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
        return Collections.singleton(new CommandSource.RelativePosition(ClientCommandSource.format((int)blockPos.getX()), ClientCommandSource.format((int)blockPos.getY()), ClientCommandSource.format((int)blockPos.getZ())));
    }

    public Collection<CommandSource.RelativePosition> getPositionSuggestions() {
        HitResult hitResult = this.client.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return super.getPositionSuggestions();
        }
        Vec3d vec3d = hitResult.getPos();
        return Collections.singleton(new CommandSource.RelativePosition(ClientCommandSource.format((double)vec3d.x), ClientCommandSource.format((double)vec3d.y), ClientCommandSource.format((double)vec3d.z)));
    }

    public Set<RegistryKey<World>> getWorldKeys() {
        return this.networkHandler.getWorldKeys();
    }

    public DynamicRegistryManager getRegistryManager() {
        return this.networkHandler.getRegistryManager();
    }

    public FeatureSet getEnabledFeatures() {
        return this.networkHandler.getEnabledFeatures();
    }

    public void onCommandSuggestions(int completionId, Suggestions suggestions) {
        if (completionId == this.completionId) {
            this.pendingCommandCompletion.complete(suggestions);
            this.pendingCommandCompletion = null;
            this.completionId = -1;
        }
    }

    public void onChatSuggestions(ChatSuggestionsS2CPacket.Action action, List<String> suggestions) {
        switch (1.field_39795[action.ordinal()]) {
            case 1: {
                this.chatSuggestions.addAll(suggestions);
                break;
            }
            case 2: {
                suggestions.forEach(this.chatSuggestions::remove);
                break;
            }
            case 3: {
                this.chatSuggestions.clear();
                this.chatSuggestions.addAll(suggestions);
            }
        }
    }
}

