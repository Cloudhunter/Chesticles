package uk.co.cloudhunter.chesticles.client.model;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelChesticle extends ModelChest {
    public static class ModelFatBoy extends ModelChesticle {

        public ModelFatBoy() {
            this.chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
            this.chestLid.addBox(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
            this.chestLid.rotationPointX = 0.0F;
            this.chestLid.rotationPointY = 0.0F;
            this.chestLid.rotationPointZ = 0.0F;
            this.chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
            this.chestKnob.addBox(-16.0F, -2.0F, -8F, 1, 4, 2, 0.0F);
            this.chestKnob.rotationPointX = 0.0F;
            this.chestKnob.rotationPointY = 0.0F;
            this.chestKnob.rotationPointZ = 0.0F;
            this.chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64);
            this.chestBelow.addBox(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
            this.chestBelow.rotationPointX = 1.0F;
            this.chestBelow.rotationPointY = 6.0F;
            this.chestBelow.rotationPointZ = 1.0F;
        }
    }

    public static class ModelTallBoy extends ModelChesticle {

        public ModelTallBoy()
        {
            this.chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
            this.chestLid.rotationPointX = 0.0F;
            this.chestLid.rotationPointY = 0.0F;
            this.chestLid.rotationPointZ = 0.0F;
            this.chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
            this.chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
            this.chestKnob.rotationPointX = 0.0F;
            this.chestKnob.rotationPointY = 0.0F;
            this.chestKnob.rotationPointZ = 0.0F;
            this.chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
            this.chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 25, 14, 0.0F);
            this.chestBelow.rotationPointX = 1.0F;
            this.chestBelow.rotationPointY = 6.0F;
            this.chestBelow.rotationPointZ = 1.0F;
        }
    }

    public void renderMainChest() {
        this.chestBelow.render(0.0625F);
    }

    public void renderLid() {
        this.chestLid.render(0.0625F);
        //this.chestKnob.render(0.0625F);
    }

    public void renderKnob() {
        this.chestKnob.render(0.0625F);
    }
}
