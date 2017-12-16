
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import org.bukkit.command.CommandSender;

/**
 * ゲート接続コマンドクラス
 * @author ecolight
 */
public class GateLinkCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateLinkCommand(PluginFrame plg_, String name_) {
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
        return "ecogate.gate.link";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:2つのみ
        if (!checkRange(sender, args, 2, 2)) return true;

        // ゲート定義がない場合はエラー
        LoaderGate gates = ecgConf.getGates();
        if (!gates.contains(args[0]) ) { Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]は存在しません", args[0]); return true; }
        if (!gates.contains(args[1]) ) { Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]は存在しません", args[1]); return true; }
        // 既にゲートのリンク定義がある場合はエラー
        if (gates.isLinked(args[0])) { Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]は既に接続設定が存在します", args[0]); return true; }
        if (gates.isLinked(args[1])) { Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]は既に接続設定が存在します", args[1]); return true; }
        // リンクを設定する
        if (!gates.linkAddGate(args[0], args[1])) { Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]と[{1}]の接続に失敗しました", args[0], args[1]); return true; }
        Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]と[{1}]を接続しました", args[0], args[1]);
        return true;
    }
    
}
