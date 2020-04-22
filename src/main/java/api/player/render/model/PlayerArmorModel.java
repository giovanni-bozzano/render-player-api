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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;

import javax.annotation.Nonnull;
import java.util.Set;

public class PlayerArmorModel<T extends LivingEntity> extends BipedModel<T> implements IPlayerModel<T>
{
    private final PlayerModelAPI<T> playerModelAPI;

    public PlayerArmorModel(float paramFloat)
    {
        this(paramFloat, 0.0F, 64, 32);
    }

    public PlayerArmorModel(float modelSize, float paramFloat2, int textureWidth, int textureHeight)
    {
        super(modelSize, paramFloat2, textureWidth, textureHeight);
        this.playerModelAPI = PlayerModelAPI.create(this, modelSize, 0F, 64, 64, false);
        PlayerModelAPI.beforeLocalConstructing(this, modelSize, 0F, 64, 64, false);
        PlayerModelAPI.afterLocalConstructing(this, modelSize, 0F, 64, 64, false);
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

    // ############################################################################

    @Override
    @Nonnull
    public ModelRenderer getArmForSide(@Nonnull HandSide side)
    {
        return PlayerModelAPI.getArmForSide(this, side);
    }

    @Override
    public ModelRenderer superGetArmForSide(HandSide paramEnumHandSide)
    {
        return super.getArmForSide(paramEnumHandSide);
    }

    // ############################################################################

    @Override
    @Nonnull
    public HandSide getMainHand(@Nonnull T livingEntity)
    {
        return PlayerModelAPI.getMainHand(this, livingEntity);
    }

    @Override
    public HandSide superGetMainHand(T paramEntity)
    {
        return super.getMainHand(paramEntity);
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

    @Override
    public void translateHand(@Nonnull HandSide side, @Nonnull MatrixStack scale) // translateHand
    {
        PlayerModelAPI.translateHand(this, side, scale);
    }

    @Override
    public void superTranslateHand(HandSide paramEnumHandSide, MatrixStack scale)
    {
        super.translateHand(paramEnumHandSide, scale);
    }

    // ############################################################################

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) // render
    {
        PlayerModelAPI.render(this, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void superRender(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
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
    public void setRotationAngles(@Nonnull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) // setRotationAngles
    {
        PlayerModelAPI.setRotationAngles(this, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    @Override
    public void superSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    // ############################################################################

    @Override
    public void setVisible(boolean visible)
    {
        PlayerModelAPI.setVisible(this, visible);
    }

    @Override
    public void superSetVisible(boolean visible)
    {
        super.setVisible(visible);
    }
}