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
package api.player.render.asm.mixins.net.minecraft.client.renderer.entity.model;

import api.player.render.asm.interfaces.IPlayerModelAccessor;
import api.player.render.asm.interfaces.IPlayerModelReal;
import api.player.render.model.PlayerModelAPI;
import api.player.render.model.PlayerModelBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(PlayerModel.class)
public abstract class MixinPlayerModel<T extends LivingEntity> extends BipedModel<T> implements IPlayerModelReal<T>, IPlayerModelAccessor
{
    @Mutable
    @Shadow
    @Final
    private boolean smallArms;
    @Mutable
    @Shadow
    @Final
    public ModelRenderer bipedBodyWear;
    @Mutable
    @Shadow
    @Final
    public ModelRenderer bipedRightArmwear;
    @Mutable
    @Shadow
    @Final
    public ModelRenderer bipedLeftArmwear;
    @Mutable
    @Shadow
    @Final
    public ModelRenderer bipedRightLegwear;
    @Mutable
    @Shadow
    @Final
    public ModelRenderer bipedLeftLegwear;
    @Shadow
    @Final
    private ModelRenderer bipedCape;
    @Shadow
    @Final
    private ModelRenderer bipedDeadmau5Head;
    @Shadow
    private List<ModelRenderer> modelRenderers;
    private PlayerModelAPI<T> playerModelAPI;
    private boolean callReal;

    public MixinPlayerModel(float modelSize)
    {
        super(modelSize);
    }

    @Override
    public PlayerModelAPI<T> getPlayerModelAPI()
    {
        return this.playerModelAPI;
    }

    @Override
    public PlayerModelBase<T> getPlayerModelBase(String baseId)
    {
        return PlayerModelAPI.getPlayerModelBase(this, baseId);
    }

    @Override
    public Set<String> getPlayerModelBaseIds()
    {
        return PlayerModelAPI.getPlayerModelBaseIds(this);
    }

    @Override
    public Object dynamic(String key, Object[] parameters)
    {
        return PlayerModelAPI.dynamic(this, key, parameters);
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/model/PlayerModel;smallArms:Z"))
    public void beforeInit(PlayerModel<T> redirectedPlayerModel, boolean redirectedSmallArms, float modelSize, boolean smallArms)
    {
        this.playerModelAPI = PlayerModelAPI.create(this, modelSize, 0F, 64, 64, smallArms);
        PlayerModelAPI.beforeLocalConstructing(this, modelSize, 0F, 64, 64, smallArms);
        this.smallArms = redirectedSmallArms;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void afterInit(float modelSize, boolean smallArms, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterLocalConstructing(this, modelSize, 0F, 64, 64, smallArms);
    }

    // ############################################################################

    @Shadow
    public abstract void accept(@Nonnull ModelRenderer renderer);

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    public void beforeAccept(ModelRenderer renderer, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeAccept(callbackInfo, this, renderer);
        }
        this.callReal = false;
    }

    @Inject(method = "accept", at = @At("RETURN"))
    public void afterAccept(ModelRenderer renderer, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterAccept(this, renderer);
    }

    @Override
    public void realAccept(ModelRenderer renderer)
    {
        this.callReal = true;
        this.accept(renderer);
    }

    // ############################################################################

    @Override
    @Nonnull
    public ModelRenderer getArmForSide(@Nonnull HandSide side)
    {
        return PlayerModelAPI.getArmForSide(this, side);
    }

    @Override
    public ModelRenderer superGetArmForSide(HandSide side)
    {
        return super.getArmForSide(side);
    }

    // ############################################################################

    @Override
    @Nonnull
    public HandSide getMainHand(@Nonnull T livingEntity)
    {
        return PlayerModelAPI.getMainHand(this, livingEntity);
    }

    @Override
    public HandSide superGetMainHand(T livingEntity)
    {
        return super.getMainHand(livingEntity);
    }

    // ############################################################################

    @Shadow
    public abstract ModelRenderer getRandomModelRenderer(Random random);

    @Inject(method = "getRandomModelRenderer", at = @At("HEAD"), cancellable = true)
    public void beforeGetRandomModelRenderer(Random random, CallbackInfoReturnable<ModelRenderer> callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeGetRandomModelRenderer(callbackInfo, this, random);
        }
        this.callReal = false;
    }

    @Inject(method = "getRandomModelRenderer", at = @At("RETURN"))
    public void afterGetRandomModelRenderer(Random random, CallbackInfoReturnable<ModelRenderer> callbackInfo)
    {
        PlayerModelAPI.afterGetRandomModelRenderer(this, random);
    }

    @Override
    public ModelRenderer realGetRandomModelRenderer(Random random)
    {
        this.callReal = true;
        return this.getRandomModelRenderer(random);
    }

