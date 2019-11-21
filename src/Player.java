import java.lang.reflect.Array;

public class Player {
    boolean winner = false;
    boolean loser = false;
    int hp = 0;
    Boolean[][] grid = new Boolean[10][10];
    String name = "0";



    public Player(Boolean[][] _grid, int _hp, String _name) {
        grid = _grid;
        hp = _hp;
        name = _name;
    }

    public boolean turn(int x,int y){
        if(grid[x][y]){
            hp--;
            if (hp == 0){
                loser = true;
                return false;
            }
            return false;
        }
        return true;
    }
    public String getName() {
        return name;
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
