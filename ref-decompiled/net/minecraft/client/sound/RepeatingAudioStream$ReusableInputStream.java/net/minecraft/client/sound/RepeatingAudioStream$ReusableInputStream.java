/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import java.io.FilterInputStream;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class RepeatingAudioStream.ReusableInputStream
extends FilterInputStream {
    RepeatingAudioStream.ReusableInputStream(InputStream stream) {
        super(stream);
    }

    @Override
    public void close() {
    }
}
