package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NbtTextContent implements TextContent {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("nbt").forGetter(NbtTextContent::getPath), Codec.BOOL.lenientOptionalFieldOf("interpret", false).forGetter(NbtTextContent::shouldInterpret), TextCodecs.CODEC.lenientOptionalFieldOf("separator").forGetter(NbtTextContent::getSeparator), NbtDataSource.CODEC.forGetter(NbtTextContent::getDataSource)).apply(instance, NbtTextContent::new);
   });
   public static final TextContent.Type TYPE;
   private final boolean interpret;
   private final Optional separator;
   private final String rawPath;
   private final NbtDataSource dataSource;
   @Nullable
   protected final NbtPathArgumentType.NbtPath path;

   public NbtTextContent(String rawPath, boolean interpret, Optional separator, NbtDataSource dataSource) {
      this(rawPath, parsePath(rawPath), interpret, separator, dataSource);
   }

   private NbtTextContent(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Optional separator, NbtDataSource dataSource) {
      this.rawPath = rawPath;
      this.path = path;
      this.interpret = interpret;
      this.separator = separator;
      this.dataSource = dataSource;
   }

   @Nullable
   private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
      try {
         return (new NbtPathArgumentType()).parse(new StringReader(rawPath));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public String getPath() {
      return this.rawPath;
   }

   public boolean shouldInterpret() {
      return this.interpret;
   }

   public Optional getSeparator() {
      return this.separator;
   }

   public NbtDataSource getDataSource() {
      return this.dataSource;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof NbtTextContent) {
            NbtTextContent nbtTextContent = (NbtTextContent)o;
            if (this.dataSource.equals(nbtTextContent.dataSource) && this.separator.equals(nbtTextContent.separator) && this.interpret == nbtTextContent.interpret && this.rawPath.equals(nbtTextContent.rawPath)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int i = this.interpret ? 1 : 0;
      i = 31 * i + this.separator.hashCode();
      i = 31 * i + this.rawPath.hashCode();
      i = 31 * i + this.dataSource.hashCode();
      return i;
   }

   public String toString() {
      String var10000 = String.valueOf(this.dataSource);
      return "nbt{" + var10000 + ", interpreting=" + this.interpret + ", separator=" + String.valueOf(this.separator) + "}";
   }

   public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      if (source != null && this.path != null) {
         Stream stream = this.dataSource.get(source).flatMap((nbt) -> {
            try {
               return this.path.get(nbt).stream();
            } catch (CommandSyntaxException var3) {
               return Stream.empty();
            }
         });
         if (this.interpret) {
            RegistryOps registryOps = source.getRegistryManager().getOps(NbtOps.INSTANCE);
            Text text = (Text)DataFixUtils.orElse(Texts.parse(source, this.separator, sender, depth), Texts.DEFAULT_SEPARATOR_TEXT);
            return (MutableText)stream.flatMap((nbt) -> {
               try {
                  Text text = (Text)TextCodecs.CODEC.parse(registryOps, nbt).getOrThrow();
                  return Stream.of(Texts.parse(source, text, sender, depth));
               } catch (Exception var6) {
                  LOGGER.warn("Failed to parse component: {}", nbt, var6);
                  return Stream.of();
               }
            }).reduce((accumulator, current) -> {
               return accumulator.append(text).append((Text)current);
            }).orElseGet(Text::empty);
         } else {
            Stream stream2 = stream.map(NbtTextContent::asString);
            return (MutableText)Texts.parse(source, this.separator, sender, depth).map((textx) -> {
               return (MutableText)stream2.map(Text::literal).reduce((accumulator, current) -> {
                  return accumulator.append((Text)textx).append((Text)current);
               }).orElseGet(Text::empty);
            }).orElseGet(() -> {
               return Text.literal((String)stream2.collect(Collectors.joining(", ")));
            });
         }
      } else {
         return Text.empty();
      }
   }

   private static String asString(NbtElement nbt) {
      if (nbt instanceof NbtString var1) {
         NbtString var10000 = var1;

         String var5;
         try {
            var5 = var10000.value();
         } catch (Throwable var4) {
            throw new MatchException(var4.toString(), var4);
         }

         String var3 = var5;
         return var3;
      } else {
         return nbt.toString();
      }
   }

   public TextContent.Type getType() {
      return TYPE;
   }

   static {
      TYPE = new TextContent.Type(CODEC, "nbt");
   }
}
