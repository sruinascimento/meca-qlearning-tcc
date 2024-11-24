package br.com.rsfot.system1.sensory;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import org.json.JSONObject;

public class StenchSensor extends SensoryCodelet {
    private GameWumpus gameWumpus;
    private Memory stenchMO;

    public StenchSensor(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus = gameWumpus;
    }

    @Override
    public void accessMemoryObjects() {
        if (stenchMO == null) {
            this.stenchMO = this.getOutput("STENCH_MO");
        }
    }

    @Override
    public void calculateActivation() {}

    @Override
    public void proc() {
        String infoWumpusWorld = gameWumpus.getCurrentStateOfAgent();
        stenchMO.setI(parseStenchInfo(infoWumpusWorld));
    }

    private boolean parseStenchInfo(String json) {
        JSONObject stenchInfo = new JSONObject(json);
        JSONObject feelings = stenchInfo.getJSONObject("feelingByCoordinate");
        return feelings.getBoolean("stench");
    }

}
