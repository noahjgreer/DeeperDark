package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

public class FunctionLoader implements ResourceReloader {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final RegistryKey FUNCTION_REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("function"));
   private static final ResourceFinder FINDER;
   private volatile Map functions = ImmutableMap.of();
   private final TagGroupLoader tagLoader;
   private volatile Map tags;
   private final int level;
   private final CommandDispatcher commandDispatcher;

   public Optional get(Identifier id) {
      return Optional.ofNullable((CommandFunction)this.functions.get(id));
   }

   public Map getFunctions() {
      return this.functions;
   }

   public List getTagOrEmpty(Identifier id) {
      return (List)this.tags.getOrDefault(id, List.of());
   }

   public Iterable getTags() {
      return this.tags.keySet();
   }

   public FunctionLoader(int level, CommandDispatcher commandDispatcher) {
      this.tagLoader = new TagGroupLoader((id, required) -> {
         return this.get(id);
      }, RegistryKeys.getTagPath(FUNCTION_REGISTRY_KEY));
      this.tags = Map.of();
      this.level = level;
      this.commandDispatcher = commandDispatcher;
   }

   public CompletableFuture reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
      CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
         return this.tagLoader.loadTags(resourceManager);
      }, executor);
      CompletableFuture completableFuture2 = CompletableFuture.supplyAsync(() -> {
         return FINDER.findResources(resourceManager);
      }, executor).thenCompose((functions) -> {
         Map map = Maps.newHashMap();
         ServerCommandSource serverCommandSource = new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld)null, this.level, "", ScreenTexts.EMPTY, (MinecraftServer)null, (Entity)null);
         Iterator var5 = functions.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry entry = (Map.Entry)var5.next();
            Identifier identifier = (Identifier)entry.getKey();
            Identifier identifier2 = FINDER.toResourceId(identifier);
            map.put(identifier2, CompletableFuture.supplyAsync(() -> {
               List list = readLines((Resource)entry.getValue());
               return CommandFunction.create(identifier2, this.commandDispatcher, serverCommandSource, list);
            }, executor));
         }

         CompletableFuture[] completableFutures = (CompletableFuture[])map.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(completableFutures).handle((unused, ex) -> {
            return map;
         });
      });
      CompletableFuture var10000 = completableFuture.thenCombine(completableFuture2, Pair::of);
      Objects.requireNonNull(synchronizer);
      return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((intermediate) -> {
         Map map = (Map)intermediate.getSecond();
         ImmutableMap.Builder builder = ImmutableMap.builder();
         map.forEach((id, functionFuture) -> {
            functionFuture.handle((function, ex) -> {
               if (ex != null) {
                  LOGGER.error("Failed to load function {}", id, ex);
               } else {
                  builder.put(id, function);
               }

               return null;
            }).join();
         });
         this.functions = builder.build();
         this.tags = this.tagLoader.buildGroup((Map)intermediate.getFirst());
      }, executor2);
   }

   private static List readLines(Resource resource) {
      try {
         BufferedReader bufferedReader = resource.getReader();

         List var2;
         try {
            var2 = bufferedReader.lines().toList();
         } catch (Throwable var5) {
            if (bufferedReader != null) {
               try {
                  bufferedReader.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (bufferedReader != null) {
            bufferedReader.close();
         }

         return var2;
      } catch (IOException var6) {
         throw new CompletionException(var6);
      }
   }

   static {
      FINDER = new ResourceFinder(RegistryKeys.getPath(FUNCTION_REGISTRY_KEY), ".mcfunction");
   }
}
