package net.minecraft.client.toast;

import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ToastManager {
   private static final int SPACES = 5;
   private static final int field_52786 = -1;
   final MinecraftClient client;
   private final List visibleEntries = new ArrayList();
   private final BitSet occupiedSpaces = new BitSet(5);
   private final Deque toastQueue = Queues.newArrayDeque();
   private final Set queuedToastSounds = new HashSet();
   @Nullable
   private Entry nowPlayingToast;

   public ToastManager(MinecraftClient client, GameOptions gameOptions) {
      this.client = client;
      if ((Boolean)gameOptions.getShowNowPlayingToast().getValue()) {
         this.showNowPlayingToast();
      }

   }

   public void update() {
      MutableBoolean mutableBoolean = new MutableBoolean(false);
      this.visibleEntries.removeIf((entry) -> {
         Toast.Visibility visibility = entry.visibility;
         entry.update();
         if (entry.visibility != visibility && mutableBoolean.isFalse()) {
            mutableBoolean.setTrue();
            entry.visibility.playSound(this.client.getSoundManager());
         }

         if (entry.isFinishedRendering()) {
            this.occupiedSpaces.clear(entry.topIndex, entry.topIndex + entry.requiredSpaceCount);
            return true;
         } else {
            return false;
         }
      });
      if (!this.toastQueue.isEmpty() && this.getEmptySpaceCount() > 0) {
         this.toastQueue.removeIf((toast) -> {
            int i = toast.getRequiredSpaceCount();
            int j = this.getTopIndex(i);
            if (j == -1) {
               return false;
            } else {
               this.visibleEntries.add(new Entry(toast, j, i));
               this.occupiedSpaces.set(j, j + i);
               SoundEvent soundEvent = toast.getSoundEvent();
               if (soundEvent != null && this.queuedToastSounds.add(soundEvent)) {
                  this.client.getSoundManager().play(PositionedSoundInstance.master(soundEvent, 1.0F, 1.0F));
               }

               return true;
            }
         });
      }

      this.queuedToastSounds.clear();
      if (this.nowPlayingToast != null) {
         this.nowPlayingToast.update();
      }

   }

   public void draw(DrawContext context) {
      if (!this.client.options.hudHidden) {
         int i = context.getScaledWindowWidth();
         if (!this.visibleEntries.isEmpty()) {
            context.createNewRootLayer();
         }

         Iterator var3 = this.visibleEntries.iterator();

         while(var3.hasNext()) {
            Entry entry = (Entry)var3.next();
            entry.draw(context, i);
         }

         if ((Boolean)this.client.options.getShowNowPlayingToast().getValue() && this.nowPlayingToast != null && (this.client.currentScreen == null || !(this.client.currentScreen instanceof GameMenuScreen))) {
            this.nowPlayingToast.draw(context, i);
         }

      }
   }

   private int getTopIndex(int requiredSpaces) {
      if (this.getEmptySpaceCount() >= requiredSpaces) {
         int i = 0;

         for(int j = 0; j < 5; ++j) {
            if (this.occupiedSpaces.get(j)) {
               i = 0;
            } else {
               ++i;
               if (i == requiredSpaces) {
                  return j + 1 - i;
               }
            }
         }
      }

      return -1;
   }

   private int getEmptySpaceCount() {
      return 5 - this.occupiedSpaces.cardinality();
   }

   @Nullable
   public Toast getToast(Class toastClass, Object type) {
      Iterator var3 = this.visibleEntries.iterator();

      Entry entry;
      do {
         if (!var3.hasNext()) {
            var3 = this.toastQueue.iterator();

            Toast toast;
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               toast = (Toast)var3.next();
            } while(!toastClass.isAssignableFrom(toast.getClass()) || !toast.getType().equals(type));

            return toast;
         }

         entry = (Entry)var3.next();
      } while(entry == null || !toastClass.isAssignableFrom(entry.getInstance().getClass()) || !entry.getInstance().getType().equals(type));

      return entry.getInstance();
   }

   public void clear() {
      this.occupiedSpaces.clear();
      this.visibleEntries.clear();
      this.toastQueue.clear();
   }

   public void add(Toast toast) {
      this.toastQueue.add(toast);
   }

   public void onMusicTrackStart() {
      if (this.nowPlayingToast != null) {
         this.nowPlayingToast.init();
         ((NowPlayingToast)this.nowPlayingToast.getInstance()).show(this.client.options);
      }

   }

   public void onMusicTrackStop() {
      if (this.nowPlayingToast != null) {
         ((NowPlayingToast)this.nowPlayingToast.getInstance()).setVisibility(Toast.Visibility.HIDE);
      }

   }

   public void showNowPlayingToast() {
      this.nowPlayingToast = new Entry(new NowPlayingToast(), 0, 0);
   }

   public void hideNowPlayingToast() {
      this.nowPlayingToast = null;
   }

   public MinecraftClient getClient() {
      return this.client;
   }

   public double getNotificationDisplayTimeMultiplier() {
      return (Double)this.client.options.getNotificationDisplayTime().getValue();
   }

   @Environment(EnvType.CLIENT)
   private class Entry {
      private static final long DISAPPEAR_TIME = 600L;
      private final Toast instance;
      final int topIndex;
      final int requiredSpaceCount;
      private long startTime;
      private long fullyVisibleTime;
      Toast.Visibility visibility;
      private long showTime;
      private float visibleWidthPortion;
      protected boolean finishedRendering;

      Entry(final Toast instance, final int topIndex, final int requiredSpaceCount) {
         this.instance = instance;
         this.topIndex = topIndex;
         this.requiredSpaceCount = requiredSpaceCount;
         this.init();
      }

      public Toast getInstance() {
         return this.instance;
      }

      public void init() {
         this.startTime = -1L;
         this.fullyVisibleTime = -1L;
         this.visibility = Toast.Visibility.HIDE;
         this.showTime = 0L;
         this.visibleWidthPortion = 0.0F;
         this.finishedRendering = false;
      }

      public boolean isFinishedRendering() {
         return this.finishedRendering;
      }

      private void updateVisibleWidthPortion(long time) {
         float f = MathHelper.clamp((float)(time - this.startTime) / 600.0F, 0.0F, 1.0F);
         f *= f;
         if (this.visibility == Toast.Visibility.HIDE) {
            this.visibleWidthPortion = 1.0F - f;
         } else {
            this.visibleWidthPortion = f;
         }

      }

      public void update() {
         long l = Util.getMeasuringTimeMs();
         if (this.startTime == -1L) {
            this.startTime = l;
            this.visibility = Toast.Visibility.SHOW;
         }

         if (this.visibility == Toast.Visibility.SHOW && l - this.startTime <= 600L) {
            this.fullyVisibleTime = l;
         }

         this.showTime = l - this.fullyVisibleTime;
         this.updateVisibleWidthPortion(l);
         this.instance.update(ToastManager.this, this.showTime);
         Toast.Visibility visibility = this.instance.getVisibility();
         if (visibility != this.visibility) {
            this.startTime = l - (long)((int)((1.0F - this.visibleWidthPortion) * 600.0F));
            this.visibility = visibility;
         }

         boolean bl = this.finishedRendering;
         this.finishedRendering = this.visibility == Toast.Visibility.HIDE && l - this.startTime > 600L;
         if (this.finishedRendering && !bl) {
            this.instance.onFinishedRendering();
         }

      }

      public void draw(DrawContext context, int scaledWindowWidth) {
         if (!this.finishedRendering) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(this.instance.getXPos(scaledWindowWidth, this.visibleWidthPortion), this.instance.getYPos(this.topIndex));
            this.instance.draw(context, ToastManager.this.client.textRenderer, this.showTime);
            context.getMatrices().popMatrix();
         }
      }
   }
}
