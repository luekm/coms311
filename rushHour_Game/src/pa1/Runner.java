package pa1;
import java.io.IOException;
import java.util.*;

public class Runner {
    public static void main(String[] args) throws IOException {
        GameBoard gb = new GameBoard();
        gb.readInput("1.txt");

        ArrayList<GameBoard.Pair> path = gb.getPlan();
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getId() + " "
                    + path.get(i).getDirection());
        }
        System.out.println(gb.getNumOfPaths());

     //   gb.test();


        //  gb.getPlan();
      //  gb.printGrid(); // DEBUGGING

    }


}
