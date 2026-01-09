package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record MessageIndicator(int indicatorColor, @Nullable Icon icon, @Nullable Text text, @Nullable String loggedName) {
   private static final Text SYSTEM_TEXT = Text.translatable("chat.tag.system");
   private static final Text SINGLE_PLAYER_TEXT = Text.translatable("chat.tag.system_single_player");
   private static final Text NOT_SECURE_TEXT = Text.translatable("chat.tag.not_secure");
   private static final Text MODIFIED_TEXT = Text.translatable("chat.tag.modified");
   private static final Text ERROR_TEXT = Text.translatable("chat.tag.error");
   private static final int NOT_SECURE_COLOR = 13684944;
   private static final int MODIFIED_COLOR = 6316128;
   private static final MessageIndicator SYSTEM;
   private static final MessageIndicator SINGLE_PLAYER;
   private static final MessageIndicator NOT_SECURE;
   private static final MessageIndicator CHAT_ERROR;

   public MessageIndicator(int i, @Nullable Icon icon, @Nullable Text text, @Nullable String string) {
      this.indicatorColor = i;
      this.icon = icon;
      this.text = text;
      this.loggedName = string;
   }

   public static MessageIndicator system() {
      return SYSTEM;
   }

   public static MessageIndicator singlePlayer() {
      return SINGLE_PLAYER;
   }

   public static MessageIndicator notSecure() {
      return NOT_SECURE;
   }

   public static MessageIndicator modified(String originalText) {
      Text text = Text.literal(originalText).formatted(Formatting.GRAY);
      Text text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append((Text)text);
      return new MessageIndicator(6316128, MessageIndicator.Icon.CHAT_MODIFIED, text2, "Modified");
   }

   public static MessageIndicator chatError() {
      return CHAT_ERROR;
   }

   public int indicatorColor() {
      return this.indicatorColor;
   }

   @Nullable
   public Icon icon() {
      return this.icon;
   }

   @Nullable
   public Text text() {
      return this.text;
   }

   @Nullable
   public String loggedName() {
      return this.loggedName;
   }

   static {
      SYSTEM = new MessageIndicator(13684944, (Icon)null, SYSTEM_TEXT, "System");
      SINGLE_PLAYER = new MessageIndicator(13684944, (Icon)null, SINGLE_PLAYER_TEXT, "System");
      NOT_SECURE = new MessageIndicator(13684944, (Icon)null, NOT_SECURE_TEXT, "Not Secure");
      CHAT_ERROR = new MessageIndicator(16733525, (Icon)null, ERROR_TEXT, "Chat Error");
   }

   @Environment(EnvType.CLIENT)
   public static enum Icon {
      CHAT_MODIFIED(Identifier.ofVanilla("icon/chat_modified"), 9, 9);

      public final Identifier texture;
      public final int width;
      public final int height;

      private Icon(final Identifier texture, final int width, final int height) {
         this.texture = texture;
         this.width = width;
         this.height = height;
      }

      public void draw(DrawContext context, int x, int y) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, x, y, this.width, this.height);
      }

      // $FF: synthetic method
      private static Icon[] method_44711() {
         return new Icon[]{CHAT_MODIFIED};
      }
   }
}
