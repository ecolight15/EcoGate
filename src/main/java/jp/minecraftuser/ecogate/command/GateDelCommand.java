
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import org.bukkit.command.CommandSender;

/**
 * ゲート削除コマンドクラス
 * @author ecolight
 */
public class GateDelCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateDelCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecgConf = (EcoGateConfig)conf;
        setAuthBlock(true);
        setAuthConsole(true);
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "ecogate.gate.del";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:1つのみ
        if (!checkRange(sender, args, 1, 1)) return true;

        // 指定ゲートが存在しない場合はエラー
        LoaderGate gates = ecgConf.getGates();
        if (!gates.contains(args[0])) { Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]は存在しません", args[0]); return true; }

        // ゲートを削除する
        gates.deleteGate(args[0]);
        Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]を削除しました", args[0]);
        return true;
    }
    
}
