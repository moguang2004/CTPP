package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import com.mo_guang.ctpp.client.renderer.EmitterBeamRenderer;

/** S2C: remove a global emitter beam from the client. */
public class DelEmitterBeamPacket implements GTNetwork.INetPacket {

    private final int id;
    private final ResourceKey<Level> dim;

    public DelEmitterBeamPacket(int id, ResourceKey<Level> dim) {
        this.id = id;
        this.dim = dim;
    }

    public DelEmitterBeamPacket(FriendlyByteBuf buffer) {
        id = buffer.readVarInt();
        ResourceLocation dimLoc = buffer.readResourceLocation();
        dim = ResourceKey.create(Registries.DIMENSION, dimLoc);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeResourceLocation(dim.location());
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EmitterBeamRenderer.removeBeam(id));
    }
}
