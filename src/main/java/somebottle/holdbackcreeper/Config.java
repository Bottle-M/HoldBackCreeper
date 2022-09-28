package somebottle.holdbackcreeper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Config {
    // Plugin对象
    private JavaPlugin plugin;
    // 插件是否开启
    private final Output outputer = new Output();
    // 获得插件配置文件所在路径
    private File configFilePath;
    private boolean pluginEnabled = false;
    // 生效的世界列表
    private List<String> affectingWorlds;
    // 忽略影响的实体列表
    private List<EntityType> affectingEntities = new ArrayList<>();

    /**
     * 读取配置文件的方法并实例化
     *
     * @param plugin Plugin对象
     */
    public Config(JavaPlugin plugin) {
        // 获得plugin对象
        this.plugin = plugin;
        // 获得插件配置文件所在路径
        this.configFilePath = new File(this.plugin.getDataFolder(), "config.yml");
        reload();
    }

    /**
     * 读取插件配置config.yml
     */
    private void read() {
        FileConfiguration currentConfigs = this.plugin.getConfig();
        this.pluginEnabled = currentConfigs.getBoolean("enable");
        this.affectingWorlds = currentConfigs.getStringList("worlds");
        // 获得实体列表
        List<String> entitiesList = currentConfigs.getStringList("entities");
        // 将其转换为一个EntityType列表
        for (String s : entitiesList) {
            // 通过EntityType.valueOf转换为对应的实体常量(全都是大写)
            String identifier = s.toUpperCase();
            try {
                this.affectingEntities.add(EntityType.valueOf(identifier));
            } catch (IllegalArgumentException e) {
                // 不存在这个实体
                outputer.toConsole("Unknown entity type identifier:" + identifier);
            }
        }
        outputer.toConsole("Loaded configs.");
        if (this.pluginEnabled) {
            // 配置：开启插件
            outputer.toConsole("Applied to worlds: " + this.affectingWorlds.toString());
            outputer.toConsole("Entities that ignored CREEPER explosions: " + entitiesList.toString());
        } else {
            outputer.toConsole("HoldBackCreeper is disabled in config file.");
        }
    }

    /**
     * 重载配置文件
     */
    void reload() {
        if (!configFilePath.exists()) {
            // 如果配置文件还不存在，就初始化配置文件
            outputer.toConsole("Made default config file.");
            this.plugin.saveDefaultConfig();
        }
        this.plugin.reloadConfig();
        read();
    }

    /**
     * 获知本插件是否被启用
     *
     * @return 布尔值
     */
    boolean getEnabled() {
        return this.pluginEnabled;
    }

    /**
     * 检查配置中有没有在某个世界启用
     *
     * @param worldName 待检查的世界名字符串
     * @return 世界是否存在于配置中
     */
    boolean worldsIncluding(String worldName) {
        return this.affectingWorlds.contains(worldName);
    }

    /**
     * 检查配置中是否有某个实体
     *
     * @param targetType 待检查实体EntityType
     * @return 实体是否存在于配置中
     */
    boolean entitiesIncluding(EntityType targetType) {
        return this.affectingEntities.contains(targetType);
    }
}
