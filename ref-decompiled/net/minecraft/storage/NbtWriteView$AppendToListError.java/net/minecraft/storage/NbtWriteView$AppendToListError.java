/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult$Error
 */
package net.minecraft.storage;

import com.mojang.serialization.DataResult;
import net.minecraft.util.ErrorReporter;

public record NbtWriteView.AppendToListError(String name, Object value, DataResult.Error<?> error) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Failed to append value '" + String.valueOf(this.value) + "' to list '" + this.name + "': " + this.error.message();
    }
}
