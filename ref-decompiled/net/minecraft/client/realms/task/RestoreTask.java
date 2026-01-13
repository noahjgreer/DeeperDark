/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.Backup
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.exception.RetryCallException
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.RestoreTask
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RestoreTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text TITLE = Text.translatable((String)"mco.backup.restoring");
    private final Backup backup;
    private final long field_61055;
    private final RealmsConfigureWorldScreen lastScreen;

    public RestoreTask(Backup backup, long l, RealmsConfigureWorldScreen realmsConfigureWorldScreen) {
        this.backup = backup;
        this.field_61055 = l;
        this.lastScreen = realmsConfigureWorldScreen;
    }

    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                realmsClient.restoreWorld(this.field_61055, this.backup.backupId);
                RestoreTask.pause((long)1L);
                if (this.aborted()) {
                    return;
                }
                RestoreTask.setScreen((Screen)this.lastScreen);
                return;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                RestoreTask.pause((long)retryCallException.delaySeconds);
                continue;
            }
            catch (RealmsServiceException realmsServiceException) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)realmsServiceException);
                RestoreTask.setScreen((Screen)new RealmsGenericErrorScreen(realmsServiceException, (Screen)this.lastScreen));
                return;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)exception);
                this.error(exception);
                return;
            }
        }
    }

    public Text getTitle() {
        return TITLE;
    }
}

