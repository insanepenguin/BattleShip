import java.lang.reflect.Array;

public class Player {
    boolean winner = false;
    int hp = 0;
    boolean[][] grid = new boolean[10][10];
    public void Player(boolean[][] _grid){
        grid = _grid;

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
