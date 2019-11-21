import java.lang.reflect.Array;

public class Player {
    boolean winner;
    int hp = 0;
    Boolean[][] grid = new Boolean[10][10];

    public Player(Boolean[][] _grid, int _hp) {
        grid = _grid;
        hp = _hp;
    }

    public int turn(int x,int y){
        if(grid[x][y]){
            hp--;
            if (hp == 0){
                winner = false;
                return 0;
            }
            return 0;
        }
        return 1;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Boolean[][] getGrid() {
        return grid;
    }

    public void setGrid(Boolean[][] grid) {
        this.grid = grid;
    }
}
