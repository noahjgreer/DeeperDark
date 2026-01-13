/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult$Error
 */
package net.minecraft.storage;

import com.mojang.serialization.DataResult;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ErrorReporter;

public record NbtReadView.DecodeError(String name, NbtElement element, DataResult.Error<?> error) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Failed to decode value '" + String.valueOf(this.element) + "' from field '" + this.name + "': " + this.error.message();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NbtReadView.DecodeError.class, "name;tag;error", "name", "element", "error"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NbtReadView.DecodeError.class, "name;tag;error", "name", "element", "error"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NbtReadView.DecodeError.class, "name;tag;error", "name", "element", "error"}, this, object);
    }
}
