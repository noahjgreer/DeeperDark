package net.minecraft.entity.passive;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.World;
import org.slf4j.Logger;

public abstract class TameableShoulderEntity extends TameableEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int READY_TO_SIT_COOLDOWN = 100;
   private int ticks;

   protected TameableShoulderEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public boolean mountOnto(ServerPlayerEntity player) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getErrorReporterContext(), LOGGER);

      boolean var4;
      label27: {
         try {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, this.getRegistryManager());
            this.writeData(nbtWriteView);
            nbtWriteView.putString("id", this.getSavedEntityId());
            if (player.addShoulderEntity(nbtWriteView.getNbt())) {
               this.discard();
               var4 = true;
               break label27;
            }
         } catch (Throwable var6) {
            try {
               logging.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         logging.close();
         return false;
      }

      logging.close();
      return var4;
   }

   public void tick() {
      ++this.ticks;
      super.tick();
   }

   public boolean isReadyToSitOnPlayer() {
      return this.ticks > 100;
   }
}
