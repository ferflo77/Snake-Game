package snake;

import java.awt.Color;
import java.util.ArrayList;

public class Snake {
    private ArrayList<ArrayList<Integer>> snake_array;
    private Color color;

    public Snake(int startY, Color color) {
        snake_array = new ArrayList<>();
        int length = 5;
        for(int i = length-1; i>=0; i--){
            ArrayList<Integer> pair = new ArrayList<>();
            pair.add(i);
            pair.add(startY);
            snake_array.add(pair);
        }
        this.color = color;
    }

    public ArrayList<ArrayList<Integer>> getSnake_array() {
        return snake_array;
    }

    public Color getColor() {
        return color;
    }
    
    
    
    
    
    
    
}
