package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class Texts {
   public static final String DEFAULT_SEPARATOR = ", ";
   public static final Text GRAY_DEFAULT_SEPARATOR_TEXT;
   public static final Text DEFAULT_SEPARATOR_TEXT;

   public static MutableText setStyleIfAbsent(MutableText text, Style style) {
      if (style.isEmpty()) {
         return text;
      } else {
         Style style2 = text.getStyle();
         if (style2.isEmpty()) {
            return text.setStyle(style);
         } else {
            return style2.equals(style) ? text : text.setStyle(style2.withParent(style));
         }
      }
   }

   public static Optional parse(@Nullable ServerCommandSource source, Optional text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      return text.isPresent() ? Optional.of(parse(source, (Text)text.get(), sender, depth)) : Optional.empty();
   }

   public static MutableText parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      if (depth > 100) {
         return text.copy();
      } else {
         MutableText mutableText = text.getContent().parse(source, sender, depth + 1);
         Iterator var5 = text.getSiblings().iterator();

         while(var5.hasNext()) {
            Text text2 = (Text)var5.next();
            mutableText.append((Text)parse(source, text2, sender, depth + 1));
         }

         return mutableText.fillStyle(parseStyle(source, text.getStyle(), sender, depth));
      }
   }

   private static Style parseStyle(@Nullable ServerCommandSource source, Style style, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      HoverEvent hoverEvent = style.getHoverEvent();
      if (hoverEvent instanceof HoverEvent.ShowText var5) {
         HoverEvent.ShowText var10000 = var5;

         Text var9;
         try {
            var9 = var10000.value();
         } catch (Throwable var8) {
            throw new MatchException(var8.toString(), var8);
         }

         Text var7 = var9;
         HoverEvent hoverEvent2 = new HoverEvent.ShowText(parse(source, var7, sender, depth + 1));
         return style.withHoverEvent(hoverEvent2);
      } else {
         return style;
      }
   }

   public static Text joinOrdered(Collection strings) {
      return joinOrdered(strings, (string) -> {
         return Text.literal(string).formatted(Formatting.GREEN);
      });
   }

   public static Text joinOrdered(Collection elements, Function transformer) {
      if (elements.isEmpty()) {
         return ScreenTexts.EMPTY;
      } else if (elements.size() == 1) {
         return (Text)transformer.apply((Comparable)elements.iterator().next());
      } else {
         List list = Lists.newArrayList(elements);
         list.sort(Comparable::compareTo);
         return join(list, (Function)transformer);
      }
   }

   public static Text join(Collection elements, Function transformer) {
      return join(elements, GRAY_DEFAULT_SEPARATOR_TEXT, transformer);
   }

   public static MutableText join(Collection elements, Optional separator, Function transformer) {
      return join(elements, (Text)DataFixUtils.orElse(separator, GRAY_DEFAULT_SEPARATOR_TEXT), transformer);
   }

   public static Text join(Collection texts, Text separator) {
      return join(texts, separator, Function.identity());
   }

   public static MutableText join(Collection elements, Text separator, Function transformer) {
      if (elements.isEmpty()) {
         return Text.empty();
      } else if (elements.size() == 1) {
         return ((Text)transformer.apply(elements.iterator().next())).copy();
      } else {
         MutableText mutableText = Text.empty();
         boolean bl = true;

         for(Iterator var5 = elements.iterator(); var5.hasNext(); bl = false) {
            Object object = var5.next();
            if (!bl) {
               mutableText.append(separator);
            }

            mutableText.append((Text)transformer.apply(object));
         }

         return mutableText;
      }
   }

   public static MutableText bracketed(Text text) {
      return Text.translatable("chat.square_brackets", text);
   }

   public static Text toText(Message message) {
      if (message instanceof Text text) {
         return text;
      } else {
         return Text.literal(message.getString());
      }
   }

   public static boolean hasTranslation(@Nullable Text text) {
      if (text != null) {
         TextContent var2 = text.getContent();
         if (var2 instanceof TranslatableTextContent) {
            TranslatableTextContent translatableTextContent = (TranslatableTextContent)var2;
            String string = translatableTextContent.getKey();
            String string2 = translatableTextContent.getFallback();
            return string2 != null || Language.getInstance().hasTranslation(string);
         }
      }

      return true;
   }

   public static MutableText bracketedCopyable(String string) {
      return bracketed(Text.literal(string).styled((style) -> {
         return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent.CopyToClipboard(string)).withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.copy.click"))).withInsertion(string);
      }));
   }

   static {
      GRAY_DEFAULT_SEPARATOR_TEXT = Text.literal(", ").formatted(Formatting.GRAY);
      DEFAULT_SEPARATOR_TEXT = Text.literal(", ");
   }
}
