/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.RealmsNewsUpdater
 *  net.minecraft.client.realms.RealmsPeriodicCheckers
 *  net.minecraft.client.realms.RealmsPeriodicCheckers$AvailableServers
 *  net.minecraft.client.realms.dto.RealmsNews
 *  net.minecraft.client.realms.dto.RealmsNotification
 *  net.minecraft.client.realms.dto.RealmsServerList
 *  net.minecraft.client.realms.dto.RealmsServerPlayerList
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory$PeriodicRunner
 *  net.minecraft.client.realms.util.RealmsPersistence
 *  net.minecraft.client.util.Backoff
 *  net.minecraft.util.TimeSupplier
 *  net.minecraft.util.Util
 */
package net.minecraft.client.realms;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsNewsUpdater;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.TimeSupplier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsPeriodicCheckers {
    public final PeriodicRunnerFactory runnerFactory = new PeriodicRunnerFactory((Executor)Util.getIoWorkerExecutor(), TimeUnit.MILLISECONDS, (TimeSupplier)Util.nanoTimeSupplier);
    private final List<PeriodicRunnerFactory.PeriodicRunner<?>> checkers;
    public final PeriodicRunnerFactory.PeriodicRunner<List<RealmsNotification>> notifications;
    public final PeriodicRunnerFactory.PeriodicRunner<AvailableServers> serverList;
    public final PeriodicRunnerFactory.PeriodicRunner<Integer> pendingInvitesCount;
    public final PeriodicRunnerFactory.PeriodicRunner<Boolean> trialAvailability;
    public final PeriodicRunnerFactory.PeriodicRunner<RealmsNews> news;
    public final PeriodicRunnerFactory.PeriodicRunner<RealmsServerPlayerList> onlinePlayers;
    public final RealmsNewsUpdater newsUpdater = new RealmsNewsUpdater(new RealmsPersistence());

    public RealmsPeriodicCheckers(RealmsClient client) {
        this.serverList = this.runnerFactory.create("server list", () -> {
            RealmsServerList realmsServerList = client.listWorlds();
            if (RealmsMainScreen.isSnapshotRealmsEligible()) {
                return new AvailableServers(realmsServerList.servers(), client.getPrereleaseEligibleServers());
            }
            return new AvailableServers(realmsServerList.servers(), List.of());
        }, Duration.ofSeconds(60L), Backoff.ONE_CYCLE);
        this.pendingInvitesCount = this.runnerFactory.create("pending invite count", () -> ((RealmsClient)client).pendingInvitesCount(), Duration.ofSeconds(10L), Backoff.exponential((int)360));
        this.trialAvailability = this.runnerFactory.create("trial availablity", () -> ((RealmsClient)client).trialAvailable(), Duration.ofSeconds(60L), Backoff.exponential((int)60));
        this.news = this.runnerFactory.create("unread news", () -> ((RealmsClient)client).getNews(), Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
        this.notifications = this.runnerFactory.create("notifications", () -> ((RealmsClient)client).listNotifications(), Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
        this.onlinePlayers = this.runnerFactory.create("online players", () -> ((RealmsClient)client).getLiveStats(), Duration.ofSeconds(10L), Backoff.ONE_CYCLE);
        this.checkers = List.of(this.notifications, this.serverList, this.pendingInvitesCount, this.trialAvailability, this.news, this.onlinePlayers);
    }

    public List<PeriodicRunnerFactory.PeriodicRunner<?>> getCheckers() {
        return this.checkers;
    }
}

