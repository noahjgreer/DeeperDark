/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.JigsawBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.JigsawBlockEntity
 *  net.minecraft.block.entity.JigsawBlockEntity$Joint
 *  net.minecraft.block.enums.Orientation
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.structure.StructureTemplate
 *  net.minecraft.structure.pool.StructurePool
 *  net.minecraft.structure.pool.StructurePoolBasedGenerator
 *  net.minecraft.structure.pool.StructurePools
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class JigsawBlockEntity
extends BlockEntity {
    public static final Codec<RegistryKey<StructurePool>> STRUCTURE_POOL_KEY_CODEC = RegistryKey.createCodec((RegistryKey)RegistryKeys.TEMPLATE_POOL);
    public static final Identifier DEFAULT_NAME = Identifier.ofVanilla((String)"empty");
    private static final int DEFAULT_PLACEMENT_PRIORITY = 0;
    private static final int DEFAULT_SELECTION_PRIORITY = 0;
    public static final String TARGET_KEY = "target";
    public static final String POOL_KEY = "pool";
    public static final String JOINT_KEY = "joint";
    public static final String PLACEMENT_PRIORITY_KEY = "placement_priority";
    public static final String SELECTION_PRIORITY_KEY = "selection_priority";
    public static final String NAME_KEY = "name";
    public static final String FINAL_STATE_KEY = "final_state";
    public static final String DEFAULT_FINAL_STATE = "minecraft:air";
    private Identifier name = DEFAULT_NAME;
    private Identifier target = DEFAULT_NAME;
    private RegistryKey<StructurePool> pool = StructurePools.EMPTY;
    private Joint joint = Joint.ROLLABLE;
    private String finalState = "minecraft:air";
    private int placementPriority = 0;
    private int selectionPriority = 0;

    public JigsawBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JIGSAW, pos, state);
    }

    public Identifier getName() {
        return this.name;
    }

    public Identifier getTarget() {
        return this.target;
    }

    public RegistryKey<StructurePool> getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public Joint getJoint() {
        return this.joint;
    }

    public int getPlacementPriority() {
        return this.placementPriority;
    }

    public int getSelectionPriority() {
        return this.selectionPriority;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setTarget(Identifier target) {
        this.target = target;
    }

    public void setPool(RegistryKey<StructurePool> pool) {
        this.pool = pool;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    public void setPlacementPriority(int placementPriority) {
        this.placementPriority = placementPriority;
    }

    public void setSelectionPriority(int selectionPriority) {
        this.selectionPriority = selectionPriority;
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put(NAME_KEY, Identifier.CODEC, (Object)this.name);
        view.put(TARGET_KEY, Identifier.CODEC, (Object)this.target);
        view.put(POOL_KEY, STRUCTURE_POOL_KEY_CODEC, (Object)this.pool);
        view.putString(FINAL_STATE_KEY, this.finalState);
        view.put(JOINT_KEY, (Codec)Joint.CODEC, (Object)this.joint);
        view.putInt(PLACEMENT_PRIORITY_KEY, this.placementPriority);
        view.putInt(SELECTION_PRIORITY_KEY, this.selectionPriority);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.name = view.read(NAME_KEY, Identifier.CODEC).orElse(DEFAULT_NAME);
        this.target = view.read(TARGET_KEY, Identifier.CODEC).orElse(DEFAULT_NAME);
        this.pool = view.read(POOL_KEY, STRUCTURE_POOL_KEY_CODEC).orElse(StructurePools.EMPTY);
        this.finalState = view.getString(FINAL_STATE_KEY, DEFAULT_FINAL_STATE);
        this.joint = view.read(JOINT_KEY, (Codec)Joint.CODEC).orElseGet(() -> StructureTemplate.getJointFromFacing((BlockState)this.getCachedState()));
        this.placementPriority = view.getInt(PLACEMENT_PRIORITY_KEY, 0);
        this.selectionPriority = view.getInt(SELECTION_PRIORITY_KEY, 0);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
        BlockPos blockPos = this.getPos().offset(((Orientation)this.getCachedState().get((Property)JigsawBlock.ORIENTATION)).getFacing());
        Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.TEMPLATE_POOL);
        RegistryEntry.Reference registryEntry = registry.getOrThrow(this.pool);
        StructurePoolBasedGenerator.generate((ServerWorld)world, (RegistryEntry)registryEntry, (Identifier)this.target, (int)maxDepth, (BlockPos)blockPos, (boolean)keepJigsaws);
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

