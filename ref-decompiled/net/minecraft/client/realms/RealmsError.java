package net.minecraft.client.realms;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public interface RealmsError {
   Text NO_DETAILS_TEXT = Text.translatable("mco.errorMessage.noDetails");
   Logger LOGGER = LogUtils.getLogger();

   int getErrorCode();

   Text getText();

   String getErrorMessage();

   static RealmsError ofHttp(int statusCode, String response) {
      if (statusCode == 429) {
         return RealmsError.SimpleHttpError.SERVICE_BUSY;
      } else if (Strings.isNullOrEmpty(response)) {
         return RealmsError.SimpleHttpError.statusCodeOnly(statusCode);
      } else {
         try {
            JsonObject jsonObject = LenientJsonParser.parse(response).getAsJsonObject();
            String string = JsonHelper.getString(jsonObject, "reason", (String)null);
            String string2 = JsonHelper.getString(jsonObject, "errorMsg", (String)null);
            int i = JsonHelper.getInt(jsonObject, "errorCode", -1);
            if (string2 != null || string != null || i != -1) {
               return new DetailedHttpError(statusCode, i != -1 ? i : statusCode, string, string2);
            }
         } catch (Exception var6) {
            LOGGER.error("Could not parse RealmsError", var6);
         }

         return new RawHttpPayloadError(statusCode, response);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record SimpleHttpError(int httpCode, @Nullable Text payload) implements RealmsError {
      public static final SimpleHttpError SERVICE_BUSY = new SimpleHttpError(429, Text.translatable("mco.errorMessage.serviceBusy"));
      public static final Text RETRY_TEXT = Text.translatable("mco.errorMessage.retry");
      public static final String BODY_HTML_OPENING_TAG = "<body>";
      public static final String BODY_HTML_CLOSING_TAG = "</body>";

      public SimpleHttpError(int i, @Nullable Text text) {
         this.httpCode = i;
         this.payload = text;
      }

      public static SimpleHttpError unknownCompatibility(String response) {
         return new SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.unknownCompatibility", response));
      }

      public static SimpleHttpError configurationError() {
         return new SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.configurationError"));
      }

      public static SimpleHttpError connectivity(RealmsHttpException exception) {
         return new SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.connectivity", exception.getMessage()));
      }

      public static SimpleHttpError retryable(int statusCode) {
         return new SimpleHttpError(statusCode, RETRY_TEXT);
      }

      public static SimpleHttpError statusCodeOnly(int statusCode) {
         return new SimpleHttpError(statusCode, (Text)null);
      }

      public static SimpleHttpError unreadableHtmlBody(int statusCode, String html) {
         int i = html.indexOf("<body>");
         int j = html.indexOf("</body>");
         if (i >= 0 && j > i) {
            return new SimpleHttpError(statusCode, Text.literal(html.substring(i + "<body>".length(), j).trim()));
         } else {
            LOGGER.error("Got an error with an unreadable html body {}", html);
            return new SimpleHttpError(statusCode, (Text)null);
         }
      }

      public int getErrorCode() {
         return this.httpCode;
      }

      public Text getText() {
         return this.payload != null ? this.payload : NO_DETAILS_TEXT;
      }

      public String getErrorMessage() {
         return this.payload != null ? String.format(Locale.ROOT, "Realms service error (%d) with message '%s'", this.httpCode, this.payload.getString()) : String.format(Locale.ROOT, "Realms service error (%d) with no payload", this.httpCode);
      }

      public int httpCode() {
         return this.httpCode;
      }

      @Nullable
      public Text payload() {
         return this.payload;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record DetailedHttpError(int httpCode, int code, @Nullable String reason, @Nullable String message) implements RealmsError {
      public DetailedHttpError(int i, int j, @Nullable String string, @Nullable String string2) {
         this.httpCode = i;
         this.code = j;
         this.reason = string;
         this.message = string2;
      }

      public int getErrorCode() {
         return this.code;
      }

      public Text getText() {
         String string = "mco.errorMessage." + this.code;
         if (I18n.hasTranslation(string)) {
            return Text.translatable(string);
         } else {
            if (this.reason != null) {
               String string2 = "mco.errorReason." + this.reason;
               if (I18n.hasTranslation(string2)) {
                  return Text.translatable(string2);
               }
            }

            return (Text)(this.message != null ? Text.literal(this.message) : NO_DETAILS_TEXT);
         }
      }

      public String getErrorMessage() {
         return String.format(Locale.ROOT, "Realms service error (%d/%d/%s) with message '%s'", this.httpCode, this.code, this.reason, this.message);
      }

      public int httpCode() {
         return this.httpCode;
      }

      public int code() {
         return this.code;
      }

      @Nullable
      public String reason() {
         return this.reason;
      }

      @Nullable
      public String message() {
         return this.message;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record RawHttpPayloadError(int httpCode, String payload) implements RealmsError {
      public RawHttpPayloadError(int i, String string) {
         this.httpCode = i;
         this.payload = string;
      }

      public int getErrorCode() {
         return this.httpCode;
      }

      public Text getText() {
         return Text.literal(this.payload);
      }

      public String getErrorMessage() {
         return String.format(Locale.ROOT, "Realms service error (%d) with raw payload '%s'", this.httpCode, this.payload);
      }

      public int httpCode() {
         return this.httpCode;
      }

      public String payload() {
         return this.payload;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record AuthenticationError(String message) implements RealmsError {
      public static final int ERROR_CODE = 401;

      public AuthenticationError(String string) {
         this.message = string;
      }

      public int getErrorCode() {
         return 401;
      }

      public Text getText() {
         return Text.literal(this.message);
      }

      public String getErrorMessage() {
         return String.format(Locale.ROOT, "Realms authentication error with message '%s'", this.message);
      }

      public String message() {
         return this.message;
      }
   }
}
