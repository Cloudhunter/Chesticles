package uk.co.cloudhunter.chesticles;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import uk.co.cloudhunter.chesticles.tile.TileEntityChesticle;

public abstract class CommonProxy {

    public CommonProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void preInit() {
        GameRegistry.registerTileEntity(TileEntityChesticle.class, Chesticles.MODID + ":tileunnaturalchest");
    }

    public void init() {
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(Chesticles.Blocks.init());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(Chesticles.Items.init());
    }

    public ResourceLocation getFatBoyTexture() {
        return null;
    }

    public ResourceLocation getTallBoyTexture()  {
        return null;
    }

    public ResourceLocation getFatBoyChristmasTexture() {
        return null;
    }

    public ResourceLocation getTallBoyChristmasTexture() {
        return null;
    }
}
