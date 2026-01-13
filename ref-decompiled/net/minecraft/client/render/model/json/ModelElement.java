/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.ModelElement
 *  net.minecraft.client.render.model.json.ModelElementFace
 *  net.minecraft.client.render.model.json.ModelElementRotation
 *  net.minecraft.util.math.Direction
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementRotation;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ModelElement(Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces, @Nullable ModelElementRotation rotation, boolean shade, int lightEmission) {
    private final Vector3fc from;
    private final Vector3fc to;
    private final Map<Direction, ModelElementFace> faces;
    private final @Nullable ModelElementRotation rotation;
    private final boolean shade;
    private final int lightEmission;
    private static final boolean field_32785 = false;
    private static final float field_32786 = -16.0f;
    private static final float field_32787 = 32.0f;

    public ModelElement(Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces) {
        this(from, to, faces, null, true, 0);
    }

    public ModelElement(Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces, @Nullable ModelElementRotation rotation, boolean shade, int lightEmission) {
        this.from = from;
        this.to = to;
        this.faces = faces;
        this.rotation = rotation;
        this.shade = shade;
        this.lightEmission = lightEmission;
    }

    public Vector3fc from() {
        return this.from;
    }

    public Vector3fc to() {
        return this.to;
    }

    public Map<Direction, ModelElementFace> faces() {
        return this.faces;
    }

    public @Nullable ModelElementRotation rotation() {
        return this.rotation;
    }

    public boolean shade() {
        return this.shade;
    }

    public int lightEmission() {
        return this.lightEmission;
    }
}

