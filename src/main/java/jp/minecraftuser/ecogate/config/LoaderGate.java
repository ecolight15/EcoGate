
package jp.minecraftuser.ecogate.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.minecraftuser.ecoframework.ConfigFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecogate.exception.InvalidStateException;
import jp.minecraftuser.ecogate.struct.Gate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * ゲート定義YAMLファイルクラス
 * @author ecolight
 */
public class LoaderGate extends LoaderYaml {
    private HashMap<String, HashMap<String, Gate>> worldGateMap; // ゲート座標マップ<ワールド, <ゲート名, 座標>>
    private HashMap<String, Gate> gateDataMap; // ゲート定義リスト<ゲート名, 座標>
    private ArrayList<String> unloadWorldGateNameList; // 未ロードワールドのゲート名リスト
    private HashMap<String, String> worldlist;
    private PluginFrame plg = null;
    private ConfigFrame defConf;

    /**
     * コンストラクタ
     * @param plg_ プラグインフレームインスタンス
     */
    public LoaderGate(PluginFrame plg_, ConfigFrame config_) {
        super(plg_,"Gates.yml");
        defConf = config_;
        defConf.registerString("server");
        this.plg = plg_;
        gateDataMap = new HashMap<>();
        unloadWorldGateNameList = new ArrayList<>();
        worldGateMap = new HashMap<>();
        for (World world : plg_.getServer().getWorlds()) {
            worldGateMap.put(world.getName(), new HashMap<>());
        }
        loadAllGate();
        log.info("AllGateConfigLoaded.");
    }

    /**
     * ゲート定義リスト取得
     * @return ゲート定義リスト
     */
    public ArrayList<String> getGateNameList() {
        return new ArrayList<String>(gateDataMap.keySet());
    }

    /**
     * ゲート定義リスト取得(ワールド名指定)
     * @return ゲート定義リスト
     */
    public ArrayList<String> getGateNameList(String worldName) {
        ArrayList<String> ret = new ArrayList<>();
        for  (Gate gate : gateDataMap.values()) {
            if (gate.worldName.equals(worldName)) {
                ret.add(gate.name);
            }
        }
        return ret;
    }

    /**
     * ワールドなしでロードサれなかったゲート定義のリスト取得
     * @return ゲート定義リスト
     */
    public ArrayList<String> getUnloadWorldGateNameList() {
        return unloadWorldGateNameList;
    }

    /**
     * ワールドリスト取得
     * @return ワールドリスト
     */
    public ArrayList<String> getWorldList() {
        ArrayList<String> ret = new ArrayList<>();
        for (String worldName : worldlist.values()) {
            if (!ret.contains(worldName)) {
                ret.add(worldName);
            }
        }
        return ret;
    }

