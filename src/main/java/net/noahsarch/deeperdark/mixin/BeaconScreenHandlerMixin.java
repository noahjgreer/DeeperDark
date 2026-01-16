package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.noahsarch.deeperdark.duck.BeaconDuck;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(BeaconScreenHandler.class)
public abstract class BeaconScreenHandlerMixin extends ScreenHandler {

    @Shadow @Final private ScreenHandlerContext context;
    @Shadow @Nullable public abstract RegistryEntry<StatusEffect> getPrimaryEffect();
    @Shadow @Nullable public abstract RegistryEntry<StatusEffect> getSecondaryEffect();

    // Payment slot is private in BeaconScreenHandler and index 0 in the 'payment' inventory
    // But 'payment' field is private.
    // However, slot 0 of the screen handler IS the payment slot.
    // getSlot(0).getStack() should work.

    protected BeaconScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "setEffects", at = @At("HEAD"))
    private void deeperDark$onSetEffects(Optional<RegistryEntry<StatusEffect>> primary, Optional<RegistryEntry<StatusEffect>> secondary, CallbackInfo ci) {
        // We only care if payment is present, as vanilla checks this too.
        ItemStack paymentStack = this.getSlot(0).getStack();
        if (paymentStack.isEmpty()) return;

        // Capture payment details before vanilla consumes it
        ItemStack paymentCopy = paymentStack.copy();

        this.context.run((world, pos) -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BeaconDuck beaconDuck) {
                RegistryEntry<StatusEffect> newPrimary = primary.orElse(null);
                RegistryEntry<StatusEffect> newSecondary = secondary.orElse(null);

                RegistryEntry<StatusEffect> oldPrimary = this.getPrimaryEffect();
                RegistryEntry<StatusEffect> oldSecondary = this.getSecondaryEffect();

                boolean changed = !Objects.equals(oldPrimary, newPrimary) || !Objects.equals(oldSecondary, newSecondary);

                if (changed) {
                    beaconDuck.deeperDark$resetBeacon();
                }

                beaconDuck.deeperDark$checkPayment(paymentCopy);
            }
        });
    }
}

