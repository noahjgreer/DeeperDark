/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.WorldDownload
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.exception.RetryCallException
 *  net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.task.DownloadTask
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DownloadTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text TITLE = Text.translatable((String)"mco.download.preparing");
    private final long worldId;
    private final int slot;
    private final Screen lastScreen;
    private final String downloadName;

    public DownloadTask(long worldId, int slot, String downloadName, Screen lastScreen) {
        this.worldId = worldId;
        this.slot = slot;
        this.lastScreen = lastScreen;
        this.downloadName = downloadName;
    }

    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                WorldDownload worldDownload = realmsClient.download(this.worldId, this.slot);
                DownloadTask.pause((long)1L);
                if (this.aborted()) {
                    return;
                }
                DownloadTask.setScreen((Screen)new RealmsDownloadLatestWorldScreen(this.lastScreen, worldDownload, this.downloadName, bl -> {}));
                return;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                DownloadTask.pause((long)retryCallException.delaySeconds);
                continue;
            }
            catch (RealmsServiceException realmsServiceException) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data", (Throwable)realmsServiceException);
                DownloadTask.setScreen((Screen)new RealmsGenericErrorScreen(realmsServiceException, this.lastScreen));
                return;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data", (Throwable)exception);
                this.error(exception);
                return;
            }
        }
    }

    public Text getTitle() {
        return TITLE;
    }
}

