package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.log.DebugSampleType;

public class SampleSubscriptionTracker {
   public static final int STOP_TRACK_TICK = 200;
   public static final int STOP_TRACK_MS = 10000;
   private final PlayerManager playerManager;
   private final Map subscriptionMap;
   private final Queue pendingQueue = new LinkedList();

   public SampleSubscriptionTracker(PlayerManager playerManager) {
      this.playerManager = playerManager;
      this.subscriptionMap = Util.mapEnum(DebugSampleType.class, (type) -> {
         return Maps.newHashMap();
      });
   }

   public boolean shouldPush(DebugSampleType type) {
      return !((Map)this.subscriptionMap.get(type)).isEmpty();
   }

   public void sendPacket(DebugSampleS2CPacket packet) {
      Set set = ((Map)this.subscriptionMap.get(packet.debugSampleType())).keySet();
      Iterator var3 = set.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         serverPlayerEntity.networkHandler.sendPacket(packet);
      }

   }

   public void addPlayer(ServerPlayerEntity player, DebugSampleType type) {
      if (this.playerManager.isOperator(player.getGameProfile())) {
         this.pendingQueue.add(new PlayerSubscriptionData(player, type));
      }

   }

   public void tick(int tick) {
      long l = Util.getMeasuringTimeMs();
      this.onSubscription(l, tick);
      this.onUnsubscription(l, tick);
   }

   private void onSubscription(long time, int tick) {
      Iterator var4 = this.pendingQueue.iterator();

      while(var4.hasNext()) {
         PlayerSubscriptionData playerSubscriptionData = (PlayerSubscriptionData)var4.next();
         ((Map)this.subscriptionMap.get(playerSubscriptionData.sampleType())).put(playerSubscriptionData.player(), new MeasureTimeTick(time, tick));
      }

   }

   private void onUnsubscription(long measuringTimeMs, int tick) {
      Iterator var4 = this.subscriptionMap.values().iterator();

      while(var4.hasNext()) {
         Map map = (Map)var4.next();
         map.entrySet().removeIf((entry) -> {
            boolean bl = !this.playerManager.isOperator(((ServerPlayerEntity)entry.getKey()).getGameProfile());
            MeasureTimeTick measureTimeTick = (MeasureTimeTick)entry.getValue();
            return bl || tick > measureTimeTick.tick() + 200 && measuringTimeMs > measureTimeTick.millis() + 10000L;
         });
      }

   }

   static record PlayerSubscriptionData(ServerPlayerEntity player, DebugSampleType sampleType) {
      PlayerSubscriptionData(ServerPlayerEntity serverPlayerEntity, DebugSampleType debugSampleType) {
         this.player = serverPlayerEntity;
         this.sampleType = debugSampleType;
      }

      public ServerPlayerEntity player() {
         return this.player;
      }

      public DebugSampleType sampleType() {
         return this.sampleType;
      }
   }

   static record MeasureTimeTick(long millis, int tick) {
      MeasureTimeTick(long l, int i) {
         this.millis = l;
         this.tick = i;
      }

      public long millis() {
         return this.millis;
      }

      public int tick() {
         return this.tick;
      }
   }
}
