package uk.co.cloudhunter.chesticles.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;
import uk.co.cloudhunter.chesticles.Chesticles;

public class ItemChesticle extends ItemBlock {

    private ItemBlock unnaturalItemBlock;

    public ItemChesticle(Block block) {
        super(block);
        unnaturalItemBlock = new ItemBlock(Chesticles.Blocks.UNNATURAL_CHEST);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block targetBlock = iblockstate.getBlock();

        if (!targetBlock.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }
        EnumFacing horizontalFacing = EnumFacing.getHorizontal(MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();

        for (EnumFacing side: EnumFacing.values())
        {
            BlockPos sidePos = pos.offset(side);
            IBlockState otherState = worldIn.getBlockState(sidePos);
            if (otherState.getBlock() == block)
            {
                EnumFacing otherFacing = otherState.getValue(BlockChest.FACING);
                if (otherFacing == horizontalFacing)
                {
                    TileEntityChest te = (TileEntityChest) worldIn.getTileEntity(sidePos);
                    if (te.adjacentChestXNeg == null && te.adjacentChestZNeg == null && te.adjacentChestXPos == null && te.adjacentChestZPos == null)
                    {
                        // we are valid, do some checks to make sure it isn't a case that will be handled sanely by Vanilla anyway
                        if (horizontalFacing.rotateY() == side || horizontalFacing.rotateYCCW() == side)
                        {
                            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                        } else {
                            return unnaturalItemBlock.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                        }
                    }
                }
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
