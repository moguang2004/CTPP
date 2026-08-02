package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import com.mo_guang.ctpp.client.toolbox.CTPPToolboxClientState;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBinding;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CTPPToolboxBindingsPacket implements GTNetwork.INetPacket {

    private final Map<Integer, CTPPToolboxBinding> bindings;

    public CTPPToolboxBindingsPacket(Map<Integer, CTPPToolboxBinding> bindings) {
        this.bindings = Map.copyOf(bindings);
    }

    public CTPPToolboxBindingsPacket(FriendlyByteBuf buffer) {
        int size = Math.min(buffer.readVarInt(), 9);
        Map<Integer, CTPPToolboxBinding> decoded = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            int slot = buffer.readVarInt();
            CTPPToolboxBinding binding = new CTPPToolboxBinding(CTPPToolboxSourceId.read(buffer),
                    buffer.readVarInt());
            if (slot >= 0 && slot < 9) decoded.put(slot, binding);
        }
        bindings = Map.copyOf(decoded);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(bindings.size());
        bindings.forEach((slot, binding) -> {
            buffer.writeVarInt(slot);
            binding.source().write(buffer);
            buffer.writeVarInt(binding.compartment());
        });
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CTPPToolboxClientState.setBindings(bindings));
    }
}
