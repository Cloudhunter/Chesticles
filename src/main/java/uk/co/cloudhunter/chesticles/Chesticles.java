package uk.co.cloudhunter.chesticles;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uk.co.cloudhunter.chesticles.block.BlockChesticle;
import uk.co.cloudhunter.chesticles.item.ItemChesticle;

import static uk.co.cloudhunter.chesticles.Chesticles.VERSION;
import static uk.co.cloudhunter.chesticles.Chesticles.MODID;

@Mod(modid = MODID, version = VERSION)
public class Chesticles
{
    public static final String MODID = "chesticles";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "uk.co.cloudhunter.chesticles.client.ClientProxy", serverSide = "uk.co.cloudhunter.chesticles.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static Chesticles instance;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    public static class Blocks {
        public static Block UNNATURAL_CHEST;
        public static Block[] init() {
            return new Block[] {
                UNNATURAL_CHEST = new BlockChesticle().setRegistryName(MODID, "unnaturalchest")
            };
        }
    }

    public static class Items {

        public static Item[] init() {
            return new Item[] {
                new ItemChesticle(net.minecraft.init.Blocks.CHEST).setRegistryName(net.minecraft.init.Blocks.CHEST.getRegistryName())
            };
        }
    }
}
