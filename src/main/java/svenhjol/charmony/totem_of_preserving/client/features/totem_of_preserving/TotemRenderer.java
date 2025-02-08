package svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemBlockEntity;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemItem;

public class TotemRenderer<T extends TotemBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemStack stack;

    public TotemRenderer(BlockEntityRendererProvider.Context context) {
        this.stack = new ItemStack(TotemOfPreserving.feature().common.get().registers.item.get());
        TotemItem.setGlint(this.stack, true);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, Vec3 vec3) {
        poseStack.pushPose();
        poseStack.scale(1f, 1f, 1f);
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var level = Minecraft.getInstance().level;

        var rotateTicks = entity.getRotateTicks();
        entity.setRotateTicks(rotateTicks += 0.25f);

        if (rotateTicks >= 360f) {
            rotateTicks = 0f;
            entity.setRotateTicks(rotateTicks);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(rotateTicks));
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, 0xf000f0, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, level, entity.hashCode());
        poseStack.popPose();
    }
}
