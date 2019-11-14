import java.lang.reflect.Array;

public class Player {
    boolean winner;
    int hp = 0;
    boolean[][] grid = new boolean[10][10];

    public Player(boolean[][] _grid, int _hp) {
        grid = _grid;
        hp = _hp;
    }

    public int turn(int x,int y){
        if(hit(x,y)){
            hp--;
            if (hp == 0){
                winner = false;
                return 0;
            }
            return 0;
        }
        return 1;
    }
    public boolean hit(int x, int y){
        return grid[x][y];
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

    public boolean[][] getGrid() {
        return grid;
    }

    public void setGrid(boolean[][] grid) {
        this.grid = grid;
    }
}
