/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.model;

import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AffineTransformations;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public class ModelRotation
implements ModelBakeSettings {
    private static final Map<DirectionTransformation, ModelRotation> BY_DIRECTION_TRANSFORMATION = Util.mapEnum(DirectionTransformation.class, ModelRotation::new);
    public static final ModelRotation IDENTITY = ModelRotation.fromDirectionTransformation(DirectionTransformation.IDENTITY);
    final DirectionTransformation transformation;
    final AffineTransformation rotation;
    final Map<Direction, Matrix4fc> faces = new EnumMap<Direction, Matrix4fc>(Direction.class);
    final Map<Direction, Matrix4fc> invertedFaces = new EnumMap<Direction, Matrix4fc>(Direction.class);
    private final UVModel uvModel = new UVModel(this);

    private ModelRotation(DirectionTransformation transformation) {
        this.transformation = transformation;
        this.rotation = transformation != DirectionTransformation.IDENTITY ? new AffineTransformation((Matrix4fc)new Matrix4f(transformation.getMatrix())) : AffineTransformation.identity();
        for (Direction direction : Direction.values()) {
            Matrix4fc matrix4fc = AffineTransformations.getTransformed(this.rotation, direction).getMatrix();
            this.faces.put(direction, matrix4fc);
            this.invertedFaces.put(direction, (Matrix4fc)matrix4fc.invertAffine(new Matrix4f()));
        }
    }

    @Override
    public AffineTransformation getRotation() {
        return this.rotation;
    }

    public static ModelRotation fromDirectionTransformation(DirectionTransformation directionTransformation) {
        return BY_DIRECTION_TRANSFORMATION.get(directionTransformation);
    }

    public ModelBakeSettings getUVModel() {
        return this.uvModel;
    }

    public String toString() {
        return "simple[" + this.transformation.asString() + "]";
    }

    @Environment(value=EnvType.CLIENT)
    record UVModel(ModelRotation parent) implements ModelBakeSettings
    {
        @Override
        public AffineTransformation getRotation() {
            return this.parent.rotation;
        }

        @Override
        public Matrix4fc forward(Direction facing) {
            return this.parent.faces.getOrDefault(facing, TRANSFORM_NONE);
        }

        @Override
        public Matrix4fc reverse(Direction facing) {
            return this.parent.invertedFaces.getOrDefault(facing, TRANSFORM_NONE);
        }

        @Override
        public String toString() {
            return "uvLocked[" + this.parent.transformation.asString() + "]";
        }
    }
}
