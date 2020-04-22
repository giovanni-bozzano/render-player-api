// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package api.player.render.asm.interfaces;

import api.player.render.renderer.PlayerRendererAPI;
import api.player.render.renderer.PlayerRendererBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public interface IPlayerRenderer
{
    PlayerRendererAPI getPlayerRendererAPI();

    PlayerRendererBase getPlayerRendererBase(String baseId);

    Set<String> getPlayerRendererBaseIds();

    Object dynamic(String key, Object[] parameters);

    void realApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks);

    void superApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks);

    boolean superCanRenderName(AbstractClientPlayerEntity player);

    BipedModel.ArmPose realGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand);

    float superGetDeathMaxRotation(AbstractClientPlayerEntity player);

    PlayerModel<AbstractClientPlayerEntity> superGetEntityModel();

    ResourceLocation realGetEntityTexture(AbstractClientPlayerEntity player);

    FontRenderer superGetFontRendererFromRenderManager();

    EntityRendererManager superGetRenderManager();

    Vec3d realGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks);

    Vec3d superGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks);

    float superGetSwingProgress(AbstractClientPlayerEntity player, float partialTickTime);

    float superHandleRotationFloat(AbstractClientPlayerEntity player, float partialTicks);

    void realPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime);

    void superPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime);

    void realRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight);

    void superRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight);

    void realRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear);

    void realRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player);

    void realRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight);

    void superRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight);

    void realRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player);

    void realSetModelVisibilities(AbstractClientPlayerEntity player);

    boolean superShouldRender(AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ);
}