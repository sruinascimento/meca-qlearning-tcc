package br.com.rsfot.system1.motor;

import br.com.rsfot.game.GameWumpus;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.MotorCodelet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AgentActuator extends MotorCodelet {
    private GameWumpus gameWumpus;
    private Memory nextActionMO;
    private BufferedWriter logWriter;

    public AgentActuator(String id, GameWumpus gameWumpus) {
        super(id);
        this.gameWumpus = gameWumpus;
        try {
            this.logWriter = new BufferedWriter(new FileWriter("agent_behavior_log.txt", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accessMemoryObjects() {
        if (nextActionMO == null) {
            this.nextActionMO = this.getInput("NEXT_ACTION_MO");
        }
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if (gameWumpus.isGameOver()) {
            System.out.println(">>>> Game Over");
            System.out.println(">>>> report: " + gameWumpus.report());
            logAction("Game Over");
            gameWumpus.resetGame();
            System.exit(1);
        }
        if (this.nextActionMO != null && this.nextActionMO.getI() != null) {
            String action = (String) this.nextActionMO.getI();
            gameWumpus.getCurrentStateOfAgent();
            gameWumpus.executeAction(action);
            logAction(action);
            gameWumpus.getCurrentStateOfAgent();
        }
    }

    private void logAction(String action) {
        try {
            logWriter.write("Action: " + action + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}