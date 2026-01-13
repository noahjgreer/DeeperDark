/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SkullBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SkullBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ProfileComponent
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class SkullBlockEntity
extends BlockEntity {
    private static final String PROFILE_NBT_KEY = "profile";
    private static final String NOTE_BLOCK_SOUND_NBT_KEY = "note_block_sound";
    private static final String CUSTOM_NAME_NBT_KEY = "custom_name";
    private @Nullable ProfileComponent owner;
    private @Nullable Identifier noteBlockSound;
    private int poweredTicks;
    private boolean powered;
    private @Nullable Text customName;

    public SkullBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SKULL, pos, state);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putNullable("profile", ProfileComponent.CODEC, (Object)this.owner);
        view.putNullable("note_block_sound", Identifier.CODEC, (Object)this.noteBlockSound);
        view.putNullable("custom_name", TextCodecs.CODEC, (Object)this.customName);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.owner = view.read("profile", ProfileComponent.CODEC).orElse(null);
        this.noteBlockSound = view.read("note_block_sound", Identifier.CODEC).orElse(null);
        this.customName = SkullBlockEntity.tryParseCustomName((ReadView)view, (String)"custom_name");
    }

    public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
        if (state.contains((Property)SkullBlock.POWERED) && ((Boolean)state.get((Property)SkullBlock.POWERED)).booleanValue()) {
            blockEntity.powered = true;
            ++blockEntity.poweredTicks;
        } else {
            blockEntity.powered = false;
        }
    }

    public float getPoweredTicks(float tickProgress) {
        if (this.powered) {
            return (float)this.poweredTicks + tickProgress;
        }
        return this.poweredTicks;
    }

    public @Nullable ProfileComponent getOwner() {
        return this.owner;
    }

    public @Nullable Identifier getNoteBlockSound() {
        return this.noteBlockSound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.owner = (ProfileComponent)components.get(DataComponentTypes.PROFILE);
        this.noteBlockSound = (Identifier)components.get(DataComponentTypes.NOTE_BLOCK_SOUND);
        this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.PROFILE, (Object)this.owner);
        builder.add(DataComponentTypes.NOTE_BLOCK_SOUND, (Object)this.noteBlockSound);
        builder.add(DataComponentTypes.CUSTOM_NAME, (Object)this.customName);
    }

    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove("profile");
        view.remove("note_block_sound");
        view.remove("custom_name");
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

