/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPart$Cuboid
 *  net.minecraft.client.model.ModelPart$CuboidConsumer
 *  net.minecraft.client.model.ModelPart$Quad
 *  net.minecraft.client.model.ModelPart$Vertex
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.util.math.random.Random
 *  org.joml.Matrix3f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class ModelPart {
    public static final float field_37937 = 1.0f;
    public float originX;
    public float originY;
    public float originZ;
    public float pitch;
    public float yaw;
    public float roll;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;
    public boolean visible = true;
    public boolean hidden;
    private final List<Cuboid> cuboids;
    private final Map<String, ModelPart> children;
    private ModelTransform defaultTransform = ModelTransform.NONE;

    public ModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        this.cuboids = cuboids;
        this.children = children;
    }

    public ModelTransform getTransform() {
        return ModelTransform.of((float)this.originX, (float)this.originY, (float)this.originZ, (float)this.pitch, (float)this.yaw, (float)this.roll);
    }

    public ModelTransform getDefaultTransform() {
        return this.defaultTransform;
    }

    public void setDefaultTransform(ModelTransform transform) {
        this.defaultTransform = transform;
    }

    public void resetTransform() {
        this.setTransform(this.defaultTransform);
    }

    public void setTransform(ModelTransform transform) {
        this.originX = transform.x();
        this.originY = transform.y();
        this.originZ = transform.z();
        this.pitch = transform.pitch();
        this.yaw = transform.yaw();
        this.roll = transform.roll();
        this.xScale = transform.xScale();
        this.yScale = transform.yScale();
        this.zScale = transform.zScale();
    }

    public boolean hasChild(String child) {
        return this.children.containsKey(child);
    }

    public ModelPart getChild(String name) {
        ModelPart modelPart = (ModelPart)this.children.get(name);
        if (modelPart == null) {
            throw new NoSuchElementException("Can't find part " + name);
        }
        return modelPart;
    }

    public void setOrigin(float x, float y, float z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }

    public void setAngles(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, -1);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.applyTransform(matrices);
        if (!this.hidden) {
            this.renderCuboids(matrices.peek(), vertices, light, overlay, color);
        }
        for (ModelPart modelPart : this.children.values()) {
            modelPart.render(matrices, vertices, light, overlay, color);
        }
        matrices.pop();
    }

    public void rotate(Quaternionf quaternion) {
        Matrix3f matrix3f = new Matrix3f().rotationZYX(this.roll, this.yaw, this.pitch);
        Matrix3f matrix3f2 = matrix3f.rotate((Quaternionfc)quaternion);
        Vector3f vector3f = matrix3f2.getEulerAnglesZYX(new Vector3f());
        this.setAngles(vector3f.x, vector3f.y, vector3f.z);
    }

    public void collectVertices(MatrixStack matrices, Consumer<Vector3fc> collector) {
        this.forEachCuboid(matrices, (matrix, path, index, cuboid) -> {
            for (Quad quad : cuboid.sides) {
                for (Vertex vertex : quad.vertices()) {
                    float f = vertex.worldX();
                    float g = vertex.worldY();
                    float h = vertex.worldZ();
                    Vector3f vector3f = matrix.getPositionMatrix().transformPosition(f, g, h, new Vector3f());
                    collector.accept((Vector3fc)vector3f);
                }
            }
        });
    }

    public void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer) {
        this.forEachCuboid(matrices, consumer, "");
    }

    private void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer, String path) {
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.applyTransform(matrices);
        MatrixStack.Entry entry = matrices.peek();
        for (int i = 0; i < this.cuboids.size(); ++i) {
            consumer.accept(entry, path, i, (Cuboid)this.cuboids.get(i));
        }
        String string = path + "/";
        this.children.forEach((name, part) -> part.forEachCuboid(matrices, consumer, string + name));
        matrices.pop();
    }

    public void applyTransform(MatrixStack matrices) {
        matrices.translate(this.originX / 16.0f, this.originY / 16.0f, this.originZ / 16.0f);
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            matrices.multiply((Quaternionfc)new Quaternionf().rotationZYX(this.roll, this.yaw, this.pitch));
        }
        if (this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            matrices.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        for (Cuboid cuboid : this.cuboids) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, color);
        }
    }

    public Cuboid getRandomCuboid(Random random) {
        return (Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    public boolean isEmpty() {
        return this.cuboids.isEmpty();
    }

    public void moveOrigin(Vector3f vec3f) {
        this.originX += vec3f.x();
        this.originY += vec3f.y();
        this.originZ += vec3f.z();
    }

    public void rotate(Vector3f vec3f) {
        this.pitch += vec3f.x();
        this.yaw += vec3f.y();
        this.roll += vec3f.z();
    }

    public void scale(Vector3f vec3f) {
        this.xScale += vec3f.x();
        this.yScale += vec3f.y();
        this.zScale += vec3f.z();
    }

    public List<ModelPart> traverse() {
        ArrayList<ModelPart> list = new ArrayList<ModelPart>();
        list.add(this);
        this.forEachChild((key, part) -> list.add((ModelPart)part));
        return List.copyOf(list);
    }

    public Function<String, @Nullable ModelPart> createPartGetter() {
        HashMap<String, ModelPart> map = new HashMap<String, ModelPart>();
        map.put("root", this);
        this.forEachChild(map::putIfAbsent);
        return map::get;
    }

    private void forEachChild(BiConsumer<String, ModelPart> partBiConsumer) {
        for (Map.Entry entry : this.children.entrySet()) {
            partBiConsumer.accept((String)entry.getKey(), (ModelPart)entry.getValue());
        }
        for (ModelPart modelPart : this.children.values()) {
            modelPart.forEachChild(partBiConsumer);
        }
    }
}

