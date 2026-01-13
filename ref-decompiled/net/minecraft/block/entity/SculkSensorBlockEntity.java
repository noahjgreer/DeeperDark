/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SculkSensorBlockEntity
 *  net.minecraft.block.entity.SculkSensorBlockEntity$VibrationCallback
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.event.Vibrations
 *  net.minecraft.world.event.Vibrations$Callback
 *  net.minecraft.world.event.Vibrations$ListenerData
 *  net.minecraft.world.event.Vibrations$VibrationListener
 *  net.minecraft.world.event.listener.GameEventListener
 *  net.minecraft.world.event.listener.GameEventListener$Holder
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkSensorBlockEntity
extends BlockEntity
implements GameEventListener.Holder<Vibrations.VibrationListener>,
Vibrations {
    private static final int DEFAULT_LAST_VIBRATION_FREQUENCY = 0;
    private Vibrations.ListenerData listenerData;
    private final Vibrations.VibrationListener listener;
    private final Vibrations.Callback callback = this.createCallback();
    private int lastVibrationFrequency = 0;

    protected SculkSensorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.listenerData = new Vibrations.ListenerData();
        this.listener = new Vibrations.VibrationListener((Vibrations)this);
    }

    public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityType.SCULK_SENSOR, pos, state);
    }

    public Vibrations.Callback createCallback() {
        return new VibrationCallback(this, this.getPos());
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.lastVibrationFrequency = view.getInt("last_vibration_frequency", 0);
        this.listenerData = view.read("listener", Vibrations.ListenerData.CODEC).orElseGet(Vibrations.ListenerData::new);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        view.put("listener", Vibrations.ListenerData.CODEC, (Object)this.listenerData);
    }

    public Vibrations.ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    public Vibrations.Callback getVibrationCallback() {
        return this.callback;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        this.lastVibrationFrequency = lastVibrationFrequency;
    }

    public Vibrations.VibrationListener getEventListener() {
        return this.listener;
    }

    public /* synthetic */ GameEventListener getEventListener() {
        return this.getEventListener();
    }
}

