
package jp.minecraftuser.ecogate.timer;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.TimerFrame;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

/**
 *
 * @author ecolight
 */
public class VehicleTimer  extends TimerFrame{
    private EntityType type = null;
    private Player p = null;
    private Location l= null;

    /**
     * コンストラクタ
     * @param plg_ プラグインタイマー
     * @param pl_ プレイヤー
     * @param loc_ ロケーション
     * @param et_ エンティティタイプ
     * @param name_ 名前
     */
    public VehicleTimer(PluginFrame plg_, Player pl_, Location loc_, EntityType et_, String name_) {
        super(plg_, name_);
        this.type = et_;
        this.p = pl_;
        this.l = loc_;
    }
    public void run()
    {
        p.teleport(l);
        Entity e = l.getWorld().spawnEntity(l, type);
        if (e.getType() == EntityType.MINECART) {
            ((Minecart)e).setPassenger(p);
        }
    }
}