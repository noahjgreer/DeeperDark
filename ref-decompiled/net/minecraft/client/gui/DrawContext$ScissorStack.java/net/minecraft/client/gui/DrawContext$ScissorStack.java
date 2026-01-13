/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class DrawContext.ScissorStack {
    private final Deque<ScreenRect> stack = new ArrayDeque<ScreenRect>();

    DrawContext.ScissorStack() {
    }

    public ScreenRect push(ScreenRect rect) {
        ScreenRect screenRect = this.stack.peekLast();
        if (screenRect != null) {
            ScreenRect screenRect2 = Objects.requireNonNullElse(rect.intersection(screenRect), ScreenRect.empty());
            this.stack.addLast(screenRect2);
            return screenRect2;
        }
        this.stack.addLast(rect);
        return rect;
    }

    public @Nullable ScreenRect pop() {
        if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
        }
        this.stack.removeLast();
        return this.stack.peekLast();
    }

    public @Nullable ScreenRect peekLast() {
        return this.stack.peekLast();
    }

    public boolean contains(int x, int y) {
        if (this.stack.isEmpty()) {
            return true;
        }
        return this.stack.peek().contains(x, y);
    }
}
