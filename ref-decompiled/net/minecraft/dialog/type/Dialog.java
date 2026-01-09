package net.minecraft.dialog.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.dynamic.Codecs;

public interface Dialog {
   Codec WIDTH_CODEC = Codecs.rangedInt(1, 1024);
   Codec CODEC = Registries.DIALOG_TYPE.getCodec().dispatch(Dialog::getCodec, (mapCodec) -> {
      return mapCodec;
   });
   Codec ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.DIALOG, CODEC);
   Codec ENTRY_LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.DIALOG, CODEC);
   PacketCodec ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.DIALOG, PacketCodecs.unlimitedRegistryCodec(CODEC));
   PacketCodec PACKET_CODEC = PacketCodecs.unlimitedCodec(CODEC);

   DialogCommonData common();

   MapCodec getCodec();

   Optional getCancelAction();
}
