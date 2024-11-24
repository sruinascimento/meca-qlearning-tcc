package br.com.rsfot.system1.sensory;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import org.json.JSONObject;

public class WumpusDeadSensor extends SensoryCodelet {
    private GameWumpus gameWumpus;
    private Memory wumpusDeadMO;

    public WumpusDeadSensor(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus = gameWumpus;
    }

    @Override
    public void accessMemoryObjects() {
        if (wumpusDeadMO == null) {
            this.wumpusDeadMO = this.getOutput("WUMPUS_DEAD_MO");
        }
    }

    @Override
    public void calculateActivation() {}

    @Override
    public void proc() {
        String infoWumpusWorld = gameWumpus.getCurrentStateOfAgent();
        wumpusDeadMO.setI(parseWumpusDeadInfo(infoWumpusWorld));
    }

    private boolean parseWumpusDeadInfo(String json) {
        JSONObject wumpusDeadInfo = new JSONObject(json);
        return wumpusDeadInfo.getBoolean("wumpusDead");
    }

}
