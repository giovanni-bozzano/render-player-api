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
package api.player.render.renderer;

import api.player.render.asm.interfaces.IPlayerRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public abstract class PlayerRendererBase
{
    private final PlayerRendererAPI internalPlayerRendererAPI;
    protected final IPlayerRenderer iPlayerRenderer;
    protected final PlayerRenderer playerRenderer;

    public PlayerRendererBase(PlayerRendererAPI playerRendererAPI)
    {
        this.internalPlayerRendererAPI = playerRendererAPI;
        this.iPlayerRenderer = playerRendererAPI.iPlayerRenderer;
        this.playerRenderer = (PlayerRenderer) playerRendererAPI.iPlayerRenderer;
    }

    public void beforeBaseAttach(boolean onTheFly)
    {
    }

    public void afterBaseAttach(boolean onTheFly)
    {
    }

    public void beforeLocalConstructing(EntityRendererManager paramRenderManager, boolean paramBoolean)
    {
    }

    public void afterLocalConstructing(EntityRendererManager paramRenderManager, boolean paramBoolean)
    {
    }

    public void beforeBaseDetach(boolean onTheFly)
    {
    }

    public void afterBaseDetach(boolean onTheFly)
    {
    }

    public Object dynamic(String key, Object[] parameters)
    {
        return this.internalPlayerRendererAPI.dynamicOverwritten(key, parameters, this);
    }

    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    // ############################################################################

    public void beforeApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
    }

    public void applyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenApplyRotations(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realApplyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
        } else if (overwritten != this) {
            overwritten.applyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
        }
    }

    public void afterApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
    }

    // ############################################################################

    public void beforeCanRenderName(AbstractClientPlayerEntity player)
    {
    }

    public boolean canRenderName(AbstractClientPlayerEntity player)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenCanRenderName(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superCanRenderName(player);
        } else if (overwritten != this) {
            return overwritten.canRenderName(player);
        } else {
            return false;
        }
    }

    public void afterCanRenderName(AbstractClientPlayerEntity player)
    {
    }

    // ############################################################################

    public void beforeGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
    }

    public BipedModel.ArmPose getArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetArmPose(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.realGetArmPose(player, itemStackMain, itemStackOff, hand);
        } else if (overwritten != this) {
            return overwritten.getArmPose(player, itemStackMain, itemStackOff, hand);
        } else {
            return null;
        }
    }

    public void afterGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
    }

    // ############################################################################

    public void beforeGetDeathMaxRotation(AbstractClientPlayerEntity player)
    {
    }

    public float getDeathMaxRotation(AbstractClientPlayerEntity player)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetDeathMaxRotation(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superGetDeathMaxRotation(player);
        } else if (overwritten != this) {
            return overwritten.getDeathMaxRotation(player);
        } else {
            return 0;
        }
    }

    public void afterGetDeathMaxRotation(AbstractClientPlayerEntity player)
    {
    }

    // ############################################################################

    public void beforeGetEntityModel()
    {
    }

    public PlayerModel<AbstractClientPlayerEntity> getEntityModel()
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetEntityModel(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superGetEntityModel();
        } else if (overwritten != this) {
            return overwritten.getEntityModel();
        } else {
            return null;
        }
    }

    public void afterGetEntityModel()
    {
    }

    // ############################################################################

    public void beforeGetEntityTexture(AbstractClientPlayerEntity player)
    {
    }

    public ResourceLocation getEntityTexture(AbstractClientPlayerEntity player)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetEntityTexture(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.realGetEntityTexture(player);
        } else if (overwritten != this) {
            return overwritten.getEntityTexture(player);
        } else {
            return null;
        }
    }

    public void afterGetEntityTexture(AbstractClientPlayerEntity player)
    {
    }

    // ############################################################################

    public void beforeGetFontRendererFromRenderManager()
    {
    }

    public FontRenderer getFontRendererFromRenderManager()
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetFontRendererFromRenderManager(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superGetFontRendererFromRenderManager();
        } else if (overwritten != this) {
            return overwritten.getFontRendererFromRenderManager();
        } else {
            return null;
        }
    }

    public void afterGetFontRendererFromRenderManager()
    {
    }

    // ############################################################################

    public void beforeGetRenderManager()
    {
    }

    public EntityRendererManager getRenderManager()
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetRenderManager(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superGetRenderManager();
        } else if (overwritten != this) {
            return overwritten.getRenderManager();
        } else {
            return null;
        }
    }

    public void afterGetRenderManager()
    {
    }

    // ############################################################################

    public void beforeGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks)
    {
    }

    public Vec3d getRenderOffset(AbstractClientPlayerEntity player, float partialTicks)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetRenderOffset(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.realGetRenderOffset(player, partialTicks);
        } else if (overwritten != this) {
            return overwritten.getRenderOffset(player, partialTicks);
        } else {
            return null;
        }
    }

    public void afterGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks)
    {
    }

    // ############################################################################

    public void beforeGetSwingProgress(AbstractClientPlayerEntity player, float partialTickTime)
    {
    }

    public float getSwingProgress(AbstractClientPlayerEntity player, float partialTickTime)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenGetSwingProgress(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superGetSwingProgress(player, partialTickTime);
        } else if (overwritten != this) {
            return overwritten.getSwingProgress(player, partialTickTime);
        } else {
            return 0;
        }
    }

    public void afterGetSwingProgress(AbstractClientPlayerEntity player, float partialTickTime)
    {
    }

    // ############################################################################

    public void beforeHandleRotationFloat(AbstractClientPlayerEntity player, float partialTicks)
    {
    }

    public float handleRotationFloat(AbstractClientPlayerEntity player, float partialTicks)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenHandleRotationFloat(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superHandleRotationFloat(player, partialTicks);
        } else if (overwritten != this) {
            return overwritten.handleRotationFloat(player, partialTicks);
        } else {
            return 0;
        }
    }

    public void afterHandleRotationFloat(AbstractClientPlayerEntity player, float partialTicks)
    {
    }

    // ############################################################################

    public void beforePreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
    }

    public void preRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenPreRenderCallback(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realPreRenderCallback(player, matrixStack, partialTickTime);
        } else if (overwritten != this) {
            overwritten.preRenderCallback(player, matrixStack, partialTickTime);
        }
    }

    public void afterPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
    }

    // ############################################################################

    public void beforeRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
    }

    public void render(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenRender(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realRender(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        } else if (overwritten != this) {
            overwritten.render(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }

    public void afterRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
    }

    // ############################################################################

    public void beforeRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
    }

    public void renderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenRenderItem(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realRenderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
        } else if (overwritten != this) {
            overwritten.renderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
        }
    }

    public void afterRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
    }

    // ############################################################################

    public void beforeRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
    }

    public void renderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenRenderLeftArm(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realRenderLeftArm(matrixStack, buffer, combinedLight, player);
        } else if (overwritten != this) {
            overwritten.renderLeftArm(matrixStack, buffer, combinedLight, player);
        }
    }

    public void afterRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
    }

    // ############################################################################

    public void beforeRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
    }

    public void renderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenRenderName(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realRenderName(player, displayName, matrixStack, buffer, packedLight);
        } else if (overwritten != this) {
            overwritten.renderName(player, displayName, matrixStack, buffer, packedLight);
        }
    }

    public void afterRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
    }

    // ############################################################################

    public void beforeRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
    }

    public void renderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenRenderRightArm(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realRenderRightArm(matrixStack, buffer, combinedLight, player);
        } else if (overwritten != this) {
            overwritten.renderRightArm(matrixStack, buffer, combinedLight, player);
        }
    }

    public void afterRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
    }

    // ############################################################################

    public void beforeSetModelVisibilities(AbstractClientPlayerEntity player)
    {
    }

    public void setModelVisibilities(AbstractClientPlayerEntity player)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenSetModelVisibilities(this);

        if (overwritten == null) {
            this.iPlayerRenderer.realSetModelVisibilities(player);
        } else if (overwritten != this) {
            overwritten.setModelVisibilities(player);
        }
    }

    public void afterSetModelVisibilities(AbstractClientPlayerEntity player)
    {
    }

    // ############################################################################

    public void beforeShouldRender(AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
    }

    public boolean shouldRender(AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
        PlayerRendererBase overwritten = this.internalPlayerRendererAPI.getOverwrittenShouldRender(this);

        if (overwritten == null) {
            return this.iPlayerRenderer.superShouldRender(player, camera, camX, camY, camZ);
        } else if (overwritten != this) {
            return overwritten.shouldRender(player, camera, camX, camY, camZ);
        } else {
            return false;
        }
    }

    public void afterShouldRender(AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
    }
}