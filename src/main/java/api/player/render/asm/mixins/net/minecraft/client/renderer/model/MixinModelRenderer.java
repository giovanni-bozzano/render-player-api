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
package api.player.render.asm.mixins.net.minecraft.client.renderer.model;

import api.player.render.asm.interfaces.IModelRenderer;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelRenderer.class)
public abstract class MixinModelRenderer implements IModelRenderer
{
    @Shadow
    private int textureOffsetX;
    @Shadow
    private int textureOffsetY;
    @Shadow
    @Final
    private ObjectList<ModelRenderer.ModelBox> cubeList;
    @Shadow
    @Final
    private ObjectList<ModelRenderer> childModels;

    @Override
    public int getTextureOffsetX()
    {
        return this.textureOffsetX;
    }

    @Override
    public int getTextureOffsetY()
    {
        return this.textureOffsetY;
    }

    @Override
    public ObjectList<ModelRenderer.ModelBox> getCubeList()
    {
        return this.cubeList;
    }

    @Override
    public ObjectList<ModelRenderer> getChildModels()
    {
        return this.childModels;
    }
}
