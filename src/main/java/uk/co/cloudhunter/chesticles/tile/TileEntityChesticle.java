package uk.co.cloudhunter.chesticles.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import uk.co.cloudhunter.chesticles.block.BlockChesticle;

import javax.annotation.Nullable;

public class TileEntityChesticle extends TileEntityLockableLoot implements ITickable {
    boolean isDoubleChest = false;
    public boolean isPrimaryChest = true;

    private RenderType type = RenderType.UPSIDE_DOWN; // default so a broken TE works

    int ticksSinceSync = 0;

    public NonNullList<ItemStack> chestContents;

    public float lidAngle = 0.0F;

    public float prevLidAngle = 0.0F;

    public int numPlayersUsing;

    private EnumFacing facing;

    private boolean isUpsideDown;

    private String customName;

    private TileEntityChesticle primaryChest;
    private TileEntityChesticle secondaryChest;
    private BlockPos primaryChestTempPos;
    private BlockPos secondaryChestTempPos;
    private boolean first = true;

    public TileEntityChesticle()
    {
        chestContents = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY); // default as single, will change to double if needed
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        if (!isPrimaryChest) return getPrimaryChest().getItems();
        return chestContents;
    }

    public TileEntityChesticle getPrimaryChest()
    {
        if (!isPrimaryChest) {
            return primaryChest;
        }
        return null;
    }

    public TileEntityChesticle getSecondaryChest()
    {
        if (isPrimaryChest) {
            return secondaryChest;
        }
        return null;
    }

    @Override
    public int getSizeInventory() { return isDoubleChest ? 54 : 27;
    }

    @Override
    public boolean isEmpty() {
        if (!isPrimaryChest) return getPrimaryChest().isEmpty();
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        if (!isPrimaryChest) return getPrimaryChest().getInventoryStackLimit();
        return 64;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, -1, -1), pos.add(2, 2, 2));
    }

    @Override
    public void update() {
        if (first)
        {
            if (secondaryChestTempPos != null && isPrimaryChest)
                secondaryChest = (TileEntityChesticle) world.getTileEntity(secondaryChestTempPos);
            else if (primaryChestTempPos != null && !isPrimaryChest)
                primaryChest = (TileEntityChesticle) world.getTileEntity(primaryChestTempPos);
            primaryChestTempPos = secondaryChestTempPos = null;
            first = false;
        }

        if (isDoubleChest)
        {
            ticksSinceSync++;
            int x = getPos().getX(), y = getPos().getY(), z = getPos().getZ();
            if (isPrimaryChest && (this.ticksSinceSync + x + y + z) % 20 == 0) {
                if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + x + y + z) % 200 == 0)
                {
                    this.numPlayersUsing = 0;

                    for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)x - 5.0F), (double)((float)y - 5.0F), (double)((float)z - 5.0F), (double)((float)(x + 1) + 5.0F), (double)((float)(y + 1) + 5.0F), (double)((float)(z + 1) + 5.0F))))
                    {
                        if (entityplayer.openContainer instanceof ContainerChest)
                        {
                            IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();

                            if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this))
                            {
                                ++this.numPlayersUsing;
                            }
                        }
                    }
                }
                checkDoubleStillValid();
            }
        }

        this.prevLidAngle = this.lidAngle;

        int x = pos.getX(), y = pos.getY(), z = pos.getZ();

        float f1 = 0.1F;

        double soundX = (double)x + 0.5D;
        double soundZ = (double)z + 0.5D;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
        {

            /*if (this.adjacentChestZPos != null)
            {
                soundZ += 0.5D;
            }

            if (this.adjacentChestXPos != null)
            {
                soundX += 0.5D;
            }*/ // TODO: Find centre a bit how they're doing it

            this.world.playSound(null, soundX, (double)y + 0.5D, soundZ, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
        {
            float f2 = this.lidAngle;

            if (this.numPlayersUsing > 0)
            {
                this.lidAngle += 0.1F;
            }
            else
            {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            float f3 = 0.5F;

            if (this.lidAngle < 0.5F && f2 >= 0.5F)
            {
                this.world.playSound((EntityPlayer)null, soundX, (double)y + 0.5D, soundZ, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }

        if (world.isRemote)
        {
            //ticksSinceSync++;
            int temp = ticksSinceSync % 40;
            temp = temp >= 20 ? 20 - (temp % 20) : temp;
            this.lidAngle = temp * 0.05F;
        }

    }

    public void setType(RenderType renderType) {
        type = renderType;
    }

    public RenderType getType() {
        return isPrimaryChest ? type : getPrimaryChest().getType();
    }

    public void checkDoubleStillValid() {
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.numPlayersUsing = type;
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public String getName() {
        if (!isPrimaryChest) return getPrimaryChest().getName();
        return this.hasCustomName() ? this.customName : "container.chest";
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBTShared(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, -1, getUpdateTag());
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBTShared(tag);
    }

    public void readFromNBTShared(NBTTagCompound compound)
    {
        isPrimaryChest = compound.getBoolean("primary");
        isDoubleChest = compound.getBoolean("double");
        if (!isPrimaryChest)
        {
            int primX = compound.getInteger("primX");
            int primY = compound.getInteger("primY");
            int primZ = compound.getInteger("primZ");

            primaryChestTempPos = new BlockPos(primX, primY, primZ);

        } else {
            if (compound.getBoolean("secondaryExists")) {
                int secX = compound.getInteger("secX");
                int secY = compound.getInteger("secY");
                int secZ = compound.getInteger("secZ");

                secondaryChestTempPos = new BlockPos(secX, secY, secZ);
            }

            if (compound.hasKey("CustomName", 8)) {
                this.customName = compound.getString("CustomName");
            }

            type = RenderType.values()[compound.getByte("render")];
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        readFromNBTShared(compound);

        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.chestContents);
        }
    }

    public NBTTagCompound writeToNBTShared(NBTTagCompound compound)
    {
        compound.setBoolean("primary", isPrimaryChest);
        compound.setBoolean("double", isDoubleChest);

        if (!isPrimaryChest)
        {
            compound.setInteger("primX", primaryChest.pos.getX());
            compound.setInteger("primY", primaryChest.pos.getY());
            compound.setInteger("primZ", primaryChest.pos.getZ());
        } else {
            if (secondaryChest != null) {
                compound.setBoolean("secondaryExists", true);
                compound.setInteger("secX", secondaryChest.pos.getX());
                compound.setInteger("secY", secondaryChest.pos.getY());
                compound.setInteger("secZ", secondaryChest.pos.getZ());
            }

            if (this.hasCustomName())
            {
                compound.setString("CustomName", this.customName);
            }

            compound.setByte("render", (byte) type.ordinal());
        }

        return compound;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        writeToNBTShared(compound);

        if (!this.checkLootAndWrite(compound))
        {
            ItemStackHelper.saveAllItems(compound, this.chestContents);
        }

        return compound;
    }

    @Override
    public void invalidate() {
        this.updateContainingBlockInfo();
        if (world.isRemote)
            return;
        if (isDoubleChest)
        {
            if (!isPrimaryChest) {
                TileEntityChesticle primary = getPrimaryChest();
                if (primary != null)
                    primary.SecondaryInvalidated(this);
            } else {
                TileEntityChesticle secondary = getSecondaryChest();
                if (secondary != null)
                    secondary.PrimaryInvalidated(this);
            }
        }
        super.invalidate();
    }

    private void SecondaryInvalidated(TileEntityChesticle secondary) {
        if (secondaryChest == null)
            return;
        // we will absorb its power
        NonNullList<ItemStack> newChestContents = NonNullList.withSize(27, ItemStack.EMPTY);
        for(int slot = 0; slot < getSizeInventory(); slot++)
        {
            if (slot < 27) {
                newChestContents.set(slot, getStackInSlot(slot));
            } else {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(),getStackInSlot(slot));
            }
        }

        isDoubleChest = false;
        secondaryChest = null;
        isPrimaryChest = true;

        String tempCustomName = customName;

        getWorld().setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(Blocks.CHEST.FACING, getWorld().getBlockState(pos).getValue(BlockChesticle.FACING)));

        ILockableContainer container = Blocks.CHEST.getContainer(world, pos, false);
        for (int slot = 0; slot < newChestContents.size(); slot++)
        {
            container.setInventorySlotContents(slot, newChestContents.get(slot));
            TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
            chest.setCustomName(tempCustomName);
        }
    }

    private void PrimaryInvalidated(TileEntityChesticle primary) {
        if (primaryChest == null)
            return;
        // we will absorb its power
        NonNullList<ItemStack> newChestContents = NonNullList.withSize(27, ItemStack.EMPTY);
        for(int slot = 0; slot < primary.getSizeInventory(); slot++)
        {
            if (slot >= 27) {
                newChestContents.set(slot - 27, primary.getStackInSlot(slot));
            } else {
                InventoryHelper.spawnItemStack(world, primary.pos.getX(), primary.pos.getY(), primary.pos.getZ(), primary.getStackInSlot(slot));
            }
        }

        isDoubleChest = false;
        primaryChest = null;
        isPrimaryChest = true;

        String tempCustomName = primary.customName;

        getWorld().setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(Blocks.CHEST.FACING, getWorld().getBlockState(pos).getValue(BlockChesticle.FACING)));

        ILockableContainer container = Blocks.CHEST.getContainer(world, pos, false);
        for (int slot = 0; slot < newChestContents.size(); slot++)
        {
            container.setInventorySlotContents(slot, newChestContents.get(slot));
            TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
            chest.setCustomName(tempCustomName);
        }
    }

    @Override
    public String getGuiID()
    {
        return "minecraft:chest";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        if (!isPrimaryChest) return getPrimaryChest().createContainer(playerInventory, playerIn);
        return new ContainerChest(playerInventory, this, playerIn);
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        if (!player.isSpectator())
        {
            if (this.numPlayersUsing < 0)
            {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockChesticle)
        {
            --this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
        }
    }

    public void setPrimary(TileEntityChesticle secondaryChestIn) {
        isPrimaryChest = true;
        isDoubleChest = true;
        chestContents = NonNullList.withSize(54, ItemStack.EMPTY);
        secondaryChest = secondaryChestIn;
        secondaryChestIn.setSecondary(this);
    }

    private void setSecondary(TileEntityChesticle primaryChestIn) {
        isPrimaryChest = false;
        isDoubleChest = true;
        primaryChest = primaryChestIn;
    }

    public enum RenderType {
        UP,
        FAT,
        UPSIDE_DOWN
    }
}
