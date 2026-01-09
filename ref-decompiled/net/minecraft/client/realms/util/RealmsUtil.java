package net.minecraft.client.realms.util;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsUtil {
   private static final Logger field_61054 = LogUtils.getLogger();
   private static final Text NOW_TEXT = Text.translatable("mco.util.time.now");
   private static final int SECONDS_PER_MINUTE = 60;
   private static final int SECONDS_PER_HOUR = 3600;
   private static final int SECONDS_PER_DAY = 86400;

   public static Text convertToAgePresentation(long milliseconds) {
      if (milliseconds < 0L) {
         return NOW_TEXT;
      } else {
         long l = milliseconds / 1000L;
         if (l < 60L) {
            return Text.translatable("mco.time.secondsAgo", l);
         } else {
            long m;
            if (l < 3600L) {
               m = l / 60L;
               return Text.translatable("mco.time.minutesAgo", m);
            } else if (l < 86400L) {
               m = l / 3600L;
               return Text.translatable("mco.time.hoursAgo", m);
            } else {
               m = l / 86400L;
               return Text.translatable("mco.time.daysAgo", m);
            }
         }
      }
   }

   public static Text convertToAgePresentation(Date date) {
      return convertToAgePresentation(System.currentTimeMillis() - date.getTime());
   }

   public static void drawPlayerHead(DrawContext context, int x, int y, int size, UUID playerUuid) {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      ProfileResult profileResult = minecraftClient.getSessionService().fetchProfile(playerUuid, false);
      SkinTextures skinTextures = profileResult != null ? minecraftClient.getSkinProvider().getSkinTextures(profileResult.profile()) : DefaultSkinHelper.getSkinTextures(playerUuid);
      PlayerSkinDrawer.draw(context, skinTextures, x, y, size);
   }

   public static CompletableFuture method_72217(class_11539 arg, @Nullable Consumer consumer) {
      return CompletableFuture.supplyAsync(() -> {
         RealmsClient realmsClient = RealmsClient.create();

         try {
            return arg.apply(realmsClient);
         } catch (Throwable var5) {
            if (var5 instanceof RealmsServiceException realmsServiceException) {
               if (consumer != null) {
                  consumer.accept(realmsServiceException);
               }
            } else {
               field_61054.error("Unhandled exception", var5);
            }

            throw new RuntimeException(var5);
         }
      }, Util.getDownloadWorkerExecutor());
   }

   public static CompletableFuture method_72216(class_11538 arg, @Nullable Consumer consumer) {
      return method_72217(arg, consumer);
   }

   public static Consumer method_72220(Function function) {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      return (realmsServiceException) -> {
         minecraftClient.execute(() -> {
            minecraftClient.setScreen((Screen)function.apply(realmsServiceException));
         });
      };
   }

   public static Consumer method_72221(Function function, String string) {
      return method_72220(function).andThen((realmsServiceException) -> {
         field_61054.error(string, realmsServiceException);
      });
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface class_11539 {
      Object apply(RealmsClient realmsClient) throws RealmsServiceException;
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface class_11538 extends class_11539 {
      void accept(RealmsClient realmsClient) throws RealmsServiceException;

      default Void apply(RealmsClient realmsClient) throws RealmsServiceException {
         this.accept(realmsClient);
         return null;
      }
   }
}
