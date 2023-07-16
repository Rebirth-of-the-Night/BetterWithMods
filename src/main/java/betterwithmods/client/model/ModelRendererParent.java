package betterwithmods.client.model;

import betterwithmods.api.IColor;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelRendererParent extends ModelRenderer {
    public ModelRendererParent(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    public void renderWithColor(float scale, IColor color) {
        if (!this.childModels.isEmpty() && this.childModels.size() > 0) {
            for (int i = 0; i < this.childModels.size(); i++) {
                if (this.childModels.get(i) instanceof ModelRendererChild) {
                    ((ModelRendererChild) this.childModels.get(i)).renderWithColor(scale, color.getColor(i));
                } else {
                    this.childModels.get(i).render(scale);
                }
            }
        }
    }
}
