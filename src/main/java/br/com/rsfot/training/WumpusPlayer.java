package br.com.rsfot.training;

import br.com.rsfot.game.CaveMatrix;
import br.com.rsoft.domain.Agent;
import br.com.rsoft.domain.Environment;
import br.com.rsoft.game.HuntWumpus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.rsoft.domain.Direction.*;
import static br.com.rsoft.domain.Feelings.*;

public class WumpusPlayer {
    private final AgentQLearningCoach agent;
    private Environment environment;
    private Map<List<Integer>, List<Double>> qTable;
    private final List<String> possibleActions = List.of("GRAB",
            "MOVE NORTH",
            "MOVE SOUTH",
            "MOVE EAST",
            "MOVE WEST",
            "SHOOT NORTH",
            "SHOOT SOUTH",
            "SHOOT EAST",
            "SHOOT WEST");

    public WumpusPlayer(String[][] wumpusWorld, String qTableFilePath) throws IOException, ClassNotFoundException {
        this.environment = new Environment(wumpusWorld);
        this.agent = new AgentQLearningCoach();
        loadQTable(qTableFilePath);
    }

    private void loadQTable(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            qTable = (Map<List<Integer>, List<Double>>) in.readObject();
        }
        agent.setqTable(qTable);
    }

    public void play() {
        HuntWumpus game = new HuntWumpus(environment);
        Agent agent = new Agent();
        game.setAgent(agent);
        boolean impact = false;

        while (agent.isAlive() && !game.isGameOver()) {
            List<Integer> state = generateStateRepresentation(game, impact);
            String action = chooseAction(state);
            executeAction(action, game);
            System.out.println("Action: " + action);
        }

        if (agent.agentWinTheGame()) {
            System.out.println("Agent won the game!");
            System.out.println("Agent's score: " + agent.getScore());
            System.out.println("Agent's score: " + agent.getScore());

        } else {
            System.out.println("Agent lost the game.");
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

    public String chooseAction(List<Integer> state) {
        Random random = new Random();
        List<Double> actions = qTable.get(state);
        if (actions == null || actions.isEmpty()) {
            // Se não houver ações para o estado, escolher uma ação aleatória
            return possibleActions.get(random.nextInt(possibleActions.size()));
        }
        // Encontrar o valor máximo
        double maxValue = Collections.max(actions);
        // Coletar todos os índices com o valor máximo
        List<Integer> maxIndices = IntStream.range(0, actions.size())
                .filter(i -> actions.get(i) == maxValue)
                .boxed()
                .collect(Collectors.toList());
        // Selecionar aleatoriamente um dos índices máximos
        int randomMaxIndex = maxIndices.get(random.nextInt(maxIndices.size()));
        return possibleActions.get(randomMaxIndex);
    }

    public void executeAction(String action, HuntWumpus game) {
        switch (action) {
            case "GRAB":
                game.grabGold();
                break;
            case "MOVE NORTH":
                game.moveToDirection(NORTH);
                break;
            case "MOVE SOUTH":
                game.moveToDirection(SOUTH);
                break;
            case "MOVE EAST":
                game.moveToDirection(EAST);
                break;
            case "MOVE WEST":
                game.moveToDirection(WEST);
                break;
            case "SHOOT NORTH":
                game.shoot(NORTH);
                break;
            case "SHOOT SOUTH":
                game.shoot(SOUTH);
                break;
            case "SHOOT EAST":
                game.shoot(EAST);
                break;
            case "SHOOT WEST":
                game.shoot(WEST);
                break;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        WumpusPlayer player = new WumpusPlayer(CaveMatrix.SECOND_CAVE.getCave(), "train_second_cave/train_20241027_192227_qTable4x4_alpha0.10_gamma0.99_epsilon1.00_epsilonDecay0.01_episodes_1000.dat");
        WumpusPlayer player = new WumpusPlayer(CaveMatrix.SECOND_CAVE.getCave(), "target/train_20241124_153655_qTable4x4_alpha0.10_gamma0.99_epsilon1.00_epsilonDecay0.00_episodes_2000__second_cave.dat");
        player.play();
    }
}
