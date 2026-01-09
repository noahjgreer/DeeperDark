package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class ServerConfigList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final File file;
   private final Map map = Maps.newHashMap();

   public ServerConfigList(File file) {
      this.file = file;
   }

   public File getFile() {
      return this.file;
   }

   public void add(ServerConfigEntry entry) {
      this.map.put(this.toString(entry.getKey()), entry);

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after adding a user.", var3);
      }

   }

   @Nullable
   public ServerConfigEntry get(Object key) {
      this.removeInvalidEntries();
      return (ServerConfigEntry)this.map.get(this.toString(key));
   }

   public void remove(Object key) {
      this.map.remove(this.toString(key));

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after removing a user.", var3);
      }

   }

   public void remove(ServerConfigEntry entry) {
      this.remove(entry.getKey());
   }

   public String[] getNames() {
      return (String[])this.map.keySet().toArray(new String[0]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String toString(Object profile) {
      return profile.toString();
   }

   protected boolean contains(Object object) {
      return this.map.containsKey(this.toString(object));
   }

   private void removeInvalidEntries() {
      List list = Lists.newArrayList();
      Iterator var2 = this.map.values().iterator();

      while(var2.hasNext()) {
         ServerConfigEntry serverConfigEntry = (ServerConfigEntry)var2.next();
         if (serverConfigEntry.isInvalid()) {
            list.add(serverConfigEntry.getKey());
         }
      }

      var2 = list.iterator();

      while(var2.hasNext()) {
         Object object = var2.next();
         this.map.remove(this.toString(object));
      }

   }

   protected abstract ServerConfigEntry fromJson(JsonObject json);

   public Collection values() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray jsonArray = new JsonArray();
      Stream var10000 = this.map.values().stream().map((entry) -> {
         JsonObject var10000 = new JsonObject();
         Objects.requireNonNull(entry);
         return (JsonObject)Util.make(var10000, entry::write);
      });
      Objects.requireNonNull(jsonArray);
      var10000.forEach(jsonArray::add);
      BufferedWriter bufferedWriter = Files.newWriter(this.file, StandardCharsets.UTF_8);

      try {
         GSON.toJson(jsonArray, GSON.newJsonWriter(bufferedWriter));
      } catch (Throwable var6) {
         if (bufferedWriter != null) {
            try {
               bufferedWriter.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (bufferedWriter != null) {
         bufferedWriter.close();
      }

   }

   public void load() throws IOException {
      if (this.file.exists()) {
         BufferedReader bufferedReader = Files.newReader(this.file, StandardCharsets.UTF_8);

         label54: {
            try {
               this.map.clear();
               JsonArray jsonArray = (JsonArray)GSON.fromJson(bufferedReader, JsonArray.class);
               if (jsonArray != null) {
                  Iterator var3 = jsonArray.iterator();

                  while(true) {
                     if (!var3.hasNext()) {
                        break label54;
                     }

                     JsonElement jsonElement = (JsonElement)var3.next();
                     JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
                     ServerConfigEntry serverConfigEntry = this.fromJson(jsonObject);
                     if (serverConfigEntry.getKey() != null) {
                        this.map.put(this.toString(serverConfigEntry.getKey()), serverConfigEntry);
                     }
                  }
               }
            } catch (Throwable var8) {
               if (bufferedReader != null) {
                  try {
                     bufferedReader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (bufferedReader != null) {
               bufferedReader.close();
            }

            return;
         }

         if (bufferedReader != null) {
            bufferedReader.close();
         }

      }
   }
}
