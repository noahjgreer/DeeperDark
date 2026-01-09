package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.realms.RepeatedNarrator;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RepeatedNarrator NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
   private final List tasks;
   private final Screen parent;
   protected final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();
   private volatile Text title;
   @Nullable
   private LoadingWidget loading;

   public RealmsLongRunningMcoTaskScreen(Screen parent, LongRunningTask... tasks) {
      super(NarratorManager.EMPTY);
      this.parent = parent;
      this.tasks = List.of(tasks);
      if (this.tasks.isEmpty()) {
         throw new IllegalArgumentException("No tasks added");
      } else {
         this.title = ((LongRunningTask)this.tasks.get(0)).getTitle();
         Runnable runnable = () -> {
            LongRunningTask[] var2 = tasks;
            int var3 = tasks.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               LongRunningTask longRunningTask = var2[var4];
               this.setTitle(longRunningTask.getTitle());
               if (longRunningTask.aborted()) {
                  break;
               }

               longRunningTask.run();
               if (longRunningTask.aborted()) {
                  return;
               }
            }

         };
         Thread thread = new Thread(runnable, "Realms-long-running-task");
         thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         thread.start();
      }
   }

   public void tick() {
      super.tick();
      if (this.loading != null) {
         NARRATOR.narrate(this.client.getNarratorManager(), this.loading.getMessage());
      }

   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onCancel();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void init() {
      this.layout.getMainPositioner().alignHorizontalCenter();
      this.layout.add(createRealmsLogoIconWidget());
      this.loading = new LoadingWidget(this.textRenderer, this.title);
      this.layout.add(this.loading, (Consumer)((positioner) -> {
         positioner.marginTop(10).marginBottom(30);
      }));
      this.layout.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.onCancel();
      }).build());
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      this.layout.refreshPositions();
      SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
   }

   protected void onCancel() {
      Iterator var1 = this.tasks.iterator();

      while(var1.hasNext()) {
         LongRunningTask longRunningTask = (LongRunningTask)var1.next();
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
