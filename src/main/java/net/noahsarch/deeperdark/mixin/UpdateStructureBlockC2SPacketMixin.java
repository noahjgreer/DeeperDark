package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpdateStructureBlockC2SPacket.class)
public abstract class UpdateStructureBlockC2SPacketMixin {

    @Shadow @Final @Mutable private BlockPos offset;
    @Shadow @Final @Mutable private Vec3i size;

    @Unique
    private static final int MAX_STRUCTURE_SIZE = 512;
    @Unique
    private static final int MAX_OFFSET = 512;

    /**
     * Inject at the end of the constructor that reads from PacketByteBuf
     * to override the clamped values with larger limits
     */
    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
            at = @At("RETURN"))
    private void onConstructFromBuffer(PacketByteBuf buf, CallbackInfo ci) {
        // Read the extended values that we'll write in the write method
        // The original constructor already consumed the byte values, so we read our int values
        if (buf.isReadable(12)) { // 3 ints for offset + 3 ints for size = 24 bytes, but check for 12 (minimum)
            try {
                int offsetX = buf.readInt();
                int offsetY = buf.readInt();
                int offsetZ = buf.readInt();
                int sizeX = buf.readInt();
                int sizeY = buf.readInt();
                int sizeZ = buf.readInt();

                // Apply our larger limits
                this.offset = new BlockPos(
                    MathHelper.clamp(offsetX, -MAX_OFFSET, MAX_OFFSET),
                    MathHelper.clamp(offsetY, -MAX_OFFSET, MAX_OFFSET),
                    MathHelper.clamp(offsetZ, -MAX_OFFSET, MAX_OFFSET)
                );

                this.size = new Vec3i(
                    MathHelper.clamp(sizeX, 0, MAX_STRUCTURE_SIZE),
                    MathHelper.clamp(sizeY, 0, MAX_STRUCTURE_SIZE),
                    MathHelper.clamp(sizeZ, 0, MAX_STRUCTURE_SIZE)
                );
            } catch (Exception e) {
                // If reading fails, keep the original clamped values
            }
        }
    }

    /**
     * Inject at the end of the write method to write extended values
     */
    @Inject(method = "write", at = @At("RETURN"))
    private void onWrite(PacketByteBuf buf, CallbackInfo ci) {
        // Write the full int values after the original byte values
        // This maintains backwards compatibility while adding extended support
        buf.writeInt(this.offset.getX());
        buf.writeInt(this.offset.getY());
        buf.writeInt(this.offset.getZ());
        buf.writeInt(this.size.getX());
        buf.writeInt(this.size.getY());
        buf.writeInt(this.size.getZ());
    }
}
