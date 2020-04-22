package api.player.render.asm.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;

import java.util.Random;

public interface IPlayerModelReal<T extends LivingEntity> extends IPlayerModel<T>
{
    void realAccept(ModelRenderer renderer);

    ModelRenderer realGetRandomModelRenderer(Random random);

    void realRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay);

    void realRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay);

    void realSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch);

    void realSetVisible(boolean visible);

    void realTranslateHand(HandSide side, MatrixStack matrixStack);
}
