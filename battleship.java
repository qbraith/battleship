import java.lang.Thread;
import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;

public class battleship{
   final static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
   final static int[] pieceLengths = {5, 4, 4, 3, 3, 3, 2, 2, 2, 2};   
   final static String reset = "\u001B[0m";
   final static String red = "\u001B[31m";
   final static String green = "\u001B[32m";
   static int compShipLeft = 10;
   static int playerShipLeft = 10; 
   static boolean guessAgain = false;
   static boolean hitNotSunk = false;
   static int beforeRow = 0, beforeCol = 0, moveAfterHit = 0;
   static String directionAfterHit = "";
   static boolean randomDirectionBoolean = false;

   public static void fillBoards(String[][] board1, String[][] board2, String[][] board3){
      for (int i = 0; i < board1.length; i++){
         for (int j = 0; j < board1[i].length; j++){
            board1[i][j] = ".";
            board2[i][j] = "."; //assumes boards are same dimensions (they have to be)
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
               boolean validUserLocation = checkUser(location, new LinkedList<>()); //new linkedlist as placeholder
               if (!validUserLocation){
                  System.out.println("That location is not on the board.");
                  continue;
               }else{
                  int row = findLetter(location);
                  int col = Integer.valueOf(location.substring(1,2)) - 1;
                  boolean isValid = checkValid(board, row, col, item, orient, false);
                  if (!isValid){
                        System.out.println("You can't place your ship there.");
                        continue;
                  }
    
                  place(board, row, col, item, orient, false, piecePlacer); //item is the length of the piece itself
                  printBoard(board);
                  iterator.remove();
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

   public static boolean checkValid(String[][] board, int row, int col, int pieceLength, String direction, boolean isComp){
      if (direction.equals("v")){
         if (row + pieceLength > 10)
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
         if (col + pieceLength > 10)
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
      for (int i = 0; i < letters.length; i++){
         if (letters[i].equals(letter))
            return i;
      }
      return -1;
   }
    
   public static boolean compGuess(String[][] board, LinkedList<String> guessed, Random r) throws InterruptedException{
      System.out.println("Comp guess method started");
      boolean duplicate = true;
      int row = 0, col = 0; //setting them to 0 as placeholders
      do{ //this loop makes sure guess isn't a duplicate
         if (hitNotSunk){
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
      System.out.println("Comp Guess made");
      
      boolean missed = false, shipSunk = false;
      String move = board[row][col];
      do{
         System.out.println("Your board (numbers represent ship numbers)");
         System.out.println("Ships remaining: " + playerShipLeft);
         if (hitNotSunk)
         moveAfterHit++;
         guessed.add(String.valueOf(row) + String.valueOf(col)); 
         missed = move.equals("."); //true if miss
         if (!missed){ //hit
            String current = board[row][col];
            board[row][col] = "X";
            shipSunk = shipSank(board, current); 
            if (shipSunk)
            playerShipLeft--;
            
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
         else{
            Thread.sleep(3500);
         }
         if (shipSunk){
            hitNotSunk = false;
            moveAfterHit = 0;
            directionAfterHit = "";
            randomDirectionBoolean = false;
            Thread.sleep(500);
            System.out.println("\nYour ship got sank!!!");
            System.out.println("Ships remaining: " + playerShipLeft + "\n");
            Thread.sleep(2500);
            boolean gameOver = checkWinner();
            if (gameOver)
               return true;
            return compGuess(board, guessed, r);
            //might have to recursively recall
         } else{ //here is where you implement logic to guess somewhere close
            hitNotSunk = true; 
            guessAgain = true;
            beforeRow = row;
            beforeCol = col;
            if (directionAfterHit.length() > 0)
               randomDirectionBoolean = true;
            int[] rowCol = compSmartGuess(board, row, col, guessed, r);
            System.out.println("Correct guess. Next guess coordinates: {" + rowCol[0] + "," + rowCol[1] + "}");
            row = rowCol[0];
            col = rowCol[1];
            move = board[row][col];
            //might have to introduce static boolean to keep track of this

         }
      } while (!missed);
      guessAgain = false;
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
      System.out.println("Start of csg method. Creating next move for: " + letters[row] + (col+1));
      if (randomDirectionBoolean){
         System.out.println("Pattern established: " + directionAfterHit);
         System.out.print("moveafterhit: " + moveAfterHit);
         System.out.println(". Just incase move: " + guessed.get(guessed.size() - moveAfterHit - 1));
      }
      int[] output = {0, 0};
      ArrayList<Integer> chosen = new ArrayList<>();
      while (true){
         output = new int[] {row, col};
         int choice = r.nextInt(4);
         if (randomDirectionBoolean){
            if (directionAfterHit.charAt(0) == 'v'){
               choice = ((int)(Math.random()*2));
               if (chosen.contains(0) && chosen.contains(1)){
                  System.out.println("We full v");
                  int prevRow = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(0, 1));
                  int prevCol = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(1, 2));
                  return compSmartGuess(board, prevRow, prevCol, guessed, r);
               }
            } else{
               choice = ((int)(Math.random()*2)) + 2;
               if (chosen.contains(2) && chosen.contains(3)){
                  System.out.println("We full h");
                  int prevRow = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(0, 1));
                  int prevCol = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(1, 2));
                  return compSmartGuess(board, prevRow, prevCol, guessed, r);
               }
            }
         }
         switch (choice){
            case 0:
            output[0]++;
            if (!randomDirectionBoolean)
               directionAfterHit = "vx";
            break;
            case 1:
            output[0]--;
            if (!randomDirectionBoolean)
               directionAfterHit = "vx";
            break;
            case 2:
            output[1]++;
            if (!randomDirectionBoolean)
               directionAfterHit = "hx";
            break;
            case 3:
            output[1]--;
            if (!randomDirectionBoolean)
               directionAfterHit = "hx";
         }
         chosen.add(choice);
         if (chosen.contains(0) && chosen.contains(1) && chosen.contains(2) && chosen.contains(3)){
            System.out.println("Contains all. Current: " + letters[row] + (col+1) + ", next" + guessed.get(guessed.size() - moveAfterHit - 1));
            int prevCol = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(0, 1));
            int prevRow = Integer.valueOf(guessed.get(guessed.size() - moveAfterHit - 1).substring(1, 2));
            return compSmartGuess(board, prevRow, prevCol, guessed, r);
         }
         try{
            String move = board[output[0]][output[1]];
            move.toUpperCase(); //line doesn't do anything
            if (!guessed.contains(String.valueOf(output[0]) + String.valueOf(output[1])))
               break;
         } catch (java.lang.ArrayIndexOutOfBoundsException e){
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
   public static void main(String[] args) throws InterruptedException{
      Random r = new Random();
      Scanner obj = new Scanner(System.in);
      System.out.println("Battleship game.");
      LinkedList<String> compGuessed = new LinkedList<>(); //saved as numbers, not coordinates
      LinkedList<String> playerGuessed = new LinkedList<>();
      String[][] compBoard = new String[10][10];
      String[][] playerBoard = new String[10][10];
      String[][] guesses = new String[10][10];
      int turn = 1;
        
      fillBoards(compBoard, playerBoard, guesses);
      compSetup(compBoard, r);
      compSetup(playerBoard, r);
      clear();
      printBoard(compBoard);
      Thread.sleep(7000);
      while (true){
         clear();
         boolean gameOver;
         if (turn%2 == 1){
            gameOver = playerGuess(compBoard, guesses, playerGuessed, obj);
            Thread.sleep(2000);
            
         } else{           
            gameOver = compGuess(playerBoard, compGuessed, r);
            Thread.sleep(7000);
         }
         if (gameOver)
            break;
         turn++;
      }
      if (turn%2==1){
         System.out.println(green + "\nYOU WIN!!!!!" + reset);
      } else{
         System.out.println(red + "\nYOU LOSE!!!!!" + reset);
      }
      
   }

}