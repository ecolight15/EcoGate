
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderWorld;
import org.bukkit.command.CommandSender;

/**
 * ワールド削除コマンドクラス
 * @author ecolight
 */
public class WorldDelCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public WorldDelCommand(PluginFrame plg_, String name_) {
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
        return "ecogate.world.del";
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

        // 指定したワールドを削除する
        LoaderWorld worlds = ecgConf.getWorlds();
        if ( worlds.deleteWorld(args[0]) ) {
            Utl.sendPluginMessage(plg, sender, "ワールド[{0}]を削除しました",args[0]);
        }
        return true;
    }
}
