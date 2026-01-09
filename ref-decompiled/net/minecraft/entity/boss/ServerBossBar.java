package net.minecraft.entity.boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ServerBossBar extends BossBar {
   private final Set players = Sets.newHashSet();
   private final Set unmodifiablePlayers;
   private boolean visible;

   public ServerBossBar(Text displayName, BossBar.Color color, BossBar.Style style) {
      super(MathHelper.randomUuid(), displayName, color, style);
      this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
      this.visible = true;
   }

   public void setPercent(float percent) {
      if (percent != this.percent) {
         super.setPercent(percent);
         this.sendPacket(BossBarS2CPacket::updateProgress);
      }

   }

   public void setColor(BossBar.Color color) {
      if (color != this.color) {
         super.setColor(color);
         this.sendPacket(BossBarS2CPacket::updateStyle);
      }

   }

   public void setStyle(BossBar.Style style) {
      if (style != this.style) {
         super.setStyle(style);
         this.sendPacket(BossBarS2CPacket::updateStyle);
      }

   }

   public BossBar setDarkenSky(boolean darkenSky) {
      if (darkenSky != this.darkenSky) {
         super.setDarkenSky(darkenSky);
         this.sendPacket(BossBarS2CPacket::updateProperties);
      }

      return this;
   }

   public BossBar setDragonMusic(boolean dragonMusic) {
      if (dragonMusic != this.dragonMusic) {
         super.setDragonMusic(dragonMusic);
         this.sendPacket(BossBarS2CPacket::updateProperties);
      }

      return this;
   }

   public BossBar setThickenFog(boolean thickenFog) {
      if (thickenFog != this.thickenFog) {
         super.setThickenFog(thickenFog);
         this.sendPacket(BossBarS2CPacket::updateProperties);
      }

      return this;
   }

   public void setName(Text name) {
      if (!Objects.equal(name, this.name)) {
         super.setName(name);
         this.sendPacket(BossBarS2CPacket::updateName);
      }

   }

   private void sendPacket(Function bossBarToPacketFunction) {
      if (this.visible) {
         BossBarS2CPacket bossBarS2CPacket = (BossBarS2CPacket)bossBarToPacketFunction.apply(this);
         Iterator var3 = this.players.iterator();

         while(var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
            serverPlayerEntity.networkHandler.sendPacket(bossBarS2CPacket);
         }
      }

   }

   public void addPlayer(ServerPlayerEntity player) {
      if (this.players.add(player) && this.visible) {
         player.networkHandler.sendPacket(BossBarS2CPacket.add(this));
      }

   }

   public void removePlayer(ServerPlayerEntity player) {
      if (this.players.remove(player) && this.visible) {
         player.networkHandler.sendPacket(BossBarS2CPacket.remove(this.getUuid()));
      }

   }

   public void clearPlayers() {
      if (!this.players.isEmpty()) {
         Iterator var1 = Lists.newArrayList(this.players).iterator();

         while(var1.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var1.next();
            this.removePlayer(serverPlayerEntity);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      if (visible != this.visible) {
         this.visible = visible;
         Iterator var2 = this.players.iterator();

         while(var2.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
            serverPlayerEntity.networkHandler.sendPacket(visible ? BossBarS2CPacket.add(this) : BossBarS2CPacket.remove(this.getUuid()));
         }
      }

   }

   public Collection getPlayers() {
      return this.unmodifiablePlayers;
   }
}
