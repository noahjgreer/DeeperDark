package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugHiveCustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.Renderer {
   private static final boolean field_32841 = true;
   private static final boolean field_32842 = true;
   private static final boolean field_32843 = true;
   private static final boolean field_32844 = true;
   private static final boolean field_32845 = true;
   private static final boolean field_32846 = false;
   private static final boolean field_32847 = true;
   private static final boolean field_32848 = true;
   private static final boolean field_32849 = true;
   private static final boolean field_32850 = true;
   private static final boolean field_32851 = true;
   private static final boolean field_32852 = true;
   private static final boolean field_32853 = true;
   private static final boolean field_32854 = true;
   private static final int HIVE_RANGE = 30;
   private static final int BEE_RANGE = 30;
   private static final int TARGET_ENTITY_RANGE = 8;
   private static final int field_32858 = 20;
   private static final float DEFAULT_DRAWN_STRING_SIZE = 0.02F;
   private static final int ORANGE = -23296;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private final MinecraftClient client;
   private final Map hives = new HashMap();
   private final Map bees = new HashMap();
   @Nullable
   private UUID targetedEntity;

   public BeeDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void clear() {
      this.hives.clear();
      this.bees.clear();
      this.targetedEntity = null;
   }

   public void addHive(DebugHiveCustomPayload.HiveInfo hive, long time) {
      this.hives.put(hive.pos(), new Hive(hive, time));
   }

   public void addBee(DebugBeeCustomPayload.Bee bee) {
      this.bees.put(bee.uuid(), bee);
   }

   public void removeBee(int id) {
      this.bees.values().removeIf((bee) -> {
         return bee.entityId() == id;
      });
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      this.removeOutdatedHives();
      this.removeInvalidBees();
      this.render(matrices, vertexConsumers);
      if (!this.client.player.isSpectator()) {
         this.updateTargetedEntity();
      }

   }

   private void removeInvalidBees() {
      this.bees.entrySet().removeIf((bee) -> {
         return this.client.world.getEntityById(((DebugBeeCustomPayload.Bee)bee.getValue()).entityId()) == null;
      });
   }

   private void removeOutdatedHives() {
      long l = this.client.world.getTime() - 20L;
      this.hives.entrySet().removeIf((hive) -> {
         return ((Hive)hive.getValue()).lastSeen() < l;
      });
   }

   private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
      BlockPos blockPos = this.getCameraPos().getBlockPos();
      this.bees.values().forEach((bee) -> {
         if (this.isInRange(bee)) {
            this.drawBee(matrices, vertexConsumers, bee);
         }

      });
      this.drawFlowers(matrices, vertexConsumers);
      Iterator var4 = this.hives.keySet().iterator();

      while(var4.hasNext()) {
         BlockPos blockPos2 = (BlockPos)var4.next();
         if (blockPos.isWithinDistance(blockPos2, 30.0)) {
            drawHive(matrices, vertexConsumers, blockPos2);
         }
      }

      Map map = this.getBlacklistingBees();
      this.hives.values().forEach((hive) -> {
         if (blockPos.isWithinDistance(hive.info.pos(), 30.0)) {
            Set set = (Set)map.get(hive.info.pos());
            this.drawHiveInfo(matrices, vertexConsumers, hive.info, (Collection)(set == null ? Sets.newHashSet() : set));
         }

      });
      this.getBeesByHive().forEach((hive, bees) -> {
         if (blockPos.isWithinDistance(hive, 30.0)) {
            this.drawHiveBees(matrices, vertexConsumers, hive, bees);
         }

      });
   }

   private Map getBlacklistingBees() {
      Map map = Maps.newHashMap();
      this.bees.values().forEach((bee) -> {
         bee.disallowedHives().forEach((pos) -> {
            ((Set)map.computeIfAbsent(pos, (pos2) -> {
               return Sets.newHashSet();
            })).add(bee.uuid());
         });
      });
      return map;
   }

   private void drawFlowers(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
      Map map = Maps.newHashMap();
      this.bees.values().forEach((bee) -> {
         if (bee.flowerPos() != null) {
            ((Set)map.computeIfAbsent(bee.flowerPos(), (flower) -> {
               return new HashSet();
            })).add(bee.uuid());
         }

      });
      map.forEach((flowerPos, bees) -> {
         Set set = (Set)bees.stream().map(NameGenerator::name).collect(Collectors.toSet());
         int i = 1;
         drawString(matrices, vertexConsumers, set.toString(), (BlockPos)flowerPos, i++, -256);
         drawString(matrices, vertexConsumers, "Flower", (BlockPos)flowerPos, i++, -1);
         float f = 0.05F;
         DebugRenderer.drawBox(matrices, vertexConsumers, flowerPos, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String toString(Collection bees) {
      if (bees.isEmpty()) {
         return "-";
      } else {
         return bees.size() > 3 ? bees.size() + " bees" : ((Set)bees.stream().map(NameGenerator::name).collect(Collectors.toSet())).toString();
      }
   }

   private static void drawHive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos) {
      float f = 0.05F;
      DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void drawHiveBees(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, List bees) {
      float f = 0.05F;
      DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      drawString(matrices, vertexConsumers, "" + String.valueOf(bees), (BlockPos)pos, 0, -256);
      drawString(matrices, vertexConsumers, "Ghost Hive", (BlockPos)pos, 1, -65536);
   }

   private void drawHiveInfo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugHiveCustomPayload.HiveInfo hive, Collection blacklistingBees) {
      int i = 0;
      if (!blacklistingBees.isEmpty()) {
         drawString(matrices, vertexConsumers, "Blacklisted by " + toString(blacklistingBees), hive, i++, -65536);
      }

      drawString(matrices, vertexConsumers, "Out: " + toString(this.getBeesForHive(hive.pos())), hive, i++, -3355444);
      if (hive.occupantCount() == 0) {
         drawString(matrices, vertexConsumers, "In: -", (DebugHiveCustomPayload.HiveInfo)hive, i++, -256);
      } else if (hive.occupantCount() == 1) {
         drawString(matrices, vertexConsumers, "In: 1 bee", (DebugHiveCustomPayload.HiveInfo)hive, i++, -256);
      } else {
         drawString(matrices, vertexConsumers, "In: " + hive.occupantCount() + " bees", (DebugHiveCustomPayload.HiveInfo)hive, i++, -256);
      }

      int var6 = hive.honeyLevel();
      drawString(matrices, vertexConsumers, "Honey: " + var6, (DebugHiveCustomPayload.HiveInfo)hive, i++, -23296);
      drawString(matrices, vertexConsumers, hive.hiveType() + (hive.sedated() ? " (sedated)" : ""), (DebugHiveCustomPayload.HiveInfo)hive, i++, -1);
   }

   private void drawPath(MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugBeeCustomPayload.Bee bee) {
      if (bee.path() != null) {
         PathfindingDebugRenderer.drawPath(matrices, vertexConsumers, bee.path(), 0.5F, false, false, this.getCameraPos().getPos().getX(), this.getCameraPos().getPos().getY(), this.getCameraPos().getPos().getZ());
      }

   }

   private void drawBee(MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugBeeCustomPayload.Bee bee) {
      boolean bl = this.isTargeted(bee);
      int i = 0;
      drawString(matrices, vertexConsumers, bee.pos(), i++, bee.toString(), -1, 0.03F);
      if (bee.hivePos() == null) {
         drawString(matrices, vertexConsumers, bee.pos(), i++, "No hive", -98404, 0.02F);
      } else {
         drawString(matrices, vertexConsumers, bee.pos(), i++, "Hive: " + this.getPositionString(bee, bee.hivePos()), -256, 0.02F);
      }

      if (bee.flowerPos() == null) {
         drawString(matrices, vertexConsumers, bee.pos(), i++, "No flower", -98404, 0.02F);
      } else {
         drawString(matrices, vertexConsumers, bee.pos(), i++, "Flower: " + this.getPositionString(bee, bee.flowerPos()), -256, 0.02F);
      }

      Iterator var6 = bee.goals().iterator();

      while(var6.hasNext()) {
         String string = (String)var6.next();
         drawString(matrices, vertexConsumers, bee.pos(), i++, string, -16711936, 0.02F);
      }

      if (bl) {
         this.drawPath(matrices, vertexConsumers, bee);
      }

      if (bee.travelTicks() > 0) {
         int j = bee.travelTicks() < 2400 ? -3355444 : -23296;
         drawString(matrices, vertexConsumers, bee.pos(), i++, "Travelling: " + bee.travelTicks() + " ticks", j, 0.02F);
      }

   }

   private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, DebugHiveCustomPayload.HiveInfo hive, int line, int color) {
      drawString(matrices, vertexConsumers, string, hive.pos(), line, color);
   }

   private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, BlockPos pos, int line, int color) {
      double d = 1.3;
      double e = 0.2;
      double f = (double)pos.getX() + 0.5;
      double g = (double)pos.getY() + 1.3 + (double)line * 0.2;
      double h = (double)pos.getZ() + 0.5;
      DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, 0.02F, true, 0.0F, true);
   }

   private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Position pos, int line, String string, int color, float size) {
      double d = 2.4;
      double e = 0.25;
      BlockPos blockPos = BlockPos.ofFloored(pos);
      double f = (double)blockPos.getX() + 0.5;
      double g = pos.getY() + 2.4 + (double)line * 0.25;
      double h = (double)blockPos.getZ() + 0.5;
      float i = 0.5F;
      DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, size, false, 0.5F, true);
   }

   private Camera getCameraPos() {
      return this.client.gameRenderer.getCamera();
   }

   private Set getBeeNamesForHive(DebugHiveCustomPayload.HiveInfo hive) {
      return (Set)this.getBeesForHive(hive.pos()).stream().map(NameGenerator::name).collect(Collectors.toSet());
   }

   private String getPositionString(DebugBeeCustomPayload.Bee bee, BlockPos pos) {
      double d = Math.sqrt(pos.getSquaredDistance(bee.pos()));
      double e = (double)Math.round(d * 10.0) / 10.0;
      String var10000 = pos.toShortString();
      return var10000 + " (dist " + e + ")";
   }

   private boolean isTargeted(DebugBeeCustomPayload.Bee bee) {
      return Objects.equals(this.targetedEntity, bee.uuid());
   }

   private boolean isInRange(DebugBeeCustomPayload.Bee bee) {
      PlayerEntity playerEntity = this.client.player;
      BlockPos blockPos = BlockPos.ofFloored(playerEntity.getX(), bee.pos().getY(), playerEntity.getZ());
      BlockPos blockPos2 = BlockPos.ofFloored(bee.pos());
      return blockPos.isWithinDistance(blockPos2, 30.0);
   }

   private Collection getBeesForHive(BlockPos hivePos) {
      return (Collection)this.bees.values().stream().filter((bee) -> {
         return bee.isHiveAt(hivePos);
      }).map(DebugBeeCustomPayload.Bee::uuid).collect(Collectors.toSet());
   }

   private Map getBeesByHive() {
      Map map = Maps.newHashMap();
      Iterator var2 = this.bees.values().iterator();

      while(var2.hasNext()) {
         DebugBeeCustomPayload.Bee bee = (DebugBeeCustomPayload.Bee)var2.next();
         if (bee.hivePos() != null && !this.hives.containsKey(bee.hivePos())) {
            ((List)map.computeIfAbsent(bee.hivePos(), (hive) -> {
               return Lists.newArrayList();
            })).add(bee.getName());
         }
      }

      return map;
   }

   private void updateTargetedEntity() {
      DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent((entity) -> {
         this.targetedEntity = entity.getUuid();
      });
   }

   @Environment(EnvType.CLIENT)
   private static record Hive(DebugHiveCustomPayload.HiveInfo info, long lastSeen) {
      final DebugHiveCustomPayload.HiveInfo info;

      Hive(DebugHiveCustomPayload.HiveInfo hiveInfo, long l) {
         this.info = hiveInfo;
         this.lastSeen = l;
      }

      public DebugHiveCustomPayload.HiveInfo info() {
         return this.info;
      }

      public long lastSeen() {
         return this.lastSeen;
      }
   }
}
