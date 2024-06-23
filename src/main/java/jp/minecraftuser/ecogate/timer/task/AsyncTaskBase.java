
package jp.minecraftuser.ecogate.timer.task;

import java.util.logging.Logger;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.timer.AsyncPayload;
import jp.minecraftuser.ecogate.timer.AsyncWorker;

/**
 * タスク別処理分割用ベースクラス
 * @author ecolight
 */
public abstract class AsyncTaskBase {
    protected final PluginFrame plg;
    protected final EcoGateConfig conf;
    protected final Logger log;

    /**
     * コンストラクタ
     * @param plg_ 
     */
    public AsyncTaskBase(PluginFrame plg_) {
        plg = plg_;
        log = plg.getLogger();
        conf = (EcoGateConfig) plg.getDefaultConfig();
    }

    /**
     * 非同期で実施する処理
     * Bukkit/Spigotインスタンス直接操作不可
     * @param thread
     * @param data 
     * @throws java.sql.SQLException 
     */
    abstract public void asyncThread(AsyncWorker thread, AsyncPayload data);
    
    /**
     * 応答後メインスレッド側で実施する処理
     * Bukkit/Spigotインスタンス直接操作可
     * @param thread
     * @param data 
     */
    abstract public void mainThread(AsyncWorker thread, AsyncPayload data);
}
