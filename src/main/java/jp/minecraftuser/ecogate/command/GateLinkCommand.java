
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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
        // パラメタが同じ場合は何もしない
        if (args[0].equals(args[1])) {
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]と[{1}]は同じです", args[0], args[1]);
            return true;
        }

        // ゲート定義がない場合はエラー
        LoaderGate gates = ecgConf.getGates();
        // リンクを設定する
        try {
            gates.linkAddGate(args[0], args[1]);
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]を[{1}]へ接続しました", args[0], args[1]);
        } catch (Exception e) {
            Utl.sendPluginMessage(plg, sender, e.getLocalizedMessage());
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]の[{1}]への接続に失敗しました", args[0], args[1]);
        }
        return true;
    }

    /**
     * タブ補完用リスト取得
     * @param sender コマンド送信者
     * @param cmd コマンド
     * @param string タブ補完対象文字列
     * @param strings その他パラメタ
     * @return 補完リスト
     */
    @Override
    protected List<String> onTabComplete(CommandSender sender, Command cmd, String string, String[] strings) {
        LoaderGate gates = ecgConf.getGates();
        ArrayList<String> nameList = gates.getGateNameList();
        ArrayList<String> unloadList = gates.getUnloadWorldGateNameList();
        ArrayList<String> ret = new ArrayList<>();
        for (String name : nameList) {
            if (unloadList.contains(name)) {
                // ret.add(name + " (unload)");
            } else {
                if (!gates.isLinked(name)) {
                    // strings に name がない場合は ret に追加する
                    boolean hit = false;
                    for (String str : strings) {
                        if (str.equals(name)) {
                            hit = true;
                            break;
                        }
                    }
                    if (!hit) ret.add(name);
                }
            }
        }
        return ret;
    }
}
