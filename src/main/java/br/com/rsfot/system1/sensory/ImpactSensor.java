package br.com.rsfot.system1.sensory;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import org.json.JSONObject;

public class ImpactSensor extends SensoryCodelet {
    private GameWumpus gameWumpus;
    private Memory impactMO;

    public ImpactSensor(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus = gameWumpus;
    }

    @Override
    public void accessMemoryObjects() {
        if (impactMO == null) {
            this.impactMO = this.getOutput("IMPACT_MO");
        }

    }

    @Override
    public void calculateActivation() {}

    @Override
    public void proc() {
        String infoWumpusWorld = gameWumpus.getCurrentStateOfAgent();
        impactMO.setI(parseImpactInfo(infoWumpusWorld));
    }

    private boolean parseImpactInfo(String json) {
        JSONObject impactInfo = new JSONObject(json);
        JSONObject feelings = impactInfo.getJSONObject("feelingByCoordinate");
        return feelings.getBoolean("impact");
    }
}
