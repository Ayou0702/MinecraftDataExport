package top.prefersmin.mcdataexport;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import top.prefersmin.mcdataexport.utils.CommonUtil;

import java.io.File;

public class DataGenerator {

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

    public void exportMobEntity() {
        ForgeRegistries.ENTITY_TYPES.getValues().forEach(entity -> {

            if (entity.getCategory() != MobCategory.MISC) {
                return;
            }

            String[] parts = CommonUtil.formatEntityNameSpace(entity).split(":");

        });
    }

}
