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
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;

@Environment(value=EnvType.CLIENT)
class RealmsCreateWorldScreen.1
extends Thread {
    RealmsCreateWorldScreen.1(String string) {
        super(string);
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            WorldTemplatePaginatedList worldTemplatePaginatedList = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
            WorldTemplatePaginatedList worldTemplatePaginatedList2 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
            WorldTemplatePaginatedList worldTemplatePaginatedList3 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
            WorldTemplatePaginatedList worldTemplatePaginatedList4 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
            RealmsCreateWorldScreen.this.client.execute(() -> {
                RealmsCreateWorldScreen.this.normalWorldTemplates = worldTemplatePaginatedList;
                RealmsCreateWorldScreen.this.adventureWorldTemplates = worldTemplatePaginatedList2;
                RealmsCreateWorldScreen.this.experienceWorldTemplates = worldTemplatePaginatedList3;
                RealmsCreateWorldScreen.this.inspirationWorldTemplates = worldTemplatePaginatedList4;
            });
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsServiceException);
        }
    }
}
