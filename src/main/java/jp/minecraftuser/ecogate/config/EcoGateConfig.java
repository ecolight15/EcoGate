
package jp.minecraftuser.ecogate.config;

import jp.minecraftuser.ecoframework.ConfigFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ListenerFrame;
import jp.minecraftuser.ecogate.listener.PlayerListener;

/**
 * デフォルトコンフィグクラス
 * @author ecolight
 */
public class EcoGateConfig extends ConfigFrame{
    private static LoaderWorld worlds = null;
    private static LoaderGate gates = null;
    /**
     * コンストラクタ
     * @param plg_ 
     */
    public EcoGateConfig(PluginFrame plg_) {
        super(plg_);
    }

    /**
     * 設定再読み込み処理
     */
    @Override
    public void reload() {
        super.reload();
        // ワールド、ゲート定義再読み込み
        worlds = new LoaderWorld(plg);
        gates = new LoaderGate(plg);
        ListenerFrame pl = plg.getPluginListerner("player");
        if (pl != null) {
            ((PlayerListener)pl).reloadConf();
        }
    }

    /**
     * LoaderWorldインスタンス取得
     * @return LoaderWorldインスタンス
     */
    public LoaderWorld getWorlds() {
        return this.worlds;
    }
    
    /**
     * LoaderGateインスタンス取得
     * @return LoaderGateインスタンス
     */
    public LoaderGate getGates() {
        return this.gates;
    }
}
