/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.loot.context;

import java.util.Optional;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public static class LootContext.Builder {
    private final LootWorldContext worldContext;
    private @Nullable Random random;

    public LootContext.Builder(LootWorldContext worldContext) {
        this.worldContext = worldContext;
    }

    public LootContext.Builder random(long seed) {
        if (seed != 0L) {
            this.random = Random.create(seed);
        }
        return this;
    }

    public LootContext.Builder random(Random random) {
        this.random = random;
        return this;
    }

    public ServerWorld getWorld() {
        return this.worldContext.getWorld();
    }

    public LootContext build(Optional<Identifier> randomId) {
        ServerWorld serverWorld = this.getWorld();
        MinecraftServer minecraftServer = serverWorld.getServer();
        Random random = Optional.ofNullable(this.random).or(() -> randomId.map(serverWorld::getOrCreateRandom)).orElseGet(serverWorld::getRandom);
        return new LootContext(this.worldContext, random, minecraftServer.getReloadableRegistries().createRegistryLookup());
    }
}
