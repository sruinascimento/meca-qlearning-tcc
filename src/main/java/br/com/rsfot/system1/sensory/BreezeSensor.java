package br.com.rsfot.system1.sensory;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import org.json.JSONObject;

public class BreezeSensor extends SensoryCodelet {
    private GameWumpus gameWumpus;
    private Memory breezeMO;

    public BreezeSensor(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus = gameWumpus;
    }

    @Override
    public void accessMemoryObjects() {
        if (breezeMO == null) {
            this.breezeMO = this.getOutput("BREEZE_MO");
        }
    }

    @Override
    public void calculateActivation() {}

    @Override
    public void proc() {
        String infoWumpusWorld = gameWumpus.getCurrentStateOfAgent();
        breezeMO.setI(parseBreezeInfo(infoWumpusWorld));
    }

    private boolean parseBreezeInfo(String json) {
        JSONObject breezeInfo = new JSONObject(json);
        JSONObject feelings = breezeInfo.getJSONObject("feelingByCoordinate");
        return feelings.getBoolean("breeze");
    }
}
