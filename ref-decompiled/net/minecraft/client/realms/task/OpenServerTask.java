/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$State
 *  net.minecraft.client.realms.exception.RetryCallException
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.OpenServerTask
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class OpenServerTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text TITLE = Text.translatable((String)"mco.configure.world.opening");
    private final RealmsServer serverData;
    private final Screen returnScreen;
    private final boolean join;
    private final MinecraftClient client;

    public OpenServerTask(RealmsServer realmsServer, Screen returnScreen, boolean join, MinecraftClient client) {
        this.serverData = realmsServer;
        this.returnScreen = returnScreen;
        this.join = join;
        this.client = client;
    }

    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = realmsClient.open(this.serverData.id);
                if (!bl) continue;
                this.client.execute(() -> {
                    Screen screen = this.returnScreen;
                    if (screen instanceof RealmsConfigureWorldScreen) {
                        RealmsConfigureWorldScreen realmsConfigureWorldScreen = (RealmsConfigureWorldScreen)screen;
                        realmsConfigureWorldScreen.stateChanged();
                    }
                    this.serverData.state = RealmsServer.State.OPEN;
                    if (this.join) {
                        RealmsMainScreen.play((RealmsServer)this.serverData, (Screen)this.returnScreen);
                    } else {
                        this.client.setScreen(this.returnScreen);
                    }
                });
                break;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                OpenServerTask.pause((long)retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to open server", (Throwable)exception);
                this.error(exception);
            }
        }
    }

    public Text getTitle() {
        return TITLE;
    }
}

