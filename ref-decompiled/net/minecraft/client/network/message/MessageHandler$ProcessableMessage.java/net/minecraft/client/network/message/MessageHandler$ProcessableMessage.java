/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network.message;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageSignatureData;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
record MessageHandler.ProcessableMessage(@Nullable MessageSignatureData signature, BooleanSupplier handler) {
    public boolean accept() {
        return this.handler.getAsBoolean();
    }
}
