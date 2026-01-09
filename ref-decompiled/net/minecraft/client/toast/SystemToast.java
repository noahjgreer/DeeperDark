package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast {
   private static final Identifier TEXTURE = Identifier.ofVanilla("toast/system");
   private static final int MIN_WIDTH = 200;
   private static final int LINE_HEIGHT = 12;
   private static final int PADDING_Y = 10;
   private final Type type;
   private Text title;
   private List lines;
   private long startTime;
   private boolean justUpdated;
   private final int width;
   private boolean hidden;
   private Toast.Visibility visibility;

   public SystemToast(Type type, Text title, @Nullable Text description) {
      this(type, title, getTextAsList(description), Math.max(160, 30 + Math.max(MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)title), description == null ? 0 : MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)description))));
   }

   public static SystemToast create(MinecraftClient client, Type type, Text title, Text description) {
      TextRenderer textRenderer = client.textRenderer;
      List list = textRenderer.wrapLines(description, 200);
      Stream var10001 = list.stream();
      Objects.requireNonNull(textRenderer);
      int i = Math.max(200, var10001.mapToInt(textRenderer::getWidth).max().orElse(200));
      return new SystemToast(type, title, list, i + 30);
   }

   private SystemToast(Type type, Text title, List lines, int width) {
      this.visibility = Toast.Visibility.HIDE;
      this.type = type;
      this.title = title;
      this.lines = lines;
      this.width = width;
   }

   private static ImmutableList getTextAsList(@Nullable Text text) {
      return text == null ? ImmutableList.of() : ImmutableList.of(text.asOrderedText());
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return 20 + Math.max(this.lines.size(), 1) * 12;
   }

   public void hide() {
      this.hidden = true;
   }

   public Toast.Visibility getVisibility() {
      return this.visibility;
   }

   public void update(ToastManager manager, long time) {
      if (this.justUpdated) {
         this.startTime = time;
         this.justUpdated = false;
      }

      double d = (double)this.type.displayDuration * manager.getNotificationDisplayTimeMultiplier();
      long l = time - this.startTime;
      this.visibility = !this.hidden && (double)l < d ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
   }

   public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
      if (this.lines.isEmpty()) {
         context.drawText(textRenderer, (Text)this.title, 18, 12, -256, false);
      } else {
         context.drawText(textRenderer, (Text)this.title, 18, 7, -256, false);

         for(int i = 0; i < this.lines.size(); ++i) {
            context.drawText(textRenderer, (OrderedText)((OrderedText)this.lines.get(i)), 18, 18 + i * 12, -1, false);
         }
      }

   }

   public void setContent(Text title, @Nullable Text description) {
      this.title = title;
      this.lines = getTextAsList(description);
      this.justUpdated = true;
   }

   public Type getType() {
      return this.type;
   }

   public static void add(ToastManager manager, Type type, Text title, @Nullable Text description) {
      manager.add(new SystemToast(type, title, description));
   }

   public static void show(ToastManager manager, Type type, Text title, @Nullable Text description) {
      SystemToast systemToast = (SystemToast)manager.getToast(SystemToast.class, type);
      if (systemToast == null) {
         add(manager, type, title, description);
      } else {
         systemToast.setContent(title, description);
      }

   }

   public static void hide(ToastManager manager, Type type) {
      SystemToast systemToast = (SystemToast)manager.getToast(SystemToast.class, type);
      if (systemToast != null) {
         systemToast.hide();
      }

   }

   public static void addWorldAccessFailureToast(MinecraftClient client, String worldName) {
      add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, Text.translatable("selectWorld.access_failure"), Text.literal(worldName));
   }

   public static void addWorldDeleteFailureToast(MinecraftClient client, String worldName) {
      add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, Text.translatable("selectWorld.delete_failure"), Text.literal(worldName));
   }

   public static void addPackCopyFailure(MinecraftClient client, String directory) {
      add(client.getToastManager(), SystemToast.Type.PACK_COPY_FAILURE, Text.translatable("pack.copyFailure"), Text.literal(directory));
   }

   public static void addFileDropFailure(MinecraftClient client, int count) {
      add(client.getToastManager(), SystemToast.Type.FILE_DROP_FAILURE, Text.translatable("gui.fileDropFailure.title"), Text.translatable("gui.fileDropFailure.detail", count));
   }

   public static void addLowDiskSpace(MinecraftClient client) {
      show(client.getToastManager(), SystemToast.Type.LOW_DISK_SPACE, Text.translatable("chunk.toast.lowDiskSpace"), Text.translatable("chunk.toast.lowDiskSpace.description"));
   }

   public static void addChunkLoadFailure(MinecraftClient client, ChunkPos pos) {
      show(client.getToastManager(), SystemToast.Type.CHUNK_LOAD_FAILURE, Text.translatable("chunk.toast.loadFailure", Text.of(pos)).formatted(Formatting.RED), Text.translatable("chunk.toast.checkLog"));
   }

   public static void addChunkSaveFailure(MinecraftClient client, ChunkPos pos) {
      show(client.getToastManager(), SystemToast.Type.CHUNK_SAVE_FAILURE, Text.translatable("chunk.toast.saveFailure", Text.of(pos)).formatted(Formatting.RED), Text.translatable("chunk.toast.checkLog"));
   }

   // $FF: synthetic method
   public Object getType() {
      return this.getType();
   }

   @Environment(EnvType.CLIENT)
   public static class Type {
      public static final Type NARRATOR_TOGGLE = new Type();
      public static final Type WORLD_BACKUP = new Type();
      public static final Type PACK_LOAD_FAILURE = new Type();
      public static final Type WORLD_ACCESS_FAILURE = new Type();
      public static final Type PACK_COPY_FAILURE = new Type();
      public static final Type FILE_DROP_FAILURE = new Type();
      public static final Type PERIODIC_NOTIFICATION = new Type();
      public static final Type LOW_DISK_SPACE = new Type(10000L);
      public static final Type CHUNK_LOAD_FAILURE = new Type();
      public static final Type CHUNK_SAVE_FAILURE = new Type();
      public static final Type UNSECURE_SERVER_WARNING = new Type(10000L);
      final long displayDuration;

      public Type(long displayDuration) {
         this.displayDuration = displayDuration;
      }

      public Type() {
         this(5000L);
      }
   }
}
