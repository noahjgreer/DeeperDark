package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

public record SelectorTextContent(ParsedSelector selector, Optional separator) implements TextContent {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ParsedSelector.CODEC.fieldOf("selector").forGetter(SelectorTextContent::selector), TextCodecs.CODEC.optionalFieldOf("separator").forGetter(SelectorTextContent::separator)).apply(instance, SelectorTextContent::new);
   });
   public static final TextContent.Type TYPE;

   public SelectorTextContent(ParsedSelector parsedSelector, Optional separator) {
      this.selector = parsedSelector;
      this.separator = separator;
   }

   public TextContent.Type getType() {
      return TYPE;
   }

   public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      if (source == null) {
         return Text.empty();
      } else {
         Optional optional = Texts.parse(source, this.separator, sender, depth);
         return Texts.join(this.selector.comp_3068().getEntities(source), (Optional)optional, Entity::getDisplayName);
      }
   }

   public Optional visit(StringVisitable.StyledVisitor visitor, Style style) {
      return visitor.accept(style, this.selector.comp_3067());
   }

   public Optional visit(StringVisitable.Visitor visitor) {
      return visitor.accept(this.selector.comp_3067());
   }

   public String toString() {
      return "pattern{" + String.valueOf(this.selector) + "}";
   }

   public ParsedSelector selector() {
      return this.selector;
   }

   public Optional separator() {
      return this.separator;
   }

   static {
      TYPE = new TextContent.Type(CODEC, "selector");
   }
}
