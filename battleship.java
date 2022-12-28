import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
//import java.lang.Thread;
import java.util.Scanner;
public class battleship{
    static Random r = new Random();
    final static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    final static int[] pieceLengths = {5, 4, 4, 3, 3, 3, 2, 2, 2, 2};
    static int[] dudePerPiece = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //this is going to be used to test sunked ships
    //                           0, 1, 2, 3, 4, 5, 6, 7, 8, 9
    static int compShipLeft = 10;
    static int playerShipLeft = 10;

    public static void fillBoards(String[][] board1, String[][] board2){
        for (int i = 0; i < board1.length; i++){
            for (int j = 0; j < board1[i].length; j++){
                board1[i][j] = ".";
                board2[i][j] = "."; //assumes boards are same dimensions (they have to be)
            }
        }
    }

    public static void printBoard(String[][] board){
        System.out.println("   1  2  3  4  5  6  7  8  9  10");
        int counter = 0;
        for (String[] row : board){
            System.out.print(letters[counter] + "  ");
            for (String c : row){
                System.out.print(c + "  ");
            }
            counter++;
            System.out.println();
        }
    }

    public static void compSetup(String[][] board){
        for (int i = 0; i < pieceLengths.length; i++){
            String direction = ((int) (Math.random() *2) == 0) ? "v" : "h";
            int row = r.nextInt(10);
            int col = r.nextInt(10);
            boolean valid = false, cont = false;
            do{
                boolean result = checkValid(board, row, col, pieceLengths[i], direction, true);
                if (!result){
                    cont = true;
                    break;
                } else{
                    valid = true;
                }
            } while (!valid);
            
            if (cont){
                i--;
                continue;
            }

            place(board, row, col, i, direction, true, 0);
        }
    }

    public static void playerSetup(String[][] board, Scanner obj){
        System.out.println("When prompted below, enter 'v' if you would like to place the piece vertically on the board, 'h' if you would like to place it horizontally, or 's' if you would like to skip placing this and place a different piece first.\nPlease be careful. Unfortunantely, pieces can't be moved after they've been placed.");
        System.out.println("\nWhen prompted for where you want to put the piece. If you chose to place your piece horizontally, the space you enter will be the left most coordinate. If you chose vertically, it will be the highest coordinate.\n");
        //possibly print text about orientation
        //and implement waiting for user to read all information (thread.sleep)

        ArrayList<Integer> piecesToPlace = new ArrayList<>();
        for (int pieceLength : pieceLengths) {piecesToPlace.add(pieceLength);}

        while (piecesToPlace.size() > 0){
            int piecePlacer = 0;
            for (Iterator<Integer> iterator = piecesToPlace.iterator(); iterator.hasNext();){
                int item = iterator.next();
                System.out.println("Current board: ");
                printBoard(board);
                System.out.println("\nPlacing piece of length " + item);
                boolean orientationBool = false, cont = false;
                String orient = "";
                do {
                    System.out.print("Enter the orientation you want (v, h, or s): ");
                    String orientation = obj.nextLine().toLowerCase();
                    if (!orientation.equals("v") && !orientation.equals("h") && !orientation.equals("s")){
                        System.out.println("\nInvalid Input.\n");
                        continue;
                    } else if (orientation.equals("s")){
                        orientationBool = true;
                        cont = true;
                    } else{
                        orientationBool = true;
                        orient = orientation;
                    }
                } while (!orientationBool);
                if (cont)
                    continue;
                boolean validPlacement = false;
                do {
                    System.out.print("Enter the coordinate that you want to place your ship (ie A8): ");
                    String location = obj.nextLine();
                    boolean validUserLocation = checkUser(location);
                    if (!validUserLocation){
                        System.out.println("That location is not on the board.");
                        continue;
                    }else{
                        //never called checkvalid method
                        int row = findLetter(location);
                        int col = Integer.valueOf(location.substring(1,2)) - 1;
                        boolean isValid = checkValid(board, row, col, item, orient, false);
                        if (!isValid)
                            continue;
    
                        place(board, row, col, item, orient, false, piecePlacer); //item is the length of the piece itself
                        printBoard(board);
                        iterator.remove();
                        validPlacement = true;
                        piecePlacer++;
                    }
                } while (!validPlacement);
                //do while would need to start here
            }
        }
        System.out.println("all pieces placed");
        printBoard(board);
    }
    
    public static void place(String[][] board, int row, int col, int index, String direction, boolean isIndex, int output){
        int realLength = (!isIndex) ? index : pieceLengths[index];
        int display = (!isIndex) ? output : index;
        if (direction.equals("v")){
            for (int i = 0; i < realLength; i++){
                board[row+i][col] = "" + display;
            }
        } else{
            for (int i = 0; i < realLength; i++){
                board[row][col+i] = "" + display;
            }
        }
    }

    public static boolean checkValid(String[][] board, int row, int col, int pieceLength, String direction, boolean isComp){
        if (direction.equals("v")){
            if (row + pieceLength >= 10)
            return false;
            for (int i = 0; i < pieceLength; i++){
                if (isComp){
                    try{
                        if (!board[row+i][col+1].equals(".") || !board[row-1+i][col].equals(".") || !board[row+1+i][col].equals(".") || !board[row+i][col-1].equals(".")){
                            return false;
                        }
                    } catch (IndexOutOfBoundsException e){
                        //do nothing
                    }
                }
                if (!board[row + i][col].equals("."))
                return false;
            }
            return true;
        } else{
            if (col + pieceLength >= 10)
                return false;
            for (int i = 0; i < pieceLength; i++){
                if (isComp){
                    try{
                        if (!board[row][col+1+i].equals(".") || !board[row-1][col+i].equals(".") || !board[row+1][col+i].equals(".") || !board[row][col-1+i].equals(".")){
                            return false;
                        }
                    } catch (IndexOutOfBoundsException e){
                        //do nothing
                    }
                }
                if (!board[row][col+i].equals("."))
                    return false;
            }
            return true;
        }
    }
    
    public static boolean checkUser(String location){
        //check whether the spot user selected is valid or not in terms of sheer input
        if (location.length() != 2 && !location.substring(1, 3).equals("10"))
            return false;
        
        int notInside = 0;
        for (String item : letters){
            if (!location.substring(0, 1).toUpperCase().equals(item))
                notInside++;
        }
        if (notInside == letters.length)
            return false;
        try{
            int num = Integer.parseInt(location.substring(1, 2)) - 1; //valueOf does the same thing
            if (num < 0 || num > 9){ //condition should never be true
                return false; 
            }
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
    
    public static int findLetter(String location){
        String letter = location.substring(0, 1).toUpperCase();
        for (int i = 0; i < letters.length; i++){
            if (letters[i].equals(letter))
                return i;
        }
        return -1;
    }
    public static void main(String[] args) {
        Scanner obj = new Scanner(System.in);
        System.out.println("Battleship game.");
        String[][] compBoard = new String[10][10];
        String[][] playerBoard = new String[10][10];
        fillBoards(compBoard, playerBoard);
        compSetup(compBoard);
        //printBoard(compBoard);

        playerSetup(playerBoard, obj);



        obj.close();
    }



}