    // ############################################################################

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        PlayerModelAPI.render(this, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void superRender(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    // ############################################################################

    @Shadow
    public abstract void renderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay);

    @Inject(method = "renderCape", at = @At("HEAD"), cancellable = true)
    public void beforeRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeRenderCape(callbackInfo, this, matrixStack, buffer, packedLight, packedOverlay);
        }
        this.callReal = false;
    }

    @Inject(method = "renderCape", at = @At("RETURN"))
    public void afterRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterRenderCape(this, matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void realRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        this.callReal = true;
        this.renderCape(matrixStack, buffer, packedLight, packedOverlay);
    }

    // ############################################################################

    @Shadow
    public abstract void renderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay);

    @Inject(method = "renderEars", at = @At("HEAD"), cancellable = true)
    public void beforeRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeRenderEars(callbackInfo, this, matrixStack, buffer, packedLight, packedOverlay);
        }
        this.callReal = false;
    }

    @Inject(method = "renderEars", at = @At("RETURN"))
    public void afterRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterRenderEars(this, matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void realRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        this.callReal = true;
        this.renderEars(matrixStack, buffer, packedLight, packedOverlay);
    }

    // ############################################################################

    @Override
    public void setLivingAnimations(@Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        PlayerModelAPI.setLivingAnimations(this, entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void superSetLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    }

    // ############################################################################

    @Override
    public void setModelAttributes(@Nonnull BipedModel<T> model)
    {
        PlayerModelAPI.setModelAttributes(this, model);
    }

    @Override
    public void superSetModelAttributes(BipedModel<T> model)
    {
        super.setModelAttributes(model);
    }

    // ############################################################################

    @Shadow
    public abstract void setRotationAngles(@Nonnull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    public void beforeSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeSetRotationAngles(callbackInfo, this, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }
        this.callReal = false;
    }

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void afterSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterSetRotationAngles(this, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    @Override
    public void realSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        this.callReal = true;
        this.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    @Override
    public void superSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    // ############################################################################

    @Shadow
    public abstract void setVisible(boolean visible);

    @Inject(method = "setVisible", at = @At("HEAD"), cancellable = true)
    public void beforeSetVisible(boolean visible, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeSetVisible(callbackInfo, this, visible);
        }
        this.callReal = false;
    }

    @Inject(method = "setVisible", at = @At("RETURN"))
    public void afterSetVisible(boolean visible, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterSetVisible(this, visible);
    }

    @Override
    public void realSetVisible(boolean visible)
    {
        this.callReal = true;
        this.setVisible(visible);
    }

    @Override
    public void superSetVisible(boolean visible)
    {
        super.setVisible(visible);
    }

    // ############################################################################

    @Shadow
    public abstract void translateHand(@Nonnull HandSide side, @Nonnull MatrixStack matrixStack);

    @Inject(method = "translateHand", at = @At("HEAD"), cancellable = true)
    public void beforeTranslateHand(HandSide side, MatrixStack matrixStack, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            PlayerModelAPI.beforeTranslateHand(callbackInfo, this, side, matrixStack);
        }
        this.callReal = false;
    }

    @Inject(method = "translateHand", at = @At("RETURN"))
    public void afterTranslateHand(HandSide side, MatrixStack matrixStack, CallbackInfo callbackInfo)
    {
        PlayerModelAPI.afterTranslateHand(this, side, matrixStack);
    }

    @Override
    public void realTranslateHand(HandSide side, MatrixStack matrixStack)
    {
        this.callReal = true;
        this.translateHand(side, matrixStack);
    }

    @Override
    public void superTranslateHand(HandSide side, MatrixStack scale)
    {
        super.translateHand(side, scale);
    }

    // ############################################################################

    @Override
    public void setBipedBodyWear(ModelRenderer bipedBodyWear)
    {
        this.bipedBodyWear = bipedBodyWear;
    }

    @Override
    public void setBipedRightArmwear(ModelRenderer bipedRightArmwear)
    {
        this.bipedRightArmwear = bipedRightArmwear;
    }

    @Override
    public void setBipedLeftArmwear(ModelRenderer bipedLeftArmwear)
    {
        this.bipedLeftArmwear = bipedLeftArmwear;
    }

    @Override
    public void setBipedRightLegwear(ModelRenderer bipedRightLegwear)
    {
        this.bipedRightLegwear = bipedRightLegwear;
    }

    @Override
    public void setBipedLeftLegwear(ModelRenderer bipedLeftLegwear)
    {
        this.bipedLeftLegwear = bipedLeftLegwear;
    }

    @Override
    public ModelRenderer getBipedCape()
    {
        return this.bipedCape;
    }

    @Override
    public ModelRenderer getBipedDeadmau5Head()
    {
        return this.bipedDeadmau5Head;
    }

    @Override
    public List<ModelRenderer> getModelRenderers()
    {
        return this.modelRenderers;
    }
}
