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
import net.minecraft.client.realms.util.PeriodicRunnerFactory;

@Environment(value=EnvType.CLIENT)
static interface RealmsNotificationsScreen.NotificationRunnersFactory {
    public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers var1);

    public boolean isNews();
}
