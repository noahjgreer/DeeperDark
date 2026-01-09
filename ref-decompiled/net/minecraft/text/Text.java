package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.datafixers.util.Either;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

public interface Text extends Message, StringVisitable {
   Style getStyle();

   TextContent getContent();

   default String getString() {
      return StringVisitable.super.getString();
   }

   default String asTruncatedString(int length) {
      StringBuilder stringBuilder = new StringBuilder();
      this.visit((string) -> {
         int j = length - stringBuilder.length();
         if (j <= 0) {
            return TERMINATE_VISIT;
         } else {
            stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
            return Optional.empty();
         }
      });
      return stringBuilder.toString();
   }

   List getSiblings();

   @Nullable
   default String getLiteralString() {
      TextContent var2 = this.getContent();
      if (var2 instanceof PlainTextContent plainTextContent) {
         if (this.getSiblings().isEmpty() && this.getStyle().isEmpty()) {
            return plainTextContent.string();
         }
      }

      return null;
   }

   default MutableText copyContentOnly() {
      return MutableText.of(this.getContent());
   }

   default MutableText copy() {
      return new MutableText(this.getContent(), new ArrayList(this.getSiblings()), this.getStyle());
   }

   OrderedText asOrderedText();

   default Optional visit(StringVisitable.StyledVisitor styledVisitor, Style style) {
      Style style2 = this.getStyle().withParent(style);
      Optional optional = this.getContent().visit(styledVisitor, style2);
      if (optional.isPresent()) {
         return optional;
      } else {
         Iterator var5 = this.getSiblings().iterator();

         Optional optional2;
         do {
            if (!var5.hasNext()) {
               return Optional.empty();
            }

            Text text = (Text)var5.next();
            optional2 = text.visit(styledVisitor, style2);
         } while(!optional2.isPresent());

         return optional2;
      }
   }

   default Optional visit(StringVisitable.Visitor visitor) {
      Optional optional = this.getContent().visit(visitor);
      if (optional.isPresent()) {
         return optional;
      } else {
         Iterator var3 = this.getSiblings().iterator();

         Optional optional2;
         do {
            if (!var3.hasNext()) {
               return Optional.empty();
            }

            Text text = (Text)var3.next();
            optional2 = text.visit(visitor);
         } while(!optional2.isPresent());

         return optional2;
      }
   }

   default List withoutStyle() {
      return this.getWithStyle(Style.EMPTY);
   }

   default List getWithStyle(Style style) {
      List list = Lists.newArrayList();
      this.visit((styleOverride, text) -> {
         if (!text.isEmpty()) {
            list.add(literal(text).fillStyle(styleOverride));
         }

         return Optional.empty();
      }, style);
      return list;
   }

   default boolean contains(Text text) {
      if (this.equals(text)) {
         return true;
      } else {
         List list = this.withoutStyle();
         List list2 = text.getWithStyle(this.getStyle());
         return Collections.indexOfSubList(list, list2) != -1;
      }
   }

   static Text of(@Nullable String string) {
      return (Text)(string != null ? literal(string) : ScreenTexts.EMPTY);
   }

   static MutableText literal(String string) {
      return MutableText.of(PlainTextContent.of(string));
   }

   static MutableText translatable(String key) {
      return MutableText.of(new TranslatableTextContent(key, (String)null, TranslatableTextContent.EMPTY_ARGUMENTS));
   }

   static MutableText translatable(String key, Object... args) {
      return MutableText.of(new TranslatableTextContent(key, (String)null, args));
   }

   static MutableText stringifiedTranslatable(String key, Object... args) {
      for(int i = 0; i < args.length; ++i) {
         Object object = args[i];
         if (!TranslatableTextContent.isPrimitive(object) && !(object instanceof Text)) {
            args[i] = String.valueOf(object);
         }
      }

      return translatable(key, args);
   }

   static MutableText translatableWithFallback(String key, @Nullable String fallback) {
      return MutableText.of(new TranslatableTextContent(key, fallback, TranslatableTextContent.EMPTY_ARGUMENTS));
   }

   static MutableText translatableWithFallback(String key, @Nullable String fallback, Object... args) {
      return MutableText.of(new TranslatableTextContent(key, fallback, args));
   }

   static MutableText empty() {
      return MutableText.of(PlainTextContent.EMPTY);
   }

   static MutableText keybind(String string) {
      return MutableText.of(new KeybindTextContent(string));
   }

   static MutableText nbt(String rawPath, boolean interpret, Optional separator, NbtDataSource dataSource) {
      return MutableText.of(new NbtTextContent(rawPath, interpret, separator, dataSource));
   }

   static MutableText score(ParsedSelector selector, String objective) {
      return MutableText.of(new ScoreTextContent(Either.left(selector), objective));
   }

   static MutableText score(String name, String objective) {
      return MutableText.of(new ScoreTextContent(Either.right(name), objective));
   }

   static MutableText selector(ParsedSelector selector, Optional separator) {
      return MutableText.of(new SelectorTextContent(selector, separator));
   }

   static Text of(Date date) {
      return literal(date.toString());
   }

   static Text of(Message message) {
      Object var10000;
      if (message instanceof Text text) {
         var10000 = text;
      } else {
         var10000 = literal(message.getString());
      }

      return (Text)var10000;
   }

   static Text of(UUID uuid) {
      return literal(uuid.toString());
   }

   static Text of(Identifier id) {
      return literal(id.toString());
   }

   static Text of(ChunkPos pos) {
      return literal(pos.toString());
   }

   static Text of(URI uri) {
      return literal(uri.toString());
   }
}
