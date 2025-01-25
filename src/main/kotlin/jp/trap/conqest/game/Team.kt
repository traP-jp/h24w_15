package jp.trap.conqest.game

class Team(val color: TeamColor) {
    private val players = mutableListOf<Player>()
    fun addPlayer(player: Player) {
        this.players.add(player)
    }

    fun getPlayers(): List<Player> {
        return players
    }
}

enum class TeamColor {
    GRAY,
    RED,
    BLUE;

    fun getConcreteMaterial(): Material {
        return when (this) {
            GRAY -> Material.GRAY_CONCRETE
            RED -> Material.RED_CONCRETE
            BLUE -> Material.BLUE_CONCRETE
        }
    }
}