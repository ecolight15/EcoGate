
package jp.minecraftuser.ecogate.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * ワールド定義YAMLファイルクラス
 * @author ecolight
 */
public class LoaderWorld extends LoaderYaml {
    public ArrayList<World> worldMap = null;
    private PluginFrame plg = null;

    /**
     * コンストラクタ
     * @param plg プラグインフレームインスタンス
     */
    public LoaderWorld(PluginFrame plg) {
        super(plg,"Worlds.yml");
        this.plg = plg;
        loadAllWorld();
    }

    /**
     * 全ワールド定義読み込み処理
     */
    public void loadAllWorld() {
        worldMap = new ArrayList<>();
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        saveCnf();
        
        Configuration conf = list.getRoot();
        Map<String, Object> root = conf.getValues(true);
        ArrayList<String> namelist = new ArrayList<>();
        HashMap<String, String> typelist = new HashMap<>();
        HashMap<String, String> seedlist = new HashMap<>();
        HashMap<String, String> envlist = new HashMap<>();
        for (String obj : root.keySet()) {
            String[] line = obj.split("\\.");
            if (line.length != 2) continue;

            // 後はセクションごとに読みだしてあれこれする
            String name = line[0];
            if (!namelist.contains(name)) { namelist.add(name); }
            String type = null;
            String seed = null;    
            String env = null;

            // [0]にワールド名があったとして、各設定情報が足りてればロードする
            if (line[1].equalsIgnoreCase("type")) type = conf.getString(name + "."+line[1]);
            if (line[1].equalsIgnoreCase("seed")) seed = conf.getString(name + "."+line[1]);
            if (line[1].equalsIgnoreCase("env")) env = conf.getString(name + "."+line[1]);
            if (type != null) { typelist.put(name, type); }
            if (seed != null) { seedlist.put(name, seed); }
            if (env != null) { envlist.put(name, env); }
        }
        for (String wl: namelist) {
            if ( typelist.containsKey(wl) && seedlist.containsKey(wl) && envlist.containsKey(wl) ) {
                World w = loadWorld(wl, typelist.get(wl), seedlist.get(wl), envlist.get(wl));
                if (w == null) continue;
            }
        }
    }
    
    /**
     * ワールド読み込み処理（Bukkitクラス指定）
     * @param name ワールド名
     * @param type ワールド種別
     * @param seed ワールドシード値
     * @param env ワールド環境
     * @return ワールド読み込み成否
     */
    public World loadWorld(String name, WorldType type, long seed, World.Environment env) {
        return loadWorld(name, type.name(), Long.toString(seed), env.name());
    }

    /**
     * ワールド読み込み処理（文字列指定）
     * @param name ワールド名
     * @param type ワールド種別
     * @param seed ワールドシード値
     * @param env ワールド環境
     * @return ワールド読み込み成否
     */
    public World loadWorld(String name, String type, String seed, String env) {

        // [0]にワールド名があったとして、各設定情報が足りてればロードする
        if ((name == null) || (type == null) || (seed == null)|| (env == null)) {
            log.warning("World:"+name+"(値指定不備[name:"+name+"][type:"+type+"][seed:"+seed+"][environment:"+env+"])"); return null;
        } else {
            log.info("World::"+name+":[type:"+type+"][seed:"+seed+"][environment:"+env+"])");
        }
        
        // の前にチェック
        WorldType wt = WorldType.valueOf(type);
        if (wt == null) { log.warning("World:"+name+"(WorldType異常:"+type+")"); return null; }
        long seedval;
        try {
            seedval = Long.parseLong(seed);
        } catch (NumberFormatException nfex) {
            log.warning("World:"+name+"(Seed異常:"+seed+")");
            return null;
        }
        World.Environment we = World.Environment.valueOf(env);
        if (we == null) { log.warning("World:"+name+"(WorldEnvironment異常:"+env+")"); return null; }

        World w = plg.getServer().getWorld(name);
        if (w != null) {
            log.info("World:"+name+"(ロード済みワールド呼び出し)");
            // reloadなどでこのルートに入るので、mapになければ入れておく。
            worldMap.add(w);
            return w;
        }
        WorldCreator wc = WorldCreator.name(name);
        wc = wc.type(wt);
        wc = wc.seed(seedval);
        wc = wc.environment(World.Environment.valueOf(env));

        Utl.sendPluginMessage(plg, null, "新規ワールドを読み込んでいます... 負荷により切断される場合があります");
        plg.getServer().savePlayers();
        for (World wl: plg.getServer().getWorlds()) {
            wl.save();
        }
        Utl.sendPluginMessage(plg, null, "新規ワールドデータを読み込み中...");
        World world = wc.createWorld();
        worldMap.add(world);
        log.info("WorldLoading:"+world.getName()+":type["+wc.type().getName()+"]:seed["+wc.seed()+"]:env["+wc.environment().name()+"]");
        Utl.sendPluginMessage(plg, null, "ワールド[{0}]を読み込みました", world.getName());
        
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set(name+".type", wc.type().name());
        list.set(name+".seed", wc.seed());
        list.set(name+".env", wc.environment().name());
        saveCnf();
        return world;
    }
    
