
package jp.minecraftuser.ecogate.timer;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.minecraftuser.ecogate.timer.task.AsyncTaskBase;
import jp.minecraftuser.ecogate.timer.task.AsyncTaskCmdTest;
import jp.minecraftuser.ecoframework.async.*;
import jp.minecraftuser.ecoframework.PluginFrame;

/**
 * 非同期プレイヤーデータ保存クラス
 * @author ecolight
 */
public class AsyncWorker extends AsyncProcessFrame {
    // 処理種別ごとの制御をクラス化
    HashMap<AsyncPayload.Type, AsyncTaskBase> tasktable;
    // リスナ生成時点でインスタンスが必要になったのでシングルトン化しておく
    private static AsyncWorker instance = null;
    public static final AsyncWorker getInstance(PluginFrame plg_, String name_) {
        if (instance == null) {
            instance = new AsyncWorker(plg_, name_);
        }
        return instance;
    }

    /**
     * 親スレッド用コンストラクタ
     * @param plg_ プラグインフレームインスタンス
     * @param name_ 名前
     */
    public AsyncWorker(PluginFrame plg_, String name_) {
        super(plg_, name_);
        initTask();
    }

    /**
     * 子スレッド用コンストラクタ
     * @param plg_ プラグインフレームインスタンス
     * @param name_ 名前
     * @param frame_ 子スレッド用フレーム
     */
    public AsyncWorker(PluginFrame plg_, String name_, AsyncFrame frame_) {
        super(plg_, name_, frame_);
        initTask();
    }
    
    /**
     * タスク処理用のクラスインスタンスを生成
     * 各クラスのシングルトンインスタンス取得して格納する
     */
    private void initTask() {
        tasktable = new HashMap<>();
        tasktable.put(AsyncPayload.Type.CMD_TEST, AsyncTaskCmdTest.getInstance(plg));
    }
    
    /**
     * セーブ・ロードスレッド停止待ち合わせ
     * @throws InterruptedException 
     */
    public synchronized void timerWait() throws InterruptedException {
        log.log(Level.INFO, "Wait for thread stop.");
        wait();
        log.log(Level.INFO, "Detect thread stop.");
    }

    /**
     * セーブ・ロードスレッド停止 
     */
    public synchronized void timerStop() {
        log.log(Level.INFO, "Notify thread stop.");
        notifyAll();
        log.log(Level.INFO, "Call thread cancel.");
        cancel();
    }

    /**
     * 子スレッドから親スレッドへの停止指示用
     */
    public void stop() {
        ((AsyncWorker) parentFrame).timerStop();
    }
    
    /**
     * Data加工子スレッド側処理
     * @param data_ ペイロードインスタンス
     */
    @Override
    protected void executeProcess(PayloadFrame data_) {
        AsyncPayload data = (AsyncPayload) data_;
        if (data.type != null) log.info("type:" + data.type.name());
        
        try {
            if (tasktable.containsKey(data.type)) {
                tasktable.get(data.type).asyncThread(this, data);
            } else {
                log.log(Level.SEVERE, "reject unknown payload");
            }
        } catch (Exception e) {
            log.warning("その他異常");
            Logger.getLogger(AsyncWorker.class.getName()).log(Level.SEVERE, null, e);
            data.result = false;
        }

        // 処理結果を返送
        receiveData(data);
    }

    /**
     * Data加工後親スレッド側処理
     * @param data_ ペイロードインスタンス
     */
    @Override
    protected void executeReceive(PayloadFrame data_) {
        AsyncPayload data = (AsyncPayload) data_;
        if (data.type != null) {
            tasktable.get(data.type).mainThread(this, data);
        }
    }

    /**
     * 継承クラスの子スレッド用インスタンス生成
     * 親子間で共有リソースがある場合、マルチスレッドセーフな作りにすること
     * synchronizedにする、スレッドセーフ対応クラスを使用するなど
     * @return AsyncFrame継承クラスのインスタンス
     */
    @Override
    protected AsyncFrame clone() {
        return new AsyncWorker(plg, name, this);
    }
  
}
