/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LoadingWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.realms.RepeatedNarrator
 *  net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.RepeatedNarrator;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsLongRunningMcoTaskScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RepeatedNarrator NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
    private final List<LongRunningTask> tasks;
    private final Screen parent;
    protected final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();
    private volatile Text title;
    private @Nullable LoadingWidget loading;

    public RealmsLongRunningMcoTaskScreen(Screen parent, LongRunningTask ... tasks) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.tasks = List.of(tasks);
        if (this.tasks.isEmpty()) {
            throw new IllegalArgumentException("No tasks added");
        }
        this.title = ((LongRunningTask)this.tasks.get(0)).getTitle();
        Runnable runnable = () -> {
            for (LongRunningTask longRunningTask : tasks) {
                this.setTitle(longRunningTask.getTitle());
                if (longRunningTask.aborted()) break;
                longRunningTask.run();
                if (!longRunningTask.aborted()) continue;
                return;
            }
        };
        Thread thread = new Thread(runnable, "Realms-long-running-task");
        thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    public boolean canInterruptOtherScreen() {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.loading != null) {
            NARRATOR.narrate(this.client.getNarratorManager(), this.loading.getMessage());
        }
    }

    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256) {
            this.onCancel();
            return true;
        }
        return super.keyPressed(input);
    }

    public void init() {
        this.layout.getMainPositioner().alignHorizontalCenter();
        this.layout.add((Widget)RealmsLongRunningMcoTaskScreen.createRealmsLogoIconWidget());
        this.loading = new LoadingWidget(this.textRenderer, this.title);
        this.layout.add((Widget)this.loading, positioner -> positioner.marginTop(10).marginBottom(30));
        this.layout.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCancel()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    protected void onCancel() {
        for (LongRunningTask longRunningTask : this.tasks) {
            longRunningTask.abortTask();
        }
        this.client.setScreen(this.parent);
    }

    public void setTitle(Text title) {
        if (this.loading != null) {
            this.loading.setMessage(title);
        }
        this.title = title;
    }
}

