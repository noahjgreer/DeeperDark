/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface RepeatingAudioStream.DelegateFactory {
    public AudioStream create(InputStream var1) throws IOException;
}
