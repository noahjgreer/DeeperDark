/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.test;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.rule.ServerGameRules;

public record TestEnvironmentDefinition.GameRules(ServerGameRules gameRulesMap) implements TestEnvironmentDefinition
{
    public static final MapCodec<TestEnvironmentDefinition.GameRules> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ServerGameRules.CODEC.fieldOf("rules").forGetter(TestEnvironmentDefinition.GameRules::gameRulesMap)).apply((Applicative)instance, TestEnvironmentDefinition.GameRules::new));

    @Override
    public void setup(ServerWorld world) {
        GameRules gameRules = world.getGameRules();
        MinecraftServer minecraftServer = world.getServer();
        gameRules.copyFrom(this.gameRulesMap, minecraftServer);
    }

    @Override
    public void teardown(ServerWorld world) {
        this.gameRulesMap.keySet().forEach(rule -> this.resetValue(world, (GameRule)rule));
    }

    private <T> void resetValue(ServerWorld serverWorld, GameRule<T> rule) {
        serverWorld.getGameRules().setValue(rule, rule.getDefaultValue(), serverWorld.getServer());
    }

    public MapCodec<TestEnvironmentDefinition.GameRules> getCodec() {
        return CODEC;
    }
}
