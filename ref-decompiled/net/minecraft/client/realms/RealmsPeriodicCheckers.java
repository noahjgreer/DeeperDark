package net.minecraft.client.realms;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsPeriodicCheckers {
   public final PeriodicRunnerFactory runnerFactory;
   private final List checkers;
   public final PeriodicRunnerFactory.PeriodicRunner notifications;
   public final PeriodicRunnerFactory.PeriodicRunner serverList;
   public final PeriodicRunnerFactory.PeriodicRunner pendingInvitesCount;
   public final PeriodicRunnerFactory.PeriodicRunner trialAvailability;
   public final PeriodicRunnerFactory.PeriodicRunner news;
   public final PeriodicRunnerFactory.PeriodicRunner onlinePlayers;
   public final RealmsNewsUpdater newsUpdater;

   public RealmsPeriodicCheckers(RealmsClient client) {
      this.runnerFactory = new PeriodicRunnerFactory(Util.getIoWorkerExecutor(), TimeUnit.MILLISECONDS, Util.nanoTimeSupplier);
      this.newsUpdater = new RealmsNewsUpdater(new RealmsPersistence());
      this.serverList = this.runnerFactory.create("server list", () -> {
         RealmsServerList realmsServerList = client.listWorlds();
         return RealmsMainScreen.isSnapshotRealmsEligible() ? new AvailableServers(realmsServerList.servers, client.getPrereleaseEligibleServers()) : new AvailableServers(realmsServerList.servers, List.of());
      }, Duration.ofSeconds(60L), Backoff.ONE_CYCLE);
      PeriodicRunnerFactory var10001 = this.runnerFactory;
      Objects.requireNonNull(client);
      this.pendingInvitesCount = var10001.create("pending invite count", client::pendingInvitesCount, Duration.ofSeconds(10L), Backoff.exponential(360));
      var10001 = this.runnerFactory;
      Objects.requireNonNull(client);
      this.trialAvailability = var10001.create("trial availablity", client::trialAvailable, Duration.ofSeconds(60L), Backoff.exponential(60));
      var10001 = this.runnerFactory;
      Objects.requireNonNull(client);
      this.news = var10001.create("unread news", client::getNews, Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
      var10001 = this.runnerFactory;
      Objects.requireNonNull(client);
      this.notifications = var10001.create("notifications", client::listNotifications, Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
      var10001 = this.runnerFactory;
      Objects.requireNonNull(client);
      this.onlinePlayers = var10001.create("online players", client::getLiveStats, Duration.ofSeconds(10L), Backoff.ONE_CYCLE);
      this.checkers = List.of(this.notifications, this.serverList, this.pendingInvitesCount, this.trialAvailability, this.news, this.onlinePlayers);
   }

   public List getCheckers() {
      return this.checkers;
   }

   @Environment(EnvType.CLIENT)
   public static record AvailableServers(List serverList, List availableSnapshotServers) {
      public AvailableServers(List list, List list2) {
         this.serverList = list;
         this.availableSnapshotServers = list2;
      }

      public List serverList() {
         return this.serverList;
      }

      public List availableSnapshotServers() {
         return this.availableSnapshotServers;
      }
   }
}
