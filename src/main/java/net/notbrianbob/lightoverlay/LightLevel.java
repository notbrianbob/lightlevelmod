package net.notbrianbob.lightoverlay;

import net.minecraft.world.level.Level;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LightLayer;

public class LightLevel {
    // Example method to get light levels around the player
    public static void checkLightLevelsAroundPlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            int radius = 5; // Example: 5 blocks in all directions
            BlockPos playerPos = mc.player.blockPosition();

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -radius; y <= radius; y++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        // Here, you specify the light layer you're interested in
                        int blockLightLevel = mc.level.getBrightness(LightLayer.BLOCK, checkPos);
                        int skyLightLevel = mc.level.getBrightness(LightLayer.SKY, checkPos);
                        // Update your overlay or store the light level as needed
                        // Example:
                        System.out.println("Block Light at " + checkPos + ": " + blockLightLevel);
                        System.out.println("Sky Light at " + checkPos + ": " + skyLightLevel);
                    }
                }
            }
        }
    }
}