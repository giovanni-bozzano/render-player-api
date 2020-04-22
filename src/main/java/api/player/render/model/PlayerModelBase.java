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
package api.player.render.model;

import api.player.render.asm.interfaces.IPlayerModel;
import api.player.render.asm.interfaces.IPlayerModelReal;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;

import java.util.Random;

public abstract class PlayerModelBase<T extends LivingEntity>
{
    private final PlayerModelAPI<T> internalPlayerModelAPI;
    protected final IPlayerModel<T> iPlayerModel;
    protected final BipedModel<T> bipedModel;
    protected final PlayerModel<T> playerModel;
    protected final PlayerArmorModel<T> playerArmorModel;

    public PlayerModelBase(PlayerModelAPI<T> playerModelAPI)
    {
        this.internalPlayerModelAPI = playerModelAPI;
        this.iPlayerModel = playerModelAPI.iPlayerModel;
        this.bipedModel = (BipedModel<T>) playerModelAPI.iPlayerModel;
        this.playerModel = this.bipedModel instanceof PlayerModel ? (PlayerModel<T>) this.bipedModel : null;
        this.playerArmorModel = this.bipedModel instanceof PlayerArmorModel ? (PlayerArmorModel<T>) this.bipedModel : null;
    }

    public void beforeBaseAttach(boolean onTheFly)
    {
    }

    public void afterBaseAttach(boolean onTheFly)
    {
    }

    public void beforeLocalConstructing(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
    {
    }

    public void afterLocalConstructing(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
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
        return this.internalPlayerModelAPI.dynamicOverwritten(key, parameters, this);
    }

    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    // ############################################################################

    public void beforeAccept(ModelRenderer renderer)
    {
    }

    public void accept(ModelRenderer renderer)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenAccept(this);

        if (overwritten == null) {
            ((IPlayerModelReal<T>) this.iPlayerModel).realAccept(renderer);
        } else if (overwritten != this) {
            overwritten.accept(renderer);
        }
    }

    public void afterAccept(ModelRenderer renderer)
    {
    }

    // ############################################################################

    public void beforeGetArmForSide(HandSide side)
    {
    }

    public ModelRenderer getArmForSide(HandSide side)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenGetArmForSide(this);

        if (overwritten == null) {
            return this.iPlayerModel.superGetArmForSide(side);
        } else if (overwritten != this) {
            return overwritten.getArmForSide(side);
        } else {
            return null;
        }
    }

    public void afterGetArmForSide(HandSide side)
    {
    }

    // ############################################################################

    public void beforeGetMainHand(T livingEntity)
    {
    }

    public HandSide getMainHand(T livingEntity)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenGetMainHand(this);

        if (overwritten == null) {
            return this.iPlayerModel.superGetMainHand(livingEntity);
        } else if (overwritten != this) {
            return overwritten.getMainHand(livingEntity);
        } else {
            return null;
        }
    }

    public void afterGetMainHand(T livingEntity)
    {
    }

    // ############################################################################

    public void beforeGetRandomModelRenderer(Random random)
    {
    }

    public ModelRenderer getRandomModelRenderer(Random random)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenGetRandomModelRenderer(this);

        if (overwritten == null) {
            return ((IPlayerModelReal<T>) this.iPlayerModel).realGetRandomModelRenderer(random);
        } else if (overwritten != this) {
            return overwritten.getRandomModelRenderer(random);
        } else {
            return null;
        }
    }

    public void afterGetRandomModelRenderer(Random random)
    {
    }

    // ############################################################################

    public void beforeRender(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
    }

    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenRender(this);

        if (overwritten == null) {
            this.iPlayerModel.superRender(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } else if (overwritten != this) {
            overwritten.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public void afterRender(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
    }

    // ############################################################################

    public void beforeRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
    }

    public void renderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenRenderCape(this);

        if (overwritten == null) {
            ((IPlayerModelReal<T>) this.iPlayerModel).realRenderCape(matrixStack, buffer, packedLight, packedOverlay);
        } else if (overwritten != this) {
            overwritten.renderCape(matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    public void afterRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
    }

    // ############################################################################

    public void beforeRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
    }

    public void renderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenRenderEars(this);

        if (overwritten == null) {
            ((IPlayerModelReal<T>) this.iPlayerModel).realRenderEars(matrixStack, buffer, packedLight, packedOverlay);
        } else if (overwritten != this) {
            overwritten.renderEars(matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    public void afterRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
    }

    // ############################################################################

    public void beforeSetLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
    }

    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenSetLivingAnimations(this);

        if (overwritten == null) {
            this.iPlayerModel.superSetLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
        } else if (overwritten != this) {
            overwritten.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
        }
    }

    public void afterSetLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
    }

    // ############################################################################

    public void beforeSetModelAttributes(BipedModel<T> model)
    {
    }

    public void setModelAttributes(BipedModel<T> model)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenSetModelAttributes(this);

        if (overwritten == null) {
            this.iPlayerModel.superSetModelAttributes(model);
        } else if (overwritten != this) {
            overwritten.setModelAttributes(model);
        }
    }

    public void afterSetModelAttributes(BipedModel<T> model)
    {
    }

    // ############################################################################

    public void beforeSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
    }

    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenSetRotationAngles(this);

        if (overwritten == null) {
            ((IPlayerModelReal<T>) this.iPlayerModel).realSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        } else if (overwritten != this) {
            overwritten.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }
    }

    public void afterSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
    }

    // ############################################################################

    public void beforeSetVisible(boolean visible)
    {
    }

    public void setVisible(boolean visible)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenSetVisible(this);

        if (overwritten == null) {
            ((IPlayerModelReal<T>) this.iPlayerModel).realSetVisible(visible);
        } else if (overwritten != this) {
            overwritten.setVisible(visible);
        }
    }

    public void afterSetVisible(boolean visible)
    {
    }

    // ############################################################################

    public void beforeTranslateHand(HandSide side, MatrixStack matrixStack)
    {
    }

    public void translateHand(HandSide side, MatrixStack matrixStack)
    {
        PlayerModelBase<T> overwritten = this.internalPlayerModelAPI.getOverwrittenTranslateHand(this);

        if (overwritten == null) {
            ((IPlayerModelReal<T>) this.iPlayerModel).realTranslateHand(side, matrixStack);
        } else if (overwritten != this) {
            overwritten.translateHand(side, matrixStack);
        }
    }

    public void afterTranslateHand(HandSide side, MatrixStack matrixStack)
    {
    }
}
