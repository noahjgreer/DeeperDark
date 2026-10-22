package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
//? if <26.3 {
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//?}

@Mixin(ServerboundChatPacket.class)
public class ServerboundChatPacketMixin {

    //? if >=26.3 {
    /*@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 256))
    private static int deeperdark$expandCodecLimit(int original) {
        return 32767;
    }
    *///?} else {
    @Redirect(
        method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readUtf(I)Ljava/lang/String;")
    )
    private static String deeperdark$expandReadLimit(FriendlyByteBuf buf, int maxLength) {
        return buf.readUtf(32767);
    }

    @ModifyConstant(method = "write", constant = @Constant(intValue = 256))
    private int deeperdark$expandWriteLimit(int original) {
        return 32767;
    }
    //?}
}
