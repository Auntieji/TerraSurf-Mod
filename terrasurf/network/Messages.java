package net.add.terrasurf.network;

import net.add.terrasurf.TerraSurfMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Messages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(TerraSurfMod.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PacketActivateBoard.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketActivateBoard::new)
                .encoder(PacketActivateBoard::toBytes)
                .consumerMainThread(PacketActivateBoard::handle)
                .add();

        net.messageBuilder(PacketDoJump.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketDoJump::new)
                .encoder(PacketDoJump::toBytes)
                .consumerMainThread(PacketDoJump::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
