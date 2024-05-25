
package jp.minecraftuser.ecogate.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jp.minecraftuser.ecoframework.PluginFrame;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * ゲート定義YAMLファイルクラス
 * @author ecolight
 */
public class LoaderGate extends LoaderYaml {
    private HashMap<String, Location> locmap; // ゲート座標リスト<ゲート名, 座標>
    private HashMap<String, String> gatemap;  // リンク定義<ゲート名, 接続先ゲート名>
    private HashMap<String, String> textlist = new HashMap<>(); // ゲート説明文定義 <ゲート名, 説明文>
    private PluginFrame plg = null;
    private boolean init = true;

    /**
     * コンストラクタ
     * @param plg プラグインフレームインスタンス
     */
    public LoaderGate(PluginFrame plg) {
        super(plg,"Gates.yml");
        this.plg = plg;
        locmap = new HashMap<>();
        gatemap = new HashMap<>();
        loadAllGate();
        log.info("AllGateConfigLoaded.");
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
        HashMap<String, String> worldlist = new HashMap<>();
        HashMap<String, String> xlist = new HashMap<>();
        HashMap<String, String> ylist = new HashMap<>();
        HashMap<String, String> zlist = new HashMap<>();
        HashMap<String, String> yawlist = new HashMap<>();
        HashMap<String, String> pitchlist = new HashMap<>();

        ArrayList<String> linknamelist = new ArrayList<>();
        HashMap<String, String> linknamelistb = new HashMap<>();

        // 全行の設定からゲート/リンク定義を分割して各マップに保存しておく
        for (String obj : root.keySet()) {
            String[] line = obj.split("\\.");
            if (line.length != 3) continue;
            if (line[0].equalsIgnoreCase("gates")) {
                // ゲート定義読み込み
                String name = line[1];
                if (!namelist.contains(name)) { namelist.add(name); }
                String world = null;
                String xstr = null;
                String ystr = null;
                String zstr = null;
                String yawstr = null;
                String pitchstr = null;
                String text = null;
                if (line[2].equalsIgnoreCase("world")) world = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("x")) xstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("y")) ystr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("z")) zstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("yaw")) yawstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("pitch")) pitchstr = conf.getString("Gates."+name + "."+line[2]);
                if (line[2].equalsIgnoreCase("text")) text = conf.getString("Gates."+name + "."+line[2]);
                if (world != null) { worldlist.put(name, world); }
                if (xstr != null) { xlist.put(name, xstr); }
                if (ystr != null) { ylist.put(name, ystr); }
                if (zstr != null) { zlist.put(name, zstr); }
                if (yawstr != null) { yawlist.put(name, yawstr); }
                if (pitchstr != null) { pitchlist.put(name, pitchstr); }
                if (text != null) { textlist.put(name, text); }
            } else if (line[0].equalsIgnoreCase("links")) {
                // リンク定義読み込み
                String linkname = line[1];
                if (!linknamelist.contains(linkname)) { linknamelist.add(linkname); }
                String linknameb = null;
                if (line[2].equalsIgnoreCase("connection")) linknameb = conf.getString("Links."+linkname + "."+line[2]);
                if (linknameb != null) { linknamelistb.put(linkname, linknameb); }
            }
        }
        // 必要な情報を読み切れていれば登録する（ゲート定義）
        for (String g: namelist) {
            if ( worldlist.containsKey(g) &&
                xlist.containsKey(g) &&
                ylist.containsKey(g) &&
                zlist.containsKey(g) &&
                yawlist.containsKey(g) &&
                pitchlist.containsKey(g)) {
                World w = plg.getServer().getWorld(worldlist.get(g));
                if (w == null) {
                    log.warning("ゲート定義ワールド未ロードエラー["+worldlist.get(g) +"]");
                    continue;
                }
                try {
                Location loc = new Location(
                        w,
                        Double.parseDouble(xlist.get(g)),
                        Double.parseDouble(ylist.get(g)),
                        Double.parseDouble(zlist.get(g)),
                        Float.parseFloat(yawlist.get(g)),
                        Float.parseFloat(pitchlist.get(g)));
                loc.getChunk().load();
                locmap.put(g, loc);
                    log.info("ゲート定義読み込み["+g+"] x:"+xlist.get(g)+" y:"+ylist.get(g)+" z:"+zlist.get(g)+" yaw:"+yawlist.get(g)+" pitch:"+pitchlist.get(g));
                } catch (Exception e) {
                    log.warning("ゲート定義パースエラー["+g+"] x:"+xlist.get(g)+" y:"+ylist.get(g)+" z:"+zlist.get(g)+" yaw:"+yawlist.get(g)+" pitch:"+pitchlist.get(g));
                }
            } else {
                if (!worldlist.containsKey(g)) {
                    log.warning("ゲート定義未定義エラー[world]");
                } else if (!xlist.containsKey(g)) {
                    log.warning("ゲート定義未定義エラー[x]");
                } else if (!ylist.containsKey(g)) {
                    log.warning("ゲート定義未定義エラー[y]");
                } else if (!zlist.containsKey(g)) {
                    log.warning("ゲート定義未定義エラー[z]");
                } else if (!yawlist.containsKey(g)) {
                    log.warning("ゲート定義未定義エラー[yaw]");
                } else if (!pitchlist.containsKey(g)) {
                    log.warning("ゲート定義未定義エラー[pitch]");
                }                
            }
        }
        // 必要な情報を読み切れていれば登録する（リンク定義）
        for (String l: linknamelist) {
            if ( linknamelistb.containsKey(l)) {
                if (!locmap.containsKey(l)) {
                    log.warning("ゲートリンク定義のうちゲート["+l+"]が見つかりませんでした");
                } else if (!locmap.containsKey(linknamelistb.get(l))) {
                    log.warning("ゲートリンク定義のうちゲート["+linknamelistb.get(l)+"]が見つかりませんでした");
                } else {
                    gatemap.put(l, linknamelistb.get(l));
                }
            }
        }
    }
    
    /**
     * ゲート検索（名前指定）
     * @param name ゲート名
     * @return 定義存在有無
     */
    public boolean contains(String name) {
        return locmap.containsKey(name);
    }
    
    /**
     * ゲート検索（座標指定）
     * @param loc 検索座標
     * @return 指定座標にゲートがあるかどうか
     */
    public boolean contains(Location loc) {
        for (Map.Entry<String, Location> e: locmap.entrySet()) {
            Location l = e.getValue();
            if (!loc.getWorld().getName().equals(l.getWorld().getName())) continue;
            if (loc.getBlockX() != l.getBlockX()) continue;
            if (loc.getBlockY() != l.getBlockY()) continue;
            if (loc.getBlockZ() != l.getBlockZ()) continue;
            return true;
        }
        return false;
    }
    
    /**
     * ゲート検索（座標およびオフセット指定）
     * @param loc 検索座標
     * @param x ずらす検索座標のX座標補正値
     * @param y ずらす検索座標のY座標補正値
     * @param z ずらす検索座標のZ座標補正値
     * @return 指定座標にゲートがあるかどうか
     */
    public boolean contains(Location loc, int x, int y, int z) {
        for (Map.Entry<String, Location> e: locmap.entrySet()) {
            Location l = e.getValue();
            if (!loc.getWorld().getName().equals(l.getWorld().getName())) continue;
            if (loc.getBlockX() != l.getBlockX() + x) continue;
            if (loc.getBlockY() != l.getBlockY() + y) continue;
            if (loc.getBlockZ() != l.getBlockZ() + z) continue;
            return true;
        }
        return false;
    }
    
    /**
     * ゲート検索（チャンク指定）
     * @param c 検索チャンク
     * @return 指定チャンクにゲートがあるかどうか
     */
    public boolean contains(Chunk c) {
        int x = c.getX();
        int z = c.getZ();
        int xmin = x * 16;
        int xmax = (x + 1) * 16;
        int zmin = z * 16;
        int zmax = (z + 1) * 16;
        for (Map.Entry<String, Location> e: locmap.entrySet()) {
            Location l = e.getValue();
            // 違うワールドは無視
            if (!c.getWorld().getName().equals(l.getWorld().getName())) continue;
            int locx = l.getBlockX();
            int locz = l.getBlockZ();
            if (locx < xmin) continue;
            if (locz < zmin) continue;
            if (locx >= xmax) continue;
            if (locz >= zmax) continue;
            return true;
        }
        return false;
    }
    
    /**
     * リンク済みゲート判定
     * @param name
     * @return 
     */
    public boolean isLinked(String name) {
        return gatemap.containsKey(name);
    }
    
    /**
     * 転送先ゲート座標取得（座標指定）
     * @param loc 転送前ゲート座標
     * @return 転送先ゲート座標
     */
    public Location search(Location loc) {
        return search(loc, 0, 0, 0);
    }
    
    /**
     * 転送先ゲート座標取得（座標およびオフセット指定）
     * @param loc 転送前ゲート座標
     * @param x ずらす転送前ゲート位置のX座標補正値
     * @param y ずらす転送前ゲート位置のY座標補正値
     * @param z ずらす転送前ゲート位置のZ座標補正値
     * @return 転送先ゲート座標
     */
    public Location search(Location loc, int x, int y, int z) {        
        for (Map.Entry<String, Location> e: locmap.entrySet()) {
            Location l = e.getValue();
            if (!loc.getWorld().getName().equals(l.getWorld().getName())) continue;
            if (loc.getBlockX() != l.getBlockX() + x) continue;
            if (loc.getBlockY() != l.getBlockY() + y) continue;
            if (loc.getBlockZ() != l.getBlockZ() + z) continue;
            // ゲートがヒットしたので相手のゲートを探す
            if (!gatemap.containsKey(e.getKey())) continue;
            // あったので相手のゲート名を取得
            String name = gatemap.get(e.getKey());
            // 相手のゲートの座標データがあるか確認する
            if (!locmap.containsKey(name)){continue;}
            
            // あったら相手のゲート座標を抽出して、少し補正して返す
            Location buf = locmap.get(name);
            return new Location(buf.getWorld(), buf.getBlockX()+0.5-x, buf.getBlockY()-y, buf.getBlockZ()+0.5-z, buf.getYaw(), buf.getPitch());
        }
        
        return null;
    }

    /**
     * 付近のゲートを取得する
     *
     * @param loc        ロケーション
     * @param link_check 接続先が存在するゲートのみを取得するか
     * @return 付近のゲート及び接続されたゲート
     */
    public String nearGateSearch(Location loc, boolean link_check) {
        String nearGateName = "null";
        double nearDistance = 999999999;
        boolean found = false;
        for (Map.Entry<String, Location> e : locmap.entrySet()) {
            String gate_name = e.getKey();
            Location get_loc = e.getValue();
            if (!loc.getWorld().getName().equals(get_loc.getWorld().getName())) continue;
            if (loc.distance(get_loc) < nearDistance) {
                String name = gatemap.get(gate_name);
                // 相手のゲートの座標データがあるか確認する
                if (link_check) {
                    if (!locmap.containsKey(name)) {
                        continue;
                    }
                }
                found = true;
                nearDistance = loc.distance(get_loc);
                nearGateName = gate_name;
            }
        }
        if (found) {
            return nearGateName;
        }
        return "null";
    }

    /**
     * 該当ゲートの接続先ゲート名を取得する
     *
     * @param gate_name ゲート名
     * @return 接続先ゲート名
     */
    public String getLinkGateName(String gate_name) {
        String link_gate_name = "null";
        if (locmap.containsKey(gate_name)) {
            link_gate_name = gatemap.get(gate_name);
            if (locmap.containsKey(link_gate_name)) {
                return link_gate_name;
            }
        }
        return "null";
    }

    /**
     * 該当ゲートのtextを取得する
     *
     * @param gate_name ゲート名
     * @return ゲートテキスト
     */
    public String getGateText(String gate_name) {
        if (textlist.containsKey(gate_name)) {
            return textlist.get(gate_name);
        }
        return "null";
    }
    /**
     * 該当ゲートのLocationを取得する
     * @param gate_name ゲート名
     * @return ゲートテキスト
     */
    public Location getGateLocation(String gate_name) {
        if (locmap.containsKey(gate_name)) {
            return locmap.get(gate_name);
        }
        return null;
    }

    /**
     * ゲート追加処理
     * @param name ゲート名
     * @param loc ゲート座標
     * @return ゲート追加成否
     */
    public boolean addGate(String name, Location loc) {
        Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getYaw(), loc.getPitch());
        locmap.put(name, l);
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Gates."+name+".world", l.getWorld().getName());
        list.set("Gates."+name+".x", l.getBlockX());
        list.set("Gates."+name+".y", l.getBlockY());
        list.set("Gates."+name+".z", l.getBlockZ());
        list.set("Gates."+name+".yaw", l.getYaw());
        list.set("Gates."+name+".pitch", l.getPitch());
        saveCnf();
        return true;
    }

    /**
     * ゲート追加処理（説明文付き）
     * @param name ゲート名
     * @param loc ゲート座標
     * @param text ゲート説明文
     * @return ゲート追加成否
     */
    public boolean addGate(String name, Location loc, String text) {
        if (!addGate(name, loc)) return false;
        textlist.put(name, text);
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Gates."+name+".text", text);
        saveCnf();
        return true;
    }
    
    /**
     * ゲート削除処理
     * @param name ゲート名
     * @return ゲート削除成否
     */
    public boolean deleteGate(String name) {
        if (!locmap.containsKey(name)) { return false; }
        // 指定ゲートを削除する
        locmap.remove(name);
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Gates."+name, null);
        // 指定ゲートのリンクを削除する
        // まず該当ゲートがリンク済みか調べる
        if (gatemap.containsKey(name)) {
            // リンクしている状態であれば、接続先ゲートの定義を調べる
            if (gatemap.containsKey(gatemap.get(name))) {
                // 接続先ゲートがあれば、その定義を削除する
                list.set("Links."+gatemap.get(gatemap.get(name)), null);
                gatemap.remove(gatemap.get(name));
            }
            // 削除対象ゲートのリンク定義を削除する
            list.set("Links."+gatemap.get(name), null);
            gatemap.remove("name");
        }
        saveCnf();
        return true;
    }
    
    /**
     * ゲート接続設定処理
     * ゲート名を2つ指定すると、そのゲート間で双方向の接続を行う
     * @param name1 接続元ゲート名
     * @param name2 接続席ゲート名
     * @return 接続結果
     */
    public boolean linkAddGate(String name1, String name2) {
        if (!locmap.containsKey(name1)) { return false; }
        if (!locmap.containsKey(name2)) { return false; }
        if (gatemap.containsKey(name1)) { return false; }
        if (gatemap.containsKey(name2)) { return false; }
        gatemap.put(name1, name2);
        gatemap.put(name2, name1);
        reloadCnf();
        FileConfiguration list = getCnf();
        list.options().copyDefaults(true);
        list.set("Links."+name1+".connection", name2);
        list.set("Links."+name2+".connection", name1);
        saveCnf();
        return true;
    }

    /**
     * ゲート接続解除処理
     * ゲート名を指定すると、そのゲートと接続先のゲート間のリンク定義を削除する
     * @param name ゲート名指定
     * @return 解除した接続先のゲート名を返す
     */
    public String linkDelGate(String name) {
        if (!locmap.containsKey(name)) { return null; }
        if (!gatemap.containsKey(name)) { return null; }
        // 指定ゲートの接続ゲートを取得してリンク解除する
        String name2 = gatemap.get(name);
        // 指定ゲートの接続ゲートがある場合
        if (!gatemap.containsKey(name2)) {
            // 接続ゲート側のリンク定義が存在しない場合は、指定ゲートのリンク定義のみ削除して終わる
            gatemap.remove(name);
            reloadCnf();
            FileConfiguration list = getCnf();
            list.options().copyDefaults(true);
            list.set("Links."+name, null);
            saveCnf();
        } else {
            // 接続ゲート側のリンク定義が存在する場合は、両方のゲートのリンク定義を削除して終わる
            gatemap.remove(name);
            gatemap.remove(name2);
            reloadCnf();
            FileConfiguration list = getCnf();
            list.options().copyDefaults(true);
            list.set("Links."+name, null);
            list.set("Links."+name2, null);
            saveCnf();
        }        
        return name2;
    }
    
    /**
     * ゲート説明文取得処理
     * @param loc ゲート座標指定
     * @return ゲート説明文返却
     */
    public String getText(Location loc) {
        for (Map.Entry<String, Location> e: locmap.entrySet()) {
            Location l = e.getValue();
            if (!loc.getWorld().getName().equals(l.getWorld().getName())) continue;
            if (loc.getBlockX() != l.getBlockX()) continue;
            if (loc.getBlockY() != l.getBlockY()) continue;
            if (loc.getBlockZ() != l.getBlockZ()) continue;
            if (!textlist.containsKey(e.getKey())) return null;
            return textlist.get(e.getKey());
        }
        return null;
    }
}
