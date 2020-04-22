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
package api.player.render.asm.mixins.net.minecraft.client.renderer.entity;

import api.player.render.asm.interfaces.IPlayerRenderer;
import api.player.render.asm.interfaces.IPlayerRendererAccessor;
import api.player.render.model.PlayerArmorModel;
import api.player.render.renderer.PlayerRendererAPI;
import api.player.render.renderer.PlayerRendererBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> implements IPlayerRenderer, IPlayerRendererAccessor
{
    private PlayerRendererAPI playerRendererAPI;
    private boolean callReal;

    public MixinPlayerRenderer(EntityRendererManager renderManager, PlayerModel<AbstractClientPlayerEntity> entityModel, float shadowSize)
    {
        super(renderManager, entityModel, shadowSize);
    }

    @Override
    public PlayerRendererAPI getPlayerRendererAPI()
    {
        return this.playerRendererAPI;
    }

    @Override
    public PlayerRendererBase getPlayerRendererBase(String baseId)
    {
        return PlayerRendererAPI.getPlayerRendererBase(this, baseId);
    }

    @Override
    public Set<String> getPlayerRendererBaseIds()
    {
        return PlayerRendererAPI.getPlayerRendererBaseIds(this);
    }

    @Override
    public Object dynamic(String key, Object[] parameters)
    {
        return PlayerRendererAPI.dynamic(this, key, parameters);
    }

    @Redirect(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererManager;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/PlayerRenderer;addLayer(Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;)Z", ordinal = 0))
    public boolean beforeInit(PlayerRenderer redirectedPlayerRenderer, LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> redirectedLayerRenderer, EntityRendererManager renderManager, boolean useSmallArms)
    {
        this.playerRendererAPI = PlayerRendererAPI.create(this);
        PlayerRendererAPI.beforeLocalConstructing(this, renderManager, useSmallArms);
        return this.addLayer(redirectedLayerRenderer);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererManager;Z)V", at = @At("RETURN"))
    public void afterInit(EntityRendererManager renderManager, boolean useSmallArms, CallbackInfo callbackInfo)
    {
        this.layerRenderers.remove(0);
        this.layerRenderers.add(0, new BipedArmorLayer<>(this, new PlayerArmorModel<>(0.5F), new PlayerArmorModel<>(1.0F)));
        PlayerRendererAPI.afterLocalConstructing(this, renderManager, useSmallArms);
    }

    // ############################################################################

    @Shadow
    protected abstract void applyRotations(@Nonnull AbstractClientPlayerEntity player, @Nonnull MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks);

    @Inject(method = "applyRotations", at = @At("HEAD"), cancellable = true)
    protected void beforeApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeApplyRotations(callbackInfo, this, player, matrixStack, ageInTicks, rotationYaw, partialTicks);
        }
        this.callReal = false;
    }

    @Inject(method = "applyRotations", at = @At("HEAD"))
    protected void afterApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterApplyRotations(this, player, matrixStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void realApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        this.callReal = true;
        this.applyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void superApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.applyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
    }

    // ############################################################################

    @Override
    public boolean canRenderName(@Nonnull AbstractClientPlayerEntity player)
    {
        return PlayerRendererAPI.canRenderName(this, player);
    }

    @Override
    public boolean superCanRenderName(AbstractClientPlayerEntity player)
    {
        return super.canRenderName(player);
    }

    // ############################################################################

    @Shadow
    protected abstract BipedModel.ArmPose getArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand);

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void beforeGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand, CallbackInfoReturnable<BipedModel.ArmPose> callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeGetArmPose(callbackInfo, this, player, itemStackMain, itemStackOff, hand);
        }
        this.callReal = false;
    }

    @Inject(method = "getArmPose", at = @At("RETURN"))
    private void afterGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand, CallbackInfoReturnable<BipedModel.ArmPose> callbackInfo)
    {
        PlayerRendererAPI.afterGetArmPose(this, player, itemStackMain, itemStackOff, hand);
    }

    @Override
    public BipedModel.ArmPose realGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
        this.callReal = true;
        return this.getArmPose(player, itemStackMain, itemStackOff, hand);
    }

    // ############################################################################

    @Override
    public float getDeathMaxRotation(@Nonnull AbstractClientPlayerEntity player)
    {
        return PlayerRendererAPI.getDeathMaxRotation(this, player);
    }

    @Override
    public float superGetDeathMaxRotation(AbstractClientPlayerEntity player)
    {
        return super.getDeathMaxRotation(player);
    }

    // ############################################################################

    @Override
    @Nonnull
    public PlayerModel<AbstractClientPlayerEntity> getEntityModel()
    {
        return PlayerRendererAPI.getEntityModel(this);
    }

    @Override
    public PlayerModel<AbstractClientPlayerEntity> superGetEntityModel()
    {
        return super.getEntityModel();
    }

    // ############################################################################

    @Shadow
    @Nonnull
    public abstract ResourceLocation getEntityTexture(@Nonnull AbstractClientPlayerEntity player);

    @Inject(method = "getEntityTexture", at = @At("HEAD"), cancellable = true)
    public void beforeGetEntityTexture(AbstractClientPlayerEntity player, CallbackInfoReturnable<ResourceLocation> callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeGetEntityTexture(callbackInfo, this, player);
        }
        this.callReal = false;
    }

    @Inject(method = "getEntityTexture", at = @At("HEAD"))
    public void afterGetEntityTexture(AbstractClientPlayerEntity player, CallbackInfoReturnable<ResourceLocation> callbackInfo)
    {
        PlayerRendererAPI.afterGetEntityTexture(this, player);
    }

    @Override
    public ResourceLocation realGetEntityTexture(AbstractClientPlayerEntity player)
    {
        this.callReal = true;
        return this.getEntityTexture(player);
    }

    // ############################################################################

    @Override
    @Nonnull
    public FontRenderer getFontRendererFromRenderManager()
    {
        return PlayerRendererAPI.getFontRendererFromRenderManager(this);
    }

    @Override
    public FontRenderer superGetFontRendererFromRenderManager()
    {
        return super.getFontRendererFromRenderManager();
    }

    // ############################################################################

    @Override
    @Nonnull
    public EntityRendererManager getRenderManager()
    {
        return PlayerRendererAPI.getRenderManager(this);
    }

    @Override
    public EntityRendererManager superGetRenderManager()
    {
        return super.getRenderManager();
    }

    // ############################################################################

    @Shadow
    @Nonnull
    public abstract Vec3d getRenderOffset(@Nonnull AbstractClientPlayerEntity player, float partialTicks);

    @Inject(method = "getRenderOffset", at = @At("HEAD"), cancellable = true)
    public void beforeGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks, CallbackInfoReturnable<Vec3d> callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeGetRenderOffset(callbackInfo, this, player, partialTicks);
        }
        this.callReal = false;
    }

    @Inject(method = "getRenderOffset", at = @At("HEAD"))
    public void afterGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks, CallbackInfoReturnable<Vec3d> callbackInfo)
    {
        PlayerRendererAPI.afterGetRenderOffset(this, player, partialTicks);
    }

    @Override
    public Vec3d realGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks)
    {
        this.callReal = true;
        return this.getRenderOffset(player, partialTicks);
    }

    @Override
    public Vec3d superGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks)
    {
        return super.getRenderOffset(player, partialTicks);
    }

    // ############################################################################

    @Override
    public float getSwingProgress(@Nonnull AbstractClientPlayerEntity player, float partialTickTime)
    {
        return PlayerRendererAPI.getSwingProgress(this, player, partialTickTime);
    }

    @Override
    public float superGetSwingProgress(AbstractClientPlayerEntity player, float partialTickTime)
    {
        return super.getSwingProgress(player, partialTickTime);
    }

    // ############################################################################

    @Override
    public float handleRotationFloat(@Nonnull AbstractClientPlayerEntity player, float partialTicks)
    {
        return PlayerRendererAPI.handleRotationFloat(this, player, partialTicks);
    }

    @Override
    public float superHandleRotationFloat(AbstractClientPlayerEntity player, float partialTicks)
    {
        return super.handleRotationFloat(player, partialTicks);
    }

    // ############################################################################

    @Shadow
    protected abstract void preRenderCallback(@Nonnull AbstractClientPlayerEntity player, @Nonnull MatrixStack matrixStack, float partialTickTime);

    @Inject(method = "preRenderCallback", at = @At("HEAD"), cancellable = true)
    protected void beforePreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforePreRenderCallback(callbackInfo, this, player, matrixStack, partialTickTime);
        }
        this.callReal = false;
    }

    @Inject(method = "preRenderCallback", at = @At("RETURN"))
    protected void afterPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterPreRenderCallback(this, player, matrixStack, partialTickTime);
    }

    @Override
    public void realPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        this.callReal = true;
        this.preRenderCallback(player, matrixStack, partialTickTime);
    }

    @Override
    public void superPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        super.preRenderCallback(player, matrixStack, partialTickTime);
    }

    // ############################################################################

    @Shadow
    public abstract void render(@Nonnull AbstractClientPlayerEntity player, float entityYaw, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int packedLight);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void beforeRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeRender(callbackInfo, this, player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
        this.callReal = false;
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void afterRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterRender(this, player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public void realRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        this.callReal = true;
        this.render(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public void superRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        super.render(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    // ############################################################################

    @Shadow
    protected abstract void renderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear);

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void beforeRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeRenderItem(callbackInfo, this, matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
        }
        this.callReal = false;
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void afterRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterRenderItem(this, matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
    }

    @Override
    public void realRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
        this.callReal = true;
        this.renderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
    }

    // ############################################################################

    @Shadow
    public abstract void renderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player);

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    public void beforeRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeRenderLeftArm(callbackInfo, this, matrixStack, buffer, combinedLight, player);
        }
        this.callReal = false;
    }

    @Inject(method = "renderLeftArm", at = @At("RETURN"))
    public void afterRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterRenderLeftArm(this, matrixStack, buffer, combinedLight, player);
    }

    @Override
    public void realRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        this.callReal = true;
        this.renderLeftArm(matrixStack, buffer, combinedLight, player);
    }

    // ############################################################################

    @Shadow
    protected abstract void renderName(@Nonnull AbstractClientPlayerEntity player, @Nonnull String displayName, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int packedLightIn);

    @Inject(method = "renderName", at = @At("HEAD"), cancellable = true)
    protected void beforeRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeRenderName(callbackInfo, this, player, displayName, matrixStack, buffer, packedLight);
        }
        this.callReal = false;
    }

    @Inject(method = "renderName", at = @At("RETURN"))
    protected void afterRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterRenderName(this, player, displayName, matrixStack, buffer, packedLight);
    }

    @Override
    public void realRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        this.callReal = true;
        this.renderName(player, displayName, matrixStack, buffer, packedLight);
    }

    @Override
    public void superRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        super.renderName(player, displayName, matrixStack, buffer, packedLight);
    }

    // ############################################################################

    @Shadow
    public abstract void renderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player);

    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    public void beforeRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeRenderRightArm(callbackInfo, this, matrixStack, buffer, combinedLight, player);
        }
        this.callReal = false;
    }

    @Inject(method = "renderRightArm", at = @At("RETURN"))
    public void afterRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterRenderRightArm(this, matrixStack, buffer, combinedLight, player);
    }

    @Override
    public void realRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        this.callReal = true;
        this.renderRightArm(matrixStack, buffer, combinedLight, player);
    }

    // ############################################################################

    @Shadow
    protected abstract void setModelVisibilities(AbstractClientPlayerEntity player);

    @Inject(method = "setModelVisibilities", at = @At("HEAD"), cancellable = true)
    private void beforeSetModelVisibilities(AbstractClientPlayerEntity player, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerRendererAPI.beforeSetModelVisibilities(callbackInfo, this, player);
        }
        this.callReal = false;
    }

    @Inject(method = "setModelVisibilities", at = @At("RETURN"))
    private void afterSetModelVisibilities(AbstractClientPlayerEntity player, CallbackInfo callbackInfo)
    {
        PlayerRendererAPI.afterSetModelVisibilities(this, player);
    }

    @Override
    public void realSetModelVisibilities(AbstractClientPlayerEntity player)
    {
        this.callReal = true;
        this.setModelVisibilities(player);
    }

    // ############################################################################

    @Override
    public boolean shouldRender(@Nonnull AbstractClientPlayerEntity player, @Nonnull ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
        return PlayerRendererAPI.shouldRender(this, player, camera, camX, camY, camZ);
    }

    @Override
    public boolean superShouldRender(AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
        return super.shouldRender(player, camera, camX, camY, camZ);
    }

    // ############################################################################

    @Override
    public List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> getLayerRenderers()
    {
        return this.layerRenderers;
    }
}
