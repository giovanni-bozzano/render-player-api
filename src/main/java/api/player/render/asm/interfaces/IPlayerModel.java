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

import api.player.render.model.PlayerModelAPI;
import api.player.render.model.PlayerModelBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;

import java.util.Random;
import java.util.Set;

public interface IPlayerModel<T extends LivingEntity>
{
    PlayerModelAPI<T> getPlayerModelAPI();

    PlayerModelBase<T> getPlayerModelBase(String baseId);

    Set<String> getPlayerModelBaseIds();

    Object dynamic(String key, Object[] parameters);

    ModelRenderer superGetArmForSide(HandSide side);

    HandSide superGetMainHand(T livingEntity);

    void superRender(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    void superSetLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick);

    void superSetModelAttributes(BipedModel<T> model);

    void superSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch);

    void superSetVisible(boolean visible);

    void superTranslateHand(HandSide side, MatrixStack matrixStack);
}
