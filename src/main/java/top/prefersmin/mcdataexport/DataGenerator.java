package top.prefersmin.mcdataexport;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import top.prefersmin.mcdataexport.utils.CommonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static top.prefersmin.mcdataexport.utils.BiomesUtil.getGeneratingDimensionsForAllowedBiomes;

public class DataGenerator {

    private static final Logger LOGGER = LogUtils.getLogger();

    public void exportFoodRewardTable(int count) {

        SNBTCompoundTag fileNBT = new SNBTCompoundTag();
        ListTag rewardList = new ListTag();

        CommonUtil.mcItems.forEach(item -> {

            // 如果不是吃的，跳过此次循环
            if (!item.isEdible()) {
                return;
            }

            SNBTCompoundTag reward = new SNBTCompoundTag();

            NBTUtils.write(reward, "item", item.getDefaultInstance());
            if (count > 1) {
                reward.putInt("count", count);
            }

            rewardList.add(reward);

        });

        fileNBT.put("rewards", rewardList);

        String filePath = System.getProperty("user.dir") + File.separator + "foodRewardTable.snbt";
        File file = new File(filePath);
        SNBT.write(file.toPath(), fileNBT);

    }

    public void exportModItemRewardTable(String modId) {

        SNBTCompoundTag fileNBT = new SNBTCompoundTag();
        ListTag rewardList = new ListTag();

        CommonUtil.mcItems.forEach(item -> {

            String[] parts = CommonUtil.formatItemNameSpace(item).split(":");

            // 如果不是这个模组的物品，跳过此次循环
            if (!parts[0].equals(modId)) {
                return;
            }

            SNBTCompoundTag reward = new SNBTCompoundTag();

            NBTUtils.write(reward, "item", item.getDefaultInstance());

            rewardList.add(reward);

        });

        fileNBT.put("rewards", rewardList);

        String filePath = System.getProperty("user.dir") + File.separator + "ModItemRewardTable.snbt";
        File file = new File(filePath);
        SNBT.write(file.toPath(), fileNBT);

    }

    public void exportBiomes(Player player) {

        LOGGER.info("输出开始");

        SNBTCompoundTag fileNBT = new SNBTCompoundTag();
        ListTag questList = new ListTag();
        Map<ResourceLocation, List<ResourceLocation>> levelMap = new HashMap<>();

        ListTag rewards = new ListTag();
        SNBTCompoundTag reward = new SNBTCompoundTag();
        reward.putLong("table_id", 8149682061826563257L);
        reward.putString("type", "random");
        rewards.add(reward);

        ListMultimap<ResourceLocation, ResourceLocation> map = getGeneratingDimensionsForAllowedBiomes((ServerLevel) player.level());

        // 按照维度分开
        map.forEach((biomeKey, level) -> {
            List<ResourceLocation> levelList = levelMap.computeIfAbsent(level, k -> new ArrayList<>());
            levelList.add(biomeKey);
        });
        LOGGER.info("区分完成");

        File wwoo = new File("F:\\wwoo.json");
        String content = getFileContent(wwoo);
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> fromJson = gson.fromJson(content, mapType);

        File bop = new File("F:\\bop.json");
        String bops = getFileContent(bop);
        Map<String, String> bopmap = gson.fromJson(bops, mapType);
        fromJson.putAll(bopmap);

        LOGGER.info("读取汉化文件完成");

        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);

        // 遍历每个维度的生物群系
        levelMap.forEach((level, biomeList) -> {

            for (ResourceLocation biome : biomeList) {

                String title = fromJson.get(biome.toString());
                ListTag tasks = new ListTag();
                SNBTCompoundTag task = new SNBTCompoundTag();
                task.putString("biome", biome.toString());
                task.putString("type", "biome");
                task.putString("icon", CommonUtil.formatItemNameSpace(CommonUtil.getRandomItem()));
                if (title != null) {
                    task.putString("title", "访问" + title);
                }
                tasks.add(task);

                SNBTCompoundTag quest = new SNBTCompoundTag();
                quest.put("rewards", rewards);
                quest.putString("shape", "hexagon");

                quest.put("tasks", tasks);
                quest.putDouble("x", x.get());
                quest.putDouble("y", y.get());

                x.set(x.get() + 1.0d);
                if (x.get() == 20d) {
                    x.set(0.5d);
                    y.set(y.get() + 1d);
                }
                if (x.get() == 20.5d) {
                    x.set(0d);
                    y.set(y.get() + 1d);
                }

                questList.add(quest);

            }

            y.set(y.get() + 3d);

        });
        LOGGER.info("循环完成");

        fileNBT.put("quests", questList);

        String filePath = System.getProperty("user.dir") + File.separator + "biomeList.snbt";
        File file = new File(filePath);
        SNBT.write(file.toPath(), fileNBT);
        LOGGER.info("输出完成" + filePath);

    }

    public String getFileContent(File fileName) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return content.toString();
    }

    public void exportMobEntity() {
        ForgeRegistries.ENTITY_TYPES.getValues().forEach(entity -> {

            if (entity.getCategory() != MobCategory.MISC) {
                return;
            }

            String[] parts = CommonUtil.formatEntityNameSpace(entity).split(":");

        });
    }

}
