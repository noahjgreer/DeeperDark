package net.minecraft.client.realms.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TextRenderingUtils {
   private TextRenderingUtils() {
   }

   @VisibleForTesting
   protected static List lineBreak(String text) {
      return Arrays.asList(text.split("\\n"));
   }

   public static List decompose(String text, LineSegment... links) {
      return decompose(text, Arrays.asList(links));
   }

   private static List decompose(String text, List links) {
      List list = lineBreak(text);
      return insertLinks(list, links);
   }

   private static List insertLinks(List lines, List links) {
      int i = 0;
      List list = Lists.newArrayList();
      Iterator var4 = lines.iterator();

      while(var4.hasNext()) {
         String string = (String)var4.next();
         List list2 = Lists.newArrayList();
         List list3 = split(string, "%link");
         Iterator var8 = list3.iterator();

         while(var8.hasNext()) {
            String string2 = (String)var8.next();
            if ("%link".equals(string2)) {
               list2.add((LineSegment)links.get(i++));
            } else {
               list2.add(TextRenderingUtils.LineSegment.text(string2));
            }
         }

         list.add(new Line(list2));
      }

      return list;
   }

   public static List split(String line, String delimiter) {
      if (delimiter.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List list = Lists.newArrayList();

         int i;
         int j;
         for(i = 0; (j = line.indexOf(delimiter, i)) != -1; i = j + delimiter.length()) {
            if (j > i) {
               list.add(line.substring(i, j));
            }

            list.add(delimiter);
         }

         if (i < line.length()) {
            list.add(line.substring(i));
         }

         return list;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class LineSegment {
      private final String fullText;
      @Nullable
      private final String linkTitle;
      @Nullable
      private final String linkUrl;

      private LineSegment(String fullText) {
         this.fullText = fullText;
         this.linkTitle = null;
         this.linkUrl = null;
      }

      private LineSegment(String fullText, @Nullable String linkTitle, @Nullable String linkUrl) {
         this.fullText = fullText;
         this.linkTitle = linkTitle;
         this.linkUrl = linkUrl;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            LineSegment lineSegment = (LineSegment)o;
            return Objects.equals(this.fullText, lineSegment.fullText) && Objects.equals(this.linkTitle, lineSegment.linkTitle) && Objects.equals(this.linkUrl, lineSegment.linkUrl);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.fullText, this.linkTitle, this.linkUrl});
      }

      public String toString() {
         return "Segment{fullText='" + this.fullText + "', linkTitle='" + this.linkTitle + "', linkUrl='" + this.linkUrl + "'}";
      }

      public String renderedText() {
         return this.isLink() ? this.linkTitle : this.fullText;
      }

      public boolean isLink() {
         return this.linkTitle != null;
      }

      public String getLinkUrl() {
         if (!this.isLink()) {
            throw new IllegalStateException("Not a link: " + String.valueOf(this));
         } else {
            return this.linkUrl;
         }
      }

      public static LineSegment link(String linkTitle, String linkUrl) {
         return new LineSegment((String)null, linkTitle, linkUrl);
      }

      @VisibleForTesting
      protected static LineSegment text(String fullText) {
         return new LineSegment(fullText);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Line {
      public final List segments;

      Line(LineSegment... segments) {
         this(Arrays.asList(segments));
      }

      Line(List segments) {
         this.segments = segments;
      }

      public String toString() {
         return "Line{segments=" + String.valueOf(this.segments) + "}";
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Line line = (Line)o;
            return Objects.equals(this.segments, line.segments);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.segments});
      }
   }
}
