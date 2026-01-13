/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.ModelElementFace
 *  net.minecraft.client.render.model.json.ModelElementFace$UV
 *  net.minecraft.util.math.AxisRotation
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, // Could not load outer class - annotation placement on inner may be incorrect
@Nullable ModelElementFace.UV uvs, AxisRotation rotation) {
    private final @Nullable Direction cullFace;
    private final int tintIndex;
    private final String textureId;
    private final // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ModelElementFace.UV uvs;
    private final AxisRotation rotation;
    public static final int field_32789 = -1;

    public ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ModelElementFace.UV textureData, AxisRotation rotation) {
        this.cullFace = cullFace;
        this.tintIndex = tintIndex;
        this.textureId = textureId;
        this.uvs = textureData;
        this.rotation = rotation;
    }

    public static float getUValue(UV uv, AxisRotation rotation, int index) {
        return uv.getUVertices(rotation.rotate(index)) / 16.0f;
    }

    public static float getVValue(UV uv, AxisRotation rotation, int index) {
        return uv.getVVertices(rotation.rotate(index)) / 16.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this, object);
    }

    public @Nullable Direction cullFace() {
        return this.cullFace;
    }

    public int tintIndex() {
        return this.tintIndex;
    }

    public String textureId() {
        return this.textureId;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ModelElementFace.UV uvs() {
        return this.uvs;
    }

    public AxisRotation rotation() {
        return this.rotation;
    }
}

