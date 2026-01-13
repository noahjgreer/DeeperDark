/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.client.render.model.ModelRotation
 *  net.minecraft.client.render.model.ModelRotation$UVModel
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.AffineTransformation
 *  net.minecraft.util.math.AffineTransformations
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.DirectionTransformation
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.model;

import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
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
    public static final ModelRotation IDENTITY = ModelRotation.fromDirectionTransformation((DirectionTransformation)DirectionTransformation.IDENTITY);
    final DirectionTransformation transformation;
    final AffineTransformation rotation;
    final Map<Direction, Matrix4fc> faces = new EnumMap(Direction.class);
    final Map<Direction, Matrix4fc> invertedFaces = new EnumMap(Direction.class);
    private final UVModel uvModel = new UVModel(this);

    private ModelRotation(DirectionTransformation transformation) {
        this.transformation = transformation;
        this.rotation = transformation != DirectionTransformation.IDENTITY ? new AffineTransformation((Matrix4fc)new Matrix4f(transformation.getMatrix())) : AffineTransformation.identity();
        for (Direction direction : Direction.values()) {
            Matrix4fc matrix4fc = AffineTransformations.getTransformed((AffineTransformation)this.rotation, (Direction)direction).getMatrix();
            this.faces.put(direction, matrix4fc);
            this.invertedFaces.put(direction, matrix4fc.invertAffine(new Matrix4f()));
        }
    }

    public AffineTransformation getRotation() {
        return this.rotation;
    }

    public static ModelRotation fromDirectionTransformation(DirectionTransformation directionTransformation) {
        return (ModelRotation)BY_DIRECTION_TRANSFORMATION.get(directionTransformation);
    }

    public ModelBakeSettings getUVModel() {
        return this.uvModel;
    }

    public String toString() {
        return "simple[" + this.transformation.asString() + "]";
    }
}

