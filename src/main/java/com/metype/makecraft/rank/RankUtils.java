package com.metype.makecraft.rank;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.utils.DBUtils;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

public class RankUtils {
    private static final Map<String, Rank> ranks = new HashMap<>();
    private static final Map<UUID, List<Rank>> user_ranks = new HashMap<>();
    private static final Map<UUID, Rank> selected_rank = new HashMap<>();

    public static void init() {
        try {
            List<Rank> ranklist = DBUtils.getInstance().getRanks();
            for(Rank rank : ranklist) {
                ranks.put(rank.id, rank);
            }

            Map<UUID, String[]> rawRanks = DBUtils.getInstance().getRankHolderRawInfo();
            for(UUID user : rawRanks.keySet()) {
                Optional<Rank> heldRank = DBUtils.getInstance().getHolderActiveRank(user);
                heldRank.ifPresent(rank -> selected_rank.put(user, rank));
                List<Rank> ranksForThisUser = new ArrayList<>();
                for(String rank_id : rawRanks.get(user)) {
                    Optional<Rank> rank = RankUtils.byID(rank_id);
                    if(rank.isEmpty()) {
                        MakeCraft.LOGGER.warn("Rank {} assigned to user {}, but no such rank found!", rank_id, user);
                        continue;
                    }
                    ranksForThisUser.add(rank.get());
                }
                user_ranks.put(user, ranksForThisUser);
            }
        } catch (SQLException e) {
            MakeCraft.LOGGER.error("SQL Error!", e);
        }
    }

    public static void giveRankToUsers(Rank rank, Collection<ServerPlayerEntity> users, CommandContext<ServerCommandSource> context) {
        for(ServerPlayerEntity user : users) {
            if(RankUtils.addRankForUser(user.getUuid(), rank)) {
                context.getSource().sendFeedback(() -> Text.of("Rank " + rank.id + " given to " + user.getStringifiedName()), true);
                if (rank.name.isEmpty()) {
                    user.sendMessage(MutableText.of(PlainTextContent.of("You have been granted a ")).append(MutableText.of(PlainTextContent.of("Color Rank")).setStyle(rank.name_color)));
                } else {
                    user.sendMessage(MutableText.of(PlainTextContent.of("You have been granted the rank ")).append(MutableText.of(PlainTextContent.of(rank.name)).setStyle(rank.rank_color)));
                }
            }
        }
    }

    public static void showRankFormatting(Rank rank, ServerPlayerEntity player) {
        player.sendMessage(
                MutableText.of(PlainTextContent.of("Applied rank! You are now "))
                        .append(player.getDisplayName()), false
        );
    }

    public static void deleteRank(@NotNull String ID) {
        ranks.remove(ID);
        try {
            DBUtils.getInstance().removeRank(ID);
        } catch (SQLException ignored) {}
    }

    public static Optional<Rank> byID(@NotNull String ID) {
        if(!ranks.containsKey(ID)) return Optional.empty();
        return Optional.of(ranks.get(ID));
    }

    public static List<Rank> getRanksForUser(@NotNull UUID user) {
        return user_ranks.getOrDefault(user, List.of());
    }

    public static Optional<Rank> getRankForUser(@NotNull UUID user) {
        Rank rank = selected_rank.getOrDefault(user, null);
        if(rank == null) return Optional.empty();
        if(byID(rank.id).isEmpty()) return Optional.empty();
        return Optional.of(rank);
    }

    public static int setRankForUser(@NotNull UUID user, @NotNull String ID) {
        if(!userHasRank(user, ID)) return -1;
        Optional<Rank> rank = byID(ID);
        if(rank.isEmpty()) return -1;
        int status = 0;
        Rank existingRank = selected_rank.getOrDefault(user, null);
        if(existingRank != null && existingRank.id.equalsIgnoreCase(rank.get().id)) {
            selected_rank.remove(user);
            status = 1;
        } else {
            selected_rank.put(user, rank.get());
        }
        try {
            DBUtils.getInstance().saveRanksForHolder(user, user_ranks.get(user));
        } catch (SQLException ignored) {
            status = -1;
        }
        return status;
    }

    public static List<Rank> getRanks() {
        return ranks.values().stream().toList();
    }

    public static boolean userHasRank(@NotNull UUID user, @NotNull String ID) {
        return user_ranks.getOrDefault(user, List.of()).stream().anyMatch(rank -> rank.id.equalsIgnoreCase(ID));
    }

    public static boolean addRankForUser(@NotNull UUID user, @NotNull String ID) {
        return byID(ID).filter(value -> addRankForUser(user, value)).isPresent();
    }

    public static boolean addRankForUser(@NotNull UUID user, @NotNull Rank rank) {
        List<Rank> userRanks = user_ranks.getOrDefault(user, new ArrayList<>());
        if(userRanks.contains(rank)) return false;
        userRanks.add(rank);
        user_ranks.put(user, userRanks);
        try {
            DBUtils.getInstance().saveRanksForHolder(user, user_ranks.get(user));
        } catch (SQLException ignored) {}
        return true;
    }

    public static boolean removeRankFromUser(@NotNull UUID user, @NotNull String ID) {
        return byID(ID).filter(value -> removeRankFromUser(user, value)).isPresent();
    }

    public static boolean removeRankFromUser(@NotNull UUID user, @NotNull Rank rank) {
        List<Rank> userRanks = user_ranks.getOrDefault(user, new ArrayList<>());
        if(!userRanks.contains(rank)) return false;
        userRanks.remove(rank);
        user_ranks.put(user, userRanks);
        try {
            DBUtils.getInstance().saveRanksForHolder(user, user_ranks.get(user));
        } catch (SQLException ignored) {}
        return true;
    }

    public static void addRank(@NotNull Rank rank) {
        ranks.put(rank.id, rank);
        try {
            DBUtils.getInstance().updateRank(rank);
        } catch (SQLException ignored) {}
    }
}
