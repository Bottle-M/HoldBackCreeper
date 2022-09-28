package somebottle.holdbackcreeper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class HoldBackCreeper extends JavaPlugin implements CommandExecutor {
    // 实例化输出类
    private final Output outputer = new Output();
    // 读取配置
    private Config configs = null;

    /**
     * 接收到用户命令
     *
     * @param sender  命令发送者
     * @param command 命令Command对象
     * @param alias   命令别名字符串
     * @param args    命令参数的字符串数组
     * @return 是否成功
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        // 如果不是/holdbackcreeper命令，不予处理
        if (!command.getName().equalsIgnoreCase("holdbackcreeper"))
            return true;
        // 如果不是/holdbackcreeper reload，就返回false
        if (!(args.length == 1 && args[0].equals("reload")))
            return false;
        if (sender instanceof Player) {
            // 转换为玩家对象
            Player player = (Player) sender;
            // 如果是玩家发的
            if (sender.hasPermission("holdbackcreeper.reload") || sender.isOp()) {
                // 如果命令发送者有权限或者是Operator，允许重载配置
                configs.reload();
                outputer.toPlayer(player, "Configs reloaded.");
            } else {
                outputer.toPlayer(player, "Permission denied.");
            }
        } else {
            // 控制台发的
            configs.reload();
            outputer.toConsole("Configs reloaded.");
        }
        return true;
    }

    @Override
    public void onEnable() {
        outputer.toConsole("Hold Back!Creeper! The plugin has been initialized");
        // 初始化配置
        this.configs = new Config(this);
        // Creeper爆炸事件拦截器
        new Interceptor(this, getServer().getPluginManager(), configs);
    }

    @Override
    public void onDisable() {
        outputer.toConsole("HoldBackCreeper has been disabled");
    }

}
