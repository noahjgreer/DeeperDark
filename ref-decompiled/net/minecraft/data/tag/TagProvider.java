package net.minecraft.data.tag;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public abstract class TagProvider implements DataProvider {
   protected final DataOutput.PathResolver pathResolver;
   private final CompletableFuture registriesFuture;
   private final CompletableFuture registryLoadFuture;
   private final CompletableFuture parentTagLookupFuture;
   protected final RegistryKey registryRef;
   private final Map tagBuilders;

   protected TagProvider(DataOutput output, RegistryKey registryRef, CompletableFuture registriesFuture) {
      this(output, registryRef, registriesFuture, CompletableFuture.completedFuture(TagProvider.TagLookup.empty()));
   }

   protected TagProvider(DataOutput output, RegistryKey registryRef, CompletableFuture registriesFuture, CompletableFuture parentTagLookupFuture) {
      this.registryLoadFuture = new CompletableFuture();
      this.tagBuilders = Maps.newLinkedHashMap();
      this.pathResolver = output.getTagResolver(registryRef);
      this.registryRef = registryRef;
      this.parentTagLookupFuture = parentTagLookupFuture;
      this.registriesFuture = registriesFuture;
   }

   public String getName() {
      return "Tags for " + String.valueOf(this.registryRef.getValue());
   }

   protected abstract void configure(RegistryWrapper.WrapperLookup registries);

   public CompletableFuture run(DataWriter writer) {
      return this.getRegistriesFuture().thenApply((registriesFuture) -> {
         this.registryLoadFuture.complete((Object)null);
         return registriesFuture;
      }).thenCombineAsync(this.parentTagLookupFuture, (registries, parent) -> {
         record RegistryInfo(RegistryWrapper.WrapperLookup contents, TagLookup parent) {
            final RegistryWrapper.WrapperLookup contents;
            final TagLookup parent;

            RegistryInfo(RegistryWrapper.WrapperLookup wrapperLookup, TagLookup tagLookup) {
               this.contents = wrapperLookup;
               this.parent = tagLookup;
            }

            public RegistryWrapper.WrapperLookup contents() {
               return this.contents;
            }

            public TagLookup parent() {
               return this.parent;
            }
         }

         return new RegistryInfo(registries, parent);
      }, Util.getMainWorkerExecutor()).thenCompose((info) -> {
         RegistryWrapper.Impl impl = info.contents.getOrThrow(this.registryRef);
         Predicate predicate = (id) -> {
            return impl.getOptional(RegistryKey.of(this.registryRef, id)).isPresent();
         };
         Predicate predicate2 = (id) -> {
            return this.tagBuilders.containsKey(id) || info.parent.contains(TagKey.of(this.registryRef, id));
         };
         return CompletableFuture.allOf((CompletableFuture[])this.tagBuilders.entrySet().stream().map((entry) -> {
            Identifier identifier = (Identifier)entry.getKey();
            TagBuilder tagBuilder = (TagBuilder)entry.getValue();
            List list = tagBuilder.build();
            List list2 = list.stream().filter((tagEntry) -> {
               return !tagEntry.canAdd(predicate, predicate2);
            }).toList();
            if (!list2.isEmpty()) {
               throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", identifier, list2.stream().map(Objects::toString).collect(Collectors.joining(","))));
            } else {
               Path path = this.pathResolver.resolveJson(identifier);
               return DataProvider.writeCodecToPath(writer, (RegistryWrapper.WrapperLookup)info.contents, TagFile.CODEC, new TagFile(list, false), path);
            }
         }).toArray((i) -> {
            return new CompletableFuture[i];
         }));
      });
   }

   protected TagBuilder getTagBuilder(TagKey tag) {
      return (TagBuilder)this.tagBuilders.computeIfAbsent(tag.id(), (id) -> {
         return TagBuilder.create();
      });
   }

   public CompletableFuture getTagLookupFuture() {
      return this.registryLoadFuture.thenApply((void_) -> {
         return (tag) -> {
            return Optional.ofNullable((TagBuilder)this.tagBuilders.get(tag.id()));
         };
      });
   }

   protected CompletableFuture getRegistriesFuture() {
      return this.registriesFuture.thenApply((registries) -> {
         this.tagBuilders.clear();
         this.configure(registries);
         return registries;
      });
   }

   @FunctionalInterface
   public interface TagLookup extends Function {
      static TagLookup empty() {
         return (tag) -> {
            return Optional.empty();
         };
      }

      default boolean contains(TagKey tag) {
         return ((Optional)this.apply(tag)).isPresent();
      }
   }
}
