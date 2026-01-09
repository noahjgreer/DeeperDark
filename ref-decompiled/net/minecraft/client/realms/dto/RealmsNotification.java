package net.minecraft.client.realms.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsNotification {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String NOTIFICATION_UUID_KEY = "notificationUuid";
   private static final String DISMISSABLE_KEY = "dismissable";
   private static final String SEEN_KEY = "seen";
   private static final String TYPE_KEY = "type";
   private static final String VISIT_URL_TYPE = "visitUrl";
   private static final String INFO_POPUP_TYPE = "infoPopup";
   static final Text OPEN_LINK_TEXT = Text.translatable("mco.notification.visitUrl.buttonText.default");
   final UUID uuid;
   final boolean dismissable;
   final boolean seen;
   final String type;

   RealmsNotification(UUID uuid, boolean dismissable, boolean seen, String type) {
      this.uuid = uuid;
      this.dismissable = dismissable;
      this.seen = seen;
      this.type = type;
   }

   public boolean isSeen() {
      return this.seen;
   }

   public boolean isDismissable() {
      return this.dismissable;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public static List parse(String json) {
      List list = new ArrayList();

      try {
         JsonArray jsonArray = LenientJsonParser.parse(json).getAsJsonObject().get("notifications").getAsJsonArray();
         Iterator var3 = jsonArray.iterator();

         while(var3.hasNext()) {
            JsonElement jsonElement = (JsonElement)var3.next();
            list.add(fromJson(jsonElement.getAsJsonObject()));
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse list of RealmsNotifications", var5);
      }

      return list;
   }

   private static RealmsNotification fromJson(JsonObject json) {
      UUID uUID = JsonUtils.getUuidOr("notificationUuid", json, (UUID)null);
      if (uUID == null) {
         throw new IllegalStateException("Missing required property notificationUuid");
      } else {
         boolean bl = JsonUtils.getBooleanOr("dismissable", json, true);
         boolean bl2 = JsonUtils.getBooleanOr("seen", json, false);
         String string = JsonUtils.getString("type", json);
         RealmsNotification realmsNotification = new RealmsNotification(uUID, bl, bl2, string);
         Object var10000;
         switch (string) {
            case "visitUrl":
               var10000 = RealmsNotification.VisitUrl.fromJson(realmsNotification, json);
               break;
            case "infoPopup":
               var10000 = RealmsNotification.InfoPopup.fromJson(realmsNotification, json);
               break;
            default:
               var10000 = realmsNotification;
         }

         return (RealmsNotification)var10000;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class VisitUrl extends RealmsNotification {
      private static final String URL_KEY = "url";
      private static final String BUTTON_TEXT_KEY = "buttonText";
      private static final String MESSAGE_KEY = "message";
      private final String url;
      private final RealmsText buttonText;
      private final RealmsText message;

      private VisitUrl(RealmsNotification parent, String url, RealmsText buttonText, RealmsText message) {
         super(parent.uuid, parent.dismissable, parent.seen, parent.type);
         this.url = url;
         this.buttonText = buttonText;
         this.message = message;
      }

      public static VisitUrl fromJson(RealmsNotification parent, JsonObject json) {
         String string = JsonUtils.getString("url", json);
         RealmsText realmsText = (RealmsText)JsonUtils.get("buttonText", json, RealmsText::fromJson);
         RealmsText realmsText2 = (RealmsText)JsonUtils.get("message", json, RealmsText::fromJson);
         return new VisitUrl(parent, string, realmsText, realmsText2);
      }

      public Text getDefaultMessage() {
         return this.message.toText(Text.translatable("mco.notification.visitUrl.message.default"));
      }

      public ButtonWidget createButton(Screen currentScreen) {
         Text text = this.buttonText.toText(RealmsNotification.OPEN_LINK_TEXT);
         return ButtonWidget.builder(text, ConfirmLinkScreen.opening(currentScreen, this.url)).build();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class InfoPopup extends RealmsNotification {
      private static final String TITLE_KEY = "title";
      private static final String MESSAGE_KEY = "message";
      private static final String IMAGE_KEY = "image";
      private static final String URL_BUTTON_KEY = "urlButton";
      private final RealmsText title;
      private final RealmsText message;
      private final Identifier image;
      @Nullable
      private final UrlButton urlButton;

      private InfoPopup(RealmsNotification parent, RealmsText title, RealmsText message, Identifier image, @Nullable UrlButton urlButton) {
         super(parent.uuid, parent.dismissable, parent.seen, parent.type);
         this.title = title;
         this.message = message;
         this.image = image;
         this.urlButton = urlButton;
      }

      public static InfoPopup fromJson(RealmsNotification parent, JsonObject json) {
         RealmsText realmsText = (RealmsText)JsonUtils.get("title", json, RealmsText::fromJson);
         RealmsText realmsText2 = (RealmsText)JsonUtils.get("message", json, RealmsText::fromJson);
         Identifier identifier = Identifier.of(JsonUtils.getString("image", json));
         UrlButton urlButton = (UrlButton)JsonUtils.getNullable("urlButton", json, UrlButton::fromJson);
         return new InfoPopup(parent, realmsText, realmsText2, identifier, urlButton);
      }

      @Nullable
      public PopupScreen createScreen(Screen backgroundScreen, Consumer dismissCallback) {
         Text text = this.title.toText();
         if (text == null) {
            RealmsNotification.LOGGER.warn("Realms info popup had title with no available translation: {}", this.title);
            return null;
         } else {
            PopupScreen.Builder builder = (new PopupScreen.Builder(backgroundScreen, text)).image(this.image).message(this.message.toText(ScreenTexts.EMPTY));
            if (this.urlButton != null) {
               builder.button(this.urlButton.urlText.toText(RealmsNotification.OPEN_LINK_TEXT), (screen) -> {
                  MinecraftClient minecraftClient = MinecraftClient.getInstance();
                  minecraftClient.setScreen(new ConfirmLinkScreen((confirmed) -> {
                     if (confirmed) {
                        Util.getOperatingSystem().open(this.urlButton.url);
                        minecraftClient.setScreen(backgroundScreen);
                     } else {
                        minecraftClient.setScreen(screen);
                     }

                  }, this.urlButton.url, true));
                  dismissCallback.accept(this.getUuid());
               });
            }

            builder.button(ScreenTexts.OK, (screen) -> {
               screen.close();
               dismissCallback.accept(this.getUuid());
            });
            builder.onClosed(() -> {
               dismissCallback.accept(this.getUuid());
            });
            return builder.build();
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static record UrlButton(String url, RealmsText urlText) {
      final String url;
      final RealmsText urlText;
      private static final String URL_KEY = "url";
      private static final String URL_TEXT_KEY = "urlText";

      private UrlButton(String string, RealmsText realmsText) {
         this.url = string;
         this.urlText = realmsText;
      }

      public static UrlButton fromJson(JsonObject json) {
         String string = JsonUtils.getString("url", json);
         RealmsText realmsText = (RealmsText)JsonUtils.get("urlText", json, RealmsText::fromJson);
         return new UrlButton(string, realmsText);
      }

      public String url() {
         return this.url;
      }

      public RealmsText urlText() {
         return this.urlText;
      }
   }
}
