
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ゲート追加コマンドクラス
 * @author ecolight
 */
public class GateAddCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateAddCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecgConf = (EcoGateConfig)conf;
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "ecogate.gate.add";
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
        boolean result;
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i != 1) sb.append(" ");
                sb.append(args[i]);
            }
            // 説明文付きゲートを登録する
            result = gates.addGate(args[0], player.getLocation(), sb.toString());
        } else {
            // 説明文無しゲートを登録する
            result = gates.addGate(args[0], player.getLocation());
        }
        if (!result) {
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]の登録に失敗しました", args[0]);
        } else {
            Utl.sendPluginMessage(plg, sender, "指定されたゲート[{0}]を登録しました", args[0]);
        }
        return true;
    }
    
}
