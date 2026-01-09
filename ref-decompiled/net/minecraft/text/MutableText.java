package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class MutableText implements Text {
   private final TextContent content;
   private final List siblings;
   private Style style;
   private OrderedText ordered;
   @Nullable
   private Language language;

   MutableText(TextContent content, List siblings, Style style) {
      this.ordered = OrderedText.EMPTY;
      this.content = content;
      this.siblings = siblings;
      this.style = style;
   }

   public static MutableText of(TextContent content) {
      return new MutableText(content, Lists.newArrayList(), Style.EMPTY);
   }

   public TextContent getContent() {
      return this.content;
   }

   public List getSiblings() {
      return this.siblings;
   }

   public MutableText setStyle(Style style) {
      this.style = style;
      return this;
   }

   public Style getStyle() {
      return this.style;
   }

   public MutableText append(String text) {
      return text.isEmpty() ? this : this.append((Text)Text.literal(text));
   }

   public MutableText append(Text text) {
      this.siblings.add(text);
      return this;
   }

   public MutableText styled(UnaryOperator styleUpdater) {
      this.setStyle((Style)styleUpdater.apply(this.getStyle()));
      return this;
   }

   public MutableText fillStyle(Style styleOverride) {
      this.setStyle(styleOverride.withParent(this.getStyle()));
      return this;
   }

   public MutableText formatted(Formatting... formattings) {
      this.setStyle(this.getStyle().withFormatting(formattings));
      return this;
   }

   public MutableText formatted(Formatting formatting) {
      this.setStyle(this.getStyle().withFormatting(formatting));
      return this;
   }

   public MutableText withColor(int color) {
      this.setStyle(this.getStyle().withColor(color));
      return this;
   }

   public OrderedText asOrderedText() {
      Language language = Language.getInstance();
      if (this.language != language) {
         this.ordered = language.reorder((StringVisitable)this);
         this.language = language;
      }

      return this.ordered;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof MutableText)) {
         return false;
      } else {
         MutableText mutableText = (MutableText)o;
         return this.content.equals(mutableText.content) && this.style.equals(mutableText.style) && this.siblings.equals(mutableText.siblings);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.content, this.style, this.siblings});
   }

   public String toString() {
      StringBuilder stringBuilder = new StringBuilder(this.content.toString());
      boolean bl = !this.style.isEmpty();
      boolean bl2 = !this.siblings.isEmpty();
      if (bl || bl2) {
         stringBuilder.append('[');
         if (bl) {
            stringBuilder.append("style=");
            stringBuilder.append(this.style);
         }

         if (bl && bl2) {
            stringBuilder.append(", ");
         }

         if (bl2) {
            stringBuilder.append("siblings=");
            stringBuilder.append(this.siblings);
         }

         stringBuilder.append(']');
      }

      return stringBuilder.toString();
   }
}
