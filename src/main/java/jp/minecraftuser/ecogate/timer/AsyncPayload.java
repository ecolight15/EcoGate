
package jp.minecraftuser.ecogate.timer;

import jp.minecraftuser.ecoframework.async.*;
import jp.minecraftuser.ecoframework.PluginFrame;
import org.bukkit.command.CommandSender;

/**
 * メインスレッドと非同期スレッド間のデータ送受用クラス(メッセージ送受用)
 * @author ecolight
 */
public class AsyncPayload extends PayloadFrame {
    private final PluginFrame plg;
    public boolean result = false;
    public Type type = Type.NONE;       // 非同期タスク種別
    public CommandSender player;

    // 処理種別を追加した場合、AsyncSaveLoadTimer の initTask に処理クラスを登録すること
    public enum Type {
        NONE,
        CMD_TEST,
    }
    
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス(ただし通信に用いられる可能性を念頭に一定以上の情報は保持しない)
     * @param type_ Type
     */
    public AsyncPayload(PluginFrame plg_, Type type_) {
        super(plg_);
        plg = plg_;
        this.type = type_;
    }
    
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス(ただし通信に用いられる可能性を念頭に一定以上の情報は保持しない)
     * @param player_ CommandSender
     * @param type_ Type
     */
    public AsyncPayload(PluginFrame plg_, CommandSender player_, Type type_) {
        super(plg_);
        plg = plg_;
        this.type = type_;
        player = player_;
    }
    
}
