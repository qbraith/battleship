import java.lang.Thread;
//import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
public class battleship{
   private final static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
   private final static int[] pieceLengths = {5, 4, 4, 3, 3, 3, 2, 2, 2, 2};
   private final static String reset = "\u001B[0m", red = "\u001B[31m", green = "\u001B[32m"; //unicode for printing colors
   private static String directionAfterHit = "";
   private static int compShipLeft = 10;
   private static int playerShipLeft = 10;     //static variables for keeping track of logic
   private static int beforeRow = 0, beforeCol = 0, moveAfterHit = 0;
   private static boolean hitButNotSunk = false, directionEstablished = false;

   public static void fillBoards(String[][] board1, String[][] board2, String[][] board3){
      for (int i = 0; i < board1.length; i++){
         for (int j = 0; j < board1[i].length; j++){
            board1[i][j] = ".";
            board2[i][j] = "."; //filling boards with . for grid
            board3[i][j] = ".";
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

   public static void compSetup(String[][] board, Random r){
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

   public static void playerSetup(String[][] board, Scanner obj) throws InterruptedException{
      System.out.println("\n\nWhen prompted below, enter 'v' if you would like to place the piece vertically on the board, 'h' if you would like to place it horizontally, or 's' if you would like to skip placing this and place a different piece first.\nPlease be careful. Unfortunantely, pieces can't be moved after they've been placed.");
      System.out.println("\nWhen prompted for where you want to put the piece. If you chose to place your piece horizontally, the space you enter will be the left most coordinate. If you chose vertically, it will be the highest coordinate.\n");
      System.out.println("Scroll up to read fully");
      Thread.sleep(3000);
      //possibly print text about orientation
      //and implement waiting for user to read all information (thread.sleep)

      ArrayList<Integer> piecesToPlace = new ArrayList<>();
      for (int pieceLength : pieceLengths) {piecesToPlace.add(pieceLength);}
      //ArrayList<Integer> shipNumbers = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

      int piecePlacer = 0;
      while (piecesToPlace.size() > 0){
         for (Iterator<Integer> iterator = piecesToPlace.iterator(); iterator.hasNext();){ //add shipnumbers iterator(try on new branch or smth maybe)
            int item = iterator.next();
            System.out.println("Current board: ");
            printBoard(board);
            System.out.println("\nPlacing ship of length " + item);
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
            if (cont){
               piecePlacer++;
               continue;
            }
            boolean validPlacement = false;
            do {
               System.out.print("Enter the coordinate that you want to place your ship (ie A8): ");
               String location = obj.nextLine();
               boolean validUserLocation = checkUser(location, new LinkedList<>()); //new linkedlist as placeholder
               if (!validUserLocation){
                  System.out.println("That location is not on the board.");
                  continue;
               }else{
                  int row = findLetter(location);
                  int col = Integer.valueOf(location.substring(1)) - 1;
                  boolean isValid = checkValid(board, row, col, item, orient, false);
                  if (!isValid){
                        System.out.println("You can't place your ship there.");
                        continue;
                  }
                  
                  place(board, row, col, item, orient, false, (piecePlacer%10)); //item is the length of the piece itself
                  printBoard(board);
                  iterator.remove();
                  //shipNumbers.remove(Integer.valueOf(piecePlacer%10));
                  validPlacement = true;
                  piecePlacer++;
               }
            } while (!validPlacement);
         }
      }
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

   public static int helper(String[][] board, int checkRow, int checkCol){
      int adjacent = 0, bounds = 0;
      try{
         if (!board[checkRow][checkCol].equals(".")){
            //there is something found next to the ship that isn't an empty space
            adjacent++;
         }
      } catch (Exception e){
         bounds++;
      }
      return adjacent;
   }
   
   public static boolean checkValid(String[][] board, int row, int col, int pieceLength, String direction, boolean isComp){
      if (direction.equals("v")){
         if (row + pieceLength > 10)
            return false;
         for (int i = 0; i < pieceLength; i++){
            if (isComp){
               int[][] places = {{row+i, col+1}, {row+i-1, col}, {row+i+1, col}, {row+i, col-1}};
               int bad = 0;
               
               for (int j = 0; j < 4; j++){
                  bad += helper(board, places[j][0], places[j][1]);
               }

               if (bad > 0)
                  return false;
            }
            if (!board[row + i][col].equals("."))
               return false;
         }
         return true;
      } else{
         if (col + pieceLength > 10)
            return false;
         for (int i = 0; i < pieceLength; i++){
            if (isComp){
               int[][] places = {{row-1, col+i}, {row+1, col+i}, {row, col+i-1}, {row, col+i+1}};
               int good = 0;

               for (int j = 0; j < 4; j++){
                  good += helper(board, places[j][0], places[j][1]);
               }

               if (good > 0)
                  return false;
            }
            if (!board[row][col+i].equals("."))
               return false;
         }
         return true;
      }
   }
    
   public static boolean checkUser(String location, LinkedList<String> userGuesses){
      //check whether the spot user selected is valid or not in terms of sheer input
      if (location.length() != 2 && location.length() != 3)
         return false;
      if (location.length() == 3 && !location.substring(1, 3).equals("10"))
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
         if (num < 0 || num > 9) // (just in case), this condition should never be true
            return false; 
      } catch (NumberFormatException e){
         return false;
      }
      if (userGuesses.contains(location.toLowerCase())){
         System.out.println("You already guessed this.");
         return false;
      }

      return true;
   }
    
   public static int findLetter(String location){
      String letter = location.substring(0, 1).toUpperCase();
      for (int i = 0; i < letters.length; i++){ //linear search
         if (letters[i].equals(letter))
            return i;
      }
      return -1;
   }
    
   public static boolean compGuess(String[][] board, LinkedList<String> guessed, Random r) throws InterruptedException{
      boolean duplicate = true;
      int row = 0, col = 0; //setting them to 0 as placeholders
      do{ //this loop makes sure guess isn't a duplicate
         if (hitButNotSunk){
            int[] rowCol = compSmartGuess(board, beforeRow, beforeCol, guessed, r);
            row = rowCol[0];
            col = rowCol[1];
            System.out.println("shipnotsunk, coordinate to guess off: " + (row) + (col));
         } else{
            row = r.nextInt(10);
            col = r.nextInt(10);
         }
         duplicate = guessed.contains(String.valueOf(row) + String.valueOf(col)); //true if not duplicate
      } while (duplicate);
      
      boolean missed = false, shipSunk = false;
      String move = board[row][col];
      do{
         System.out.println("Your board (numbers represent ship numbers)");
         System.out.println("Ships remaining: " + playerShipLeft);
         if (hitButNotSunk)
            moveAfterHit++;
         guessed.add(String.valueOf(row) + String.valueOf(col)); 
         missed = move.equals("."); //true if miss
         if (!missed){ //hit
            String current = board[row][col];
            board[row][col] = "X";
            shipSunk = shipSank(board, current); 
            if (shipSunk)
               playerShipLeft--;
            if (playerShipLeft == 0)
               return true;
            
         } else{
            board[row][col] = "O";
         }
         printBoard(board);

         String result = (missed) ? red + "MISS" + reset : green + "HIT" + reset;
         System.out.println("Computer guessed: " + letters[row] + (col+1));
         System.out.print("Result... ");
         Thread.sleep(750);
         System.out.println(result);
         if (missed)
            return false;
         else
            Thread.sleep(2500);

         if (shipSunk){
            hitButNotSunk = false;
            moveAfterHit = 0;
            directionAfterHit = "";
            directionEstablished = false;
            Thread.sleep(500);
            System.out.println("\nYour ship got sank!!!");
            System.out.println("Ships remaining: " + playerShipLeft + "\n");
            Thread.sleep(2500);
            return compGuess(board, guessed, r);
         } else{
            hitButNotSunk = true; 
            beforeRow = row;
            beforeCol = col;
            if (directionAfterHit.length() > 0)
               directionEstablished = true;
            int[] rowCol = compSmartGuess(board, row, col, guessed, r);
            row = rowCol[0];
            col = rowCol[1];
            move = board[row][col];
         }
      } while (!missed);
      return false;
   }
    
   public static boolean playerGuess(String[][] board, String[][] guessBoard, LinkedList<String> guessedList, Scanner obj) throws InterruptedException{ //boolean checks if game is over
      boolean missed = false;
      do{
         boolean validGuess = false;
         String finalGuess = "";
         clear();
         System.out.println("Your guesses (x = hit, o = miss): ");
         System.out.println("Computer ships remaining: " + compShipLeft);
         printBoard(guessBoard);
         do{
            System.out.print("Enter a coordinate where you think the opponent ship is: ");
            String guess = obj.nextLine();
            boolean valid = checkUser(guess, guessedList);
            if (!valid)
               continue;
            else{
               guessedList.add(guess.toLowerCase());
               finalGuess = guess;
               validGuess = true;
            }
         } while (!validGuess);
         //guess on board established
         int row = findLetter(finalGuess.substring(0, 1));
         int col = Integer.valueOf(finalGuess.substring(1, finalGuess.length())) - 1;
         String current = board[row][col];
    
         String result = (current.equals(".")) ? red + "MISS" + reset : green + "HIT" + reset;
         boolean sunkShip = false;
         if (result.equals(red + "MISS" + reset)){
            guessBoard[row][col] = "O";
            missed = true;
         } else{
            guessBoard[row][col] = "X";
            board[row][col] = "X" + current; //turns 8 into X8 (signifying hit)
            sunkShip = shipSank(board, current);
            if (sunkShip){
               compShipLeft--;
               boolean gameOver = checkWinner();
               if (gameOver){
                  return true;
               }
            }        
         }

         System.out.print("Result... ");
         Thread.sleep(750);
         System.out.println(result);
         if (result.equals(green + "HIT" + reset))
            Thread.sleep(1000);
         if (sunkShip){
            Thread.sleep(500);
            System.out.println("\nYou sunk a ship!!!");
            System.out.println("Computer ships remaining: " + compShipLeft + "\n");
            Thread.sleep(2500);
         }

      } while (!missed);
      return false;
   }
    
   public static int[] compSmartGuess(String[][] board, int row, int col, LinkedList<String> guessed, Random r){
      int[] output = {0, 0}; //guessing logically, keeping track of hits and misses
      ArrayList<Integer> chosen = new ArrayList<>();

      while (true){
         output = new int[] {row, col};
         int choice = r.nextInt(4);
         if (directionEstablished){
            if (directionAfterHit.charAt(0) == 'v'){
               choice = ((int)(Math.random()*2));
               if (chosen.contains(0) && chosen.contains(1)){
                  int prevRow = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(0, 1));
                  int prevCol = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(1));
                  return compSmartGuess(board, prevRow, prevCol, guessed, r);
               }
            } else{
               choice = ((int)(Math.random()*2)) + 2;
               if (chosen.contains(2) && chosen.contains(3)){
                  int prevRow = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(0, 1));
                  int prevCol = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(1));
                  return compSmartGuess(board, prevRow, prevCol, guessed, r);
               }
            }
         }
         switch (choice){
            case 0:
            output[0]++;
            if (!directionEstablished)
               directionAfterHit = "v";
            break;
            case 1:
            output[0]--;
            if (!directionEstablished)
               directionAfterHit = "v";
            break;
            case 2:
            output[1]++;
            if (!directionEstablished)
               directionAfterHit = "h";
            break;
            case 3:
            output[1]--;
            if (!directionEstablished)
               directionAfterHit = "h";
            break;
         }
         chosen.add(choice);
         if (chosen.contains(0) && chosen.contains(1) && chosen.contains(2) && chosen.contains(3)){
            int prevCol = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(0, 1));
            int prevRow = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(1));
            return compSmartGuess(board, prevRow, prevCol, guessed, r);
         }
         try{
            String move = board[output[0]][output[1]];
            move.toUpperCase(); //line doesn't do anything
            if (!guessed.contains(String.valueOf(output[0]) + String.valueOf(output[1]))) //check if duplicate
               break;
         } catch (java.lang.ArrayIndexOutOfBoundsException e){ //check if on board
            continue;
         }
      }
      return output;
   }

   public static boolean shipSank(String[][] board, String target){
      for (String[] row : board){
         for (String item : row){
            if (item.equals(target))
               return false;
         }
      }
      return true;
   }
    
   public static boolean checkWinner(){
      if (compShipLeft == 0 || playerShipLeft == 0)
         return true;
      return false;
   }
    
   public static void clear(){
      System.out.print("\033[H\033[2J");  
      System.out.flush(); 
   }
   public static void main(String[] args) throws InterruptedException{ //could make in diff class but no need
      Random r = new Random();
      Scanner obj = new Scanner(System.in);
      LinkedList<String> compGuessed = new LinkedList<>(), playerGuessed = new LinkedList<>(); //saving only numbers not coordinates
      String[][] compBoard = new String[10][10], playerBoard = new String[10][10], guesses = new String[10][10];
      
      int turn = 1;
      
      fillBoards(compBoard, playerBoard, guesses);
      compSetup(compBoard, r);

      System.out.println("Battleship game.");
      System.out.println("In this game, the player and computer will have: ");
      System.out.println("  - 1 ship length 5\n  - 2 ships length 4\n  - 3 ships length 3\n  - 4 ships length 2");
      System.out.print("\nEnter 'y' if you want the cpu to automatically make your board (defualt) or 'n' if you would like to make your board yourself: ");

      String decision = obj.nextLine().toLowerCase();
      if (decision.equals("n"))
         playerSetup(playerBoard, obj);
      else
         compSetup(playerBoard, r);
      Thread.sleep(1000);
      System.out.println("comp baord");
      printBoard(compBoard);
      System.out.println("player board");
      printBoard(playerBoard);
      System.out.println("\n\n");
      Thread.sleep(10000);

      while (true){
         clear();
         boolean gameOver;
         if (turn%2 == 1){
            gameOver = playerGuess(compBoard, guesses, playerGuessed, obj);
            Thread.sleep(2000);
         } else{           
            gameOver = compGuess(playerBoard, compGuessed, r);
            Thread.sleep(3500);
         }
         if (gameOver)
            break;
         turn++;
      }

      if (turn%2==1)
         System.out.println(green + "\nYOU WIN!!!!!" + reset);
      else
         System.out.println(red + "\nYOU LOSE!!!!!" + reset);

   }

}