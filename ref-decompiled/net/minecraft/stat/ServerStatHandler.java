package net.minecraft.stat;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatHandler extends StatHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec CODEC;
   private final MinecraftServer server;
   private final File file;
   private final Set pendingStats = Sets.newHashSet();

   private static Codec createCodec(StatType statType) {
      Codec codec = statType.getRegistry().getCodec();
      Objects.requireNonNull(statType);
      Codec codec2 = codec.flatComapMap(statType::getOrCreateStat, (stat) -> {
         return stat.getType() == statType ? DataResult.success(stat.getValue()) : DataResult.error(() -> {
            String var10000 = String.valueOf(statType);
            return "Expected type " + var10000 + ", but got " + String.valueOf(stat.getType());
         });
      });
      return Codec.unboundedMap(codec2, Codec.INT);
   }

   public ServerStatHandler(MinecraftServer server, File file) {
      this.server = server;
      this.file = file;
      if (file.isFile()) {
         try {
            this.parse(server.getDataFixer(), FileUtils.readFileToString(file));
         } catch (IOException var4) {
            LOGGER.error("Couldn't read statistics file {}", file, var4);
         } catch (JsonParseException var5) {
            LOGGER.error("Couldn't parse statistics file {}", file, var5);
         }
      }

   }

   public void save() {
      try {
         FileUtils.writeStringToFile(this.file, this.asString());
      } catch (IOException var2) {
         LOGGER.error("Couldn't save stats", var2);
      }

   }

   public void setStat(PlayerEntity player, Stat stat, int value) {
      super.setStat(player, stat, value);
      this.pendingStats.add(stat);
   }

   private Set takePendingStats() {
      Set set = Sets.newHashSet(this.pendingStats);
      this.pendingStats.clear();
      return set;
   }

   public void parse(DataFixer dataFixer, String json) {
      try {
         JsonElement jsonElement = StrictJsonParser.parse(json);
         if (jsonElement.isJsonNull()) {
            LOGGER.error("Unable to parse Stat data from {}", this.file);
            return;
         }

         Dynamic dynamic = new Dynamic(JsonOps.INSTANCE, jsonElement);
         dynamic = DataFixTypes.STATS.update(dataFixer, dynamic, NbtHelper.getDataVersion((Dynamic)dynamic, 1343));
         this.statMap.putAll((Map)CODEC.parse(dynamic.get("stats").orElseEmptyMap()).resultOrPartial((string) -> {
            LOGGER.error("Failed to parse statistics for {}: {}", this.file, string);
         }).orElse(Map.of()));
      } catch (JsonParseException var5) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, var5);
      }

   }

   protected String asString() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("stats", (JsonElement)CODEC.encodeStart(JsonOps.INSTANCE, this.statMap).getOrThrow());
      jsonObject.addProperty("DataVersion", SharedConstants.getGameVersion().dataVersion().id());
      return jsonObject.toString();
   }

   public void updateStatSet() {
      this.pendingStats.addAll(this.statMap.keySet());
   }

   public void sendStats(ServerPlayerEntity player) {
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      Iterator var3 = this.takePendingStats().iterator();

      while(var3.hasNext()) {
         Stat stat = (Stat)var3.next();
         object2IntMap.put(stat, this.getStat(stat));
      }

      player.networkHandler.sendPacket(new StatisticsS2CPacket(object2IntMap));
   }

   static {
      CODEC = Codec.dispatchedMap(Registries.STAT_TYPE.getCodec(), Util.memoize(ServerStatHandler::createCodec)).xmap((statsByTypes) -> {
         Map map = new HashMap();
         statsByTypes.forEach((type, stats) -> {
            map.putAll(stats);
         });
         return map;
      }, (stats) -> {
         return (Map)stats.entrySet().stream().collect(Collectors.groupingBy((entry) -> {
            return ((Stat)entry.getKey()).getType();
         }, Util.toMap()));
      });
   }
}
