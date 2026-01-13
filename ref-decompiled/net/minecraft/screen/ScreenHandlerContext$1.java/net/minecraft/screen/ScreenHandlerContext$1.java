/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class ScreenHandlerContext.1
implements ScreenHandlerContext {
    ScreenHandlerContext.1() {
    }

    @Override
    public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
        return Optional.empty();
    }
}
