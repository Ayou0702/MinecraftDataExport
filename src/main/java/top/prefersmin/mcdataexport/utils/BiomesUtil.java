package top.prefersmin.mcdataexport.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BiomesUtil {

    /**
     * 获取给定维度的生物群系注册表。
     * @param level 维度
     * @return 生物群系注册表
     */
    public static Optional<? extends Registry<Biome>> getBiomeRegistry(Level level) {
        return level.registryAccess().registry(ForgeRegistries.Keys.BIOMES);
    }

    /**
     * 根据给定维度和生物群系获取生物群系的唯一标识符。
     * @param level 维度
     * @param biome 生物群系
     * @return 唯一标识
     */
    public static Optional<ResourceLocation> getKeyForBiome(Level level, Biome biome) {
        return getBiomeRegistry(level).isPresent() ? Optional.ofNullable(getBiomeRegistry(level).get().getKey(biome)) : Optional.empty();
    }

    /**
     * 根据给定维度和生物群系的唯一标识符获取生物群系。
     * @param level 维度
     * @param key 唯一标识
     * @return 生物群系
     */
    public static Optional<Biome> getBiomeForKey(Level level, ResourceLocation key) {
        return getBiomeRegistry(level).isPresent() ? getBiomeRegistry(level).get().getOptional(key) : Optional.empty();
    }

    /**
     * 获取在给定维度中允许出现的所有生物群系的唯一标识符
     * @param level 维度
     * @return 唯一标识
     */
    public static List<ResourceLocation> getAllowedBiomeKeys(Level level) {
        final List<ResourceLocation> biomeKeys = new ArrayList<>();
        if (getBiomeRegistry(level).isPresent()) {
            for (Map.Entry<ResourceKey<Biome>, Biome> entry : getBiomeRegistry(level).get().entrySet()) {
                Biome biome = entry.getValue();
                if (biome != null) {
                    Optional<ResourceLocation> optionalBiomeKey = getKeyForBiome(level, biome);
                    optionalBiomeKey.ifPresent(biomeKeys::add);
                }
            }
        }

        return biomeKeys;
    }

    /**
     * 获取生成给定生物群系的维度的唯一标识符
     * @param serverLevel 维度
     * @param biome 生物群系
     * @return 唯一标识
     */
    public static List<ResourceLocation> getGeneratingDimensionKeys(ServerLevel serverLevel, Biome biome) {
        final List<ResourceLocation> dimensions = new ArrayList<>();
        final Registry<Biome> biomeRegistry = getBiomeRegistry(serverLevel).get();
        for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
            Set<Holder<Biome>> biomeSet = level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes();
            Holder<Biome> biomeHolder = biomeRegistry.getHolder(biomeRegistry.getResourceKey(biome).get()).get();
            if (biomeSet.contains(biomeHolder)) {
                dimensions.add(level.dimension().location());
            }
        }
        return dimensions;
    }

    /**
     * 获取所有允许出现的生物群系及其生成的维度的映射关系。
     * @param serverLevel 维度
     * @return 关系映射
     */
    public static ListMultimap<ResourceLocation, ResourceLocation> getGeneratingDimensionsForAllowedBiomes(ServerLevel serverLevel) {
        ListMultimap<ResourceLocation, ResourceLocation> dimensionsForAllowedStructures = ArrayListMultimap.create();
        for (ResourceLocation biomeKey : getAllowedBiomeKeys(serverLevel)) {
            Optional<Biome> optionalBiome = getBiomeForKey(serverLevel, biomeKey);
            optionalBiome.ifPresent(biome -> dimensionsForAllowedStructures.putAll(biomeKey, getGeneratingDimensionKeys(serverLevel, biome)));
        }
        return dimensionsForAllowedStructures;
    }

}