    /**
     * 全ゲート設定読み込み処理
     */
    public void loadAllGate() {
        // 設定ファイルロード
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        saveCnf();
        
        // YAMLの全行を取得する
        Configuration conf = list.getRoot();
        Map<String, Object> root = conf.getValues(true);

        ArrayList<String> namelist = new ArrayList<>();
        HashMap<String, String> serverlist = new HashMap<>();
        worldlist = new HashMap<>();
        HashMap<String, String> xlist = new HashMap<>();
        HashMap<String, String> ylist = new HashMap<>();
        HashMap<String, String> zlist = new HashMap<>();
        HashMap<String, String> yawlist = new HashMap<>();
        HashMap<String, String> pitchlist = new HashMap<>();
        HashMap<String, String> textlist = new HashMap<>();

        ArrayList<String> linkGatelist = new ArrayList<>();
        HashMap<String, String> linkGateMap = new HashMap<>();

        // 全行の設定からゲート/リンク定義を分割して各マップに保存しておく
        for (String obj : root.keySet()) {
            String[] line = obj.split("\\.");
            if (line.length != 3) continue;
            if (line[0].equalsIgnoreCase("gates")) {
                // ゲート定義読み込み
                // Gate定義サンプル
                //  100b:
                //    world: home01
                //    x: 1290
                //    y: 64
                //    z: 2823
                //    yaw: 223.48871
                //    pitch: -7.49999
                //    text: '&e～100タウン～'
                //  '100':
                //    world: world
                //    x: -70
                //    y: 60
                //    z: -5
                //    yaw: 2.1191406
                //    pitch: -6.1243014
                //    text: '&e～エントランス～'
                String name = line[1];
                if (!namelist.contains(name)) { namelist.add(name); }
                String server = null;
                String world = null;
                String xstr = null;
                String ystr = null;
                String zstr = null;
                String yawstr = null;
                String pitchstr = null;
                String text = null;
                if (line[2].equalsIgnoreCase("server")) server = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("world")) world = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("x")) xstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("y")) ystr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("z")) zstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("yaw")) yawstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("pitch")) pitchstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("text")) text = conf.getString("Gates."+name + "."+line[2]);
                if (server != null) { serverlist.put(name, server); }
                if (world != null) { worldlist.put(name, world); }
                if (xstr != null) { xlist.put(name, xstr); }
                if (ystr != null) { ylist.put(name, ystr); }
                if (zstr != null) { zlist.put(name, zstr); }
                if (yawstr != null) { yawlist.put(name, yawstr); }
                if (pitchstr != null) { pitchlist.put(name, pitchstr); }
                if (text != null) { textlist.put(name, text); }
            } else if (line[0].equalsIgnoreCase("links")) {
                // リンク定義読み込み
                // リンク定義サンプル
                //  '100':
                //    connection: 100b
                //  100b:
                //    connection: '100'
                String linkname = line[1];
                if (!linkGatelist.contains(linkname)) { linkGatelist.add(linkname); } // 転移元ゲート名
                String linknameb = null;
                if (line[2].equalsIgnoreCase("connection")) {
                    linknameb = conf.getString("Links." + linkname + "."+line[2]); // 転移先ゲート名
                    linkGateMap.put(linkname, linknameb);
                }
            }
        }
        // 必要な情報を読み切れていれば登録する（ゲート定義）
        for (String gateName: namelist) {
            if (!serverlist.containsKey(gateName)) {
                // サーバー名が設定されていない場合は設定ファイルのサーバー名を格納する
                String serverName = defConf.getString("server");
                serverlist.put(gateName, serverName);
                // 現在のサーバーのワールドであれば、サーバー設定を更新する
                if (worldlist.containsKey(gateName)) {
                    ArrayList<String> localList = new ArrayList<>();
                    for (World world : plg.getServer().getWorlds()) {
                        localList.add(world.getName());
                    }
                    if (localList.contains(worldlist.get(gateName))) {
                        list.set("Gates." + gateName + ".server", serverName);
                        saveCnf();
                    }
                }
            }
            if (worldlist.containsKey(gateName) &&
                xlist.containsKey(gateName) &&
                ylist.containsKey(gateName) &&
                zlist.containsKey(gateName) &&
                yawlist.containsKey(gateName) &&
                pitchlist.containsKey(gateName)) {
                World world = plg.getServer().getWorld(worldlist.get(gateName));
                try {
                    if (world == null) {
                        log.warning("ゲート定義ワールド未ロードエラー["+worldlist.get(gateName) +"]");
                        unloadWorldGateNameList.add(gateName);
                        // ワールドが未ロードの場合、ワールド名だけでGate定義を登録する
                        Gate gate = new Gate(this, serverlist.get(gateName), gateName,
                            worldlist.get(gateName),
                            Double.parseDouble(xlist.get(gateName)),
                            Double.parseDouble(ylist.get(gateName)),
                            Double.parseDouble(zlist.get(gateName)),
                            Float.parseFloat(yawlist.get(gateName)),
                            Float.parseFloat(pitchlist.get(gateName)),
                            textlist.get(gateName));
                        gate.unload = true;
                        // 全ワールド共通リスト二追加
                        gateDataMap.put(gateName, gate);
                    } else {
                        Location loc = new Location(
                            world,
                            Double.parseDouble(xlist.get(gateName)),
                            Double.parseDouble(ylist.get(gateName)),
                            Double.parseDouble(zlist.get(gateName)),
                            Float.parseFloat(yawlist.get(gateName)),
                            Float.parseFloat(pitchlist.get(gateName)));
                        loc.getChunk().load();
                        Gate gate = new Gate(this, serverlist.get(gateName), gateName, loc, textlist.get(gateName));
                        // 全ワールド共通リスト二追加
                        gateDataMap.put(gateName, gate);
                        // ワールド別リストに追加
                        worldGateMap.get(world.getName()).put(gateName, gateDataMap.get(gateName));
                    }

                    log.info("ゲート定義読み込み["+gateName+"](" + serverlist.get(gateName) + ") x:"+xlist.get(gateName)+" y:"+ylist.get(gateName)+" z:"+zlist.get(gateName)+" yaw:"+yawlist.get(gateName)+" pitch:"+pitchlist.get(gateName));
                } catch (Exception e) {
                    log.warning("ゲート定義パースエラー["+gateName+"] x:"+xlist.get(gateName)+" y:"+ylist.get(gateName)+" z:"+zlist.get(gateName)+" yaw:"+yawlist.get(gateName)+" pitch:"+pitchlist.get(gateName));
                }
            } else {
                if (!worldlist.containsKey(gateName)) {
                    log.warning("ゲート定義未定義エラー[world]");
                } else if (!xlist.containsKey(gateName)) {
                    log.warning("ゲート定義未定義エラー[x]");
                } else if (!ylist.containsKey(gateName)) {
                    log.warning("ゲート定義未定義エラー[y]");
                } else if (!zlist.containsKey(gateName)) {
                    log.warning("ゲート定義未定義エラー[z]");
                } else if (!yawlist.containsKey(gateName)) {
                    log.warning("ゲート定義未定義エラー[yaw]");
                } else if (!pitchlist.containsKey(gateName)) {
                    log.warning("ゲート定義未定義エラー[pitch]");
                }                
            }
        }
        // 必要な情報を読み切れていれば登録する（リンク定義）
        for (String linknameA : linkGatelist) {                 // 転移元ゲート名のリストでループ
            if (linkGateMap.containsKey(linknameA)) {           // 転移先ゲート名とペアにしてmapに登録してある(ペアが見つかっている)
                if (!gateDataMap.containsKey(linknameA)) {      // 転移元ゲートがゲート一覧にない
                    log.warning("ゲートリンク定義のうちゲート["+ linknameA +"]が見つかりませんでした");
                } else {
                    String linknameB = linkGateMap.get(linknameA);
                    if (!gateDataMap.containsKey(linknameB)) {  // 転移先ゲートがゲート一覧にない
                        log.warning("ゲートリンク定義のうちゲート["+linkGateMap.get(linknameA)+"]が見つかりませんでした");
                    } else {                                    // どちらもゲート一覧にある
                        Gate gateA = gateDataMap.get(linknameA);
                        gateA.link = gateDataMap.get(linknameB);
                    }
                }
            }
        }
    }
    
    /**
     * ゲート検索（名前指定）
     * @param name_ ゲート名
     * @return 定義存在有無
     */
    public boolean contains(String name_) {
        return gateDataMap.containsKey(name_);
    }
    
    /**
     * ゲート検索（座標指定）
     * @param loc_ 検索座標
     * @return 指定座標にゲートがあるかどうか
     */
    public boolean contains(Location loc_) {
        return contains(loc_, 0, 0, 0);
    }

    /**
     * ゲート検索（座標およびオフセット指定）
     * @param loc_ 検索座標
     * @param x_ ずらす検索座標のX座標補正値
     * @param y_ ずらす検索座標のY座標補正値
     * @param z_ ずらす検索座標のZ座標補正値
     * @return 指定座標にゲートがあるかどうか
     */
    public boolean contains(Location loc_, int x_, int y_, int z_) {
        if (loc_.getWorld() == null) return false;
        String worldName = loc_.getWorld().getName();
        if (!worldGateMap.containsKey(worldName)) return false;
        for (Gate e: worldGateMap.get(worldName).values()) {
            if (e.isLocation(loc_, x_, y_, z_)) return true;
        }
        return false;
    }

    /**
     * ゲート検索（名前指定）
     * @param gateName_ ゲート名
     * @return ゲート
     */
    public Gate get(String gateName_) {
        return gateDataMap.get(gateName_);
    }

    /**
     * ゲート検索（座標指定）
     * @param loc_ ロケーション
     * @return ゲート
     */
    public Gate get(Location loc_) {
        if (!contains(loc_)) return null;
        if (loc_.getWorld() == null) return null;
        String worldName = loc_.getWorld().getName();
        for (Gate e: worldGateMap.get(worldName).values()) {
            if (e.isLocation(loc_)) return e;
        }
        return null;
    }

    /**
     * ゲート検索（座標およびオフセット指定）
     * @param loc_ ロケーション
     * @param x_ Xオフセット
     * @param y_ Yオフセット
     * @param z_ Zオフセット
     * @return ゲート
     */
    public Gate get(Location loc_, int x_, int y_, int z_) {
        if (!contains(loc_, x_, y_, z_)) return null;
        if (loc_.getWorld() == null) return null;
        String worldName = loc_.getWorld().getName();
        for (Gate e: worldGateMap.get(worldName).values()) {
            if (e.isLocation(loc_)) return e;
        }
        return null;
    }

    /**
     * リンク済みゲート判定
     * @param name リンク名
     * @return リンク状態
     */
    public boolean isLinked(String name) {
        if (!contains(name)) return false;
        return (get(name).link != null);
    }

    /**
     * 転送先ゲート座標取得（座標およびオフセット指定）
     * @param loc_ 転送前ゲート座標
     * @param x_ ずらす転送前ゲート位置のX座標補正値
     * @param y_ ずらす転送前ゲート位置のY座標補正値
     * @param z_ ずらす転送前ゲート位置のZ座標補正値
     * @return 転送先ゲート座標
     */
    public Gate search(Location loc_, int x_, int y_, int z_) {
        Gate gate = get(loc_, x_, y_, z_);
        if (gate == null) return null;
        if (gate.link == null) return null;
        return gate.link;
    }

    /**
     * 転送先ゲート座標取得（座標およびオフセット指定）
     * @param loc_ 転送前ゲート座標
     * @return 転送先ゲート座標
     */
    public Gate search(Location loc_) {
        Gate gate = get(loc_);
        if (gate == null) return null;
        if (gate.link == null) return null;
        return gate.link;
    }

    /**
     * 付近のゲートを取得する
     *
     * @param loc        ロケーション
     * @param link_check 接続先が存在するゲートのみを取得するか
     * @return 付近のゲート及び接続されたゲート
     */
    public Gate nearGateSearch(Location loc, boolean link_check) {
        if (loc.getWorld() == null) return null;
        //対象のロケーションにワールドにゲートが存在していなければnullを返す
        String world_name = loc.getWorld().getName();
        if (!worldGateMap.containsKey(world_name)) return null;

        Gate nearGate = null;
        double nearDistance = Double.MAX_VALUE;
        boolean found = false;
        for (Gate gate : worldGateMap.get(world_name).values()) {
            if (loc.distance(gate.loc) < nearDistance) {
                // 相手のゲートの座標データがあるか確認する
                if (link_check && gate.link == null) continue;
                found = true;
                nearDistance = loc.distance(gate.loc);
                nearGate = gate;
            }
        }
        if (found) {
            return nearGate;
        }
        return null;
    }

    /**
     * ゲート追加処理
     * @param name_ ゲート名
     * @param loc_ ゲート座標
     * @throws InvalidStateException 条件不備
     */
    public void addGate(String name_, Location loc_) throws InvalidStateException {
        addGate(name_, loc_, null);
    }

    /**
     * ゲート追加処理
     * @param name_ ゲート名
     * @param loc_ ゲート座標
     * @param text_ ゲート説明文
     * @throws InvalidStateException 条件不備
     */
    public void addGate(String name_, Location loc_, String text_) throws InvalidStateException {
        if (contains(name_)) throw new InvalidStateException("既にゲートが存在します"); // 既にゲートが存在する
        if (loc_.getWorld() == null) throw new InvalidStateException("ワールドが存在しません"); // ワールドが存在しない
        Gate gate = new Gate(this, defConf.getString("server"), name_, loc_, text_);
        gateDataMap.put(name_, gate);
        if (worldGateMap.containsKey(loc_.getWorld().getName())) {
            worldGateMap.get(loc_.getWorld().getName()).put(name_, gate);
        } else {
            worldGateMap.put(loc_.getWorld().getName(), new HashMap<>());
            worldGateMap.get(loc_.getWorld().getName()).put(name_, gate);
        }
        if (!worldlist.containsValue(loc_.getWorld().getName())) {
            worldlist.put(name_, loc_.getWorld().getName());
        }

        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Gates."+name_+".server", defConf.getString("server"));
        list.set("Gates."+name_+".world", gate.world.getName());
        list.set("Gates."+name_+".x", gate.getNormalizedLocation().getBlockX());
        list.set("Gates."+name_+".y", gate.getNormalizedLocation().getBlockY());
        list.set("Gates."+name_+".z", gate.getNormalizedLocation().getBlockZ());
        list.set("Gates."+name_+".yaw", gate.yaw);
        list.set("Gates."+name_+".pitch", gate.pitch);
        list.set("Gates."+name_+".text", text_);
        saveCnf();
    }

    /**
     * ゲート更新処理
     * @param name_ ゲート名
     * @param loc_ ゲート座標
     * @throws InvalidStateException 条件不備
     */
    public void updateGate(String name_, Location loc_) throws InvalidStateException {
        if (!contains(name_)) throw new InvalidStateException("ゲートが存在しません"); // ゲートが存在しない
        Gate gate = get(name_);
        updateGate(name_, loc_, gate.text);
    }

    /**
     * ゲート更新処理
     * @param name_ ゲート名
     * @param loc_ ゲート座標
     * @param text_ ゲート説明文
     * @throws InvalidStateException 条件不備
     */
    public void updateGate(String name_, Location loc_, String text_) throws InvalidStateException {
        if (!contains(name_)) throw new InvalidStateException("ゲートが存在しません"); // ゲートが存在しない
        if (loc_.getWorld() == null) throw new InvalidStateException("ワールドが存在しません"); // ワールドが存在しない

        Gate linkGate = get(name_).link;
        Gate gate = new Gate(this, defConf.getString("server"), name_, loc_, text_);
        gate.link = linkGate;
        if (linkGate != null) {
            linkGate.link = gate;
        }

        gateDataMap.replace(name_, gate);
        if (worldGateMap.containsKey(loc_.getWorld().getName())) {
            worldGateMap.get(loc_.getWorld().getName()).replace(name_, gate);
        }
        unloadWorldGateNameList.remove(name_);
        if (!worldlist.containsValue(loc_.getWorld().getName())) {
            worldlist.put(name_, loc_.getWorld().getName());
        }

        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Gates."+name_+".server", defConf.getString("server"));
        list.set("Gates."+name_+".world", gate.world.getName());
        list.set("Gates."+name_+".x", gate.getNormalizedLocation().getBlockX());
        list.set("Gates."+name_+".y", gate.getNormalizedLocation().getBlockY());
        list.set("Gates."+name_+".z", gate.getNormalizedLocation().getBlockZ());
        list.set("Gates."+name_+".yaw", gate.yaw);
        list.set("Gates."+name_+".pitch", gate.pitch);
        list.set("Gates."+name_+".text", text_);
        saveCnf();
    }

    /**
     * ゲート削除処理
     * @param name_ ゲート名
     * @throws InvalidStateException 条件不備
     */
    public void deleteGate(String name_) throws InvalidStateException {
        if (!gateDataMap.containsKey(name_)) throw new InvalidStateException("指定ゲートがありません");
        // 指定ゲートを削除する
        Gate gate = gateDataMap.get(name_);
        if (gate.world != null) {
            worldGateMap.get(gate.world.getName()).remove(name_);
        }
        gateDataMap.remove(name_);
        unloadWorldGateNameList.remove(name_);

        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Gates." + name_, null);
        // 指定ゲートのリンクを削除する
        // まず該当ゲートがリンク済みか調べる
        if (gate.link != null) {
            // リンク済みの場合は自分のゲードに加えて対抗リンクも削除する
            Gate other = gate.link;
            other.link = null;
            list.set("Links."+ other.name, null);
            list.set("Links."+ name_, null);
        }
        saveCnf();
    }
    
    /**
     * ゲート接続設定処理
     * ゲート名を2つ指定すると、そのゲート間で双方向の接続を行う
     * @param name1_ 接続元ゲート名
     * @param name2_ 接続先ゲート名
     * @throws InvalidStateException 条件不備
     */
    public void linkAddGate(String name1_, String name2_) throws InvalidStateException {
        if (!contains(name1_)) throw new InvalidStateException("1つ目のゲートが見つかりませんでした");
        if (!contains(name2_)) throw new InvalidStateException("2つ目のゲートが見つかりませんでした");
        Gate gate = get(name1_);

        // A to B のゲート接続のみする
        gate.link = get(name2_);

        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Links."+name1_+".connection", name2_);
        saveCnf();
    }

    /**
     * ゲート接続解除処理
     * ゲート名を指定すると、そのゲートと接続先のゲート間のリンク定義を削除する
     * @param name_ ゲート名指定
     * @return 解除した接続先のゲート名を返す
     */
    public String linkDelGate(String name_) throws InvalidStateException {
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        boolean isDelete;
        if (list.contains("Links." + name_)) {
            // コンフィグファイル上にリンク定義がある場合は削除する
            list.set("Links." + name_, null);
            isDelete = true;
            saveCnf();
        } else {
            isDelete = false;
        }

        if (!contains(name_)) {
            if (isDelete) {
                throw new InvalidStateException("1つ目のゲートが見つかりませんでしたが、リンク定義は削除されました");
            } else {
                throw new InvalidStateException("1つ目のゲートが見つかりませんでした");
            }
        }
        Gate gate1 = get(name_);
        if (gate1.link == null) {
            if (isDelete) {
                throw new InvalidStateException("指定したゲートはリンクされていませんが、リンク定義は削除されました");
            } else {
                throw new InvalidStateException("指定したゲートはリンクされていません");
            }
        }

        Gate gate2 = gate1.link;
        gate1.link = null;

        return gate2.name;
    }

}
