/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.util.Collection;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;

public static interface TestRunContext.Batcher {
    public Collection<GameTestBatch> batch(Collection<GameTestState> var1);
}
