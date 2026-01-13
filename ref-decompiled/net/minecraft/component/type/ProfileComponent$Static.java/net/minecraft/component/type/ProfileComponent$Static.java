/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.component.type;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.GameProfileResolver;
import net.minecraft.text.Text;

public static final class ProfileComponent.Static
extends ProfileComponent {
    public static final ProfileComponent.Static EMPTY = new ProfileComponent.Static((Either<GameProfile, ProfileComponent.Data>)Either.right((Object)ProfileComponent.Data.EMPTY), SkinTextures.SkinOverride.EMPTY);
    private final Either<GameProfile, ProfileComponent.Data> profileOrData;

    ProfileComponent.Static(Either<GameProfile, ProfileComponent.Data> profileOrData, SkinTextures.SkinOverride override) {
        super((GameProfile)profileOrData.map(profile -> profile, ProfileComponent.Data::createGameProfile), override);
        this.profileOrData = profileOrData;
    }

    @Override
    public CompletableFuture<GameProfile> resolve(GameProfileResolver resolver) {
        return CompletableFuture.completedFuture(this.profile);
    }

    @Override
    protected Either<GameProfile, ProfileComponent.Data> get() {
        return this.profileOrData;
    }

    @Override
    public Optional<String> getName() {
        return (Optional)this.profileOrData.map(profile -> Optional.of(profile.name()), data -> data.name);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileComponent.Static)) return false;
        ProfileComponent.Static static_ = (ProfileComponent.Static)o;
        if (!this.profileOrData.equals(static_.profileOrData)) return false;
        if (!this.override.equals(static_.override)) return false;
        return true;
    }

    public int hashCode() {
        int i = 31 + this.profileOrData.hashCode();
        i = 31 * i + this.override.hashCode();
        return i;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
    }
}
