package api.player.render.asm.interfaces;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;

import java.util.List;

public interface IPlayerRendererAccessor
{
    List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> getLayerRenderers();
}
