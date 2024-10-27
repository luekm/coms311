package pa1;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameBoard {

    int[] grid = new int[36];

    State curState;
    State initState;
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    ArrayList<State> solutions = new ArrayList<>();
    Queue<State> stateQueue = new LinkedList<>();
    HashMap<Integer, State> stateTrack = new HashMap<>();


    int numPaths = 0;

    /*
    For debugging
     */
    public void printGrid() {
//
//        for (int i = 0; i < grid.length; i++) {
//            // Loop through the columns within each row
//            for (int j = 0; j < grid[i]; j++) {
//                System.out.print(grid[i] + " ");
//
//            }
//            System.out.println();
//        }
//
//        for (int i = 0; i < vehicles.size(); i++) {
//            System.out.println(vehicles.get(i).vehicleNum);
//            System.out.println(vehicles.get(i).viableMoves);
//
//        }
        for (int i = 0; i < grid.length; i++) {
            System.out.print(initState.map[i] + " ");

        }
        System.out.println();
        for (int i = 1; i < grid.length + 1; i++) {
            System.out.print(i + " ");
        }
    }

    public void test() {
        for(int i = 0; i < solutions.size(); i++) {

            System.out.println(solutions.get(i).numMoves);
        }
    }


    public void readInput(String FileName) throws IOException {

        int n;
        int i = 1;
        Arrays.fill(grid, -1);

        File file = new File(FileName);
        Scanner scnr = new Scanner(file);
        scnr.nextLine();

        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            Scanner lineScnr = new Scanner(line);

            Vehicle newCar = new Vehicle();
            while (lineScnr.hasNextInt()) {

                n = lineScnr.nextInt();

                newCar.addCoord(n);
                grid[n - 1] = i - 1;

            }
            lineScnr.close();
            newCar.setVehicleNum(i - 1);
            newCar.setMoves();
            vehicles.add(newCar);

            i++;

        }
        scnr.close();
        curState = new State(grid);
        curState.copyStateVehicles(vehicles);
        initState = curState;
        curState.numMoves = 0;
        stateTrack.put(Arrays.hashCode(curState.map), curState);
        stateQueue.add(curState);
    }


    public ArrayList<Pair> getPlan() {
        ArrayList<Pair> plan = new ArrayList<>();

        while (!stateQueue.isEmpty()) {
            curState = stateQueue.poll();

            if (curState.map[17] == 0) {
                numPaths += 1;
                //   plan = backtracePath(curState);
            }

            findStates();

        }

        if (!solutions.isEmpty()) {
            plan = backtracePath(solutions.get(0));
        }

        return plan;
    }


    public ArrayList<Pair> backtracePath(State finalState) {
        ArrayList<Pair> path = new ArrayList<>();
        State current = finalState;

        while (current != null && current.parentMove != null) {
            Pair move = current.parentMove;
            path.add(move);

            current = current.parentState;
        }

        Collections.reverse(path);
        return path;

    }


    public void findStates() {

        Vehicle v;
        int[] initMap = Arrays.copyOf(curState.map, 36);
        int hashCode;

        for (int i = 0; i < vehicles.size(); i++) {
            v = curState.stateVehicles.get(i);

            if (canMove('n', v)) {
                State northState = new State(initMap, curState.stateVehicles);
                v.moveNorth(northState, v);

                northState.parentState = curState;
                northState.parentMove = new Pair(v.vehicleNum, 'n');

                hashCode = Arrays.hashCode(northState.map);
                if (!stateTrack.containsKey(hashCode)) {
                    northState.plusMove();
                    stateTrack.put(hashCode, northState);
                    stateQueue.add(northState);
                }

            }

            if (canMove('s', v)) {
                State southState = new State(initMap, curState.stateVehicles);
                v.moveSouth(southState, v);

                southState.parentState = curState;
                southState.parentMove = new Pair(v.vehicleNum, 's');

                hashCode = Arrays.hashCode(southState.map);

                if (!stateTrack.containsKey(hashCode)) {
                    southState.plusMove();
                    stateTrack.put(hashCode,southState);
                    stateQueue.add(southState);
                }

            }

            if (canMove('e', v)) {
                State eastState = new State(initMap, curState.stateVehicles);
                v.moveEast(eastState, v);
                eastState.parentState = curState;
                eastState.parentMove = new Pair(v.vehicleNum, 'e');

                hashCode = Arrays.hashCode(eastState.map);


                if (!stateTrack.containsKey(hashCode)) {

                    if (eastState.map[17] == 0) {
                        solutions.add(eastState);
                    }

                    eastState.plusMove();
                    stateTrack.put(hashCode, eastState);
                    stateQueue.add(eastState);
                }


            }

            if (canMove('w', v)) {
                State westState = new State(initMap, curState.stateVehicles);
                v.moveWest(westState, v);
                westState.parentState = curState;
                westState.parentMove = new Pair(v.vehicleNum, 'w');

                hashCode = Arrays.hashCode(westState.map);

                if (!stateTrack.containsKey(hashCode)) {
                    westState.plusMove();
                    stateTrack.put(hashCode, westState);
                    stateQueue.add(westState);
                }

            }

        }
    }

    public boolean checkCollision(Vehicle v, State mape, char dir) {
        boolean canMove = true;

        switch (dir) {

            case 'n':
                if (mape.map[(v.location.get(0)) - 7] != -1) {

                    return false;
                }
                break;
            case 'e':

                if (mape.map[v.location.get(v.location.size() - 1)] != -1) {
                    return false;
                }

                break;
            case 's':
                if (mape.map[v.location.get(v.location.size() - 1) - 1 + 6] != -1) {
                    return false;
                }

                break;
            case 'w':

                if (mape.map[v.location.get(0) - 2] != -1) {
                    return false;
                }
                break;
        }

        return canMove;
    }

    public boolean canMove(char dir, Vehicle v) {
        int coord;
        switch (dir) {

            case 'w':
                coord = v.location.get(0);
                if ((coord - 1 >= 0 && coord - 1 <= 35) && coord - 1 != 0 && checkCollision(v, curState, dir) && v.viableMoves[1] == 'w'
                        && coord - 1 != 6 && coord - 1 != 12 && coord - 1 != 18 && coord - 1 != 24 && coord - 1 != 30) {
                    return true;
                }
                break;
            case 'e':
                coord = v.location.get(v.location.size() - 1);
                if ((coord + 1 >= 0 && coord + 1 <= 35) && checkCollision(v, curState, dir) && v.viableMoves[1] == 'w'
                        && coord != 6 && coord != 12 && coord != 18 && coord != 24 && coord != 30) {
                    return true;
                }
                break;
            case 'n':
                coord = v.location.get(0);
                if ((coord - 7 >= 0 && coord - 7 <= 35) && checkCollision(v, curState, dir) && v.viableMoves[1] == 's') {
                    return true;
                }
                break;

            case 's':
                coord = v.location.get(v.location.size() - 1);
                if ((coord + 6 >= 0 && coord + 6 <= 35) && checkCollision(v, curState, dir) && v.viableMoves[1] == 's') {
                    return true;
                }
                break;
        }
        return false;
    }

    public int getNumOfPaths() {
        return numPaths;
    }

    public static class State {
        public State(int[] carCoords, ArrayList<Vehicle> copy) {

            map = Arrays.copyOf(carCoords, 36);
            for (int i = 0; i < copy.size(); i++) {
                stateVehicles.add(copy.get(i));
            }
            finished = false;
            numMoves = 0;
        }

        public State(int[] carCoords) {
            map = Arrays.copyOf(carCoords, 36);
            finished = false;
            numMoves = 0;
        }

        ArrayList<Vehicle> stateVehicles = new ArrayList<>();

        boolean finished;
        State parentState;
        Pair parentMove;

        int[] map;
        int numMoves;

        void plusMove() {
            numMoves = parentState.numMoves + 1;
        }

        void addVehicle(Vehicle v) {
            stateVehicles.add(v);
        }

        void copyStateVehicles(ArrayList<Vehicle> v) {
            stateVehicles = v;
        }

        void setStateVehicles(int i, Vehicle v) {
            stateVehicles.set(i, v);
        }

    }

    public static class Pair {
        int id;
        char direction;

        public Pair(int i, char d) {
            id = i;
            direction = d;
        }

        char getDirection() {
            return direction;
        }

        int getId() {
            return id;
        }

        void setDirection(char d) {
            direction = d;
        }

        void setId(int i) {
            id = i;
        }
    }

    public class Vehicle {
        int vehicleNum;
        ArrayList<Integer> location = new ArrayList<>();
        char[] viableMoves;

        void setVehicleNum(int n) {
            vehicleNum = n;
        }

        void addCoord(int n) {
            location.add(n);
        }

        public Vehicle copyVehicle(Vehicle original) {
            Vehicle copy = new Vehicle();
            copy.setVehicleNum(original.vehicleNum);
            for (int coord : original.location) {
                copy.addCoord(coord);
            }
            copy.setMoves();
            return copy;
        }

        void setMoves() {
            char[] nsMoves = {'n', 's'};
            char[] ewMoves = {'e', 'w'};
            if (location.get(0) + 1 == location.get(1) || location.get(0) - 1 == location.get(1)) {
                viableMoves = ewMoves;
            } else {
                viableMoves = nsMoves;
            }
        }

        void moveEast(State thisState, Vehicle v) {
            Vehicle newV = copyVehicle(v);

            for (int i = (location.size()); i > 0; i--) {
                int coord = location.get(i - 1);
//                location.set(i, coord + 1);

                thisState.map[coord - 1] = -1;
                thisState.map[coord] = vehicleNum;
                // thisState.map[coord] = vehicleNum;
                newV.location.set(i - 1, coord + 1);
            }

            if (thisState.map[17] == 0) {
              //  solutions.add(thisState);
                numPaths += 1;
             //   thisState.finished = true;
            }
            thisState.setStateVehicles(v.vehicleNum, newV);
        }

        void moveWest(State thisState, Vehicle v) {
            Vehicle newV = copyVehicle(v);

            if (v.location.size() == 2) {

                for (int i = 0; i < location.size(); i++) {
                    int coord = location.get(i);
                    thisState.map[coord - 1] = -1;
                    thisState.map[coord - 2] = vehicleNum;

                    newV.location.set(i, coord - 1);
                }
            } else {
                for (int i = 0; i < location.size(); i++) {
                    int coord = location.get(i) - 1;
                    if (thisState.map[coord + 1] == vehicleNum) {
                        thisState.map[coord + 1] = -1;
                    }
                    thisState.map[coord - 1] = vehicleNum;
                    newV.location.set(i, coord);
                }
            }
            thisState.setStateVehicles(v.vehicleNum, newV);
        }

        void moveNorth(State thisState, Vehicle v) {
            Vehicle newV = copyVehicle(v);

            for (int i = 0; i < location.size(); i++) {
                int coord = location.get(i);
                thisState.map[coord - 1] = -1;
                thisState.map[coord - 7] = vehicleNum;
                newV.location.set(i, coord - 6);
            }
            thisState.setStateVehicles(v.vehicleNum, newV);
        }

        void moveSouth(State thisState, Vehicle v) {
            Vehicle newV = copyVehicle(v);
            for (int i = location.size(); i > 0; i--) {
                int coord = location.get(i - 1);
                thisState.map[coord - 1] = -1;
                thisState.map[coord + 5] = vehicleNum;
                newV.location.set(i - 1, coord + 6);
            }

            thisState.setStateVehicles(v.vehicleNum, newV);
        }
    }
}