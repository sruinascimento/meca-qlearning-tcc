package br.com.rsfot;

import br.com.rsfot.game.CaveMatrix;
import br.com.rsfot.game.GameWumpus;
import br.com.rsfot.system1.motor.AgentActuator;
import br.com.rsfot.system1.sensory.*;
import br.com.rsfot.system2.learning.QLearningCodelet;
import br.com.rsfot.training.AgentQLearningCoach;
import br.com.rsoft.domain.Environment;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.meca.mind.MecaMind;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AgentMind extends MecaMind {

    public AgentMind(CaveMatrix caveMatrix, String fileName) {
        this(caveMatrix, fileName, false, 0, 0, 0, 0, 0);
    }

    public AgentMind(CaveMatrix caveMatrix, String fileName, boolean train, double alpha, double gamma, double epsilon, double epsilonDecay, int numberOfEpisodes) {
        String qTableFileName;
        if (train) {
            qTableFileName = createQTableFileName(alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes, caveMatrix.name());
            Environment environment = new Environment(caveMatrix.getCave());
            AgentQLearningCoach agentCoach = new AgentQLearningCoach(alpha, gamma, epsilon, epsilonDecay);
            String episodesReportFileName = createEpisodesReportFileName(alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes, caveMatrix.name());
            agentCoach.train(environment, numberOfEpisodes, episodesReportFileName);
            try {
                agentCoach.saveQTableDat(qTableFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            qTableFileName = fileName;
        }


        GameWumpus gameWumpus = new GameWumpus(caveMatrix.getCave());

        // Declare the memory objects
        MemoryObject stenchMO = this.createMemoryObject("STENCH_MO");
        MemoryObject breezeMO = this.createMemoryObject("BREEZE_MO");
        MemoryObject glitterMO = this.createMemoryObject("GLITTER_MO");
        MemoryObject impactMO = this.createMemoryObject("IMPACT_MO");
        MemoryObject agentStatusMO = this.createMemoryObject("AGENT_STATUS_MO");
        MemoryObject nextActionMO = this.createMemoryObject("NEXT_ACTION_MO");
        MemoryObject wumpusDeadMO = this.createMemoryObject("WUMPUS_DEAD_MO");

        // Declare and create the sensors
        StenchSensor stenchSensor = new StenchSensor("STENCH_SENSOR", gameWumpus);
        stenchSensor.addOutput(stenchMO);
        insertCodelet(stenchSensor);

        BreezeSensor breezeSensor = new BreezeSensor("BREEZE_SENSOR", gameWumpus);
        breezeSensor.addOutput(breezeMO);
        insertCodelet(breezeSensor);

        GlitterSensor glitterSensor = new GlitterSensor("GLITTER_SENSOR", gameWumpus);
        glitterSensor.addOutput(glitterMO);
        insertCodelet(glitterSensor);

        ImpactSensor impactSensor = new ImpactSensor("IMPACT_SENSOR", gameWumpus);
        impactSensor.addOutput(impactMO);
        insertCodelet(impactSensor);

        WumpusDeadSensor wumpusDeadSensor = new WumpusDeadSensor("WUMPUS_DEAD_SENSOR", gameWumpus);
        wumpusDeadSensor.addOutput(wumpusDeadMO);
        insertCodelet(wumpusDeadSensor);

        AgentStatusSensor agentStatusSensor = new AgentStatusSensor("AGENT_STATUS_SENSOR", gameWumpus);
        agentStatusSensor.addOutput(agentStatusMO);
        insertCodelet(agentStatusSensor);

        // Declare and create the learning codelet
        QLearningCodelet qLearningCodelet = new QLearningCodelet(qTableFileName);
        qLearningCodelet.addInput(agentStatusMO);
        qLearningCodelet.addInput(breezeMO);
        qLearningCodelet.addInput(glitterMO);
        qLearningCodelet.addInput(impactMO);
        qLearningCodelet.addInput(stenchMO);
        qLearningCodelet.addInput(wumpusDeadMO);
        qLearningCodelet.addOutput(nextActionMO);
        insertCodelet(qLearningCodelet);

        // Declare and create the actuators
        AgentActuator agentActuator = new AgentActuator("AGENT_ACTUATOR", gameWumpus);
        agentActuator.addInput(nextActionMO);
        insertCodelet(agentActuator);
    }

    public static void main(String[] args) {
        if (args.length == 6) {
            // Scenario 1: Train and test the agent
            double alpha = Double.parseDouble(args[0]);
            double gamma = Double.parseDouble(args[1]);
            double epsilon = Double.parseDouble(args[2]);
            double epsilonDecay = Double.parseDouble(args[3]);
            int numberOfEpisodes = Integer.parseInt(args[4]);
            String caveName = args[5];

            CaveMatrix caveMatrix = CaveMatrix.valueOf(caveName);
            AgentMind agentMind = new AgentMind(caveMatrix, "", true, alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes);
            agentMind.start();
        } else if (args.length == 2) {
            // Scenario 2: Play the game without training
            String qTableFileName = args[0];
            String caveName = args[1];

            CaveMatrix caveMatrix = CaveMatrix.valueOf(caveName);
            AgentMind agentMind = new AgentMind(caveMatrix, qTableFileName);
            agentMind.start();
        } else {
            System.out.println("Usage:");
            System.out.println("1. To train and test: java -jar yourjarfile.jar <alpha> <gamma> <epsilon> <epsilonDecay> <numberOfEpisodes> <caveName>");
            System.out.println("2. To play without training: java -jar yourjarfile.jar <qTableFileName> <caveName>");
            System.exit(1);
        }
    }

    private static String createEpisodesReportFileName(double alpha, double gamma, double epsilon, double epsilonDecay, int numberOfEpisodes, String caveName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("train_%s_episodesReport_alpha%.2f_gamma%.2f_epsilon%.2f_epsilonDecay%.2f_%d__%s.csv",
                timestamp, alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes, caveName);
    }

    private static String createQTableFileName(double alpha, double gamma, double epsilon, double epsilonDecay, int numberOfEpisodes, String caveName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("train_%s_qTable4x4_alpha%.2f_gamma%.2f_epsilon%.2f_epsilonDecay%.2f_episodes_%d__%s.dat",
                timestamp, alpha, gamma, epsilon, epsilonDecay, numberOfEpisodes, caveName.toLowerCase());
    }
}