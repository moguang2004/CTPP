package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraftforge.network.NetworkDirection;

import com.mo_guang.ctpp.network.packet.CTPPToolboxActionPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxBindingsPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxMenuFiltersPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxOpenNearestPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxSnapshotPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxSnapshotRequestPacket;

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
    }
}
