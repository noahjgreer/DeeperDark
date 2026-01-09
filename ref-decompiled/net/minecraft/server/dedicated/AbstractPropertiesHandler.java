package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.registry.DynamicRegistryManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractPropertiesHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Properties properties;

   public AbstractPropertiesHandler(Properties properties) {
      this.properties = properties;
   }

   public static Properties loadProperties(Path path) {
      try {
         Properties properties;
         Properties var4;
         try {
            InputStream inputStream = Files.newInputStream(path);

            try {
               CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
               properties = new Properties();
               properties.load(new InputStreamReader(inputStream, charsetDecoder));
               var4 = properties;
            } catch (Throwable var8) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return var4;
         } catch (CharacterCodingException var9) {
            LOGGER.info("Failed to load properties as UTF-8 from file {}, trying ISO_8859_1", path);
            Reader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1);

            try {
               properties = new Properties();
               properties.load(reader);
               var4 = properties;
            } catch (Throwable var7) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var5) {
                     var7.addSuppressed(var5);
                  }
               }

               throw var7;
            }

            if (reader != null) {
               reader.close();
            }

            return var4;
         }
      } catch (IOException var10) {
         LOGGER.error("Failed to load properties from file: {}", path, var10);
         return new Properties();
      }
   }

   public void saveProperties(Path path) {
      try {
         Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

         try {
            this.properties.store(writer, "Minecraft server properties");
         } catch (Throwable var6) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (writer != null) {
            writer.close();
         }
      } catch (IOException var7) {
         LOGGER.error("Failed to store properties to file: {}", path);
      }

   }

   private static Function wrapNumberParser(Function parser) {
      return (string) -> {
         try {
            return (Number)parser.apply(string);
         } catch (NumberFormatException var3) {
            return null;
         }
      };
   }

   protected static Function combineParser(IntFunction intParser, Function fallbackParser) {
      return (string) -> {
         try {
            return intParser.apply(Integer.parseInt(string));
         } catch (NumberFormatException var4) {
            return fallbackParser.apply(string);
         }
      };
   }

   @Nullable
   private String getStringValue(String key) {
      return (String)this.properties.get(key);
   }

   @Nullable
   protected Object getDeprecated(String key, Function stringifier) {
      String string = this.getStringValue(key);
      if (string == null) {
         return null;
      } else {
         this.properties.remove(key);
         return stringifier.apply(string);
      }
   }

   protected Object get(String key, Function parser, Function stringifier, Object fallback) {
      String string = this.getStringValue(key);
      Object object = MoreObjects.firstNonNull(string != null ? parser.apply(string) : null, fallback);
      this.properties.put(key, stringifier.apply(object));
      return object;
   }

   protected PropertyAccessor accessor(String key, Function parser, Function stringifier, Object fallback) {
      String string = this.getStringValue(key);
      Object object = MoreObjects.firstNonNull(string != null ? parser.apply(string) : null, fallback);
      this.properties.put(key, stringifier.apply(object));
      return new PropertyAccessor(key, object, stringifier);
   }

   protected Object get(String key, Function parser, UnaryOperator parsedTransformer, Function stringifier, Object fallback) {
      return this.get(key, (value) -> {
         Object object = parser.apply(value);
         return object != null ? parsedTransformer.apply(object) : null;
      }, stringifier, fallback);
   }

   protected Object get(String key, Function parser, Object fallback) {
      return this.get(key, parser, Objects::toString, fallback);
   }

   protected PropertyAccessor accessor(String key, Function parser, Object fallback) {
      return this.accessor(key, parser, Objects::toString, fallback);
   }

   protected String getString(String key, String fallback) {
      return (String)this.get(key, Function.identity(), Function.identity(), fallback);
   }

   @Nullable
   protected String getDeprecatedString(String key) {
      return (String)this.getDeprecated(key, Function.identity());
   }

   protected int getInt(String key, int fallback) {
      return (Integer)this.get(key, wrapNumberParser(Integer::parseInt), fallback);
   }

   protected PropertyAccessor intAccessor(String key, int fallback) {
      return this.accessor(key, wrapNumberParser(Integer::parseInt), fallback);
   }

   protected int transformedParseInt(String key, UnaryOperator transformer, int fallback) {
      return (Integer)this.get(key, wrapNumberParser(Integer::parseInt), transformer, Objects::toString, fallback);
   }

   protected long parseLong(String key, long fallback) {
      return (Long)this.get(key, wrapNumberParser(Long::parseLong), fallback);
   }

   protected boolean parseBoolean(String key, boolean fallback) {
      return (Boolean)this.get(key, Boolean::valueOf, fallback);
   }

   protected PropertyAccessor booleanAccessor(String key, boolean fallback) {
      return this.accessor(key, Boolean::valueOf, fallback);
   }

   @Nullable
   protected Boolean getDeprecatedBoolean(String key) {
      return (Boolean)this.getDeprecated(key, Boolean::valueOf);
   }

   protected Properties copyProperties() {
      Properties properties = new Properties();
      properties.putAll(this.properties);
      return properties;
   }

   protected abstract AbstractPropertiesHandler create(DynamicRegistryManager registryManager, Properties properties);

   public class PropertyAccessor implements Supplier {
      private final String key;
      private final Object value;
      private final Function stringifier;

      PropertyAccessor(final String key, final Object value, final Function stringifier) {
         this.key = key;
         this.value = value;
         this.stringifier = stringifier;
      }

      public Object get() {
         return this.value;
      }

      public AbstractPropertiesHandler set(DynamicRegistryManager registryManager, Object value) {
         Properties properties = AbstractPropertiesHandler.this.copyProperties();
         properties.put(this.key, this.stringifier.apply(value));
         return AbstractPropertiesHandler.this.create(registryManager, properties);
      }
   }
}
