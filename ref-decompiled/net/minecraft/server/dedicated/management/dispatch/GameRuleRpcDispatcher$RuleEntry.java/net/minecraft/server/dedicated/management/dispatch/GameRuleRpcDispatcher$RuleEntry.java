/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management.dispatch;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.server.dedicated.management.RpcException;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleType;

public record GameRuleRpcDispatcher.RuleEntry<T>(GameRule<T> gameRule, T value) {
    public static final Codec<GameRuleRpcDispatcher.RuleEntry<?>> TYPED_CODEC = Registries.GAME_RULE.getCodec().dispatch("key", GameRuleRpcDispatcher.RuleEntry::gameRule, GameRuleRpcDispatcher.RuleEntry::typedCodec);
    public static final Codec<GameRuleRpcDispatcher.RuleEntry<?>> UNTYPED_CODEC = Registries.GAME_RULE.getCodec().dispatch("key", GameRuleRpcDispatcher.RuleEntry::gameRule, GameRuleRpcDispatcher.RuleEntry::untypedCodec);

    private static <T> MapCodec<? extends GameRuleRpcDispatcher.RuleEntry<T>> untypedCodec(GameRule<T> rule) {
        return rule.getCodec().fieldOf("value").xmap(entry -> new GameRuleRpcDispatcher.RuleEntry<Object>(rule, entry), GameRuleRpcDispatcher.RuleEntry::value);
    }

    private static <T> MapCodec<? extends GameRuleRpcDispatcher.RuleEntry<T>> typedCodec(GameRule<T> rule) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)StringIdentifiable.createCodec(GameRuleType::values).fieldOf("type").forGetter(entry -> entry.gameRule.getType()), (App)rule.getCodec().fieldOf("value").forGetter(GameRuleRpcDispatcher.RuleEntry::value)).apply((Applicative)instance, (type, value) -> GameRuleRpcDispatcher.RuleEntry.validateType(rule, type, value)));
    }

    private static <T> GameRuleRpcDispatcher.RuleEntry<T> validateType(GameRule<T> rule, GameRuleType type, T value) {
        if (rule.getType() != type) {
            throw new RpcException("Stated type \"" + String.valueOf(type) + "\" mismatches with actual type \"" + String.valueOf(rule.getType()) + "\" of gamerule \"" + rule.toShortString() + "\"");
        }
        return new GameRuleRpcDispatcher.RuleEntry<T>(rule, value);
    }
}
