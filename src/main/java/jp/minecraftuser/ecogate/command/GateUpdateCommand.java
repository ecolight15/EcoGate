
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import jp.minecraftuser.ecogate.struct.Gate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * ゲート更新コマンドクラス
 * @author ecolight
 */
public class GateUpdateCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateUpdateCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecgConf = (EcoGateConfig)conf;
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "ecogate.gate.update";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:1つ以上/上限なし
        if (!checkRange(sender, args, 1, -1)) return true;

        // ゲート名取得し、後続文字列はゲート説明文として抽出して登録する
        Player player = (Player) sender;
        LoaderGate gates = ecgConf.getGates();
        try {
            // 既存ゲートの情報取得
            Gate oldGate = gates.get(args[0]).clone();
            if (args.length > 1) {
                if (args[1].equals("(unload)")) {
                    gates.updateGate(args[0], player.getLocation());
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        if (i != 1) sb.append(" ");
                        sb.append(args[i]);
                    }
                    // 説明文付きゲートを登録する
                    gates.updateGate(args[0], player.getLocation(), sb.toString());
                }
            } else {
                // 説明文無しゲートを登録する
                gates.updateGate(args[0], player.getLocation());
            }
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]を更新しました", args[0]);

            Gate newGate = gates.get(args[0]).clone();
            Utl.sendPluginMessage(plg, sender, "旧ゲート:" + oldGate.name + "[" + oldGate.text + "]");
            Utl.sendPluginMessage(plg, sender, "旧座標" + oldGate.loc.toString());
            Utl.sendPluginMessage(plg, sender, "新ゲート:" + newGate.name + "[" + newGate.text + "]");
            Utl.sendPluginMessage(plg, sender, "新座標" + newGate.loc.toString());
        } catch (Exception e) {
            Utl.sendPluginMessage(plg, sender, e.getLocalizedMessage());
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]の更新に失敗しました", args[0]);
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
                ret.add(name + " (unload)");
            } else {
                ret.add(name);
            }
        }
        return ret;
    }
}
