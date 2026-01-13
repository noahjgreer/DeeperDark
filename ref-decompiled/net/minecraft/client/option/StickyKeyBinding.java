/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.option.KeyBinding$Category
 *  net.minecraft.client.option.StickyKeyBinding
 *  net.minecraft.client.util.InputUtil$Type
 */
package net.minecraft.client.option;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(value=EnvType.CLIENT)
public class StickyKeyBinding
extends KeyBinding {
    private final BooleanSupplier toggleGetter;
    private boolean resetOnScreenClose;
    private final boolean restore;

    public StickyKeyBinding(String id, int code, KeyBinding.Category category, BooleanSupplier toggleGetter, boolean restore) {
        this(id, InputUtil.Type.KEYSYM, code, category, toggleGetter, restore);
    }

    public StickyKeyBinding(String id, InputUtil.Type type, int code, KeyBinding.Category category, BooleanSupplier toggleGetter, boolean restore) {
        super(id, type, code, category);
        this.toggleGetter = toggleGetter;
        this.restore = restore;
    }

    protected boolean shouldSetOnGameFocus() {
        return super.shouldSetOnGameFocus() && !this.toggleGetter.getAsBoolean();
    }

    public void setPressed(boolean pressed) {
        if (this.toggleGetter.getAsBoolean()) {
            if (pressed) {
                super.setPressed(!this.isPressed());
            }
        } else {
            super.setPressed(pressed);
        }
    }

    protected void reset() {
        if (this.toggleGetter.getAsBoolean() && this.isPressed() || this.resetOnScreenClose) {
            this.resetOnScreenClose = true;
        }
        this.untoggle();
    }

    public boolean shouldRestoreOnScreenClose() {
        boolean bl = this.restore && this.toggleGetter.getAsBoolean() && this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.resetOnScreenClose;
        this.resetOnScreenClose = false;
        return bl;
    }

    protected void untoggle() {
        super.setPressed(false);
    }
}

