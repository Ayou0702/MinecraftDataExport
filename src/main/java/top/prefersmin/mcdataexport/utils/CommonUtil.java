package top.prefersmin.mcdataexport.utils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraftforge.registries.ForgeRegistries.ITEMS;

public class CommonUtil {

    public static final List<Item> mcItems = new ArrayList<>();

    static {
        mcItems.addAll(ITEMS.getValues());
    }

    /**
     * 格式化命名空间
     * @param item 物品实例
     * @return 命名空间
     */
    public static String formatItemNameSpace(Item item) {
        return item.getDescriptionId().replaceFirst("^(item|block)\\.", "").replaceFirst("\\.", ":");
    }

    /**
     * 格式化命名空间
     * @param entity 实体实例
     * @return 命名空间
     */
    public static String formatEntityNameSpace(EntityType<?> entity) {
        return entity.getDescriptionId().replaceFirst("^(entity)\\.", "").replaceFirst("\\.", ":");
    }

    public static Item getRandomItem() {
        Random random = new Random();
        int randomIndex = random.nextInt(mcItems.size());
        return mcItems.get(randomIndex);
    }

}
