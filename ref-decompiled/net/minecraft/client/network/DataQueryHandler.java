package net.minecraft.client.network;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DataQueryHandler {
   private final ClientPlayNetworkHandler networkHandler;
   private int expectedTransactionId = -1;
   @Nullable
   private Consumer callback;

   public DataQueryHandler(ClientPlayNetworkHandler networkHandler) {
      this.networkHandler = networkHandler;
   }

   public boolean handleQueryResponse(int transactionId, @Nullable NbtCompound nbt) {
      if (this.expectedTransactionId == transactionId && this.callback != null) {
         this.callback.accept(nbt);
         this.callback = null;
         return true;
      } else {
         return false;
      }
   }

   private int nextQuery(Consumer callback) {
      this.callback = callback;
      return ++this.expectedTransactionId;
   }

   public void queryEntityNbt(int entityNetworkId, Consumer callback) {
      int i = this.nextQuery(callback);
      this.networkHandler.sendPacket(new QueryEntityNbtC2SPacket(i, entityNetworkId));
   }

   public void queryBlockNbt(BlockPos pos, Consumer callback) {
      int i = this.nextQuery(callback);
      this.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(i, pos));
   }
}
