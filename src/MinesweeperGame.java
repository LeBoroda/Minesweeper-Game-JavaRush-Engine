
import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE*SIDE;
    private int score;
    private boolean isGameStopped;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        boolean mined;
        List<GameObject> minesAround;
        for (int j = 0; j < SIDE; j++){
            for (int i = 0; i < SIDE; i++){
                mined = gameField[j][i].isMine;
                if(!mined){
                    minesAround = getNeighbors(gameField[j][i]);
                    gameField[j][i].countMineNeighbors = 0;
                    for (GameObject cell: minesAround) {
                        if(cell.isMine) gameField[j][i].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y){
        if (!gameField[y][x].isFlag && !gameField[y][x].isOpen && !isGameStopped) {

            gameField[y][x].isOpen = true;
            countClosedTiles = countClosedTiles - 1;

            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            } else {
                if(countClosedTiles == countMinesOnField) win();
                setCellColor(x, y, Color.GREEN);
                score = score + 5;
                setScore(score);
                if (gameField[y][x].countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    for (GameObject tile : getNeighbors(gameField[y][x])) {
                        if (!tile.isOpen) {
                            openTile(tile.x, tile.y);
                        }
                    }
                } else {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                }
            }
        }
    }

    private void markTile(int x, int y) {
        if (!gameField[y][x].isOpen && !isGameStopped) {
            if (gameField[y][x].isFlag) {
                gameField[y][x].isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.ORANGE);
            } else {
                if (countFlags > 0) {
                    gameField[y][x].isFlag = true;
                    countFlags--;
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                }
            }
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "ТЫ ПРОДУЛ, НЕУДАЧНИК!", Color.BLACK ,22);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLUE, "ПОБЕДА! УРА!!!", Color.BLACK, 22);
    }

    private void restart() {
        isGameStopped = false;
        score = 0;
        setScore(score);
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if(isGameStopped){
            restart();
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }
}
