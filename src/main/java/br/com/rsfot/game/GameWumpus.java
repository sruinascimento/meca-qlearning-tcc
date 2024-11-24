package br.com.rsfot.game;

import br.com.rsoft.domain.Direction;
import br.com.rsoft.domain.Environment;
import br.com.rsoft.domain.action.CommandType;
import br.com.rsoft.game.HuntWumpus;
import br.com.rsoft.report.Report;
import br.com.rsoft.util.CommandKeyExtractor;
import br.com.rsoft.util.DirectionExtractor;

import java.util.Objects;
import java.util.logging.Logger;

public class GameWumpus {
    private HuntWumpus huntWumpus;
    private String currentStateOfAgent;
    private final Logger LOGGER = Logger.getLogger(GameWumpus.class.getName());

    public GameWumpus(String[][] wumpusCave) {
        this.huntWumpus = new HuntWumpus(new Environment(wumpusCave));
        currentStateOfAgent = Report.generate(huntWumpus, false);
    }

    public String getCurrentStateOfAgent() {
        return currentStateOfAgent;
    }

    public void executeAction(String command) {
        if (Objects.isNull(command) || command.isEmpty()) {
            throw new IllegalArgumentException("Invalid command");
        }
        try {
            Direction direction = DirectionExtractor.from(command);
            String commandKey = CommandKeyExtractor.from(command);
            CommandType commandType = CommandType.valueOf(commandKey);
            this.currentStateOfAgent = commandType.execute(huntWumpus, direction);

            LOGGER.info("Command --> {}" + command);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid command");
        }
    }

    public boolean isGameOver() {
        return huntWumpus.isGameOver();
    }

    public String report() {
        return Report.generate(huntWumpus, false);
    }

    public void resetGame() {
        this.huntWumpus = new HuntWumpus(new Environment(CaveMatrix.SECOND_CAVE.getCave()));
        currentStateOfAgent = Report.generate(huntWumpus, false);
    }

}
