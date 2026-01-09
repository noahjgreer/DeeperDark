package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public final class ModelPart {
   public static final float field_37937 = 1.0F;
   public float originX;
   public float originY;
   public float originZ;
   public float pitch;
   public float yaw;
   public float roll;
   public float xScale = 1.0F;
   public float yScale = 1.0F;
   public float zScale = 1.0F;
   public boolean visible = true;
   public boolean hidden;
   private final List cuboids;
   private final Map children;
   private ModelTransform defaultTransform;

   public ModelPart(List cuboids, Map children) {
      this.defaultTransform = ModelTransform.NONE;
      this.cuboids = cuboids;
      this.children = children;
   }

   public ModelTransform getTransform() {
      return ModelTransform.of(this.originX, this.originY, this.originZ, this.pitch, this.yaw, this.roll);
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

   public void copyTransform(ModelPart part) {
      this.xScale = part.xScale;
      this.yScale = part.yScale;
      this.zScale = part.zScale;
      this.pitch = part.pitch;
      this.yaw = part.yaw;
      this.roll = part.roll;
      this.originX = part.originX;
      this.originY = part.originY;
      this.originZ = part.originZ;
   }

   public boolean hasChild(String child) {
      return this.children.containsKey(child);
   }

   public ModelPart getChild(String name) {
      ModelPart modelPart = (ModelPart)this.children.get(name);
      if (modelPart == null) {
         throw new NoSuchElementException("Can't find part " + name);
      } else {
         return modelPart;
      }
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
      if (this.visible) {
         if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
            matrices.push();
            this.applyTransform(matrices);
            if (!this.hidden) {
               this.renderCuboids(matrices.peek(), vertices, light, overlay, color);
            }

            Iterator var6 = this.children.values().iterator();

            while(var6.hasNext()) {
               ModelPart modelPart = (ModelPart)var6.next();
               modelPart.render(matrices, vertices, light, overlay, color);
            }

            matrices.pop();
         }
      }
   }

   public void rotate(Quaternionf quaternion) {
      Matrix3f matrix3f = (new Matrix3f()).rotationZYX(this.roll, this.yaw, this.pitch);
      Matrix3f matrix3f2 = matrix3f.rotate(quaternion);
      Vector3f vector3f = matrix3f2.getEulerAnglesZYX(new Vector3f());
      this.setAngles(vector3f.x, vector3f.y, vector3f.z);
   }

   public void collectVertices(MatrixStack matrices, Set vertices) {
      this.forEachCuboid(matrices, (matrix, path, index, cuboid) -> {
         Quad[] var5 = cuboid.sides;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Quad quad = var5[var7];
            Vertex[] var9 = quad.vertices();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               Vertex vertex = var9[var11];
               float f = vertex.pos().x() / 16.0F;
               float g = vertex.pos().y() / 16.0F;
               float h = vertex.pos().z() / 16.0F;
               Vector3f vector3f = matrix.getPositionMatrix().transformPosition(f, g, h, new Vector3f());
               vertices.add(vector3f);
            }
         }

      });
   }

   public void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer) {
      this.forEachCuboid(matrices, consumer, "");
   }

   private void forEachCuboid(MatrixStack matrices, CuboidConsumer consumer, String path) {
      if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
         matrices.push();
         this.applyTransform(matrices);
         MatrixStack.Entry entry = matrices.peek();

         for(int i = 0; i < this.cuboids.size(); ++i) {
            consumer.accept(entry, path, i, (Cuboid)this.cuboids.get(i));
         }

         String string = path + "/";
         this.children.forEach((name, part) -> {
            part.forEachCuboid(matrices, consumer, string + name);
         });
         matrices.pop();
      }
   }

   public void applyTransform(MatrixStack matrices) {
      matrices.translate(this.originX / 16.0F, this.originY / 16.0F, this.originZ / 16.0F);
      if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
         matrices.multiply((new Quaternionf()).rotationZYX(this.roll, this.yaw, this.pitch));
      }

      if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
         matrices.scale(this.xScale, this.yScale, this.zScale);
      }

   }

   private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
      Iterator var6 = this.cuboids.iterator();

      while(var6.hasNext()) {
         Cuboid cuboid = (Cuboid)var6.next();
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

   public List traverse() {
      List list = new ArrayList();
      list.add(this);
      this.forEachChild((key, part) -> {
         list.add(part);
      });
      return List.copyOf(list);
   }

   public Function createPartGetter() {
      Map map = new HashMap();
      map.put("root", this);
      Objects.requireNonNull(map);
      this.forEachChild(map::putIfAbsent);
      Objects.requireNonNull(map);
      return map::get;
   }

   private void forEachChild(BiConsumer partBiConsumer) {
      Iterator var2 = this.children.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         partBiConsumer.accept((String)entry.getKey(), (ModelPart)entry.getValue());
      }

      var2 = this.children.values().iterator();

      while(var2.hasNext()) {
         ModelPart modelPart = (ModelPart)var2.next();
         modelPart.forEachChild(partBiConsumer);
      }

   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface CuboidConsumer {
      void accept(MatrixStack.Entry matrix, String path, int index, Cuboid cuboid);
   }

   @Environment(EnvType.CLIENT)
   public static class Cuboid {
      public final Quad[] sides;
      public final float minX;
      public final float minY;
      public final float minZ;
      public final float maxX;
      public final float maxY;
      public final float maxZ;

      public Cuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set sides) {
         this.minX = x;
         this.minY = y;
         this.minZ = z;
         this.maxX = x + sizeX;
         this.maxY = y + sizeY;
         this.maxZ = z + sizeZ;
         this.sides = new Quad[sides.size()];
         float f = x + sizeX;
         float g = y + sizeY;
         float h = z + sizeZ;
         x -= extraX;
         y -= extraY;
         z -= extraZ;
         f += extraX;
         g += extraY;
         h += extraZ;
         if (mirror) {
            float i = f;
            f = x;
            x = i;
         }

         Vertex vertex = new Vertex(x, y, z, 0.0F, 0.0F);
         Vertex vertex2 = new Vertex(f, y, z, 0.0F, 8.0F);
         Vertex vertex3 = new Vertex(f, g, z, 8.0F, 8.0F);
         Vertex vertex4 = new Vertex(x, g, z, 8.0F, 0.0F);
         Vertex vertex5 = new Vertex(x, y, h, 0.0F, 0.0F);
         Vertex vertex6 = new Vertex(f, y, h, 0.0F, 8.0F);
         Vertex vertex7 = new Vertex(f, g, h, 8.0F, 8.0F);
         Vertex vertex8 = new Vertex(x, g, h, 8.0F, 0.0F);
         float j = (float)u;
         float k = (float)u + sizeZ;
         float l = (float)u + sizeZ + sizeX;
         float m = (float)u + sizeZ + sizeX + sizeX;
         float n = (float)u + sizeZ + sizeX + sizeZ;
         float o = (float)u + sizeZ + sizeX + sizeZ + sizeX;
         float p = (float)v;
         float q = (float)v + sizeZ;
         float r = (float)v + sizeZ + sizeY;
         int s = 0;
         if (sides.contains(Direction.DOWN)) {
            this.sides[s++] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN);
         }

         if (sides.contains(Direction.UP)) {
            this.sides[s++] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP);
         }

         if (sides.contains(Direction.WEST)) {
            this.sides[s++] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST);
         }

         if (sides.contains(Direction.NORTH)) {
            this.sides[s++] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH);
         }

         if (sides.contains(Direction.EAST)) {
            this.sides[s++] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
         }

         if (sides.contains(Direction.SOUTH)) {
            this.sides[s] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
         }

      }

      public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
         Matrix4f matrix4f = entry.getPositionMatrix();
         Vector3f vector3f = new Vector3f();
         Quad[] var8 = this.sides;
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Quad quad = var8[var10];
            Vector3f vector3f2 = entry.transformNormal(quad.direction, vector3f);
            float f = vector3f2.x();
            float g = vector3f2.y();
            float h = vector3f2.z();
            Vertex[] var16 = quad.vertices;
            int var17 = var16.length;

            for(int var18 = 0; var18 < var17; ++var18) {
               Vertex vertex = var16[var18];
               float i = vertex.pos.x() / 16.0F;
               float j = vertex.pos.y() / 16.0F;
               float k = vertex.pos.z() / 16.0F;
               Vector3f vector3f3 = matrix4f.transformPosition(i, j, k, vector3f);
               vertexConsumer.vertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), color, vertex.u, vertex.v, overlay, light, f, g, h);
            }
         }

      }
   }

   @Environment(EnvType.CLIENT)
   public static record Quad(Vertex[] vertices, Vector3f direction) {
      final Vertex[] vertices;
      final Vector3f direction;

      public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
         this(vertices, direction.getUnitVector());
         float f = 0.0F / squishU;
         float g = 0.0F / squishV;
         vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
         vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
         vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
         vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
         if (flip) {
            int i = vertices.length;

            for(int j = 0; j < i / 2; ++j) {
               Vertex vertex = vertices[j];
               vertices[j] = vertices[i - 1 - j];
               vertices[i - 1 - j] = vertex;
            }
         }

         if (flip) {
            this.direction.mul(-1.0F, 1.0F, 1.0F);
         }

      }

      public Quad(Vertex[] vertexs, Vector3f vector3f) {
         this.vertices = vertexs;
         this.direction = vector3f;
      }

      public Vertex[] vertices() {
         return this.vertices;
      }

      public Vector3f direction() {
         return this.direction;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Vertex(Vector3f pos, float u, float v) {
      final Vector3f pos;
      final float u;
      final float v;

      public Vertex(float x, float y, float z, float u, float v) {
         this(new Vector3f(x, y, z), u, v);
      }

      public Vertex(Vector3f pos, float u, float v) {
         this.pos = pos;
         this.u = u;
         this.v = v;
      }

      public Vertex remap(float u, float v) {
         return new Vertex(this.pos, u, v);
      }

      public Vector3f pos() {
         return this.pos;
      }

      public float u() {
         return this.u;
      }

      public float v() {
         return this.v;
      }
   }
}
