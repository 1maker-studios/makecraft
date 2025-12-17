package com.metype.makecraft.utils;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DBUtils {
    private Connection connection;
    private Connection connection2;
    private Statement statement;
    private Statement statement2;
    private PreparedStatement insertRankStatement;
    private PreparedStatement getRankStatement;
    private PreparedStatement getRanksStatement;
    private PreparedStatement deleteRankStatement;

    private PreparedStatement getRankHolderStatement;
    private PreparedStatement getRankHoldersStatement;
    private PreparedStatement saveRankHolderStatement;

    private PreparedStatement saveInventoryStatement;
    private PreparedStatement getInventoryStatement;

    private static DBUtils instance;

    public static void init() throws SQLException {
        if (instance == null) {
            instance = new DBUtils();
        }
        instance.connection = DriverManager.getConnection("jdbc:sqlite:makecraft.db");
        instance.connection2 = DriverManager.getConnection("jdbc:sqlite:inv_data.db");
        instance.statement = instance.connection.createStatement();
        instance.statement2 = instance.connection2.createStatement();
        instance.statement.setQueryTimeout(10);
        instance.statement2.setQueryTimeout(10);
        instance.statement.execute("create table if not exists ranks (key string PRIMARY KEY, name string, rank_color string, name_color string)");
        instance.statement.execute("create table if not exists rank_owners (key string PRIMARY KEY, rank_list string, selected string)");
        instance.statement2.execute("create table if not exists inv_storage (key string, gamemode string, data string, PRIMARY KEY (key, gamemode))");
        instance.insertRankStatement = instance.connection.prepareStatement("insert into ranks values(?, ?, ?, ?) on conflict(key) do update SET name=excluded.name, rank_color=excluded.rank_color, name_color=excluded.name_color");
        instance.getRankStatement = instance.connection.prepareStatement("select * from ranks where key = ?");
        instance.getRanksStatement = instance.connection.prepareStatement("select * from ranks");
        instance.deleteRankStatement = instance.connection.prepareStatement("delete from ranks where key = ?");

        instance.saveRankHolderStatement = instance.connection.prepareStatement("insert into rank_owners values(?, ?, ?) on conflict(key) do update SET rank_list=excluded.rank_list, selected=excluded.selected");
        instance.getRankHolderStatement = instance.connection.prepareStatement("select * from rank_owners where key = ?");
        instance.getRankHoldersStatement = instance.connection.prepareStatement("select * from rank_owners");

        instance.saveInventoryStatement = instance.connection2.prepareStatement("insert into inv_storage values(?, ?, ?) on conflict(key, gamemode) do update SET data=excluded.data");
        instance.getInventoryStatement = instance.connection2.prepareStatement("select * from inv_storage where key = ? and gamemode = ?");
    }

    public static DBUtils getInstance() {
        return instance;
    }

    public void saveInventory(@NotNull UUID user, @NotNull String gamemode, @NotNull String invData) throws SQLException {
        saveInventoryStatement.setString(1, user.toString());
        saveInventoryStatement.setString(2, gamemode);
        saveInventoryStatement.setString(3, invData);
        saveInventoryStatement.executeUpdate();
    }

    public String getInventory(@NotNull UUID user, @NotNull String gamemode) throws SQLException {
        getInventoryStatement.setString(1, user.toString());
        getInventoryStatement.setString(2, gamemode);
        ResultSet results = getInventoryStatement.executeQuery();
        return results.getString("data");
    }

    public void updateRank(@NotNull Rank rank) throws SQLException {
        insertRankStatement.setString(1, rank.id);
        insertRankStatement.setString(2, rank.name);
        insertRankStatement.setString(3, styleToString(rank.rank_color));
        insertRankStatement.setString(4, styleToString(rank.name_color));
        insertRankStatement.executeUpdate();
    }

    public Rank getRank(String id) throws SQLException {
        getRankStatement.setString(1, id);
        ResultSet results = getRankStatement.executeQuery();
        return new Rank(results.getString("key"), results.getString("name"), styleFromString(results.getString("rank_color")), styleFromString(results.getString("name_color")));
    }

    public List<Rank> getRanks() throws SQLException {
        ResultSet results = getRanksStatement.executeQuery();
        List<Rank> ranks = new ArrayList<>();
        do {
            ranks.add(new Rank(results.getString("key"), results.getString("name"), styleFromString(results.getString("rank_color")), styleFromString(results.getString("name_color"))));
        } while (results.next());
        return ranks;
    }

    public void removeRank(String id) throws SQLException {
        deleteRankStatement.setString(1, id);
        deleteRankStatement.execute();
    }

    public List<Rank> getRanksForHolder(@NotNull UUID id) throws SQLException {
        getRankHolderStatement.setString(1, id.toString());
        ResultSet results = getRankHolderStatement.executeQuery();
        String rankStr = results.getString("rank_list");
        List<Rank> ranks = new ArrayList<>();
        if(results.wasNull()) return ranks;
        String[] ranks_arr = rankStr.split(",");
        for(String rank_id : ranks_arr) {
            Optional<Rank> rank = RankUtils.byID(rank_id);
            if(rank.isEmpty()) {
                MakeCraft.LOGGER.warn("Rank {} assigned to user {}, but no such rank found!", rank_id, id);
                continue;
            }
            ranks.add(rank.get());
        }
        return ranks;
    }

    public Optional<Rank> getHolderActiveRank(UUID user) throws SQLException {
        getRankHolderStatement.setString(1, user.toString());
        ResultSet results = getRankHolderStatement.executeQuery();
        String activeRankID = results.getString("selected");
        if(activeRankID == null) return Optional.empty();
        return RankUtils.byID(activeRankID);
    }

    public Map<UUID, String[]> getRankHolderRawInfo() throws SQLException {
        ResultSet results = getRankHoldersStatement.executeQuery();
        Map<UUID, String[]> rankmap = new HashMap<>();
        while (results.next()) {
            rankmap.put(UUID.fromString(results.getString("key")), results.getString("rank_list").split(","));
        }
        return rankmap;
    }

    public void saveRanksForHolder(@NotNull UUID id, @NotNull List<Rank> ranks) throws SQLException {
        saveRankHolderStatement.setString(1, id.toString());
        saveRankHolderStatement.setString(2, ranks.stream().map(rank -> rank.id + ",").collect(Collectors.joining()));
        Optional<Rank> userRank = RankUtils.getRankForUser(id);
        if(userRank.isPresent()) {
            saveRankHolderStatement.setString(3, userRank.get().id);
        } else {
            saveRankHolderStatement.setString(3, "");
        }
        saveRankHolderStatement.executeUpdate();
    }

    public Style styleFromString(String data) {
        if(data == null) return Style.EMPTY;
        Style style = Style.EMPTY;
        String[] params = data.split(",");
        for(String param : params) {
            String[] args = param.split("=");
            String key = args[0].trim();
            String value = args[1].trim();
            if(key.equals("bold")) {
                style = style.withBold(Boolean.parseBoolean(value));
            }
            if(key.equals("italic")) {
                style = style.withItalic(Boolean.parseBoolean(value));
            }
            if(key.equals("underlined")) {
                style = style.withUnderline(Boolean.parseBoolean(value));
            }
            if(key.equals("color")) {
                style = style.withColor(TextColor.parse(value).getOrThrow());
            }
            if(key.equals("shadow_color")) {
                style = style.withShadowColor(Integer.parseInt(value));
            }
        }
        return style;
    }

    public String styleToString(@NotNull Style style) {
        StringBuilder builder = new StringBuilder();
        builder.append("bold=");
        builder.append(style.isBold());
        builder.append(',');
        builder.append("italic=");
        builder.append(style.isItalic());
        builder.append(',');
        builder.append("underlined=");
        builder.append(style.isUnderlined());
        builder.append(',');
        if(style.getColor() != null) {
            builder.append("color=");
            builder.append(style.getColor());
            builder.append(',');
        }
        if(style.getShadowColor() != null) {
            builder.append("shadow_color=");
            builder.append(style.getShadowColor());
        }
        return builder.toString();
    }
}
