package uk.co.cloudhunter.chesticles.client.texture;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public abstract class ChesticleTexture extends SimpleTexture
{
    protected String chestType;

    public ChesticleTexture(ResourceLocation textureResourceLocation, String chestType)
    {
        super(textureResourceLocation);
        this.chestType = chestType;
    }

    public abstract void loadTexture(IResourceManager resourceManager) throws IOException;

    public static class ChesticleTextureFat extends ChesticleTexture {
        public ChesticleTextureFat(ResourceLocation textureResourceLocation, String chestType) {
            super(textureResourceLocation, chestType);
        }

        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException
        {
            ResourceLocation regularResourceLocation = new ResourceLocation("minecraft","textures/entity/chest/" + chestType + "_double.png");
            this.deleteGlTexture();
            IResource iresource = null;

            try
            {
                iresource = resourceManager.getResource(regularResourceLocation);
                BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
                boolean flag = false;
                boolean flag1 = false;

                if (iresource.hasMetadata())
                {
                    try
                    {
                        TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                        if (texturemetadatasection != null)
                        {
                            flag = texturemetadatasection.getTextureBlur();
                            flag1 = texturemetadatasection.getTextureClamp();
                        }
                    }
                    catch (RuntimeException runtimeexception)
                    {
                    }
                }

                // TODO: Translate values based on size of texture

                Graphics2D g = bufferedimage.createGraphics();

                copy(g, bufferedimage, 25, 15, 3, 15, 8, 3); // knob holder top
                copy(g, bufferedimage, 25, 33, 3, 33, 8, 3); // knob holder bottom

                // Main
                copy(g, bufferedimage, 58, 33, 14, 33, 30, 10);
                copy(g, bufferedimage, 58, 14, 14, 14, 30, 5);

                // Knob

                // TODO: Fix the knob to look right
                copy(g, bufferedimage, 1, 1, 0, 2, 2, 4);
                copy(g, bufferedimage, 0, 1, 2, 2, 1, 4);
                copy(g, bufferedimage, 3, 1, 3, 2, 3, 4);
                copy(g, bufferedimage, 1, 0, 2, 0, 1, 1);
                copy(g, bufferedimage, 2, 0, 3, 1, 1, 1);
                copy(g, bufferedimage, 3, 0, 2, 0, 1, 1);
                copy(g, bufferedimage, 4, 0, 3, 1, 1, 1);

                g.dispose();

                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
            }
            finally
            {
                IOUtils.closeQuietly(iresource);
            }
        }
    }

    public static void copy(Graphics2D g, BufferedImage image, int sourceX, int sourceY, int destX, int destY, int width, int height)
    {
        g.drawImage(image, destX, destY, destX + width, destY + height, sourceX, sourceY, sourceX + width, sourceY + height, null);
    }

    public static class ChesticleTextureTall extends ChesticleTexture {

        public ChesticleTextureTall(ResourceLocation textureResourceLocation, String chestType) {
            super(textureResourceLocation, chestType);
        }

        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException {
            ResourceLocation singleResourceLocation = new ResourceLocation("minecraft", "textures/entity/chest/" + chestType + ".png");
            this.deleteGlTexture();
            IResource iresource = null;

            try
            {
                iresource = resourceManager.getResource(singleResourceLocation);
                BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
                boolean flag = false;
                boolean flag1 = false;

                if (iresource.hasMetadata())
                {
                    try
                    {
                        TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                        if (texturemetadatasection != null)
                        {
                            flag = texturemetadatasection.getTextureBlur();
                            flag1 = texturemetadatasection.getTextureClamp();
                        }
                    }
                    catch (RuntimeException runtimeexception)
                    {
                    }
                }

                // TODO: Translate values based on size of texture

                Graphics2D g = bufferedimage.createGraphics();

                copy(g, bufferedimage, 0, 41, 0, 57, 56, 2);

                copy(g, bufferedimage, 0, 36, 0, 41, 56, 5);
                copy(g, bufferedimage, 0, 36, 0, 46, 56, 5);
                copy(g, bufferedimage, 0, 36, 0, 51, 56, 6);

                g.dispose();

                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
            }
            finally
            {
                IOUtils.closeQuietly(iresource);
            }
        }
    }

}
