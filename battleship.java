import java.util.Random;
import java.util.ArrayList;
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
        for (String[] row : board){
            for (String c : row){
                System.out.print(c + "  ");
            }
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
                boolean result = checkValid(board, row, col, pieceLengths[i], direction);
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

            place(board, row, col, i, direction, true);
        }
    }

    public static void playerSetup(String[][] board, Scanner obj){
        int piecesPlaced = 0;
        System.out.println("When prompted below, enter 'v' if you would like to place the piece vertically on the board, 'h' if you would like to place it horizontally, or 's' if you would like to skip placing this and place a different piece first.\nPlease be careful. Unfortunantely, pieces can't be moved after they've been placed.");
        System.out.print("\nWhen prompted for where you want to put the piece. If you chose to place your piece horizontally, ");
        //possibly print text about orientation
        //and implement waiting for user to read all information (thread.sleep)

        while (piecesPlaced < 10){
            ArrayList<Integer> piecesToPlace = new ArrayList<>();
            for (int pieceLength : pieceLengths) {piecesToPlace.add(pieceLength);}

            while (piecesToPlace.size() > 0){
                for (int item : piecesToPlace){
                    System.out.println("Placing piece of length " + item);
                    System.out.print("Enter the orientation you want (v, h, or s): ");
                    String orientation = obj.nextLine().toLowerCase();
                    if (!orientation.equals("v") && !orientation.equals("h") && !orientation.equals("s")){
                        System.out.println("\nInvalid Input.\n");
                        continue;
                    } else if (orientation.equals("s"))
                        continue;
                    
                    String location = obj.next();
                    boolean validUserLocation = checkUser(location);
                    if (!validUserLocation)
                        continue;
                    else{
                        int row = findLetter(location);
                        int col = Integer.valueOf(location.substring(1,2));

                        place(board, row, col, item, orientation, false); //item is the length of the piece itself
                        piecesToPlace.remove(item);
                    }
                }
            }
        }
    }
    
    public static void place(String[][] board, int row, int col, int index, String direction, boolean isIndex){
        int realLength = (!isIndex) ? index : pieceLengths[index];
        if (direction.equals("v")){
            for (int i = 0; i < realLength; i++){
                board[row+i][col] = "" + index;
            }
        } else{
            for (int i = 0; i < realLength; i++){
                board[row][col+i] = "" + index;
            }
        }
    }

    public static boolean checkValid(String[][] board, int row, int col, int pieceLength, String direction){
        if (direction.equals("v")){
            if (row + pieceLength >= 10)
            return false;
            for (int i = 0; i < pieceLength; i++){
                try{
                    if (!board[row+i][col+1].equals(".") || !board[row-1+i][col].equals(".") || !board[row+1+i][col].equals(".") || !board[row+i][col-1].equals(".")){
                        return false;
                    }
                } catch (IndexOutOfBoundsException e){
                    //do nothing
                }
                if (!board[row + i][col].equals("."))
                return false;
            }
            return true;
        } else{
            if (col + pieceLength >= 10)
                return false;
            for (int i = 0; i < pieceLength; i++){
                try{
                    if (!board[row][col+1+i].equals(".") || !board[row-1][col+i].equals(".") || !board[row+1][col+i].equals(".") || !board[row][col-1+i].equals(".")){
                        return false;
                    }
                } catch (IndexOutOfBoundsException e){
                    //do nothing
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
        printBoard(compBoard);



        obj.close();
    }



}