/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import net.minecraft.test.GameTestException;
import net.minecraft.text.Text;

public class TimedTaskRunner.Trigger {
    private static final int UNTRIGGERED_TICK = -1;
    private int triggeredTick = -1;

    void trigger(int tick) {
        if (this.triggeredTick != -1) {
            throw new IllegalStateException("Condition already triggered at " + this.triggeredTick);
        }
        this.triggeredTick = tick;
    }

    public void checkTrigger() {
        int i = TimedTaskRunner.this.test.getTick();
        if (this.triggeredTick != i) {
            if (this.triggeredTick == -1) {
                throw new GameTestException(Text.translatable("test.error.sequence.condition_not_triggered"), i);
            }
            throw new GameTestException(Text.translatable("test.error.sequence.condition_already_triggered", this.triggeredTick), i);
        }
    }
}
