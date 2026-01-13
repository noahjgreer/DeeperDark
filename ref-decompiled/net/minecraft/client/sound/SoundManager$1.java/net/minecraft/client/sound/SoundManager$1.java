/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.reflect.TypeToken
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import com.google.gson.reflect.TypeToken;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundEntry;

@Environment(value=EnvType.CLIENT)
class SoundManager.1
extends TypeToken<Map<String, SoundEntry>> {
    SoundManager.1() {
    }
}
