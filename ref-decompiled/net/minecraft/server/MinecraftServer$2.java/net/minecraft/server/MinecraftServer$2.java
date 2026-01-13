/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.util.List;
import java.util.Locale;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleVisitor;
import net.minecraft.world.rule.GameRules;

class MinecraftServer.2
implements GameRuleVisitor {
    final /* synthetic */ List field_61878;
    final /* synthetic */ GameRules field_61879;

    MinecraftServer.2() {
        this.field_61878 = list;
        this.field_61879 = gameRules;
    }

    @Override
    public <T> void visit(GameRule<T> rule) {
        this.field_61878.add(String.format(Locale.ROOT, "%s=%s\n", rule.getId(), this.field_61879.getRuleValueName(rule)));
    }
}
