
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderWorld;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ワールド一覧表示コマンドクラス
 * @author ecolight
 */
public class WorldListCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public WorldListCommand(PluginFrame plg_, String name_) {
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
        return "ecogate.world.list";
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
        if (!checkRange(sender, args, 0, 1)) return true;

        // ワールドの一覧表示
        LoaderWorld worlds = ecgConf.getWorlds();
        Utl.sendPluginMessage(plg, sender, "ワールド一覧表示");
        if (sender.hasPermission("ecogate.world.list.seed")) {
            for (World w : worlds.worldMap) {
                if ((args.length == 1) && (!args[0].equalsIgnoreCase(w.getName()))) continue;
                Utl.sendPluginMessage(plg, sender, "world={0}, env={1}, type={2}, seed={3}",
                        w.getName(),
                        w.getEnvironment().name(),
                        w.getWorldType().getName(),
                        Long.toHexString(w.getSeed()));
            }
        } else {
            for (World w : worlds.worldMap) {
                if ((args.length == 1) && (!args[0].equalsIgnoreCase(w.getName()))) continue;
                Utl.sendPluginMessage(plg, sender, "world={0}, env={1}, type={2}",
                        w.getName(),
                        w.getEnvironment().name(),
                        w.getWorldType().getName());
            }
        }
        return true;
    }
    
}
