/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.exception.RetryCallException
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.ResettingWorldTask
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class ResettingWorldTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long serverId;
    private final Text title;
    private final Runnable callback;

    public ResettingWorldTask(long serverId, Text title, Runnable callback) {
        this.serverId = serverId;
        this.title = title;
        this.callback = callback;
    }

    protected abstract void resetWorld(RealmsClient var1, long var2) throws RealmsServiceException;

    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                this.resetWorld(realmsClient, this.serverId);
                if (this.aborted()) {
                    return;
                }
                this.callback.run();
                return;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                ResettingWorldTask.pause((long)retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't reset world");
                this.error(exception);
                return;
            }
        }
    }

    public Text getTitle() {
        return this.title;
    }
}

