package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public class GlobalSettings implements AutoCloseable {
   public static final int SIZE = (new Std140SizeCalculator()).putVec2().putFloat().putFloat().putInt().get();
   private final GpuBuffer buffer;

   public GlobalSettings() {
      this.buffer = RenderSystem.getDevice().createBuffer(() -> {
         return "Global Settings UBO";
      }, 136, SIZE);
   }

   public void set(int width, int height, double glintStrength, long time, RenderTickCounter tickCounter, int menuBackgroundBluriness) {
      MemoryStack memoryStack = MemoryStack.stackPush();

      try {
         ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, SIZE).putVec2((float)width, (float)height).putFloat((float)glintStrength).putFloat(((float)(time % 24000L) + tickCounter.getTickProgress(false)) / 24000.0F).putInt(menuBackgroundBluriness).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
      } catch (Throwable var13) {
         if (memoryStack != null) {
            try {
               memoryStack.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (memoryStack != null) {
         memoryStack.close();
      }

      RenderSystem.setGlobalSettingsUniform(this.buffer);
   }

   public void close() {
      this.buffer.close();
   }
}
