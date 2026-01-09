package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public class TranslatableTextContent implements TextContent {
   public static final Object[] EMPTY_ARGUMENTS = new Object[0];
   private static final Codec OBJECT_ARGUMENT_CODEC;
   private static final Codec ARGUMENT_CODEC;
   public static final MapCodec CODEC;
   public static final TextContent.Type TYPE;
   private static final StringVisitable LITERAL_PERCENT_SIGN;
   private static final StringVisitable NULL_ARGUMENT;
   private final String key;
   @Nullable
   private final String fallback;
   private final Object[] args;
   @Nullable
   private Language languageCache;
   private List translations = ImmutableList.of();
   private static final Pattern ARG_FORMAT;

   private static DataResult validate(@Nullable Object object) {
      return !isPrimitive(object) ? DataResult.error(() -> {
         return "This value needs to be parsed as component";
      }) : DataResult.success(object);
   }

   public static boolean isPrimitive(@Nullable Object argument) {
      return argument instanceof Number || argument instanceof Boolean || argument instanceof String;
   }

   private static Optional toOptionalList(Object[] args) {
      return args.length == 0 ? Optional.empty() : Optional.of(Arrays.asList(args));
   }

   private static Object[] toArray(Optional args) {
      return (Object[])args.map((list) -> {
         return list.isEmpty() ? EMPTY_ARGUMENTS : list.toArray();
      }).orElse(EMPTY_ARGUMENTS);
   }

   private static TranslatableTextContent of(String key, Optional fallback, Optional args) {
      return new TranslatableTextContent(key, (String)fallback.orElse((Object)null), toArray(args));
   }

   public TranslatableTextContent(String key, @Nullable String fallback, Object[] args) {
      this.key = key;
      this.fallback = fallback;
      this.args = args;
   }

   public TextContent.Type getType() {
      return TYPE;
   }

   private void updateTranslations() {
      Language language = Language.getInstance();
      if (language != this.languageCache) {
         this.languageCache = language;
         String string = this.fallback != null ? language.get(this.key, this.fallback) : language.get(this.key);

         try {
            ImmutableList.Builder builder = ImmutableList.builder();
            Objects.requireNonNull(builder);
            this.forEachPart(string, builder::add);
            this.translations = builder.build();
         } catch (TranslationException var4) {
            this.translations = ImmutableList.of(StringVisitable.plain(string));
         }

      }
   }

   private void forEachPart(String translation, Consumer partsConsumer) {
      Matcher matcher = ARG_FORMAT.matcher(translation);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            String string;
            if (k > j) {
               string = translation.substring(j, k);
               if (string.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               partsConsumer.accept(StringVisitable.plain(string));
            }

            string = matcher.group(2);
            String string2 = translation.substring(k, l);
            if ("%".equals(string) && "%%".equals(string2)) {
               partsConsumer.accept(LITERAL_PERCENT_SIGN);
            } else {
               if (!"s".equals(string)) {
                  throw new TranslationException(this, "Unsupported format: '" + string2 + "'");
               }

               String string3 = matcher.group(1);
               int m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
               partsConsumer.accept(this.getArg(m));
            }
         }

         if (j < translation.length()) {
            String string4 = translation.substring(j);
            if (string4.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            partsConsumer.accept(StringVisitable.plain(string4));
         }

      } catch (IllegalArgumentException var12) {
         throw new TranslationException(this, var12);
      }
   }

   public final StringVisitable getArg(int index) {
      if (index >= 0 && index < this.args.length) {
         Object object = this.args[index];
         if (object instanceof Text) {
            Text text = (Text)object;
            return text;
         } else {
            return object == null ? NULL_ARGUMENT : StringVisitable.plain(object.toString());
         }
      } else {
         throw new TranslationException(this, index);
      }
   }

   public Optional visit(StringVisitable.StyledVisitor visitor, Style style) {
      this.updateTranslations();
      Iterator var3 = this.translations.iterator();

      Optional optional;
      do {
         if (!var3.hasNext()) {
            return Optional.empty();
         }

         StringVisitable stringVisitable = (StringVisitable)var3.next();
         optional = stringVisitable.visit(visitor, style);
      } while(!optional.isPresent());

      return optional;
   }

   public Optional visit(StringVisitable.Visitor visitor) {
      this.updateTranslations();
      Iterator var2 = this.translations.iterator();

      Optional optional;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         StringVisitable stringVisitable = (StringVisitable)var2.next();
         optional = stringVisitable.visit(visitor);
      } while(!optional.isPresent());

      return optional;
   }

   public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      Object[] objects = new Object[this.args.length];

      for(int i = 0; i < objects.length; ++i) {
         Object object = this.args[i];
         if (object instanceof Text text) {
            objects[i] = Texts.parse(source, text, sender, depth);
         } else {
            objects[i] = object;
         }
      }

      return MutableText.of(new TranslatableTextContent(this.key, this.fallback, objects));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof TranslatableTextContent) {
            TranslatableTextContent translatableTextContent = (TranslatableTextContent)o;
            if (Objects.equals(this.key, translatableTextContent.key) && Objects.equals(this.fallback, translatableTextContent.fallback) && Arrays.equals(this.args, translatableTextContent.args)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int i = Objects.hashCode(this.key);
      i = 31 * i + Objects.hashCode(this.fallback);
      i = 31 * i + Arrays.hashCode(this.args);
      return i;
   }

   public String toString() {
      String var10000 = this.key;
      return "translation{key='" + var10000 + "'" + (this.fallback != null ? ", fallback='" + this.fallback + "'" : "") + ", args=" + Arrays.toString(this.args) + "}";
   }

   public String getKey() {
      return this.key;
   }

   @Nullable
   public String getFallback() {
      return this.fallback;
   }

   public Object[] getArgs() {
      return this.args;
   }

   static {
      OBJECT_ARGUMENT_CODEC = Codecs.BASIC_OBJECT.validate(TranslatableTextContent::validate);
      ARGUMENT_CODEC = Codec.either(OBJECT_ARGUMENT_CODEC, TextCodecs.CODEC).xmap((either) -> {
         return either.map((object) -> {
            return object;
         }, (text) -> {
            return Objects.requireNonNullElse(text.getLiteralString(), text);
         });
      }, (argument) -> {
         Either var10000;
         if (argument instanceof Text text) {
            var10000 = Either.right(text);
         } else {
            var10000 = Either.left(argument);
         }

         return var10000;
      });
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.fieldOf("translate").forGetter((content) -> {
            return content.key;
         }), Codec.STRING.lenientOptionalFieldOf("fallback").forGetter((content) -> {
            return Optional.ofNullable(content.fallback);
         }), ARGUMENT_CODEC.listOf().optionalFieldOf("with").forGetter((content) -> {
            return toOptionalList(content.args);
         })).apply(instance, TranslatableTextContent::of);
      });
      TYPE = new TextContent.Type(CODEC, "translatable");
      LITERAL_PERCENT_SIGN = StringVisitable.plain("%");
      NULL_ARGUMENT = StringVisitable.plain("null");
      ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
   }
}
