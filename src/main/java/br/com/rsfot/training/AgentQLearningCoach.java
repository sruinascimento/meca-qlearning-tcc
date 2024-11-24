package br.com.rsfot.training;

import br.com.rsfot.game.CaveMatrix;
import br.com.rsoft.domain.Agent;
import br.com.rsoft.domain.Direction;
import br.com.rsoft.domain.Environment;
import br.com.rsoft.game.HuntWumpus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static br.com.rsoft.domain.Direction.*;
import static br.com.rsoft.domain.Feelings.*;


public class AgentQLearningCoach {
    private Map<List<Integer>, List<Double>> qTable;
    private double alpha;
    private double gamma;
    private double epsilon;
    private double epsilonDecay;
    private final int numActions = 9;
    private final List<String> possibleActions = List.of("GRAB",
            "MOVE NORTH",
            "MOVE SOUTH",
            "MOVE EAST",
            "MOVE WEST",
            "SHOOT NORTH",
            "SHOOT SOUTH",
            "SHOOT EAST",
            "SHOOT WEST");

    public AgentQLearningCoach(double alpha, double gamma, double epsilon, double epsilonDecay) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.epsilonDecay = epsilonDecay;
        qTable = new HashMap<>();
    }

    public AgentQLearningCoach() {
    }

    public Map<List<Integer>, List<Double>> getqTable() {
        return qTable;
    }

    public void setqTable(Map<List<Integer>, List<Double>> qTable) {
        this.qTable = qTable;
    }

    private List<Double> initializeQValues() {
        List<Double> qValues = new ArrayList<>(numActions);
        for (int i = 0; i < numActions; i++) {
            qValues.add(0.0);
        }
        return qValues;
    }

    public int chooseAction(List<Integer> state) {
        if (Math.random() < epsilon) {
            return (int) (Math.random() * numActions);
        } else {
            return getMaxAction(state);
        }
    }

    public void updateQValue(List<Integer> state, int action, double reward, List<Integer> nextState) {
        double maxNextQ = getMaxQValue(nextState);
        List<Double> qValues = qTable.getOrDefault(state, initializeQValues());
        double currentQ = qValues.get(action);
        qValues.set(action, currentQ + alpha * (reward + gamma * maxNextQ - currentQ));
        qTable.put(state, qValues);
    }

    public void decayEpsilon() {
        if (epsilon > 0.01) {
            epsilon *= (1 - epsilonDecay);
        }
    }

    private int getMaxAction(List<Integer> state) {
        List<Double> qValues = qTable.getOrDefault(state, initializeQValues());
        double maxQ = -Double.MAX_VALUE;
        int bestAction = 0;
        for (int i = 0; i < numActions; i++) {
            if (qValues.get(i) > maxQ) {
                maxQ = qValues.get(i);
                bestAction = i;
            }
        }
        return bestAction;
    }

    private double getMaxQValue(List<Integer> state) {
        List<Double> qValues = qTable.getOrDefault(state, initializeQValues());
        double maxQ = -Double.MAX_VALUE;
        for (double qValue : qValues) {
            if (qValue > maxQ) {
                maxQ = qValue;
            }
        }
        return maxQ;
    }

    public void saveQTableDat(String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(qTable);
        }
    }

    public void train(Environment environment, int episodes, String filePath) {
        HuntWumpus game = new HuntWumpus(environment);
        List<EpisodeReport> episodesData = new ArrayList<>();
        boolean impact = false;
        for (int i = 0; i < episodes; i++) {
            int steps = 0;
            double rewardByEpisode = 0;
            Agent agent = new Agent();
            game.setAgent(agent);
            List<Integer> state = generateStateRepresentation(game, impact);
            while (agent.isAlive() && !game.isGameOver()) {
                int action = chooseAction(state);
                String actionAsString = possibleActions.get(action);
                List<Object> rewardAndImpact = executeAction(actionAsString, game);
                impact = (boolean) rewardAndImpact.get(1);
                rewardByEpisode += (double) rewardAndImpact.get(0);
                List<Integer> nextState = generateStateRepresentation(game, impact);
                updateQValue(state, action, (double) rewardAndImpact.get(0), nextState);
                state = nextState;
                steps++;
            }
            if (!agent.isAlive()) {
                // Significant penalty for dying
                double penalty = -1000;
                rewardByEpisode += penalty;
                updateQValue(state, 0, penalty, state); // Assuming action 0 for terminal state
            }

            if (agent.agentWinTheGame()) {
                double reward = 1000; // Significant reward for winning
                rewardByEpisode += reward;
                updateQValue(state, 0, reward, state); // Assuming action 0 for terminal state
            }

            episodesData.add(new EpisodeReport(i, steps, agent.agentWinTheGame(), agent.isKilledTheWumpus(), agent.hasGold(), rewardByEpisode, alpha, gamma, epsilon, epsilonDecay));

            decayEpsilon();
            game.resetGame();
        }

        saveEpisodeData(episodesData, filePath);
    }


    private void saveEpisodeData(List<EpisodeReport> episodeDataList, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT
                     .withHeader("episodeNumber", "steps", "agentWinTheGame", "agentKilledTheWumpus", "agentHasGold", "reward", "alpha", "gamma", "epsilon", "epsilonDecay"))) {

            for (EpisodeReport report : episodeDataList) {
                csvPrinter.printRecord(report.episodeNumber(), report.steps(), report.agentWinTheGame(), report.agentKilledTheWumpus(), report.agentHasGold() ,report.totalReward(), report.alpha(), report.gamma(), report.epsilon(), report.epsilonDecay());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] generateCoordinate(HuntWumpus game) {
        Random random = new Random();
        while (true) {
            int x = random.nextInt(game.getEnvironment().getDimension());
            int y = random.nextInt(game.getEnvironment().getDimension());
            if (game.getEnvironment().isThereAPitAt(x, y) || game.getEnvironment().isThereAWumpusAt(x, y)) {
                continue;
            }
            return new int[]{x, y};
        }
    }

    private List<Integer> generateStateRepresentation(HuntWumpus huntWumpus, boolean impact) {
        int coordinateX = huntWumpus.getAgent().getCoordinateX();
        int coordinateY = huntWumpus.getAgent().getCoordinateY();
        int isAlive = huntWumpus.getAgent().isAlive() ? 1 : 0;
        int hasGold = huntWumpus.getAgent().hasGold() ? 1 : 0;
        int hasArrow = huntWumpus.getAgent().hasArrow() ? 1 : 0;
        int isWumpusAlive = huntWumpus.getAgent().isKilledTheWumpus() ? 0 : 1;
        int breeze = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()).contains(BREEZE) ? 1 : 0;
        int stench = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()).contains(STENCH) ? 1 : 0;
        int glitter = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()).contains(GLITTER) && !huntWumpus.getAgent().hasGold() ? 1 : 0;
        int impactValue = impact ? 1 : 0;

        return List.of(coordinateX, coordinateY, isAlive, hasGold, hasArrow, isWumpusAlive, breeze, stench, glitter, impactValue);
    }

    private List<Object> executeAction(String action, HuntWumpus game) {
        double reward = 0.0;
        boolean impact;

        switch (action) {
            case "MOVE NORTH" -> {
                var result = handleMoveAction(game, NORTH, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "MOVE SOUTH" -> {
                var result = handleMoveAction(game, Direction.SOUTH, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "MOVE EAST" -> {
                var result = handleMoveAction(game, EAST, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "MOVE WEST" -> {
                var result = handleMoveAction(game, WEST, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "SHOOT NORTH" -> {
                var result = handleShootAction(game, NORTH, reward);
                impact = result.impact;
                reward = result.reward;
                reward -= 10;
            }
            case "SHOOT SOUTH" -> {
                var result = handleShootAction(game, SOUTH, reward);
                impact = result.impact;
                reward = result.reward;
                reward -= 10;
            }
            case "SHOOT EAST" -> {
                var result = handleShootAction(game, EAST, reward);
                impact = result.impact;
                reward = result.reward;
                reward -= 10;
            }
            case "SHOOT WEST" -> {
                var result = handleShootAction(game, WEST, reward);
                impact = result.impact;
                reward = result.reward;
                reward -= 10;
            }
            case "GRAB" -> {
                var result = handleGrabAction(game, reward);
                impact = result.impact;
                reward = result.reward;
            }
            default -> throw new IllegalArgumentException("Action invalid");
        }

        if (impact) {
            reward -= 500;
        }

        return List.of(reward, impact);
    }

    private double calculateReward(HuntWumpus game) {
        if (game.isAgentWinTheGame()) {
            return 1000;
        }
        if (!game.getAgent().isAlive()) {
            return -1000;
        }
        return -1;
    }

    private MoveResult handleMoveAction(HuntWumpus game, Direction direction, double reward) {
        boolean impact = !game.moveToDirection(direction);
        reward = calculateReward(game);
        return new MoveResult(reward, impact);
    }

    private MoveResult handleShootAction(HuntWumpus game, Direction direction, double reward) {
        game.shoot(direction);
        reward = calculateReward(game);
        boolean impact = !game.getAgent().isAlive();
        return new MoveResult(reward, impact);
    }

    private MoveResult handleGrabAction(HuntWumpus game, double reward) {
        game.grabGold();
        reward = calculateReward(game);
        boolean impact = !game.getAgent().isAlive();
        return new MoveResult(reward, impact);
    }

    private static class MoveResult {
        private final double reward;
        private final boolean impact;

        public MoveResult(double reward, boolean impact) {
            this.reward = reward;
            this.impact = impact;
        }
    }

    public static void main(String[] args) throws IOException {
        double alpha = 0.1;
        double gamma = 0.99;
        double epsilon = 1.0;
        double epsilonDecay = 0.01;
        int numberOfEpisodes = 2000;
        AgentQLearningCoach agent = new AgentQLearningCoach(alpha, gamma, epsilon, epsilonDecay);

        Environment environment = new Environment(CaveMatrix.SECOND_CAVE.getCave());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        String episodesReportFileName = String.format("train_second_cave/train_%s_episodesReport_alpha%.2f_gamma%.2f_epsilon%.2f_epsilonDecay%.2f_%d.csv",
                timestamp, alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes);
        String qTableFileName = String.format("train_second_cave/train_%s_qTable4x4_alpha%.2f_gamma%.2f_epsilon%.2f_epsilonDecay%.2f_episodes_%d.dat",
                timestamp, alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes);

        agent.train(environment, numberOfEpisodes, episodesReportFileName);
        agent.saveQTableDat(qTableFileName);
    }

}
