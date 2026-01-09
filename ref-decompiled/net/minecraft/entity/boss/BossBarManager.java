package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BossBarManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec CODEC;
   private final Map commandBossBars = Maps.newHashMap();

   @Nullable
   public CommandBossBar get(Identifier id) {
      return (CommandBossBar)this.commandBossBars.get(id);
   }

   public CommandBossBar add(Identifier id, Text displayName) {
      CommandBossBar commandBossBar = new CommandBossBar(id, displayName);
      this.commandBossBars.put(id, commandBossBar);
      return commandBossBar;
   }

   public void remove(CommandBossBar bossBar) {
      this.commandBossBars.remove(bossBar.getId());
   }

   public Collection getIds() {
      return this.commandBossBars.keySet();
   }

   public Collection getAll() {
      return this.commandBossBars.values();
   }

   public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
      Map map = Util.transformMapValues(this.commandBossBars, CommandBossBar::toSerialized);
      return (NbtCompound)CODEC.encodeStart(registries.getOps(NbtOps.INSTANCE), map).getOrThrow();
   }

   public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
      Map map = (Map)CODEC.parse(registries.getOps(NbtOps.INSTANCE), nbt).resultOrPartial((error) -> {
         LOGGER.error("Failed to parse boss bar events: {}", error);
      }).orElse(Map.of());
      map.forEach((id, serialized) -> {
         this.commandBossBars.put(id, CommandBossBar.fromSerialized(id, serialized));
      });
   }

   public void onPlayerConnect(ServerPlayerEntity player) {
      Iterator var2 = this.commandBossBars.values().iterator();

      while(var2.hasNext()) {
         CommandBossBar commandBossBar = (CommandBossBar)var2.next();
         commandBossBar.onPlayerConnect(player);
      }

   }

   public void onPlayerDisconnect(ServerPlayerEntity player) {
      Iterator var2 = this.commandBossBars.values().iterator();

      while(var2.hasNext()) {
         CommandBossBar commandBossBar = (CommandBossBar)var2.next();
         commandBossBar.onPlayerDisconnect(player);
      }

   }

   static {
      CODEC = Codec.unboundedMap(Identifier.CODEC, CommandBossBar.Serialized.CODEC);
   }
}
