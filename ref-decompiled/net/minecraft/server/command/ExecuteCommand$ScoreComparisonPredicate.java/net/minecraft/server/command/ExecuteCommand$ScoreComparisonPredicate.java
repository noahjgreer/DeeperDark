/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

@FunctionalInterface
static interface ExecuteCommand.ScoreComparisonPredicate {
    public boolean test(int var1, int var2);
}
