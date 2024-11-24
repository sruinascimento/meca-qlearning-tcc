package br.com.rsfot.system1.sensory;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import org.json.JSONObject;

public class AgentStatusSensor extends SensoryCodelet {
    private GameWumpus gameWumpus;
    private Memory agentStatusMO;

    public AgentStatusSensor(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus =gameWumpus;
    }

    @Override
    public void accessMemoryObjects() {
        if (agentStatusMO == null) {
            this.agentStatusMO = this.getOutput("AGENT_STATUS_MO");
        }
    }

    @Override
    public void calculateActivation() {
    }

    @Override
    public void proc() {
        String infoWumpusWorld = gameWumpus.getCurrentStateOfAgent();
        agentStatusMO.setI(parseAgentStatusInfo(infoWumpusWorld));
    }

    private AgentStatus parseAgentStatusInfo(String infoWumpusWord) {
        JSONObject agentStatusInfo = new JSONObject(infoWumpusWord)
                .getJSONObject("agentStatus");
        return new AgentStatus(
                agentStatusInfo.getString("coordinate"),
                agentStatusInfo.getBoolean("isAlive"),
                agentStatusInfo.getBoolean("hasGold"),
                agentStatusInfo.getBoolean("hasArrow")
        );
    }

    public record AgentStatus(
            int coordinateX,
            int coordinateY,
            int isAlive,
            int hasGold,
            int hasArrow
    ) {

        public AgentStatus(String coordinate, boolean isAlive, boolean hasGold, boolean hasArrow) {
            this(
                    Integer.parseInt(coordinate.replaceAll("[^0-9,]", "").split(",")[0]),
                    Integer.parseInt(coordinate.replaceAll("[^0-9,]", "").split(",")[1]),
                    isAlive ? 1 : 0,
                    hasGold ? 1 : 0,
                    hasArrow ? 1 : 0
            );
        }
    }
}
