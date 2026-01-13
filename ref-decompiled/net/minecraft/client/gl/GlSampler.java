/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlConst
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GlSampler
 *  net.minecraft.client.gl.GlSampler$1
 *  net.minecraft.client.gl.GpuSampler
 *  org.lwjgl.opengl.GL33C
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import java.util.OptionalDouble;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlSampler;
import net.minecraft.client.gl.GpuSampler;
import org.lwjgl.opengl.GL33C;

@Environment(value=EnvType.CLIENT)
public class GlSampler
extends GpuSampler {
    private final int samplerId;
    private final AddressMode addressModeU;
    private final AddressMode addressModeV;
    private final FilterMode minFilterMode;
    private final FilterMode magFilterMode;
    private final int maxAnisotropy;
    private final OptionalDouble maxLevelOfDetail;
    private boolean closed;

    public GlSampler(AddressMode addressModeU, AddressMode addressModeV, FilterMode minFilterMode, FilterMode magFilterMode, int maxAnisotropy, OptionalDouble maxLevelOfDetail) {
        this.addressModeU = addressModeU;
        this.addressModeV = addressModeV;
        this.minFilterMode = minFilterMode;
        this.magFilterMode = magFilterMode;
        this.maxAnisotropy = maxAnisotropy;
        this.maxLevelOfDetail = maxLevelOfDetail;
        this.samplerId = GL33C.glGenSamplers();
        GL33C.glSamplerParameteri((int)this.samplerId, (int)10242, (int)GlConst.toGl((AddressMode)addressModeU));
        GL33C.glSamplerParameteri((int)this.samplerId, (int)10243, (int)GlConst.toGl((AddressMode)addressModeV));
        if (maxAnisotropy > 1) {
            GL33C.glSamplerParameterf((int)this.samplerId, (int)34046, (float)maxAnisotropy);
        }
        switch (1.field_63447[minFilterMode.ordinal()]) {
            case 1: {
                GL33C.glSamplerParameteri((int)this.samplerId, (int)10241, (int)9986);
                break;
            }
            case 2: {
                GL33C.glSamplerParameteri((int)this.samplerId, (int)10241, (int)9987);
            }
        }
        switch (1.field_63447[magFilterMode.ordinal()]) {
            case 1: {
                GL33C.glSamplerParameteri((int)this.samplerId, (int)10240, (int)9728);
                break;
            }
            case 2: {
                GL33C.glSamplerParameteri((int)this.samplerId, (int)10240, (int)9729);
            }
        }
        if (maxLevelOfDetail.isPresent()) {
            GL33C.glSamplerParameterf((int)this.samplerId, (int)33083, (float)((float)maxLevelOfDetail.getAsDouble()));
        }
    }

    public int getSamplerId() {
        return this.samplerId;
    }

    public AddressMode getAddressModeU() {
        return this.addressModeU;
    }

    public AddressMode getAddressModeV() {
        return this.addressModeV;
    }

    public FilterMode getMinFilterMode() {
        return this.minFilterMode;
    }

    public FilterMode getMagFilterMode() {
        return this.magFilterMode;
    }

    public int getMaxAnisotropy() {
        return this.maxAnisotropy;
    }

    public OptionalDouble getMaxLevelOfDetail() {
        return this.maxLevelOfDetail;
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            GL33C.glDeleteSamplers((int)this.samplerId);
        }
    }

    public boolean isClosed() {
        return this.closed;
    }
}

