package uk.co.cloudhunter.chesticles.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import uk.co.cloudhunter.chesticles.Chesticles;
import uk.co.cloudhunter.chesticles.block.BlockChesticle;
import uk.co.cloudhunter.chesticles.client.model.ModelChesticle;
import uk.co.cloudhunter.chesticles.tile.TileEntityChesticle;

import java.util.Calendar;

import static net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.*;

public class TileEntityChesticleRenderer extends TileEntitySpecialRenderer<TileEntityChesticle> {
    private static final ResourceLocation TEXTURE_FAT_CHRISTMAS_DOUBLE = Chesticles.proxy.getFatBoyChristmasTexture();
    private static final ResourceLocation TEXTURE_FAT_NORMAL_DOUBLE = Chesticles.proxy.getFatBoyTexture();
    private static final ResourceLocation TEXTURE_TALL_CHRISTMAS_DOUBLE = Chesticles.proxy.getTallBoyChristmasTexture();
    private static final ResourceLocation TEXTURE_TALL_NORMAL_DOUBLE = Chesticles.proxy.getTallBoyTexture();
    private final ModelChesticle modelFatBoy = new ModelChesticle.ModelFatBoy();
    private final ModelChesticle modelTallBoy = new ModelChesticle.ModelTallBoy();

    private boolean isChristmas;

    public TileEntityChesticleRenderer() {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
    }

    public void render(TileEntityChesticle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!te.isPrimaryChest)
        {
            if (destroyStage >= 0) {
                BlockPos blockpos = te.getPrimaryChest().getPos();
                render(te.getPrimaryChest(), (double) blockpos.getX() - staticPlayerX, (double) blockpos.getY() - staticPlayerY, (double) blockpos.getZ() - staticPlayerZ, partialTicks, destroyStage, alpha);
            }
            return;
        }
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        int i;

        EnumFacing facing;

        if (te.hasWorld()) {
            Block block = te.getBlockType();
            i = te.getBlockMetadata();
            facing = getWorld().getBlockState(te.getPos()).getValue(BlockChesticle.FACING);
            //System.out.println(facing + " " + i);

            if (block instanceof BlockChest && i == 0) {
                ((BlockChest) block).checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
                i = te.getBlockMetadata();
            }

        } else {
            facing = EnumFacing.UP;
            i = 0;
        }

        ModelChesticle modelchest = modelTallBoy;
        ResourceLocation texture = TEXTURE_TALL_NORMAL_DOUBLE;
        if (this.isChristmas) {
            texture = TEXTURE_TALL_CHRISTMAS_DOUBLE;
        }
        
        TileEntityChesticle.RenderType type = te.getType();

        if (type == TileEntityChesticle.RenderType.FAT)
        {
            modelchest = this.modelFatBoy;
            texture = TEXTURE_FAT_NORMAL_DOUBLE;
            if (this.isChristmas) {
                texture = TEXTURE_FAT_CHRISTMAS_DOUBLE;
            }
        }

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(8.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(texture);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        }

        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int j = 0;

        if (facing == EnumFacing.NORTH) {
            j = 180;
            if (type == TileEntityChesticle.RenderType.FAT) {
                j = 90;
                GlStateManager.translate(0.0F, 0.0F, 1.0F);
            }
        } else if (facing == EnumFacing.SOUTH) {
            if (type == TileEntityChesticle.RenderType.FAT) {
                j = -90;
                GlStateManager.translate(0.0F, 0.0F, -1.0F);
            }
        } else if (facing == EnumFacing.WEST) {
            j = 90;
            if (type == TileEntityChesticle.RenderType.FAT) {
                j = 0;
                GlStateManager.translate(-1.0F, 0.0F, 0.0F);
            }
        } else if (facing == EnumFacing.EAST) {
            j = 270;
            if (type == TileEntityChesticle.RenderType.FAT) {
                j = -180;
                GlStateManager.translate(1.0F, 0.0F, 0.0F);
            }
        }

        GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

        f = 1.0F - f;
        f = 1.0F - f * f * f;

        float rotateAngleMine = -(f * 90F);

        modelchest.renderMainChest();

        GlStateManager.pushMatrix();

        openLidBack(te, rotateAngleMine);

        GlStateManager.translate(0.0625, 0.4375f, 0.9375);

        modelchest.renderLid();
        if (type == TileEntityChesticle.RenderType.FAT)
            GlStateManager.translate(1.0F - .064, 0, 0);
        else
            GlStateManager.translate(0.5 - .064, 0, 0);

        modelchest.renderKnob();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    public void openLidLeft(float angle)
    {
        GlStateManager.translate(0, 0.4, 0.95);
        GlStateManager.rotate(angle, 1, 0, 0);
        GlStateManager.translate(0, -0.4, -0.95);
    }

    public void openLidBack(TileEntityChesticle te, float angle)
    {
        float x = 0.0F, y = 0.4F, z = 0.95F, xAngle = 1, zAngle = 0;
        if (te.getType() == TileEntityChesticle.RenderType.FAT)
        {
            x = 1.95F;
            y = 0.4F;
            z = 0F;
            zAngle = -1;
            xAngle = 0;
        }
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, xAngle, 0, zAngle);
        GlStateManager.translate(-x, -y, -z);
    }
}