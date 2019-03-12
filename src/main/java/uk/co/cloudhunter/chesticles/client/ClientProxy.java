package uk.co.cloudhunter.chesticles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import uk.co.cloudhunter.chesticles.Chesticles;
import uk.co.cloudhunter.chesticles.CommonProxy;
import uk.co.cloudhunter.chesticles.client.render.TileEntityChesticleRenderer;
import uk.co.cloudhunter.chesticles.client.texture.ChesticleTexture;
import uk.co.cloudhunter.chesticles.tile.TileEntityChesticle;

import java.io.IOException;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {
    @Override
    public void preInit() {
        super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChesticle.class, new TileEntityChesticleRenderer());

    }

    @Override
    public void init() {
        super.init();
        IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        manager.registerReloadListener(this);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        ITextureObject tex = new ChesticleTexture.ChesticleTextureFat(getFatBoyTexture(), "normal");
        ITextureObject tex2 = new ChesticleTexture.ChesticleTextureTall(getTallBoyTexture(), "normal");
        ITextureObject tex3 = new ChesticleTexture.ChesticleTextureTall(getFatBoyChristmasTexture(), "christmas");
        ITextureObject tex4 = new ChesticleTexture.ChesticleTextureTall(getTallBoyChristmasTexture(), "christmas");
        try
        {
            tex.loadTexture(resourceManager);
            tex2.loadTexture(resourceManager);
            tex3.loadTexture(resourceManager);
            tex4.loadTexture(resourceManager);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager manager = mc.getTextureManager();
        manager.loadTexture(getFatBoyTexture(), tex);
        manager.loadTexture(getTallBoyTexture(), tex2);
        manager.loadTexture(getFatBoyChristmasTexture(), tex3);
        manager.loadTexture(getTallBoyChristmasTexture(), tex4);

    }

    @Override
    public ResourceLocation getFatBoyChristmasTexture() {
        return new ResourceLocation(Chesticles.MODID,"textures/entity/chest/christmas_fat.png");
    }

    @Override
    public ResourceLocation getFatBoyTexture() {
        return new ResourceLocation(Chesticles.MODID,"textures/entity/chest/normal_fat.png");
    }

    @Override
    public ResourceLocation getTallBoyChristmasTexture() {
        return new ResourceLocation(Chesticles.MODID,"textures/entity/chest/christmas_tall.png");
    }

    @Override
    public ResourceLocation getTallBoyTexture() {
        return new ResourceLocation(Chesticles.MODID,"textures/entity/chest/normal_tall.png");
    }
}
