package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackOverlaysMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Range;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ResourcePackProfile {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourcePackInfo info;
   private final PackFactory packFactory;
   private final Metadata metaData;
   private final ResourcePackPosition position;

   @Nullable
   public static ResourcePackProfile create(ResourcePackInfo info, PackFactory packFactory, ResourceType type, ResourcePackPosition position) {
      int i = SharedConstants.getGameVersion().packVersion(type);
      Metadata metadata = loadMetadata(info, packFactory, i);
      return metadata != null ? new ResourcePackProfile(info, packFactory, metadata, position) : null;
   }

   public ResourcePackProfile(ResourcePackInfo info, PackFactory packFactory, Metadata metaData, ResourcePackPosition position) {
      this.info = info;
      this.packFactory = packFactory;
      this.metaData = metaData;
      this.position = position;
   }

   @Nullable
   public static Metadata loadMetadata(ResourcePackInfo info, PackFactory packFactory, int currentPackFormat) {
      try {
         ResourcePack resourcePack = packFactory.open(info);

         PackFeatureSetMetadata packFeatureSetMetadata;
         label57: {
            Metadata var11;
            try {
               PackResourceMetadata packResourceMetadata = (PackResourceMetadata)resourcePack.parseMetadata(PackResourceMetadata.SERIALIZER);
               if (packResourceMetadata == null) {
                  LOGGER.warn("Missing metadata in pack {}", info.id());
                  packFeatureSetMetadata = null;
                  break label57;
               }

               packFeatureSetMetadata = (PackFeatureSetMetadata)resourcePack.parseMetadata(PackFeatureSetMetadata.SERIALIZER);
               FeatureSet featureSet = packFeatureSetMetadata != null ? packFeatureSetMetadata.flags() : FeatureSet.empty();
               Range range = getSupportedFormats(info.id(), packResourceMetadata);
               ResourcePackCompatibility resourcePackCompatibility = ResourcePackCompatibility.from(range, currentPackFormat);
               PackOverlaysMetadata packOverlaysMetadata = (PackOverlaysMetadata)resourcePack.parseMetadata(PackOverlaysMetadata.SERIALIZER);
               List list = packOverlaysMetadata != null ? packOverlaysMetadata.getAppliedOverlays(currentPackFormat) : List.of();
               var11 = new Metadata(packResourceMetadata.description(), resourcePackCompatibility, featureSet, list);
            } catch (Throwable var13) {
               if (resourcePack != null) {
                  try {
                     resourcePack.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (resourcePack != null) {
               resourcePack.close();
            }

            return var11;
         }

         if (resourcePack != null) {
            resourcePack.close();
         }

         return packFeatureSetMetadata;
      } catch (Exception var14) {
         LOGGER.warn("Failed to read pack {} metadata", info.id(), var14);
         return null;
      }
   }

   private static Range getSupportedFormats(String packId, PackResourceMetadata metadata) {
      int i = metadata.packFormat();
      if (metadata.supportedFormats().isEmpty()) {
         return new Range(i);
      } else {
         Range range = (Range)metadata.supportedFormats().get();
         if (!range.contains((Comparable)i)) {
            LOGGER.warn("Pack {} declared support for versions {} but declared main format is {}, defaulting to {}", new Object[]{packId, range, i, i});
            return new Range(i);
         } else {
            return range;
         }
      }
   }

   public ResourcePackInfo getInfo() {
      return this.info;
   }

   public Text getDisplayName() {
      return this.info.title();
   }

   public Text getDescription() {
      return this.metaData.description();
   }

   public Text getInformationText(boolean enabled) {
      return this.info.getInformationText(enabled, this.metaData.description);
   }

   public ResourcePackCompatibility getCompatibility() {
      return this.metaData.compatibility();
   }

   public FeatureSet getRequestedFeatures() {
      return this.metaData.requestedFeatures();
   }

   public ResourcePack createResourcePack() {
      return this.packFactory.openWithOverlays(this.info, this.metaData);
   }

   public String getId() {
      return this.info.id();
   }

   public ResourcePackPosition getPosition() {
      return this.position;
   }

   public boolean isRequired() {
      return this.position.required();
   }

   public boolean isPinned() {
      return this.position.fixedPosition();
   }

   public InsertionPosition getInitialPosition() {
      return this.position.defaultPosition();
   }

   public ResourcePackSource getSource() {
      return this.info.source();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ResourcePackProfile)) {
         return false;
      } else {
         ResourcePackProfile resourcePackProfile = (ResourcePackProfile)o;
         return this.info.equals(resourcePackProfile.info);
      }
   }

   public int hashCode() {
      return this.info.hashCode();
   }

   public interface PackFactory {
      ResourcePack open(ResourcePackInfo info);

      ResourcePack openWithOverlays(ResourcePackInfo info, Metadata metadata);
   }

   public static record Metadata(Text description, ResourcePackCompatibility compatibility, FeatureSet requestedFeatures, List overlays) {
      final Text description;

      public Metadata(Text text, ResourcePackCompatibility resourcePackCompatibility, FeatureSet featureSet, List list) {
         this.description = text;
         this.compatibility = resourcePackCompatibility;
         this.requestedFeatures = featureSet;
         this.overlays = list;
      }

      public Text description() {
         return this.description;
      }

      public ResourcePackCompatibility compatibility() {
         return this.compatibility;
      }

      public FeatureSet requestedFeatures() {
         return this.requestedFeatures;
      }

      public List overlays() {
         return this.overlays;
      }
   }

   public static enum InsertionPosition {
      TOP,
      BOTTOM;

      public int insert(List items, Object item, Function profileGetter, boolean listInverted) {
         InsertionPosition insertionPosition = listInverted ? this.inverse() : this;
         int i;
         ResourcePackPosition resourcePackPosition;
         if (insertionPosition == BOTTOM) {
            for(i = 0; i < items.size(); ++i) {
               resourcePackPosition = (ResourcePackPosition)profileGetter.apply(items.get(i));
               if (!resourcePackPosition.fixedPosition() || resourcePackPosition.defaultPosition() != this) {
                  break;
               }
            }

            items.add(i, item);
            return i;
         } else {
            for(i = items.size() - 1; i >= 0; --i) {
               resourcePackPosition = (ResourcePackPosition)profileGetter.apply(items.get(i));
               if (!resourcePackPosition.fixedPosition() || resourcePackPosition.defaultPosition() != this) {
                  break;
               }
            }

            items.add(i + 1, item);
            return i + 1;
         }
      }

      public InsertionPosition inverse() {
         return this == TOP ? BOTTOM : TOP;
      }

      // $FF: synthetic method
      private static InsertionPosition[] method_36583() {
         return new InsertionPosition[]{TOP, BOTTOM};
      }
   }
}
