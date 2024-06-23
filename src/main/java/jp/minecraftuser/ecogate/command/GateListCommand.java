
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import jp.minecraftuser.ecogate.struct.Gate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * ゲートリストコマンドクラス
 * @author ecolight
 */
public class GateListCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateListCommand(PluginFrame plg_, String name_) {
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
        return "ecogate.gate.list";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:0または1つ
        if (!checkRange(sender, args, 0, 1)) return true;

        LoaderGate gates = ecgConf.getGates();
        // パラメタなしの場合
        ArrayList<String> list = null;
        if (args.length == 0) {
            if (sender instanceof Player) {
                list = gates.getGateNameList(((Player) sender).getWorld().getName());
            } else {
                list = gates.getGateNameList();
            }
        }
        // パラメタありの場合
        else {
            // パラメタが all の場合
            if (args[0].equalsIgnoreCase("all")) {
                list = gates.getGateNameList();
            }
            // パラメタがワールド名指定
            else {
                list = gates.getGateNameList(args[0]);
            }
        }
        StringBuilder sb = new StringBuilder("gate list:");
        boolean first = true;
        for (String name : list) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            Gate gate = gates.get(name);
            if (gate != null) {
                if (gate.unload) {
                    sb.append(ChatColor.RED);
                } else if (gate.link == null) {
                    sb.append(ChatColor.GRAY);
                }
                sb.append(name);
                sb.append(ChatColor.RESET);
            } else {
                sb.append(ChatColor.DARK_RED);
                sb.append(name);
                sb.append(ChatColor.RESET);
            }
        }
        if (first) {
            sb.append("none");
        }
        Utl.sendPluginMessage(plg, sender, sb.toString());
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
        return gates.getWorldList();
    }
}
