/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.util.Set;
import net.minecraft.util.Language;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleVisitor;

static class Bootstrap.1
implements GameRuleVisitor {
    final /* synthetic */ Language field_24373;
    final /* synthetic */ Set field_24374;

    Bootstrap.1() {
        this.field_24373 = language;
        this.field_24374 = set;
    }

    @Override
    public <T> void visit(GameRule<T> rule) {
        if (!this.field_24373.hasTranslation(rule.getTranslationKey())) {
            this.field_24374.add(rule.toShortString());
        }
    }
}
