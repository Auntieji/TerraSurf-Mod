package net.add.terrasurf.sounds;

import net.add.terrasurf.entity.TerraSurferEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class LoopingTerraSurferSound extends AbstractTickableSoundInstance {
    private final Player player;
    private final TerraSurferEntity board;

    public LoopingTerraSurferSound(Player pPlayer, TerraSurferEntity pBoard, SoundEvent pSoundEvent) {
        super(pSoundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.player = pPlayer;
        this.board = pBoard;
        this.looping = true; // This makes the sound loop
        this.delay = 0;
        this.volume = 0.5F;
    }

    @Override
    public void tick() {
        // Stop the sound if the player is no longer riding the board or the board is removed
        if (!this.board.isVehicle() || this.board.isRemoved() || !this.player.isPassenger()) {
            this.stop();
        }

        // Update the sound's position to follow the board
        this.x = (float)this.board.getX();
        this.y = (float)this.board.getY();
        this.z = (float)this.board.getZ();
    }
}
