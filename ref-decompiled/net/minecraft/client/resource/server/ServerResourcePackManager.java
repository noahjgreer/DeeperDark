package net.minecraft.client.resource.server;

import com.google.common.hash.HashCode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Downloader;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ServerResourcePackManager {
   private final DownloadQueuer queuer;
   final PackStateChangeCallback stateChangeCallback;
   private final ReloadScheduler reloadScheduler;
   private final Runnable packChangeCallback;
   private AcceptanceStatus acceptanceStatus;
   final List packs = new ArrayList();

   public ServerResourcePackManager(DownloadQueuer queuer, PackStateChangeCallback stateChangeCallback, ReloadScheduler reloadScheduler, Runnable packChangeCallback, AcceptanceStatus acceptanceStatus) {
      this.queuer = queuer;
      this.stateChangeCallback = stateChangeCallback;
      this.reloadScheduler = reloadScheduler;
      this.packChangeCallback = packChangeCallback;
      this.acceptanceStatus = acceptanceStatus;
   }

   void onPackChanged() {
      this.packChangeCallback.run();
   }

   private void markReplaced(UUID id) {
      Iterator var2 = this.packs.iterator();

      while(var2.hasNext()) {
         PackEntry packEntry = (PackEntry)var2.next();
         if (packEntry.id.equals(id)) {
            packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REPLACED);
         }
      }

   }

   public void addResourcePack(UUID id, URL url, @Nullable HashCode hashCode) {
      if (this.acceptanceStatus == ServerResourcePackManager.AcceptanceStatus.DECLINED) {
         this.stateChangeCallback.onFinish(id, PackStateChangeCallback.FinishState.DECLINED);
      } else {
         this.onAdd(id, new PackEntry(id, url, hashCode));
      }
   }

   public void addResourcePack(UUID id, Path path) {
      if (this.acceptanceStatus == ServerResourcePackManager.AcceptanceStatus.DECLINED) {
         this.stateChangeCallback.onFinish(id, PackStateChangeCallback.FinishState.DECLINED);
      } else {
         URL uRL;
         try {
            uRL = path.toUri().toURL();
         } catch (MalformedURLException var5) {
            throw new IllegalStateException("Can't convert path to URL " + String.valueOf(path), var5);
         }

         PackEntry packEntry = new PackEntry(id, uRL, (HashCode)null);
         packEntry.loadStatus = ServerResourcePackManager.LoadStatus.DONE;
         packEntry.path = path;
         this.onAdd(id, packEntry);
      }
   }

   private void onAdd(UUID id, PackEntry pack) {
      this.markReplaced(id);
      this.packs.add(pack);
      if (this.acceptanceStatus == ServerResourcePackManager.AcceptanceStatus.ALLOWED) {
         this.accept(pack);
      }

      this.onPackChanged();
   }

   private void accept(PackEntry pack) {
      this.stateChangeCallback.onStateChanged(pack.id, PackStateChangeCallback.State.ACCEPTED);
      pack.accepted = true;
   }

   @Nullable
   private PackEntry get(UUID id) {
      Iterator var2 = this.packs.iterator();

      PackEntry packEntry;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         packEntry = (PackEntry)var2.next();
      } while(packEntry.isDiscarded() || !packEntry.id.equals(id));

      return packEntry;
   }

   public void remove(UUID id) {
      PackEntry packEntry = this.get(id);
      if (packEntry != null) {
         packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REMOVED);
         this.onPackChanged();
      }

   }

   public void removeAll() {
      Iterator var1 = this.packs.iterator();

      while(var1.hasNext()) {
         PackEntry packEntry = (PackEntry)var1.next();
         packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REMOVED);
      }

      this.onPackChanged();
   }

   public void acceptAll() {
      this.acceptanceStatus = ServerResourcePackManager.AcceptanceStatus.ALLOWED;
      Iterator var1 = this.packs.iterator();

      while(var1.hasNext()) {
         PackEntry packEntry = (PackEntry)var1.next();
         if (!packEntry.accepted && !packEntry.isDiscarded()) {
            this.accept(packEntry);
         }
      }

      this.onPackChanged();
   }

   public void declineAll() {
      this.acceptanceStatus = ServerResourcePackManager.AcceptanceStatus.DECLINED;
      Iterator var1 = this.packs.iterator();

      while(var1.hasNext()) {
         PackEntry packEntry = (PackEntry)var1.next();
         if (!packEntry.accepted) {
            packEntry.discard(ServerResourcePackManager.DiscardReason.DECLINED);
         }
      }

      this.onPackChanged();
   }

   public void resetAcceptanceStatus() {
      this.acceptanceStatus = ServerResourcePackManager.AcceptanceStatus.PENDING;
   }

   public void update() {
      boolean bl = this.enqueueDownloads();
      if (!bl) {
         this.applyDownloadedPacks();
      }

      this.removeInactivePacks();
   }

   private void removeInactivePacks() {
      this.packs.removeIf((pack) -> {
         if (pack.status != ServerResourcePackManager.Status.INACTIVE) {
            return false;
         } else if (pack.discardReason != null) {
            PackStateChangeCallback.FinishState finishState = pack.discardReason.state;
            if (finishState != null) {
               this.stateChangeCallback.onFinish(pack.id, finishState);
            }

            return true;
         } else {
            return false;
         }
      });
   }

   private void onDownload(Collection packs, Downloader.DownloadResult result) {
      Iterator var3;
      PackEntry packEntry;
      if (!result.failed().isEmpty()) {
         var3 = this.packs.iterator();

         while(var3.hasNext()) {
            packEntry = (PackEntry)var3.next();
            if (packEntry.status != ServerResourcePackManager.Status.ACTIVE) {
               if (result.failed().contains(packEntry.id)) {
                  packEntry.discard(ServerResourcePackManager.DiscardReason.DOWNLOAD_FAILED);
               } else {
                  packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
               }
            }
         }
      }

      var3 = packs.iterator();

      while(var3.hasNext()) {
         packEntry = (PackEntry)var3.next();
         Path path = (Path)result.downloaded().get(packEntry.id);
         if (path != null) {
            packEntry.loadStatus = ServerResourcePackManager.LoadStatus.DONE;
            packEntry.path = path;
            if (!packEntry.isDiscarded()) {
               this.stateChangeCallback.onStateChanged(packEntry.id, PackStateChangeCallback.State.DOWNLOADED);
            }
         }
      }

      this.onPackChanged();
   }

   private boolean enqueueDownloads() {
      List list = new ArrayList();
      boolean bl = false;
      Iterator var3 = this.packs.iterator();

      while(var3.hasNext()) {
         PackEntry packEntry = (PackEntry)var3.next();
         if (!packEntry.isDiscarded() && packEntry.accepted) {
            if (packEntry.loadStatus != ServerResourcePackManager.LoadStatus.DONE) {
               bl = true;
            }

            if (packEntry.loadStatus == ServerResourcePackManager.LoadStatus.REQUESTED) {
               packEntry.loadStatus = ServerResourcePackManager.LoadStatus.PENDING;
               list.add(packEntry);
            }
         }
      }

      if (!list.isEmpty()) {
         Map map = new HashMap();
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            PackEntry packEntry2 = (PackEntry)var7.next();
            map.put(packEntry2.id, new Downloader.DownloadEntry(packEntry2.url, packEntry2.hashCode));
         }

         this.queuer.enqueue(map, (result) -> {
            this.onDownload(list, result);
         });
      }

      return bl;
   }

   private void applyDownloadedPacks() {
      boolean bl = false;
      final List list = new ArrayList();
      final List list2 = new ArrayList();
      Iterator var4 = this.packs.iterator();

      PackEntry packEntry;
      while(var4.hasNext()) {
         packEntry = (PackEntry)var4.next();
         if (packEntry.status == ServerResourcePackManager.Status.PENDING) {
            return;
         }

         boolean bl2 = packEntry.accepted && packEntry.loadStatus == ServerResourcePackManager.LoadStatus.DONE && !packEntry.isDiscarded();
         if (bl2 && packEntry.status == ServerResourcePackManager.Status.INACTIVE) {
            list.add(packEntry);
            bl = true;
         }

         if (packEntry.status == ServerResourcePackManager.Status.ACTIVE) {
            if (!bl2) {
               bl = true;
               list2.add(packEntry);
            } else {
               list.add(packEntry);
            }
         }
      }

      if (bl) {
         var4 = list.iterator();

         while(var4.hasNext()) {
            packEntry = (PackEntry)var4.next();
            if (packEntry.status != ServerResourcePackManager.Status.ACTIVE) {
               packEntry.status = ServerResourcePackManager.Status.PENDING;
            }
         }

         for(var4 = list2.iterator(); var4.hasNext(); packEntry.status = ServerResourcePackManager.Status.PENDING) {
            packEntry = (PackEntry)var4.next();
         }

         this.reloadScheduler.scheduleReload(new ReloadScheduler.ReloadContext() {
            public void onSuccess() {
               Iterator var1 = list.iterator();

               PackEntry packEntry;
               while(var1.hasNext()) {
                  packEntry = (PackEntry)var1.next();
                  packEntry.status = ServerResourcePackManager.Status.ACTIVE;
                  if (packEntry.discardReason == null) {
                     ServerResourcePackManager.this.stateChangeCallback.onFinish(packEntry.id, PackStateChangeCallback.FinishState.APPLIED);
                  }
               }

               for(var1 = list2.iterator(); var1.hasNext(); packEntry.status = ServerResourcePackManager.Status.INACTIVE) {
                  packEntry = (PackEntry)var1.next();
               }

               ServerResourcePackManager.this.onPackChanged();
            }

            public void onFailure(boolean force) {
               Iterator var2;
               PackEntry packEntry;
               if (!force) {
                  list.clear();
                  var2 = ServerResourcePackManager.this.packs.iterator();

                  while(var2.hasNext()) {
                     packEntry = (PackEntry)var2.next();
                     switch (packEntry.status.ordinal()) {
                        case 0:
                           packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
                           break;
                        case 1:
                           packEntry.status = ServerResourcePackManager.Status.INACTIVE;
                           packEntry.discard(ServerResourcePackManager.DiscardReason.ACTIVATION_FAILED);
                           break;
                        case 2:
                           list.add(packEntry);
                     }
                  }

                  ServerResourcePackManager.this.onPackChanged();
               } else {
                  var2 = ServerResourcePackManager.this.packs.iterator();

                  while(var2.hasNext()) {
                     packEntry = (PackEntry)var2.next();
                     if (packEntry.status == ServerResourcePackManager.Status.PENDING) {
                        packEntry.status = ServerResourcePackManager.Status.INACTIVE;
                     }
                  }
               }

            }

            public List getPacks() {
               return list.stream().map((pack) -> {
                  return new ReloadScheduler.PackInfo(pack.id, pack.path);
               }).toList();
            }
         });
      }

   }

   @Environment(EnvType.CLIENT)
   public static enum AcceptanceStatus {
      PENDING,
      ALLOWED,
      DECLINED;

      // $FF: synthetic method
      private static AcceptanceStatus[] method_55574() {
         return new AcceptanceStatus[]{PENDING, ALLOWED, DECLINED};
      }
   }

   @Environment(EnvType.CLIENT)
   static class PackEntry {
      final UUID id;
      final URL url;
      @Nullable
      final HashCode hashCode;
      @Nullable
      Path path;
      @Nullable
      DiscardReason discardReason;
      LoadStatus loadStatus;
      Status status;
      boolean accepted;

      PackEntry(UUID id, URL url, @Nullable HashCode hashCode) {
         this.loadStatus = ServerResourcePackManager.LoadStatus.REQUESTED;
         this.status = ServerResourcePackManager.Status.INACTIVE;
         this.id = id;
         this.url = url;
         this.hashCode = hashCode;
      }

      public void discard(DiscardReason reason) {
         if (this.discardReason == null) {
            this.discardReason = reason;
         }

      }

      public boolean isDiscarded() {
         return this.discardReason != null;
      }
   }

   @Environment(EnvType.CLIENT)
   private static enum DiscardReason {
      DOWNLOAD_FAILED(PackStateChangeCallback.FinishState.DOWNLOAD_FAILED),
      ACTIVATION_FAILED(PackStateChangeCallback.FinishState.ACTIVATION_FAILED),
      DECLINED(PackStateChangeCallback.FinishState.DECLINED),
      DISCARDED(PackStateChangeCallback.FinishState.DISCARDED),
      SERVER_REMOVED((PackStateChangeCallback.FinishState)null),
      SERVER_REPLACED((PackStateChangeCallback.FinishState)null);

      @Nullable
      final PackStateChangeCallback.FinishState state;

      private DiscardReason(@Nullable final PackStateChangeCallback.FinishState state) {
         this.state = state;
      }

      // $FF: synthetic method
      private static DiscardReason[] method_55575() {
         return new DiscardReason[]{DOWNLOAD_FAILED, ACTIVATION_FAILED, DECLINED, DISCARDED, SERVER_REMOVED, SERVER_REPLACED};
      }
   }

   @Environment(EnvType.CLIENT)
   private static enum LoadStatus {
      REQUESTED,
      PENDING,
      DONE;

      // $FF: synthetic method
      private static LoadStatus[] method_55573() {
         return new LoadStatus[]{REQUESTED, PENDING, DONE};
      }
   }

   @Environment(EnvType.CLIENT)
   private static enum Status {
      INACTIVE,
      PENDING,
      ACTIVE;

      // $FF: synthetic method
      private static Status[] method_55572() {
         return new Status[]{INACTIVE, PENDING, ACTIVE};
      }
   }
}
