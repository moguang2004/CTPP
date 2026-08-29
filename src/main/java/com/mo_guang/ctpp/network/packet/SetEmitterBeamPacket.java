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
import com.mo_guang.ctpp.common.beam.EmitterBeam;

/** S2C: register or update a global emitter beam on the client. */
public class SetEmitterBeamPacket implements GTNetwork.INetPacket {

    private final int id;
    private final ResourceKey<Level> dim;
    private final EmitterBeam beam;

    public SetEmitterBeamPacket(int id, ResourceKey<Level> dim, EmitterBeam beam) {
        this.id = id;
        this.dim = dim;
        this.beam = beam;
    }

    public SetEmitterBeamPacket(FriendlyByteBuf buffer) {
        id = buffer.readVarInt();
        ResourceLocation dimLoc = buffer.readResourceLocation();
        dim = ResourceKey.create(Registries.DIMENSION, dimLoc);
        beam = new EmitterBeam(id, buffer.readBlockPos(),
                new net.minecraft.world.phys.Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                buffer.readVarLong(), buffer.readVarLong(), buffer.readVarInt(), buffer.readDouble());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeResourceLocation(dim.location());
        buffer.writeBlockPos(beam.src());
        buffer.writeDouble(beam.dir().x);
        buffer.writeDouble(beam.dir().y);
        buffer.writeDouble(beam.dir().z);
        buffer.writeVarLong(beam.voltage());
        buffer.writeVarLong(beam.amps());
        buffer.writeVarInt(beam.tier());
        buffer.writeDouble(beam.distance());
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EmitterBeamRenderer.setBeam(id, dim, beam));
    }
}
