/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.UploadResult;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class UploadResult.Builder {
    private int statusCode = -1;
    private @Nullable String errorMessage;

    public UploadResult.Builder withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public UploadResult.Builder withErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public UploadResult build() {
        return new UploadResult(this.statusCode, this.errorMessage);
    }
}
