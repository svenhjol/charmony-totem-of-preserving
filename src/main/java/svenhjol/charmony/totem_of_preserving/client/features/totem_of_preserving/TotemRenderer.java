package svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemBlockEntity;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemItem;

public class TotemRenderer<T extends TotemBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemStack stack;

    public TotemRenderer(BlockEntityRendererProvider.Context context) {
        this.stack = new ItemStack(TotemOfPreserving.feature().common.get().registers.item.get());
        TotemItem.setGlint(this.stack, true);
    }

    @Override
    public void submit(T entity, float tickDelta, PoseStack poseStack, int light, int overlay, Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();
        poseStack.scale(1f, 1f, 1f);
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        var level = Minecraft.getInstance().level;
        var resolver = Minecraft.getInstance().getItemModelResolver();


        var rotateTicks = entity.getRotateTicks();
        entity.setRotateTicks(rotateTicks += 0.25f);

        if (rotateTicks >= 360f) {
            rotateTicks = 0f;
            entity.setRotateTicks(rotateTicks);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(rotateTicks));

        var itemStackRenderState = new ItemStackRenderState();
        resolver.updateForTopItem(itemStackRenderState, this.stack, ItemDisplayContext.FIXED, level, null, (int)rotateTicks);
        itemStackRenderState.submit(poseStack, submitNodeCollector, 0xf000f0, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}
