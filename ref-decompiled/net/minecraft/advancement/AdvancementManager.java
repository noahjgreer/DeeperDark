package net.minecraft.advancement;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AdvancementManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map advancements = new Object2ObjectOpenHashMap();
   private final Set roots = new ObjectLinkedOpenHashSet();
   private final Set dependents = new ObjectLinkedOpenHashSet();
   @Nullable
   private Listener listener;

   private void remove(PlacedAdvancement advancement) {
      Iterator var2 = advancement.getChildren().iterator();

      while(var2.hasNext()) {
         PlacedAdvancement placedAdvancement = (PlacedAdvancement)var2.next();
         this.remove(placedAdvancement);
      }

      LOGGER.info("Forgot about advancement {}", advancement.getAdvancementEntry());
      this.advancements.remove(advancement.getAdvancementEntry().id());
      if (advancement.getParent() == null) {
         this.roots.remove(advancement);
         if (this.listener != null) {
            this.listener.onRootRemoved(advancement);
         }
      } else {
         this.dependents.remove(advancement);
         if (this.listener != null) {
            this.listener.onDependentRemoved(advancement);
         }
      }

   }

   public void removeAll(Set advancements) {
      Iterator var2 = advancements.iterator();

      while(var2.hasNext()) {
         Identifier identifier = (Identifier)var2.next();
         PlacedAdvancement placedAdvancement = (PlacedAdvancement)this.advancements.get(identifier);
         if (placedAdvancement == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
         } else {
            this.remove(placedAdvancement);
         }
      }

   }

   public void addAll(Collection advancements) {
      List list = new ArrayList(advancements);

      while(!list.isEmpty()) {
         if (!list.removeIf(this::tryAdd)) {
            LOGGER.error("Couldn't load advancements: {}", list);
            break;
         }
      }

      LOGGER.info("Loaded {} advancements", this.advancements.size());
   }

   private boolean tryAdd(AdvancementEntry advancement) {
      Optional optional = advancement.value().parent();
      Map var10001 = this.advancements;
      Objects.requireNonNull(var10001);
      PlacedAdvancement placedAdvancement = (PlacedAdvancement)optional.map(var10001::get).orElse((Object)null);
      if (placedAdvancement == null && optional.isPresent()) {
         return false;
      } else {
         PlacedAdvancement placedAdvancement2 = new PlacedAdvancement(advancement, placedAdvancement);
         if (placedAdvancement != null) {
            placedAdvancement.addChild(placedAdvancement2);
         }

         this.advancements.put(advancement.id(), placedAdvancement2);
         if (placedAdvancement == null) {
            this.roots.add(placedAdvancement2);
            if (this.listener != null) {
               this.listener.onRootAdded(placedAdvancement2);
            }
         } else {
            this.dependents.add(placedAdvancement2);
            if (this.listener != null) {
               this.listener.onDependentAdded(placedAdvancement2);
            }
         }

         return true;
      }
   }

   public void clear() {
      this.advancements.clear();
      this.roots.clear();
      this.dependents.clear();
      if (this.listener != null) {
         this.listener.onClear();
      }

   }

   public Iterable getRoots() {
      return this.roots;
   }

   public Collection getAdvancements() {
      return this.advancements.values();
   }

   @Nullable
   public PlacedAdvancement get(Identifier id) {
      return (PlacedAdvancement)this.advancements.get(id);
   }

   @Nullable
   public PlacedAdvancement get(AdvancementEntry advancement) {
      return (PlacedAdvancement)this.advancements.get(advancement.id());
   }

   public void setListener(@Nullable Listener listener) {
      this.listener = listener;
      if (listener != null) {
         Iterator var2 = this.roots.iterator();

         PlacedAdvancement placedAdvancement;
         while(var2.hasNext()) {
            placedAdvancement = (PlacedAdvancement)var2.next();
            listener.onRootAdded(placedAdvancement);
         }

         var2 = this.dependents.iterator();

         while(var2.hasNext()) {
            placedAdvancement = (PlacedAdvancement)var2.next();
            listener.onDependentAdded(placedAdvancement);
         }
      }

   }

   public interface Listener {
      void onRootAdded(PlacedAdvancement root);

      void onRootRemoved(PlacedAdvancement root);

      void onDependentAdded(PlacedAdvancement dependent);

      void onDependentRemoved(PlacedAdvancement dependent);

      void onClear();
   }
}
