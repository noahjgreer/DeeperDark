/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;

@Environment(value=EnvType.CLIENT)
class RealmsNotificationsScreen.2
implements RealmsNotificationsScreen.NotificationRunnersFactory {
    RealmsNotificationsScreen.2() {
    }

    @Override
    public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers checkers) {
        PeriodicRunnerFactory.RunnersManager runnersManager = checkers.runnerFactory.create();
        RealmsNotificationsScreen.this.addNotificationRunner(checkers, runnersManager);
        return runnersManager;
    }

    @Override
    public boolean isNews() {
        return false;
    }
}
