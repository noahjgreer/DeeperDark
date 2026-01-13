/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

static class ScreenHandlerContext.2
implements ScreenHandlerContext {
    final /* synthetic */ World field_17305;
    final /* synthetic */ BlockPos field_17306;

    ScreenHandlerContext.2() {
        this.field_17305 = world;
        this.field_17306 = blockPos;
    }

    @Override
    public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
        return Optional.of(getter.apply(this.field_17305, this.field_17306));
    }
}
