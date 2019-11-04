import java.lang.reflect.Array;

public class Player {
    boolean winner = false;
    int hp = 0;
    boolean[][] grid = new boolean[10][10];

    public Player(boolean[][] _grid, int _hp) {
        grid = _grid;
        hp = _hp;
    }

    public int turn(String x,int y){
        if(hit(Integer.parseInt(x),y)){
            hp--;
            if (hp == 0){
                return -1;
            }
            return 0;
        }
        return 1;
    }
    public boolean hit(int x, int y){
        return grid[x][y];
    }
}
