package net.minecraft.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class Identifier implements Comparable {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final SimpleCommandExceptionType COMMAND_EXCEPTION;
   public static final char NAMESPACE_SEPARATOR = ':';
   public static final String DEFAULT_NAMESPACE = "minecraft";
   public static final String REALMS_NAMESPACE = "realms";
   private final String namespace;
   private final String path;

   private Identifier(String namespace, String path) {
      assert isNamespaceValid(namespace);

      assert isPathValid(path);

      this.namespace = namespace;
      this.path = path;
   }

   private static Identifier ofValidated(String namespace, String path) {
      return new Identifier(validateNamespace(namespace, path), validatePath(namespace, path));
   }

   public static Identifier of(String namespace, String path) {
      return ofValidated(namespace, path);
   }

   public static Identifier of(String id) {
      return splitOn(id, ':');
   }

   public static Identifier ofVanilla(String path) {
      return new Identifier("minecraft", validatePath("minecraft", path));
   }

   @Nullable
   public static Identifier tryParse(String id) {
      return trySplitOn(id, ':');
   }

   @Nullable
   public static Identifier tryParse(String namespace, String path) {
      return isNamespaceValid(namespace) && isPathValid(path) ? new Identifier(namespace, path) : null;
   }

   public static Identifier splitOn(String id, char delimiter) {
      int i = id.indexOf(delimiter);
      if (i >= 0) {
         String string = id.substring(i + 1);
         if (i != 0) {
            String string2 = id.substring(0, i);
            return ofValidated(string2, string);
         } else {
            return ofVanilla(string);
         }
      } else {
         return ofVanilla(id);
      }
   }

   @Nullable
   public static Identifier trySplitOn(String id, char delimiter) {
      int i = id.indexOf(delimiter);
      if (i >= 0) {
         String string = id.substring(i + 1);
         if (!isPathValid(string)) {
            return null;
         } else if (i != 0) {
            String string2 = id.substring(0, i);
            return isNamespaceValid(string2) ? new Identifier(string2, string) : null;
         } else {
            return new Identifier("minecraft", string);
         }
      } else {
         return isPathValid(id) ? new Identifier("minecraft", id) : null;
      }
   }

   public static DataResult validate(String id) {
      try {
         return DataResult.success(of(id));
      } catch (InvalidIdentifierException var2) {
         return DataResult.error(() -> {
            return "Not a valid resource location: " + id + " " + var2.getMessage();
         });
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public Identifier withPath(String path) {
      return new Identifier(this.namespace, validatePath(this.namespace, path));
   }

   public Identifier withPath(UnaryOperator pathFunction) {
      return this.withPath((String)pathFunction.apply(this.path));
   }

   public Identifier withPrefixedPath(String prefix) {
      return this.withPath(prefix + this.path);
   }

   public Identifier withSuffixedPath(String suffix) {
      return this.withPath(this.path + suffix);
   }

   public String toString() {
      return this.namespace + ":" + this.path;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Identifier)) {
         return false;
      } else {
         Identifier identifier = (Identifier)o;
         return this.namespace.equals(identifier.namespace) && this.path.equals(identifier.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(Identifier identifier) {
      int i = this.path.compareTo(identifier.path);
      if (i == 0) {
         i = this.namespace.compareTo(identifier.namespace);
      }

      return i;
   }

   public String toUnderscoreSeparatedString() {
      return this.toString().replace('/', '_').replace(':', '_');
   }

   public String toTranslationKey() {
      return this.namespace + "." + this.path;
   }

   public String toShortTranslationKey() {
      return this.namespace.equals("minecraft") ? this.path : this.toTranslationKey();
   }

   public String toTranslationKey(String prefix) {
      return prefix + "." + this.toTranslationKey();
   }

   public String toTranslationKey(String prefix, String suffix) {
      return prefix + "." + this.toTranslationKey() + "." + suffix;
   }

   private static String readString(StringReader reader) {
      int i = reader.getCursor();

      while(reader.canRead() && isCharValid(reader.peek())) {
         reader.skip();
      }

      return reader.getString().substring(i, reader.getCursor());
   }

   public static Identifier fromCommandInput(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();
      String string = readString(reader);

      try {
         return of(string);
      } catch (InvalidIdentifierException var4) {
         reader.setCursor(i);
         throw COMMAND_EXCEPTION.createWithContext(reader);
      }
   }

   public static Identifier fromCommandInputNonEmpty(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();
      String string = readString(reader);
      if (string.isEmpty()) {
         throw COMMAND_EXCEPTION.createWithContext(reader);
      } else {
         try {
            return of(string);
         } catch (InvalidIdentifierException var4) {
            reader.setCursor(i);
            throw COMMAND_EXCEPTION.createWithContext(reader);
         }
      }
   }

   public static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }

   public static boolean isPathValid(String path) {
      for(int i = 0; i < path.length(); ++i) {
         if (!isPathCharacterValid(path.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isNamespaceValid(String namespace) {
      for(int i = 0; i < namespace.length(); ++i) {
         if (!isNamespaceCharacterValid(namespace.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   private static String validateNamespace(String namespace, String path) {
      if (!isNamespaceValid(namespace)) {
         throw new InvalidIdentifierException("Non [a-z0-9_.-] character in namespace of location: " + namespace + ":" + path);
      } else {
         return namespace;
      }
   }

   public static boolean isPathCharacterValid(char character) {
      return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '/' || character == '.';
   }

   private static boolean isNamespaceCharacterValid(char character) {
      return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
   }

   private static String validatePath(String namespace, String path) {
      if (!isPathValid(path)) {
         throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
      } else {
         return path;
      }
   }

   // $FF: synthetic method
   public int compareTo(final Object other) {
      return this.compareTo((Identifier)other);
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(Identifier::validate, Identifier::toString).stable();
      PACKET_CODEC = PacketCodecs.STRING.xmap(Identifier::of, Identifier::toString);
      COMMAND_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.id.invalid"));
   }
}
