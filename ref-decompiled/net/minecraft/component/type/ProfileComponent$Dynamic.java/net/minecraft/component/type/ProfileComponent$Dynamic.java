/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.component.type;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.GameProfileResolver;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public static final class ProfileComponent.Dynamic
extends ProfileComponent {
    private static final Text TEXT = Text.translatable("component.profile.dynamic").formatted(Formatting.GRAY);
    private final Either<String, UUID> nameOrId;

    ProfileComponent.Dynamic(Either<String, UUID> nameOrId, SkinTextures.SkinOverride override) {
        super(ProfileComponent.createGameProfile(nameOrId.left(), nameOrId.right(), PropertyMap.EMPTY), override);
        this.nameOrId = nameOrId;
    }

    @Override
    public Optional<String> getName() {
        return this.nameOrId.left();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileComponent.Dynamic)) return false;
        ProfileComponent.Dynamic dynamic = (ProfileComponent.Dynamic)o;
        if (!this.nameOrId.equals(dynamic.nameOrId)) return false;
        if (!this.override.equals(dynamic.override)) return false;
        return true;
    }

    public int hashCode() {
        int i = 31 + this.nameOrId.hashCode();
        i = 31 * i + this.override.hashCode();
        return i;
    }

    @Override
    protected Either<GameProfile, ProfileComponent.Data> get() {
        return Either.right((Object)new ProfileComponent.Data(this.nameOrId.left(), this.nameOrId.right(), PropertyMap.EMPTY));
    }

    @Override
    public CompletableFuture<GameProfile> resolve(GameProfileResolver resolver) {
        return CompletableFuture.supplyAsync(() -> resolver.getProfile(this.nameOrId).orElse(this.profile), Util.getDownloadWorkerExecutor());
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(TEXT);
    }
}
