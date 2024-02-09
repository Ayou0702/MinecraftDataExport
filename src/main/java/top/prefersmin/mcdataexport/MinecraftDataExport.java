package top.prefersmin.mcdataexport;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import top.prefersmin.mcdataexport.command.CommandRegistrar;

@Mod(MinecraftDataExport.MODID)
public class MinecraftDataExport {

    public static final String MODID = "mcdataexport";

    public MinecraftDataExport() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(CommandRegistrar.class);
    }

}
