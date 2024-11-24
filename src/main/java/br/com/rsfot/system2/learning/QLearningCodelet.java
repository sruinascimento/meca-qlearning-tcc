package br.com.rsfot.system2.learning;

import br.com.rsfot.system1.sensory.AgentStatusSensor;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system2.codelets.EpisodicLearningCodelet;

import java.io.IOException;
import java.util.List;

public class QLearningCodelet extends EpisodicLearningCodelet {
    private Memory agentStatusMO;
    private Memory wumpusDeadMO;
    private Memory breezeMO;
    private Memory glitterMO;
    private Memory impactMO;
    private Memory stenchMO;
    private Memory nextActionMO;
    private final List<String> possibleActions = List.of("GRAB",
            "MOVE NORTH",
            "MOVE SOUTH",
            "MOVE EAST",
            "MOVE WEST",
            "SHOOT NORTH",
            "SHOOT SOUTH",
            "SHOOT EAST",
            "SHOOT WEST");
    private QTableLoader qTableLoader = new QTableLoader();

    public QLearningCodelet(String fileName) {
        try {
            qTableLoader.loadQTable(fileName);
//            qTableLoader.loadQTable("train_second_cave/train_20241028_203603_qTable4x4_alpha0.10_gamma0.99_epsilon1.00_epsilonDecay0.01_episodes_2000.dat");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void accessMemoryObjects() {
        if (agentStatusMO == null) {
            this.agentStatusMO = this.getInput("AGENT_STATUS_MO");
        }

        if (wumpusDeadMO == null) {
            this.wumpusDeadMO = this.getInput("WUMPUS_DEAD_MO");
        }

        if (breezeMO == null) {
            this.breezeMO = this.getInput("BREEZE_MO");
        }

        if (stenchMO == null) {
            this.stenchMO = this.getInput("STENCH_MO");
        }

        if (glitterMO == null) {
            this.glitterMO = this.getInput("GLITTER_MO");
        }

        if (impactMO == null) {
            this.impactMO = this.getInput("IMPACT_MO");
        }

        if (nextActionMO == null) {
            this.nextActionMO = this.getOutput("NEXT_ACTION_MO");
        }
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if (
                this.agentStatusMO == null
                        || this.agentStatusMO.getI() == null
                        || this.breezeMO == null
                        || this.breezeMO.getI() == null
                        || this.glitterMO == null
                        || this.glitterMO.getI() == null
                        || this.impactMO == null
                        || this.impactMO.getI() == null
                        || this.stenchMO == null
                        || this.stenchMO.getI() == null
                        || this.wumpusDeadMO == null
                        || this.wumpusDeadMO.getI() == null
        ) {
            return;
        }

        if (((AgentStatusSensor.AgentStatus) this.agentStatusMO.getI()).isAlive() == 0) {
            return;
        }

        ActionSelector actionSelector = new ActionSelector(qTableLoader.getQTable(), possibleActions);
        List<Integer> inputAsNumericRepresentation = getInputAsNumericRepresentation();
        String predictedAction = actionSelector.chooseAction(inputAsNumericRepresentation);
        System.out.println(">>>>>> QLearning CODELET -> Predicted action: " + predictedAction);

        this.nextActionMO.setI(predictedAction);
    }

    private List<Integer> getInputAsNumericRepresentation() {
        int coordinateX = ((AgentStatusSensor.AgentStatus) this.agentStatusMO.getI()).coordinateX();
        int coordinateY = ((AgentStatusSensor.AgentStatus) this.agentStatusMO.getI()).coordinateY();
        int isAlive = ((AgentStatusSensor.AgentStatus) this.agentStatusMO.getI()).isAlive();
        int hasGold = ((AgentStatusSensor.AgentStatus) this.agentStatusMO.getI()).hasGold();
        int hasArrow = ((AgentStatusSensor.AgentStatus) this.agentStatusMO.getI()).hasArrow();
        int isWumpusAlive = (boolean) this.wumpusDeadMO.getI() ? 0 : 1;
        int breeze = (boolean) this.breezeMO.getI() ? 1 : 0;
        int stench = (boolean) this.stenchMO.getI() ? 1 : 0;
        int glitter = (boolean) this.glitterMO.getI() ? 1 : 0;
        int impactValue = (boolean) this.impactMO.getI() ? 1 : 0;

        return List.of(coordinateX, coordinateY, isAlive, hasGold, hasArrow, isWumpusAlive, breeze, stench, glitter, impactValue);
    }
}
