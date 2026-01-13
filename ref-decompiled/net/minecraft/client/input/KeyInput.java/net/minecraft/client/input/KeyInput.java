/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.util.InputUtil;

@Environment(value=EnvType.CLIENT)
public record KeyInput(@InputUtil.Keycode int key, int scancode, @AbstractInput.Modifier int modifiers) implements AbstractInput
{
    @Override
    public int getKeycode() {
        return this.key;
    }

    @Retention(value=RetentionPolicy.CLASS)
    @Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
    @Environment(value=EnvType.CLIENT)
    public static @interface KeyAction {
    }
}
