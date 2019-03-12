package uk.co.cloudhunter.chesticles.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;
import uk.co.cloudhunter.chesticles.tile.TileEntityChesticle;

import javax.annotation.Nullable;

public class BlockChesticle extends Block {

    protected static final AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0D, 0.9375D, 0.875D, 0.9375D);
    protected static final AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 1.0D);
    protected static final AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
    protected static final AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 1.0D, 0.875D, 0.9375D);
    protected static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockChesticle() {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setHardness(2.5F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntity te = source.getTileEntity(pos);
        if (!(te instanceof TileEntityChesticle))
        {
            return super.getBoundingBox(state, source, pos);
        }

        TileEntityChesticle chest = (TileEntityChesticle) te;
        EnumFacing facing = state.getValue(FACING);

        if(!chest.isPrimaryChest && chest.getPrimaryChest() == null)
            return super.getBoundingBox(state, source, pos);

        if (chest.getType() == TileEntityChesticle.RenderType.UP)
        {
            return super.getBoundingBox(state, source, pos); // TODO: proper up
        }
        else if (facing == EnumFacing.NORTH)
        {
            return chest.isPrimaryChest ? NORTH_CHEST_AABB : SOUTH_CHEST_AABB;
        }
        else if (facing == EnumFacing.SOUTH)
        {
            return chest.isPrimaryChest ? SOUTH_CHEST_AABB : NORTH_CHEST_AABB;
        }
        else if (facing == EnumFacing.WEST)
        {
            return chest.isPrimaryChest ? WEST_CHEST_AABB : EAST_CHEST_AABB;
        }
        else
        {
            return chest.isPrimaryChest ? EAST_CHEST_AABB : WEST_CHEST_AABB;
        }

        //TODO: return correct
        //return super.getBoundingBox(state, source, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
        // TODO: handle up and down for our own stuff, will also need altering ItemChesticle to account for this placing
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (worldIn.isRemote)
            return;
        EnumFacing horizontalFacing = state.getValue(FACING);
        for (EnumFacing side: EnumFacing.values())
        {
            BlockPos sidePos = pos.offset(side);
            IBlockState otherState = worldIn.getBlockState(sidePos);
            if (otherState.getBlock() == Blocks.CHEST)
            {
                EnumFacing otherFacing = otherState.getValue(BlockChest.FACING);
                if (otherFacing == horizontalFacing)
                {
                    TileEntityChest te = (TileEntityChest) worldIn.getTileEntity(sidePos);
                    if (te.adjacentChestXNeg == null && te.adjacentChestXPos == null && te.adjacentChestZNeg == null && te.adjacentChestZPos == null)
                    {
                        if (!(horizontalFacing.rotateY() == side || horizontalFacing.rotateYCCW() == side))
                        {
                            ILockableContainer container = Blocks.CHEST.getContainer(worldIn, sidePos, false);
                            if (container == null) // meh, for now
                                return;
                            NonNullList<ItemStack> tempInventory = NonNullList.withSize(container.getSizeInventory(), ItemStack.EMPTY);
                            for (int slot = 0; slot < container.getSizeInventory(); slot++) {
                                tempInventory.set(slot, container.getStackInSlot(slot));
                                container.setInventorySlotContents(slot, ItemStack.EMPTY);
                            }
                            worldIn.setBlockState(sidePos, state);
                            TileEntityChesticle primaryChest;
                            TileEntityChesticle secondaryChest;
                            TileEntityChesticle oldNewChest = (TileEntityChesticle) worldIn.getTileEntity(sidePos);
                            TileEntityChesticle newChest = (TileEntityChesticle) worldIn.getTileEntity(pos);
                            int slotOffset = 27;
                            if (horizontalFacing == side || side == EnumFacing.DOWN) {
                                primaryChest = newChest;
                                secondaryChest = oldNewChest;
                            } else {
                                slotOffset = 0;
                                primaryChest = oldNewChest;
                                secondaryChest = newChest;
                            }
                            // back or top will be primary, so new chest is going to be primary

                            primaryChest.setPrimary(secondaryChest);
                            primaryChest.setType((side == EnumFacing.DOWN || side == EnumFacing.UP) ? TileEntityChesticle.RenderType.UP : TileEntityChesticle.RenderType.FAT);
                            for(int slot = 0; slot < tempInventory.size(); slot++)
                            {
                                primaryChest.chestContents.set(slot + slotOffset, tempInventory.get(slot));
                            }

                            primaryChest.markDirty();
                            secondaryChest.markDirty();

                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

            if (ilockablecontainer != null)
            {
                playerIn.displayGUIChest(ilockablecontainer);
            }

            return true;
        }
    }

    @Nullable
    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
    {
        return this.getContainer(worldIn, pos, false);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }

    @Nullable
    public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityChesticle))
        {
            return null;
        }
        else
        {
            TileEntityChesticle chest = (TileEntityChesticle) tileentity;
            ILockableContainer ilockablecontainer = chest.isPrimaryChest ? chest : chest.getPrimaryChest();

            if (!allowBlocking && this.isBlocked(worldIn, pos))
            {
                return null;
            }
            else
            {
                return ilockablecontainer;
            }
        }
    }

    private boolean isBlocked(World worldIn, BlockPos pos)
    {
        //TODO: Our own blocking logic
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    private boolean isBelowSolidBlock(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos.up(), EnumFacing.DOWN);
    }

    private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos)
    {
        for (Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))))
        {
            EntityOcelot entityocelot = (EntityOcelot)entity;

            if (entityocelot.isSitting())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(FACING)).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityChesticle();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
