package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class ReloadableResourceManagerImpl implements ResourceManager, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private LifecycledResourceManager activeManager;
   private final List reloaders = Lists.newArrayList();
   private final ResourceType type;

   public ReloadableResourceManagerImpl(ResourceType type) {
      this.type = type;
      this.activeManager = new LifecycledResourceManagerImpl(type, List.of());
   }

   public void close() {
      this.activeManager.close();
   }

   public void registerReloader(ResourceReloader reloader) {
      this.reloaders.add(reloader);
   }

   public ResourceReload reload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture initialStage, List packs) {
      LOGGER.info("Reloading ResourceManager: {}", LogUtils.defer(() -> {
         return packs.stream().map(ResourcePack::getId).collect(Collectors.joining(", "));
      }));
      this.activeManager.close();
      this.activeManager = new LifecycledResourceManagerImpl(this.type, packs);
      return SimpleResourceReload.start(this.activeManager, this.reloaders, prepareExecutor, applyExecutor, initialStage, LOGGER.isDebugEnabled());
   }

   public Optional getResource(Identifier identifier) {
      return this.activeManager.getResource(identifier);
   }

   public Set getAllNamespaces() {
      return this.activeManager.getAllNamespaces();
   }

   public List getAllResources(Identifier id) {
      return this.activeManager.getAllResources(id);
   }

   public Map findResources(String startingPath, Predicate allowedPathPredicate) {
      return this.activeManager.findResources(startingPath, allowedPathPredicate);
   }

   public Map findAllResources(String startingPath, Predicate allowedPathPredicate) {
      return this.activeManager.findAllResources(startingPath, allowedPathPredicate);
   }

   public Stream streamResourcePacks() {
      return this.activeManager.streamResourcePacks();
   }
}
