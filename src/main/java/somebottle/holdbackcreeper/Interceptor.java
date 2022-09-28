package somebottle.holdbackcreeper;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public final class Interceptor implements Listener {
    // 实例化输出类
    private final Output outputer = new Output();
    // 读取配置
    private Config configs;

    public Interceptor(JavaPlugin plugin, PluginManager manager, Config configs) {
        // 在PluginManager实例上注册事件
        manager.registerEvents(this, plugin);
        outputer.toConsole("Event registered.");
        // 初始化配置
        this.configs = configs;
    }

    /**
     * 拦截预爆炸事件
     *
     * @param event EntityExplodeEvent
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        // 如果没有开启插件就不作拦截
        if (!configs.getEnabled())
            return;
        // 获得待爆炸实体
        Entity targetEntity = event.getEntity();
        // 获得实体类型
        EntityType targetType = targetEntity.getType();
        // 获得实体所在的世界名
        String targetWorld = targetEntity.getWorld().getName();
        // 爆炸的是苦力怕且这个苦力怕所在世界在配置中
        if (targetType == EntityType.CREEPER && configs.worldsIncluding(targetWorld)) {
            // 将爆炸即将影响的方块列表给清空, 咱就当没事发生哈！
            event.blockList().clear();
        }
    }

    /**
     * 阻止苦力怕爆炸影响实体之EntityDamageByEntityEvent事件
     *
     * @param event EntityExplodeEvent
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (preventEntityDamage(event)) {
            // 取消实体破坏
            event.setCancelled(true);
        }
    }

    /**
     * 阻止苦力怕爆炸影响实体之HangingBreakByEntityEvent事件
     *
     * @param event HangingBreakByEntityEvent
     */
    @EventHandler
    public void onEntityBreak(HangingBreakByEntityEvent event) {
        if (preventEntityDamage(event)) {
            // 取消实体破坏
            event.setCancelled(true);
        }
    }

    /**
     * EntityDamageByEntityEvent和HangingBreakByEntityEvent事件的handler
     * 阻止苦力怕破坏实体
     * 参考：<a href="https://wiki.biligame.com/mc/%E5%AE%9E%E4%BD%93">MinecraftWiki-实体</a>
     *
     * @param event EntityDamageByEntityEvent或HangingBreakByEntityEvent对象
     * @return 是否阻止事件发生
     */
    private boolean preventEntityDamage(Event event) {
        // 如果没有开启插件就不作拦截
        if (!configs.getEnabled())
            return false;
        // 获得受爆炸影响的实体
        Entity targetEntity;
        // 获得造成爆炸的实体类型
        EntityType damagerType;
        if (event instanceof EntityDamageByEntityEvent) {
            // 如果是EntityDamageByEntityEvent
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            targetEntity = damageEvent.getEntity();
            damagerType = damageEvent.getDamager().getType();
        } else if (event instanceof HangingBreakByEntityEvent) {
            // 如果是HangingBreakByEntityEvent
            HangingBreakByEntityEvent breakEvent = (HangingBreakByEntityEvent) event;
            targetEntity = breakEvent.getEntity();
            Entity damagerEntity = breakEvent.getRemover();
            // 如果破坏者实体未定义，就不做处理
            if (damagerEntity == null)
                return false;
            damagerType = damagerEntity.getType();
        } else {
            return false;
        }
        // 获得实体类型
        EntityType targetType = targetEntity.getType();
        // 获得实体所在的世界名
        String targetWorld = targetEntity.getWorld().getName();
        // 如果破坏者是苦力怕，且爆炸发生的世界在配置中，且被破坏的实体也在配置中，就取消实体被破坏的事件
        return damagerType == EntityType.CREEPER && configs.worldsIncluding(targetWorld) && configs.entitiesIncluding(targetType);
    }
}
