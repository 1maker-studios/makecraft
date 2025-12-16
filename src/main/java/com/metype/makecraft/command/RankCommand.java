package com.metype.makecraft.command;

import com.metype.makecraft.inventory.InteractOnlyScreenHandler;
import com.metype.makecraft.inventory.RankSelectUI;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] aliases = new String[]{"rank"};

        for(String alias : aliases) {
            LiteralArgumentBuilder<ServerCommandSource> args = literal(alias)
                    .then(literal("create")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_create", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .then(argument("rank_name", StringArgumentType.string())
                                            .executes(this::createRank)
                                    )
                            )
                    )
                    .then(literal("delete")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_delete", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .executes(this::deleteRank)
                            )
                    )
                    .then(literal("give")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_give", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .then(argument("players", EntityArgumentType.players())
                                            .executes(this::giveRank)
                                    )
                            )
                    )
                    .then(literal("giveall")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_give", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .executes(this::giveAllRank)
                            )
                    )
                    .then(literal("revoke")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_give", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .then(argument("player", EntityArgumentType.player())
                                            .executes(this::revokeRank)
                                    )
                            )
                    )
                    .then(literal("use")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_use", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .executes(this::useRank)
                            )
                    )
                    .then(literal("select")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_use", 2))
                            .executes(this::selectRank)
                    )
                    .then(literal("set")
                            .requires(cs -> Permissions.check(cs, "makecraft.rank_set", 2))
                            .then(argument("identifier", IdentifierArgumentType.identifier())
                                    .suggests(new RankIDProvider())
                                    .then(literal("name_color").
                                            then(argument("color", StringArgumentType.greedyString()).
                                                    suggests(new ColorProvider()).
                                                    executes(this::setNameColor)
                                            )
                                    )
                                    .then(literal("rank_color").
                                            then(argument("color", StringArgumentType.greedyString()).
                                                    suggests(new ColorProvider()).
                                                    executes(this::setRankColor)
                                            )
                                    )
                                    .then(literal("name").
                                            then(argument("name", StringArgumentType.string()).
                                                    executes(this::setRankName)
                                            )
                                    )
                            )
                    )
                    .executes(this::execute);
            dispatcher.register(args);
        }
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int createRank(CommandContext<ServerCommandSource> context) {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        String rank_name = StringArgumentType.getString(context, "rank_name");
        if(RankUtils.byID(id.toString()).isPresent()) {
            context.getSource().sendFeedback(() -> Text.of("A rank with this ID already exists."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Rank rank = new Rank(id.asString(), rank_name, Style.EMPTY, Style.EMPTY);
        RankUtils.addRank(rank);
        context.getSource().sendFeedback(() -> Text.of("Rank " + id + " created!"), true);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int deleteRank(CommandContext<ServerCommandSource> context) {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        RankUtils.deleteRank(id.toString());
        context.getSource().sendFeedback(() -> Text.of("Rank " + id + " deleted!"), true);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int giveRank(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        Collection<ServerPlayerEntity> users = EntityArgumentType.getPlayers(context, "players");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        giveRankToUsers(rank.get(), users, context);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int giveAllRank(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Collection<ServerPlayerEntity> users = context.getSource().getServer().getPlayerManager().getPlayerList();
        giveRankToUsers(rank.get(), users, context);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private void giveRankToUsers(Rank rank, Collection<ServerPlayerEntity> users, CommandContext<ServerCommandSource> context) {
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

    private int revokeRank(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        ServerPlayerEntity user = EntityArgumentType.getPlayer(context, "player");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        RankUtils.removeRankFromUser(user.getUuid(), rank.get());
        context.getSource().sendFeedback(() -> Text.of("Rank " + id + " revoked from " + user.getStringifiedName()), true);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int useRank(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");

        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Optional<Rank> rank = RankUtils.byID(id.toString());

        if(rank.isEmpty() || !RankUtils.getRanksForUser(player.getUuid()).contains(rank.get())) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }

        int ret = RankUtils.setRankForUser(player.getUuid(), rank.get().id);
        switch(ret) {
            case -1:
                context.getSource().sendFeedback(() -> Text.of("An error occurred applying rank " + id + ". Contact staff."), true);
                break;
            case 0:
                context.getSource().sendFeedback(() -> MutableText.of(PlainTextContent.of("Applied rank! You are now ")).append(player.getDisplayName()), false);
                break;
            case 1:
                context.getSource().sendFeedback(() -> Text.of("User already had rank applied, it has been removed."), false);
                break;
        }
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int setNameColor(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        String colorStr = StringArgumentType.getString(context, "color");
        int color = ColorProvider.parse(colorStr);
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Rank rankVal = rank.get();
        rankVal.name_color = rankVal.name_color.withColor(color);
        RankUtils.addRank(rankVal);
        context.getSource().sendFeedback(() -> MutableText.of(PlainTextContent.of("Set name color to ")).append(MutableText.of(PlainTextContent.of(colorStr)).setStyle(rankVal.name_color)), false);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int setRankColor(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        String colorStr = StringArgumentType.getString(context, "color");
        int color = ColorProvider.parse(colorStr);
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Rank rankVal = rank.get();
        rankVal.rank_color = rankVal.rank_color.withColor(color);
        RankUtils.addRank(rankVal);
        context.getSource().sendFeedback(() -> MutableText.of(PlainTextContent.of("Set rank color to ")).append(MutableText.of(PlainTextContent.of(colorStr)).setStyle(rankVal.rank_color)), false);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int setRankName(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        String name = StringArgumentType.getString(context, "name");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Rank rankVal = rank.get();
        rankVal.name = name;
        RankUtils.addRank(rankVal);
        context.getSource().sendFeedback(() -> MutableText.of(PlainTextContent.of("Set name to ")).append(MutableText.of(PlainTextContent.of(rankVal.name)).setStyle(rankVal.rank_color)), false);
        return CommandUtils.COMMAND_SUCCESS;
    }

    private int selectRank(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        context.getSource().getPlayerOrThrow().openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.of("Select Rank");
            }

            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new InteractOnlyScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, new RankSelectUI((ServerPlayerEntity) player), 5);
            }
        });
        return CommandUtils.COMMAND_SUCCESS;
    }
}
