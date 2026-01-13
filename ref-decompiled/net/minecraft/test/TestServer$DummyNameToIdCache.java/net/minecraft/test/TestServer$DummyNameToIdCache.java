/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.util.NameToIdCache;

static class TestServer.DummyNameToIdCache
implements NameToIdCache {
    private final Set<PlayerConfigEntry> players = new HashSet<PlayerConfigEntry>();

    TestServer.DummyNameToIdCache() {
    }

    @Override
    public void add(PlayerConfigEntry player) {
        this.players.add(player);
    }

    @Override
    public Optional<PlayerConfigEntry> findByName(String name) {
        return this.players.stream().filter(player -> player.name().equals(name)).findFirst().or(() -> Optional.of(PlayerConfigEntry.fromNickname(name)));
    }

    @Override
    public Optional<PlayerConfigEntry> getByUuid(UUID uuid) {
        return this.players.stream().filter(player -> player.id().equals(uuid)).findFirst();
    }

    @Override
    public void setOfflineMode(boolean offlineMode) {
    }

    @Override
    public void save() {
    }
}
