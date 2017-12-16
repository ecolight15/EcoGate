
package jp.minecraftuser.ecogate.timer;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.TimerFrame;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author ecolight
 */
public class BlockSetTimer  extends TimerFrame {
    private Location l= null;
    private Material m = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public BlockSetTimer(PluginFrame plg_, String name_) {
        super(plg_, name_);
    }
    
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param loc_ 設置ロケーション
     * @param ma_ 設置マテリアル
     * @param name_ 設置名
     */
    public BlockSetTimer(PluginFrame plg_, Location loc_, Material ma_, String name_) {
        super(plg_, name_);
        this.l = loc_;
        this.m = ma_;
    }
    public void run()
    {
        l.getWorld().getBlockAt(l).setType(m);
    }
}