    /**
     * 通常ワールド追加処理
     * @param name ワールド名
     * @return ワールド追加成否
     */
    public World addNormalWorld(String name) {
        return loadWorld(name, WorldType.NORMAL, new Random().nextLong(), World.Environment.NORMAL);
    }
    
    /**
     * ネザーワールド追加処理
     * @param name ワールド名
     * @return ワールド追加成否
     */
    public World addNetherWorld(String name) {
        return loadWorld(name, WorldType.NORMAL, new Random().nextLong(), World.Environment.NETHER);
    }

    /**
     * ジエンドワールド追加処理
     * @param name ワールド名
     * @return ワールド追加成否
     */
    public World addEndWorld(String name) {
        return loadWorld(name, WorldType.NORMAL, new Random().nextLong(), World.Environment.THE_END);
    }
    
    /**
     * ラージバイオームワールド追加処理
     * @param name ワールド名
     * @return ワールド追加成否
     */
    public World addLargeWorld(String name) {
        return loadWorld(name, WorldType.LARGE_BIOMES, new Random().nextLong(), World.Environment.NORMAL);
    }
    
    /**
     * スーパーフラットワールド追加処理
     * @param name ワールド名
     * @return ワールド追加成否
     */
    public World addFlatWorld(String name) {
        return loadWorld(name, WorldType.FLAT, new Random().nextLong(), World.Environment.NORMAL);
    }

    /**
     * アンプリファイドワールド追加処理
     * @param name ワールド名
     * @return ワールド追加成否
     */
    public World addAmplifiedWorld(String name) {
        return loadWorld(name, WorldType.AMPLIFIED, new Random().nextLong(), World.Environment.NORMAL);
    }

    /**
     * ワールド削除処理
     * @param name ワールド名
     * @return ワールド削除成否
     */
    public boolean deleteWorld(String name) {
        boolean result = false;
        Utl.sendPluginMessage(plg, null, "ワールド[{0}]を切断しています...", name);
        plg.getServer().savePlayers();
        for (World wl: plg.getServer().getWorlds()) {
            wl.save();
        }
        List <World> wl = plg.getServer().getWorlds();
        for (World w: wl) {
            if (name.equalsIgnoreCase(w.getName())) {
                Utl.sendPluginMessage(plg, null, "ワールド[{0}]を切断中です...", w.getName());
                plg.getServer().unloadWorld(w, true);
                reloadCnf();
                FileConfiguration list = getCnf();
                list.options().copyDefaults(true);
                list.set(w.getName(), null);
                saveCnf();
                worldMap.remove(w);
                Utl.sendPluginMessage(plg, null, "ワールド[{0}]を切断しました", w.getName());
                result = true;
                break;
            }
        }
        if (!result) { Utl.sendPluginMessage(plg, null, "ワールド[{0}]の切断に失敗しました", name); }
        return result;
    }
}
