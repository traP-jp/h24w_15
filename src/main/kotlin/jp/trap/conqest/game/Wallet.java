package jp.trap.conqest.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Wallet {
    public static void setupScoreboard(Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("wallet", "dummy", ChatColor.AQUA + "wallet");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score coin = objective.getScore(ChatColor.YELLOW + "Coin");
        coin.setScore(0);
    }

    public static void useCoin(Player player, int use){
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("wallet");
        if(objective == null)return;
        Score score = objective.getScore(ChatColor.YELLOW + "Coin");
        int currentCoin = score.getScore();
        if(currentCoin < use)return;
        score.setScore(currentCoin - use);
    }

    public static void getCoin(Player player, int get){
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("wallet");
        if(objective == null)return;
        Score score = objective.getScore(ChatColor.YELLOW + "Coin");
        int currentCoin = score.getScore();
        if(currentCoin < get)return;
        score.setScore(currentCoin + get);
    }
}
