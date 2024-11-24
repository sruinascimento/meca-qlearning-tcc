package br.com.rsfot.training;

import java.io.Serializable;

public record EpisodeReport(
        int episodeNumber,
        int steps,
        boolean agentWinTheGame,
        boolean agentKilledTheWumpus,
        boolean agentHasGold,
        double totalReward,
        double alpha,
        double gamma,
        double epsilon,
        double epsilonDecay
) implements Serializable {
}
