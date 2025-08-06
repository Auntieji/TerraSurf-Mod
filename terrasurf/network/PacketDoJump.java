package net.add.terrasurf.network;

import net.add.terrasurf.entity.TerraSurferEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDoJump {

    public PacketDoJump() {
    }

    public PacketDoJump(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() instanceof TerraSurferEntity board) {
                board.doJump();
            }
        });
        return true;
    }
}
