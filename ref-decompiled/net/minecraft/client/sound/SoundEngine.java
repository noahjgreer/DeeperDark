/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.AlUtil
 *  net.minecraft.client.sound.SoundEngine
 *  net.minecraft.client.sound.SoundEngine$RunMode
 *  net.minecraft.client.sound.SoundEngine$SourceSet
 *  net.minecraft.client.sound.SoundEngine$SourceSetImpl
 *  net.minecraft.client.sound.SoundListener
 *  net.minecraft.client.sound.Source
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.ALC11
 *  org.lwjgl.openal.ALCCapabilities
 *  org.lwjgl.openal.ALCapabilities
 *  org.lwjgl.openal.ALUtil
 *  org.lwjgl.system.MemoryStack
 *  org.slf4j.Logger
 */
package net.minecraft.client.sound;

import com.mojang.logging.LogUtils;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AlUtil;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundListener;
import net.minecraft.client.sound.Source;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SoundEngine {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_34945 = 0;
    private static final int field_31897 = 30;
    private long devicePointer;
    private long contextPointer;
    private boolean disconnectExtensionPresent;
    private @Nullable String deviceSpecifier;
    private static final SourceSet EMPTY_SOURCE_SET = new /* Unavailable Anonymous Inner Class!! */;
    private SourceSet streamingSources = EMPTY_SOURCE_SET;
    private SourceSet staticSources = EMPTY_SOURCE_SET;
    private final SoundListener listener = new SoundListener();

    public SoundEngine() {
        this.deviceSpecifier = SoundEngine.findAvailableDeviceSpecifier();
    }

    public void init(@Nullable String deviceSpecifier, boolean directionalAudio) {
        this.devicePointer = SoundEngine.openDeviceOrFallback((String)deviceSpecifier);
        this.disconnectExtensionPresent = false;
        ALCCapabilities aLCCapabilities = ALC.createCapabilities((long)this.devicePointer);
        if (AlUtil.checkAlcErrors((long)this.devicePointer, (String)"Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!aLCCapabilities.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = this.createAttributes(memoryStack, aLCCapabilities.ALC_SOFT_HRTF && directionalAudio);
            this.contextPointer = ALC10.alcCreateContext((long)this.devicePointer, (IntBuffer)intBuffer);
        }
        if (AlUtil.checkAlcErrors((long)this.devicePointer, (String)"Create context")) {
            throw new IllegalStateException("Unable to create OpenAL context");
        }
        ALC10.alcMakeContextCurrent((long)this.contextPointer);
        int i = this.getMonoSourceCount();
        int j = MathHelper.clamp((int)((int)MathHelper.sqrt((float)i)), (int)2, (int)8);
        int k = MathHelper.clamp((int)(i - j), (int)8, (int)255);
        this.streamingSources = new SourceSetImpl(k);
        this.staticSources = new SourceSetImpl(j);
        ALCapabilities aLCapabilities = AL.createCapabilities((ALCCapabilities)aLCCapabilities);
        AlUtil.checkErrors((String)"Initialization");
        if (!aLCapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable((int)512);
        if (!aLCapabilities.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        AlUtil.checkErrors((String)"Enable per-source distance models");
        LOGGER.info("OpenAL initialized on device {}", (Object)this.getCurrentDeviceName());
        this.disconnectExtensionPresent = ALC10.alcIsExtensionPresent((long)this.devicePointer, (CharSequence)"ALC_EXT_disconnect");
    }

    private IntBuffer createAttributes(MemoryStack stack, boolean directionalAudio) {
        int i = 5;
        IntBuffer intBuffer = stack.callocInt(11);
        int j = ALC10.alcGetInteger((long)this.devicePointer, (int)6548);
        if (j > 0) {
            intBuffer.put(6546).put(directionalAudio ? 1 : 0);
            intBuffer.put(6550).put(0);
        }
        intBuffer.put(6554).put(1);
        return intBuffer.put(0).flip();
    }

    private int getMonoSourceCount() {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = ALC10.alcGetInteger((long)this.devicePointer, (int)4098);
            if (AlUtil.checkAlcErrors((long)this.devicePointer, (String)"Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(i);
            ALC10.alcGetIntegerv((long)this.devicePointer, (int)4099, (IntBuffer)intBuffer);
            if (AlUtil.checkAlcErrors((long)this.devicePointer, (String)"Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int j = 0;
            while (j < i) {
                int k;
                if ((k = intBuffer.get(j++)) == 0) {
                    break;
                }
                int l = intBuffer.get(j++);
                if (k != 4112) continue;
                int n = l;
                return n;
            }
        }
        return 30;
    }

    public static @Nullable String findAvailableDeviceSpecifier() {
        if (!ALC10.alcIsExtensionPresent((long)0L, (CharSequence)"ALC_ENUMERATE_ALL_EXT")) {
            return null;
        }
        ALUtil.getStringList((long)0L, (int)4115);
        return ALC10.alcGetString((long)0L, (int)4114);
    }

    public String getCurrentDeviceName() {
        String string = ALC10.alcGetString((long)this.devicePointer, (int)4115);
        if (string == null) {
            string = ALC10.alcGetString((long)this.devicePointer, (int)4101);
        }
        if (string == null) {
            string = "Unknown";
        }
        return string;
    }

    public synchronized boolean updateDeviceSpecifier() {
        String string = SoundEngine.findAvailableDeviceSpecifier();
        if (Objects.equals(this.deviceSpecifier, string)) {
            return false;
        }
        this.deviceSpecifier = string;
        return true;
    }

    private static long openDeviceOrFallback(@Nullable String deviceSpecifier) {
        OptionalLong optionalLong = OptionalLong.empty();
        if (deviceSpecifier != null) {
            optionalLong = SoundEngine.openDevice((String)deviceSpecifier);
        }
        if (optionalLong.isEmpty()) {
            optionalLong = SoundEngine.openDevice((String)SoundEngine.findAvailableDeviceSpecifier());
        }
        if (optionalLong.isEmpty()) {
            optionalLong = SoundEngine.openDevice(null);
        }
        if (optionalLong.isEmpty()) {
            throw new IllegalStateException("Failed to open OpenAL device");
        }
        return optionalLong.getAsLong();
    }

    private static OptionalLong openDevice(@Nullable String deviceSpecifier) {
        long l = ALC10.alcOpenDevice((CharSequence)deviceSpecifier);
        if (l != 0L && !AlUtil.checkAlcErrors((long)l, (String)"Open device")) {
            return OptionalLong.of(l);
        }
        return OptionalLong.empty();
    }

    public void close() {
        this.streamingSources.close();
        this.staticSources.close();
        ALC10.alcDestroyContext((long)this.contextPointer);
        if (this.devicePointer != 0L) {
            ALC10.alcCloseDevice((long)this.devicePointer);
        }
    }

    public SoundListener getListener() {
        return this.listener;
    }

    public @Nullable Source createSource(RunMode mode) {
        return (mode == RunMode.STREAMING ? this.staticSources : this.streamingSources).createSource();
    }

    public void release(Source source) {
        if (!this.streamingSources.release(source) && !this.staticSources.release(source)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }

    public String getDebugString() {
        return String.format(Locale.ROOT, "Sounds: %d/%d + %d/%d", this.streamingSources.getSourceCount(), this.streamingSources.getMaxSourceCount(), this.staticSources.getSourceCount(), this.staticSources.getMaxSourceCount());
    }

    public List<String> getSoundDevices() {
        List list = ALUtil.getStringList((long)0L, (int)4115);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    public boolean isDeviceUnavailable() {
        return this.disconnectExtensionPresent && ALC11.alcGetInteger((long)this.devicePointer, (int)787) == 0;
    }
}

