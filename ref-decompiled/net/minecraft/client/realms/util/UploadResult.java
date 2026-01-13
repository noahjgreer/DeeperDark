/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.util.UploadResult
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record UploadResult(int statusCode, @Nullable String errorMessage) {
    private final int statusCode;
    private final @Nullable String errorMessage;

    public UploadResult(int statusCode, @Nullable String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public @Nullable String getErrorMessage() {
        if (this.statusCode < 200 || this.statusCode >= 300) {
            if (this.statusCode == 400 && this.errorMessage != null) {
                return this.errorMessage;
            }
            return String.valueOf(this.statusCode);
        }
        return null;
    }

    public int statusCode() {
        return this.statusCode;
    }

    public @Nullable String errorMessage() {
        return this.errorMessage;
    }
}

