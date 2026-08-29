package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraftforge.network.NetworkDirection;

import com.mo_guang.ctpp.network.packet.CTPPTerminalCancelWireSelectionPacket;
import com.mo_guang.ctpp.network.packet.CTPPTerminalWireSelectionPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxActionPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxBindingsPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxMenuFiltersPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxOpenNearestPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxSnapshotPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxSnapshotRequestPacket;
import com.mo_guang.ctpp.network.packet.DelEmitterBeamPacket;
import com.mo_guang.ctpp.network.packet.PickEmitterPacket;
import com.mo_guang.ctpp.network.packet.SetEmitterBeamPacket;

public final class CTPPNetwork {

    private CTPPNetwork() {}

    public static void init() {
        GTNetwork.register(CTPPToolboxActionPacket.class, CTPPToolboxActionPacket::new,
                NetworkDirection.PLAY_TO_SERVER);
        GTNetwork.register(CTPPToolboxSnapshotRequestPacket.class, CTPPToolboxSnapshotRequestPacket::new,
                NetworkDirection.PLAY_TO_SERVER);
        GTNetwork.register(CTPPToolboxOpenNearestPacket.class, CTPPToolboxOpenNearestPacket::new,
                NetworkDirection.PLAY_TO_SERVER);
        GTNetwork.register(CTPPToolboxBindingsPacket.class, CTPPToolboxBindingsPacket::new,
                NetworkDirection.PLAY_TO_CLIENT);
        GTNetwork.register(CTPPToolboxSnapshotPacket.class, CTPPToolboxSnapshotPacket::new,
                NetworkDirection.PLAY_TO_CLIENT);
        GTNetwork.register(CTPPToolboxMenuFiltersPacket.class, CTPPToolboxMenuFiltersPacket::new,
                NetworkDirection.PLAY_TO_CLIENT);
        GTNetwork.register(CTPPTerminalWireSelectionPacket.class, CTPPTerminalWireSelectionPacket::new,
                NetworkDirection.PLAY_TO_CLIENT);
        GTNetwork.register(CTPPTerminalCancelWireSelectionPacket.class,
                CTPPTerminalCancelWireSelectionPacket::new, NetworkDirection.PLAY_TO_SERVER);
        GTNetwork.register(SetEmitterBeamPacket.class, SetEmitterBeamPacket::new,
                NetworkDirection.PLAY_TO_CLIENT);
        GTNetwork.register(DelEmitterBeamPacket.class, DelEmitterBeamPacket::new,
                NetworkDirection.PLAY_TO_CLIENT);
        GTNetwork.register(PickEmitterPacket.class, PickEmitterPacket::new, NetworkDirection.PLAY_TO_SERVER);
    }
}
