package top.prefersmin.mcdataexport;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraftforge.registries.ForgeRegistries.ITEMS;
import static top.prefersmin.mcdataexport.utils.BiomesUtil.getGeneratingDimensionsForAllowedBiomes;

public class ExportMethod {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<Item> mcItems = new ArrayList<>();

    static {
        mcItems.addAll(ITEMS.getValues());
    }

    public static void exportBiomes(Player player) {

        ListMultimap<ResourceLocation, ResourceLocation> map = getGeneratingDimensionsForAllowedBiomes((ServerLevel) player.level());

        map.forEach((biomeKey, level) -> {

        });
    }

    public static void exportEntityIDs() {
        List<String> entityNames = new ArrayList<>();

        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (entityType.getCategory() != MobCategory.MISC) {
                String registryName = entityType.getDescriptionId();
                entityNames.add(registryName.replaceAll("entity.", "").replace(".", ":"));
            }
        }

        List<String> questData = new ArrayList<>();
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        entityNames.forEach(entityName -> {
            questData.add("{");
            questData.add("description: [\"\"]");
            questData.add("id: \"72CD619B2CCF11DE\"");
            questData.add("rewards: [{");
            questData.add("exclude_from_claim_all: true");
            questData.add("id: \"17C54BE8CD652E93\"");
            questData.add("table_id: 1989698555466274620L");
            questData.add("type: \"random\"");
            questData.add("}]");
            questData.add("shape: \"hexagon\"");
            questData.add("size: 1.0d");
            questData.add("tasks: [{");
            questData.add("entity: \"" + entityName + "\"");
            String[] parts = entityName.split(":");
            if (parts[0].equals("alexsmobs")) {
                questData.add("icon: \"alexsmobs:spawn_egg_" + parts[1] + "\"");
            } else if (parts[0].equals("aquaculture")) {
                questData.add("icon: \"" + entityName + "\"");
            } else {
                questData.add("icon: \"" + entityName + "_spawn_egg\"");
            }
            questData.add("id: \"3528CE3A9C53F153\"");
            questData.add("type: \"kill\"");
            questData.add("value: 25L");
            questData.add("}]");
            questData.add("x: " + String.format("%.1f", x.get()) + "d");
            questData.add("y: " + String.format("%.1f", y.get()) + "d");
            questData.add("}");
            x.set(x.get() + 1.0d);
            if (x.get() == 20d) {
                x.set(0.5d);
                y.set(y.get() + 1d);
            }
            if (x.get() == 20.5d) {
                x.set(0d);
                y.set(y.get() + 1d);
            }
        });

        // 创建 Gson 对象，用于将数据转换为 JSON 格式
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // 将实体名称列表转换为 JSON 字符串
        String json = gson.toJson(questData);

        // 将 JSON 字符串写入文件
        try (FileWriter writer = new FileWriter("entity.json")) {
            writer.write(json);
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    public static void exportModItem(String MODID) {
        List<String> questData = new ArrayList<>();
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        String regex = "^(item|block)\\.";
        mcItems.forEach(item -> {
            String[] parts = item.getDescriptionId().replaceFirst(regex, "").replace(".", ":").split(":");
            if (parts[0].equals(MODID)) {
                questData.add("{");
                questData.add("disable_toast: true");
                questData.add("id: \"69B10FDDB37D4FBB\"");
                questData.add("shape: \"hexagon\"");
                questData.add("tasks: [{");
                questData.add("id: \"51B4F8ECAEC1C2A7\"");
                questData.add("item: {");
                questData.add("Count: 1b");
                questData.add("id: \"" + item + "\"");
                questData.add("tag: {");
                questData.add("Damage: 0");
                questData.add("}");
                questData.add("}");
                questData.add("type: \"item\"");
                questData.add("}]");
                questData.add("x: " + String.format("%.1f", x.get()) + "d");
                questData.add("y: " + String.format("%.1f", y.get()) + "d");
                questData.add("}");
                x.set(x.get() + 1.0d);
                if (x.get() == 20d) {
                    x.set(0.5d);
                    y.set(y.get() + 1d);
                }
                if (x.get() == 20.5d) {
                    x.set(0d);
                    y.set(y.get() + 1d);
                }
            }
        });

        // 转换为 JSON 格式
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // 将食物 ID 列表转换为 JSON 字符串
        String json = gson.toJson(questData);

        // 将 JSON 字符串写入文件
        try (FileWriter writer = new FileWriter(MODID + ".json")) {
            writer.write(json);
        } catch (IOException e) {
            LOGGER.error(e.toString());

        }

    }

}
