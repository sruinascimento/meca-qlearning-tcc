package br.com.rsfot.system1.sensory;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import org.json.JSONObject;

public class GlitterSensor extends SensoryCodelet {
    private GameWumpus gameWumpus;
    private Memory glitterMO;

    public GlitterSensor(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus = gameWumpus;
    }

    @Override
    public void accessMemoryObjects() {
        if (glitterMO == null) {
            this.glitterMO = this.getOutput("GLITTER_MO");
        }
    }

    @Override
    public void calculateActivation() {
    }

    @Override
    public void proc() {
        String infoWumpusWorld = gameWumpus.getCurrentStateOfAgent();
        glitterMO.setI(parseGlitterInfo(infoWumpusWorld));
    }

    private boolean parseGlitterInfo(String json) {
        JSONObject glitterInfo = new JSONObject(json);
        JSONObject feelings = glitterInfo.getJSONObject("feelingByCoordinate");
        return feelings.getBoolean("glitter");
    }
}
