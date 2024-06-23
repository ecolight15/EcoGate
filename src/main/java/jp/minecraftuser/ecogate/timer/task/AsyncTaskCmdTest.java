
package jp.minecraftuser.ecogate.timer.task;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecogate.timer.AsyncPayload;
import jp.minecraftuser.ecogate.timer.AsyncWorker;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;

/**
 * タスク別処理分割用 Test クラス
 * @author ecolight
 */
public class AsyncTaskCmdTest extends AsyncTaskBase {
    
    // シングルトン実装
    private static AsyncTaskCmdTest instance = null;
    public static final AsyncTaskCmdTest getInstance(PluginFrame plg_) {
        if (instance == null) {
            instance = new AsyncTaskCmdTest(plg_);
        }
        return instance;
    }
    
    /**
     * コンストラクタ
     * @param plg_ 
     */
    public AsyncTaskCmdTest(PluginFrame plg_) {
        super(plg_);
    }

    /**
     * 非同期で実施する処理
     * Bukkit/Spigotインスタンス直接操作不可
     * @param thread
     * @param db
     * @param con
     * @param data 
     * @throws java.sql.SQLException 
     */
    @Override
    public void asyncThread(AsyncWorker thread, AsyncPayload data) {
        // Testメッセージの送信
        sendPluginMessage(plg, data.player, "Test worker thread message");
        data.result = true;
    }

    /**
     * 応答後メインスレッド側で実施する処理
     * Bukkit/Spigotインスタンス直接操作可
     * @param thread
     * @param data 
     */
    @Override
    public void mainThread(AsyncWorker thread, AsyncPayload data) {
        if (data.result) {
            sendPluginMessage(plg, data.player, "Test worker thread message success");
        } else {
            sendPluginMessage(plg, data.player, "Test worker thread message failed");
        }
    }

}
