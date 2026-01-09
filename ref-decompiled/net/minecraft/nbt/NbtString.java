package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public record NbtString(String value) implements NbtPrimitive {
   private static final int SIZE = 36;
   public static final NbtType TYPE = new NbtType.OfVariableSize() {
      public NbtString read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return NbtString.of(readString(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitString(readString(input, tracker));
      }

      private static String readString(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(36L);
         String string = input.readUTF();
         tracker.add(2L, (long)string.length());
         return string;
      }

      public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
         NbtString.skip(input);
      }

      public String getCrashReportName() {
         return "STRING";
      }

      public String getCommandFeedbackName() {
         return "TAG_String";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };
   private static final NbtString EMPTY = new NbtString("");
   private static final char DOUBLE_QUOTE = '"';
   private static final char SINGLE_QUOTE = '\'';
   private static final char BACKSLASH = '\\';
   private static final char NULL = '\u0000';

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public NbtString(String value) {
      this.value = value;
   }

   public static void skip(DataInput input) throws IOException {
      input.skipBytes(input.readUnsignedShort());
   }

   public static NbtString of(String value) {
      return value.isEmpty() ? EMPTY : new NbtString(value);
   }

   public void write(DataOutput output) throws IOException {
      output.writeUTF(this.value);
   }

   public int getSizeInBytes() {
      return 36 + 2 * this.value.length();
   }

   public byte getType() {
      return 8;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitString(this);
      return stringNbtWriter.getString();
   }

   public NbtString copy() {
      return this;
   }

   public Optional asString() {
      return Optional.of(this.value);
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitString(this);
   }

   public static String escape(String value) {
      StringBuilder stringBuilder = new StringBuilder();
      appendEscaped(value, stringBuilder);
      return stringBuilder.toString();
   }

   public static void appendEscaped(String value, StringBuilder builder) {
      int i = builder.length();
      builder.append(' ');
      char c = 0;

      for(int j = 0; j < value.length(); ++j) {
         char d = value.charAt(j);
         if (d == '\\') {
            builder.append("\\\\");
         } else if (d != '"' && d != '\'') {
            String string = SnbtParsing.escapeSpecialChar(d);
            if (string != null) {
               builder.append('\\');
               builder.append(string);
            } else {
               builder.append(d);
            }
         } else {
            if (c == 0) {
               c = d == '"' ? 39 : 34;
            }

            if (c == d) {
               builder.append('\\');
            }

            builder.append(d);
         }
      }

      if (c == 0) {
         c = 34;
      }

      builder.setCharAt(i, (char)c);
      builder.append((char)c);
   }

   public static String method_72226(String string) {
      StringBuilder stringBuilder = new StringBuilder();
      method_72225(string, stringBuilder);
      return stringBuilder.toString();
   }

   public static void method_72225(String string, StringBuilder stringBuilder) {
      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         switch (c) {
            case '"':
            case '\'':
            case '\\':
               stringBuilder.append('\\');
               stringBuilder.append(c);
               break;
            default:
               String string2 = SnbtParsing.escapeSpecialChar(c);
               if (string2 != null) {
                  stringBuilder.append('\\');
                  stringBuilder.append(string2);
               } else {
                  stringBuilder.append(c);
               }
         }
      }

   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitString(this.value);
   }

   public String value() {
      return this.value;
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }
}
