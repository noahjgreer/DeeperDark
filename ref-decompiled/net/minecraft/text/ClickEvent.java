package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public interface ClickEvent {
   Codec CODEC = ClickEvent.Action.CODEC.dispatch("action", ClickEvent::getAction, (action) -> {
      return action.codec;
   });

   Action getAction();

   public static enum Action implements StringIdentifiable {
      OPEN_URL("open_url", true, ClickEvent.OpenUrl.CODEC),
      OPEN_FILE("open_file", false, ClickEvent.OpenFile.CODEC),
      RUN_COMMAND("run_command", true, ClickEvent.RunCommand.CODEC),
      SUGGEST_COMMAND("suggest_command", true, ClickEvent.SuggestCommand.CODEC),
      SHOW_DIALOG("show_dialog", true, ClickEvent.ShowDialog.CODEC),
      CHANGE_PAGE("change_page", true, ClickEvent.ChangePage.CODEC),
      COPY_TO_CLIPBOARD("copy_to_clipboard", true, ClickEvent.CopyToClipboard.CODEC),
      CUSTOM("custom", true, ClickEvent.Custom.CODEC);

      public static final Codec UNVALIDATED_CODEC = StringIdentifiable.createCodec(Action::values);
      public static final Codec CODEC = UNVALIDATED_CODEC.validate(Action::validate);
      private final boolean userDefinable;
      private final String name;
      final MapCodec codec;

      private Action(final String name, final boolean userDefinable, final MapCodec codec) {
         this.name = name;
         this.userDefinable = userDefinable;
         this.codec = codec;
      }

      public boolean isUserDefinable() {
         return this.userDefinable;
      }

      public String asString() {
         return this.name;
      }

      public MapCodec getCodec() {
         return this.codec;
      }

      public static DataResult validate(Action action) {
         return !action.isUserDefinable() ? DataResult.error(() -> {
            return "Click event type not allowed: " + String.valueOf(action);
         }) : DataResult.success(action, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] method_36945() {
         return new Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, SHOW_DIALOG, CHANGE_PAGE, COPY_TO_CLIPBOARD, CUSTOM};
      }
   }

   public static record Custom(Identifier id, Optional payload) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("id").forGetter(Custom::id), Codecs.NBT_ELEMENT.optionalFieldOf("payload").forGetter(Custom::payload)).apply(instance, Custom::new);
      });

      public Custom(Identifier identifier, Optional optional) {
         this.id = identifier;
         this.payload = optional;
      }

      public Action getAction() {
         return ClickEvent.Action.CUSTOM;
      }

      public Identifier id() {
         return this.id;
      }

      public Optional payload() {
         return this.payload;
      }
   }

   public static record CopyToClipboard(String value) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.fieldOf("value").forGetter(CopyToClipboard::value)).apply(instance, CopyToClipboard::new);
      });

      public CopyToClipboard(String string) {
         this.value = string;
      }

      public Action getAction() {
         return ClickEvent.Action.COPY_TO_CLIPBOARD;
      }

      public String value() {
         return this.value;
      }
   }

   public static record ChangePage(int page) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.POSITIVE_INT.fieldOf("page").forGetter(ChangePage::page)).apply(instance, ChangePage::new);
      });

      public ChangePage(int i) {
         this.page = i;
      }

      public Action getAction() {
         return ClickEvent.Action.CHANGE_PAGE;
      }

      public int page() {
         return this.page;
      }
   }

   public static record ShowDialog(RegistryEntry dialog) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Dialog.ENTRY_CODEC.fieldOf("dialog").forGetter(ShowDialog::dialog)).apply(instance, ShowDialog::new);
      });

      public ShowDialog(RegistryEntry registryEntry) {
         this.dialog = registryEntry;
      }

      public Action getAction() {
         return ClickEvent.Action.SHOW_DIALOG;
      }

      public RegistryEntry dialog() {
         return this.dialog;
      }
   }

   public static record SuggestCommand(String command) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.CHAT_TEXT.fieldOf("command").forGetter(SuggestCommand::command)).apply(instance, SuggestCommand::new);
      });

      public SuggestCommand(String string) {
         this.command = string;
      }

      public Action getAction() {
         return ClickEvent.Action.SUGGEST_COMMAND;
      }

      public String command() {
         return this.command;
      }
   }

   public static record RunCommand(String command) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.CHAT_TEXT.fieldOf("command").forGetter(RunCommand::command)).apply(instance, RunCommand::new);
      });

      public RunCommand(String string) {
         this.command = string;
      }

      public Action getAction() {
         return ClickEvent.Action.RUN_COMMAND;
      }

      public String command() {
         return this.command;
      }
   }

   public static record OpenFile(String path) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.fieldOf("path").forGetter(OpenFile::path)).apply(instance, OpenFile::new);
      });

      public OpenFile(File file) {
         this(file.toString());
      }

      public OpenFile(Path path) {
         this(path.toFile());
      }

      public OpenFile(String string) {
         this.path = string;
      }

      public File file() {
         return new File(this.path);
      }

      public Action getAction() {
         return ClickEvent.Action.OPEN_FILE;
      }

      public String path() {
         return this.path;
      }
   }

   public static record OpenUrl(URI uri) implements ClickEvent {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.URI.fieldOf("url").forGetter(OpenUrl::uri)).apply(instance, OpenUrl::new);
      });

      public OpenUrl(URI uRI) {
         this.uri = uRI;
      }

      public Action getAction() {
         return ClickEvent.Action.OPEN_URL;
      }

      public URI uri() {
         return this.uri;
      }
   }
}
