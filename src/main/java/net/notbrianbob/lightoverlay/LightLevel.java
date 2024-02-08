package net.notbrianbob.lightoverlay;




import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;


@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.FORGE)
public class LightLevel {

    private static final int radius = 10; // Example: 5 blocks in all directions

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (LightOverlay.isOverlayEnabled) {
            Minecraft mc = Minecraft.getInstance();
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || mc.player == null || mc.level == null) {
                return;
            }
            PoseStack poseStack = event.getPoseStack();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            Vec3 cameraPos = dispatcher.camera.getPosition();
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            checkLightLevelsAroundPlayer(poseStack, mc.font, Minecraft.getInstance().renderBuffers().bufferSource());
            poseStack.popPose();
        }
    }

    public static void checkLightLevelsAroundPlayer(PoseStack poseStack, Font font, MultiBufferSource.BufferSource buffer) {
        Minecraft mc = Minecraft.getInstance();
        BlockPos playerPos = mc.player.blockPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockPos blockBelowPos = checkPos.below(); // Get the position of the block below
                    if (mc.level.isEmptyBlock(blockBelowPos)) { // Check if the block below is air
                        continue;
                    }
                    int blockLightLevel = mc.level.getBrightness(LightLayer.BLOCK, checkPos);
                    int color = getColorByLightLevel(blockLightLevel);
                    renderTextAtBlock(poseStack, font, String.valueOf(blockLightLevel), checkPos, color, buffer);
                }
            }
        }
    }

    private static int getColorByLightLevel(int blockLightLevel) {
        Minecraft mc = Minecraft.getInstance();
        int skyLightLevel = mc.level.getBrightness(LightLayer.SKY, mc.player.blockPosition().above());


        if (blockLightLevel >= 8) {
            return 0x00FF00; // Green
        } else if (blockLightLevel < 8 && skyLightLevel >= 8) {
            return 0xFFFF00; // Yellow
        } else {
            return 0xFF0000; // Red
        }
    }


    private static void renderTextAtBlock(PoseStack poseStack, Font font, String text, BlockPos pos, int color, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.translate(pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5); // Adjusted Y position to lay flat on the block
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation()); // Make the text face the player
        poseStack.scale(-0.03F, -0.03F, 0.03F); // Adjusted scaling factor
        Matrix4f matrix = poseStack.last().pose();
        int packedLight = 15728880;

        // Set the alpha value of the color to 0 to make the background transparent
        int transparentColor = 0x99000000;
        font.drawInBatch(text, -font.width(text) / 2.0f, 0.0f, color, false, matrix, buffer, Font.DisplayMode.NORMAL, transparentColor, packedLight);
        //font.drawInBatch(text, -font.width(text) / 2.0f, 0.0f, transparentColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
//        font.drawInBatch(
//                text,                                       // The text to draw
//                -font.width(text) / 2.0f,                   // X-coordinate for centering the text horizontally
//                0.0f,                                       // Y-coordinate (0.0f means the text is drawn on the same level as the block)
//                color,                           // Color of the text
//                false,                                      // Not using strikethrough
//                matrix,                                     // Transformation matrix
//                buffer,                                     // Buffer source for rendering
//                Font.DisplayMode.NORMAL,                    // Display mode of the text
//                transparentColor,                                         // Background color (set to black)
//                packedLight                                 // Packed light level of the block
//        );
        poseStack.popPose();
    }
}
