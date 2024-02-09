package top.prefersmin.mcdataexport.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.prefersmin.mcdataexport.DataGenerator;
import top.prefersmin.mcdataexport.MinecraftDataExport;

import static top.prefersmin.mcdataexport.ExportMethod.exportBiomes;
import static top.prefersmin.mcdataexport.ExportMethod.exportEntityIDs;
import static top.prefersmin.mcdataexport.ExportMethod.exportModItem;

@Mod.EventBusSubscriber(modid = MinecraftDataExport.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistrar {

    static DataGenerator dataGenerator = new DataGenerator();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("exportFoodRewardTable").then(Commands.argument("count", IntegerArgumentType.integer()).executes(context -> {
            int count = IntegerArgumentType.getInteger(context, "count");
            dataGenerator.exportFoodRewardTable(count);
            return 1;
        })));

        dispatcher.register(Commands.literal("exportModItemRewardTable").then(Commands.argument("modId", StringArgumentType.string()).executes(context -> {
            String modid = StringArgumentType.getString(context, "modId");
            dataGenerator.exportModItemRewardTable(modid);
            return 1;
        })));

        dispatcher.register(Commands.literal("exportEntity").executes(context -> {
            exportEntityIDs();
            return 1;
        }));

        dispatcher.register(Commands.literal("exportBiomes").executes(context -> {
            Player player = context.getSource().getPlayerOrException();
            exportBiomes(player);
            return 1;
        }));

        dispatcher.register(Commands.literal("exportModItem").then(Commands.argument("modid", StringArgumentType.string()).executes(context -> {
            String modid = StringArgumentType.getString(context, "modid");
            exportModItem(modid);
            return 1;
        })));

    }

}
