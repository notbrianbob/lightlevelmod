package net.notbrianbob.lightoverlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.core.BlockPos;

import net.minecraftforge.client.event.RenderLevelStageEvent;

import com.mojang.math.MatrixUtil;

//@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.FORGE)
public class LightLevelRenderer {

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return; // We only want to render after most blocks have been rendered
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        PoseStack poseStack = event.getPoseStack();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        Vec3 cameraPos = dispatcher.camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        BlockPos playerPos = mc.player.blockPosition();
        int lightLevel = mc.level.getBrightness(LightLayer.BLOCK, playerPos);
        int color = getColorByLightLevel(lightLevel);

        renderTextAtBlock(poseStack, mc.font, String.valueOf(lightLevel), playerPos, color);

        poseStack.popPose();
    }

    private static int getColorByLightLevel(int lightLevel) {
        if (lightLevel > 8) return 0x00FF00; // Green
        else if (lightLevel <= 8 && lightLevel > 0) return 0xFFFF00; // Yellow
        else return 0xFF0000; // Red
    }

    private static void renderTextAtBlock(PoseStack poseStack, Font font, String text, BlockPos pos, int color) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = poseStack.last().pose();
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F) << 24;
        int packedLight = 15728880; // Full bright lighting. You may want to change this to the actual light level.

        // Use the NORMAL display mode for the Font
        font.drawInBatch(text, -font.width(text) / 2.0f, 0.0f, color, false, matrix, buffer, Font.DisplayMode.NORMAL, backgroundColor, packedLight);

        poseStack.popPose();
        buffer.endBatch();
    }
}