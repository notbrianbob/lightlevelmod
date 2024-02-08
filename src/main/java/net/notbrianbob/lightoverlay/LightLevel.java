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

    private static final int radius = 5; // Example: 5 blocks in all directions

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
                    int blockLightLevel = mc.level.getBrightness(LightLayer.BLOCK, checkPos);
                    int color = getColorByLightLevel(blockLightLevel);
                    renderTextAtBlock(poseStack, font, String.valueOf(blockLightLevel), checkPos, color, buffer);
                }
            }
        }
    }

    private static int getColorByLightLevel(int lightLevel) {
        if (lightLevel > 8) return 0x00FF00; // Green
        else if (lightLevel <= 8 && lightLevel > 0) return 0xFFFF00; // Yellow
        else return 0xFF0000; // Red
    }

    private static void renderTextAtBlock(PoseStack poseStack, Font font, String text, BlockPos pos, int color, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.translate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = poseStack.last().pose();
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F) << 24;
        int packedLight = 15728880; // Full bright lighting. You may want to change this to the actual light level.

        font.drawInBatch(text, -font.width(text) / 2.0f, 0.0f, color, false, matrix, buffer, Font.DisplayMode.NORMAL, backgroundColor, packedLight);

        poseStack.popPose();
    }
}
