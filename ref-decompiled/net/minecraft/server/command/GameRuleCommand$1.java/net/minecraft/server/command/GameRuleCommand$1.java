/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleVisitor;

static class GameRuleCommand.1
implements GameRuleVisitor {
    final /* synthetic */ LiteralArgumentBuilder field_64600;

    GameRuleCommand.1(LiteralArgumentBuilder literalArgumentBuilder) {
        this.field_64600 = literalArgumentBuilder;
    }

    @Override
    public <T> void visit(GameRule<T> rule) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal(rule.toShortString());
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = CommandManager.literal(rule.getId().toString());
        ((LiteralArgumentBuilder)this.field_64600.then(GameRuleCommand.appendRule(rule, literalArgumentBuilder))).then(GameRuleCommand.appendRule(rule, literalArgumentBuilder2));
    }
}
