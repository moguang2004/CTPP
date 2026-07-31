package com.mo_guang.ctpp.common.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import com.mo_guang.ctpp.client.CTPPToolboxClientState;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBinding;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSnapshot;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CTPPToolboxSnapshotPacket implements GTNetwork.INetPacket {

    private final List<CTPPToolboxSnapshot> snapshots;
    private final Map<Integer, CTPPToolboxBinding> bindings;
    private final int hotbarSlot;

    public CTPPToolboxSnapshotPacket(List<CTPPToolboxSnapshot> snapshots,
                                     Map<Integer, CTPPToolboxBinding> bindings, int hotbarSlot) {
        this.snapshots = List.copyOf(snapshots);
        this.bindings = Map.copyOf(bindings);
        this.hotbarSlot = hotbarSlot;
    }

    public CTPPToolboxSnapshotPacket(FriendlyByteBuf buffer) {
        int size = Math.min(buffer.readVarInt(), 128);
        List<CTPPToolboxSnapshot> decoded = new ArrayList<>(size);
        for (int i = 0; i < size; i++) decoded.add(CTPPToolboxSnapshot.read(buffer));
        snapshots = List.copyOf(decoded);
        int bindingCount = Math.min(buffer.readVarInt(), 9);
        Map<Integer, CTPPToolboxBinding> decodedBindings = new LinkedHashMap<>();
        for (int i = 0; i < bindingCount; i++) {
            int slot = buffer.readVarInt();
            CTPPToolboxBinding binding = new CTPPToolboxBinding(CTPPToolboxSourceId.read(buffer),
                    buffer.readVarInt());
            if (slot >= 0 && slot < 9) decodedBindings.put(slot, binding);
        }
        bindings = Map.copyOf(decodedBindings);
        hotbarSlot = buffer.readVarInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(snapshots.size());
        snapshots.forEach(snapshot -> snapshot.write(buffer));
        buffer.writeVarInt(bindings.size());
        bindings.forEach((slot, binding) -> {
            buffer.writeVarInt(slot);
            binding.source().write(buffer);
            buffer.writeVarInt(binding.compartment());
        });
        buffer.writeVarInt(hotbarSlot);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> CTPPToolboxClientState.openRadial(snapshots, bindings, hotbarSlot));
    }
}
