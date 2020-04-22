package api.player.render.asm.interfaces;

import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.List;

public interface IPlayerModelAccessor
{
    void setBipedBodyWear(ModelRenderer bipedBodyWear);

    void setBipedRightArmwear(ModelRenderer bipedRightArmwear);

    void setBipedLeftArmwear(ModelRenderer bipedLeftArmwear);

    void setBipedRightLegwear(ModelRenderer bipedRightLegwear);

    void setBipedLeftLegwear(ModelRenderer bipedLeftLegwear);

    List<ModelRenderer> getModelRenderers();

    ModelRenderer getBipedCape();

    ModelRenderer getBipedDeadmau5Head();
}
