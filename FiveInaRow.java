import ChatApp.*;          // The package containing our stubs. 
import java.util.*;

class FiveInaRow{
  private static int maxX = 8; // width
  private static int maxY = 8; // height
  // who is playing? nick + obj
  public Map<String, ChatCallback> players = new HashMap<String, ChatCallback>();
  // who has which color?
  public Map<String, String> colors = new HashMap<String, String>();
  // how does the board look?
  public String[][] board = new String[maxX][maxY];

  public FiveInaRow(){
    reset();
  }
  
  public void join(ChatCallback objref, String nick, String color)
  {
    if(players.containsKey(nick)){
      objref.callback (nick + " is already playing :/");
      return;
    }
    for (ChatCallback callback : players.values()) // goes out to everyone
    {
      callback.callback(nick + " is now playing!");
    }   
    objref.callback("Welcome " + nick + "!!");
    players.put(nick, objref); // extend hash with new player
    colors.put(nick, color);  // extend hash with player's choice of color
  
    // Print board for joining gamers
    objref.callback(buildString());
  }
  public void put(ChatCallback objref, String nick, String pos)
  {
    String color = "";
    int inx, iny;
    
    // Array positions
    inx = pos.charAt(0) - '0' - 1; 
    iny = pos.charAt(1) - '0' - 1;

    if(inx > maxX-1 || inx < 0 || iny > maxY-1 || iny < 0){
      objref.callback("Invalid position");
      return;
    }
    
    for(String joinedNicks : colors.keySet()) // pick a nick for each color
    {
      // when it is our nick, fetch our color
      if (nick.equals(joinedNicks)){
        color = colors.get(joinedNicks);
      }        
    }

    if(board[inx][iny] == " "){
      board[inx][iny] = color; 
    }
    else{
      objref.callback("Position is occupied by another marker");
      return;
    }
    // Print the board for all gamers
    for (ChatCallback callback : players.values()){
      callback.callback(buildString());
    }

    // WINNERS? //    
    // *** Horizontal***
    // cols
    for(int x=0; x<maxX; x++){
      int counter = 0;
      // rows
      for(int y=0; y<maxY; y++){
        if(board[x][y].equals(color)){
          counter++;
        }
        else{
          counter = 0;
        }
        // victory
        if(counter == 5){
          // send victory message to victors, loser message to losers
          for (Map.Entry<String,String> entry : colors.entrySet()) {
            String name = entry.getKey();
            String col = entry.getValue();

            if (col.equals(color)){
              ChatCallback callback = players.get(name);
              callback.callback("YOU WON YAY");
            }
            else{
              ChatCallback callback = players.get(name);
              callback.callback("YOU LOST OH NO");
            }
          }
          reset();
          return;
        }
      }
    }
    // *** Vertical ***
    for(int y=0; y<maxY; y++){
      int counter = 0;
      for(int x=0; x<maxX; x++){
        if(board[x][y].equals(color)){
          counter++;
        }
        else{
          counter = 0; 
        }
        // victory
        if(counter == 5){
          // send victory message to victors, loser message to losers
          for (Map.Entry<String,String> entry : colors.entrySet()) {
            String name = entry.getKey();
            String col = entry.getValue();

            if (col.equals(color)){
                ChatCallback callback = players.get(name);
                callback.callback("YOU WON YAY");
              }
            else{
              ChatCallback callback = players.get(name);
              callback.callback("YOU LOST OH NO");
            }
          }
          reset();
          return;
        }
      }
    }
    // *** Diagonal ***
    for(int x1=0, y1=0; x1<maxX && y1<maxY; x1++){
      int counter = 0;
      
      //falling 
      for(int x=x1,y=y1; x<maxX && y<maxY; x++, y++){
        if(board[x][y].equals(color)){
          counter++;
        }
        else{
          break; 
        }
        // Falling victory
        if(counter == 5){
          // send victory message to victors, loser message to losers
          for (Map.Entry<String,String> entry : colors.entrySet()) {
            String name = entry.getKey();
            String col = entry.getValue();

            if (col.equals(color)){
              ChatCallback callback = players.get(name);
              callback.callback("YOU WON YAY");
            }
            else{
              ChatCallback callback = players.get(name);
              callback.callback("YOU LOST OH NO");
            }
          }
          reset();
          return;
        }
      } //falling

      counter = 0;
      
      // rising
      for(int x=x1,y=y1; x>=0 && y<maxY; x--, y++){
        if(board[x][y].equals(color)){
          counter++;
        }
        else{
          break; 
        }
        // rising victory
        if(counter == 5){
          // send victory message to victors, loser message to losers
          for (Map.Entry<String,String> entry : colors.entrySet()) {
            String name = entry.getKey();
            String col = entry.getValue();

            if (col.equals(color)){
              ChatCallback callback = players.get(name);
              callback.callback("YOU WON YAY");
            }
            else{
              ChatCallback callback = players.get(name);
              callback.callback("YOU LOST OH NO");
            }
          }
          reset();
          return;
        }
      } // rising

      if(x1==maxX-1){
        x1 = 0;
        y1++;     
      } 
    } // diagonal
  } // put

  public void leave(String nick){
    players.remove(nick);
    colors.remove(nick);
  }

  private void reset(){
  //Sätta in space på alla positioner, när någon vunnit eller i konstruktorn

    for(String[] row: board){
      Arrays.fill(row, " ");
    }
  }

  private String buildString(){
    StringBuilder builder = new StringBuilder();

    for(int i=0; i <= maxX + 1; ++i)
      builder.append("-");
    builder.append("\n");
    for(int x=0; x<board.length; x++){
      builder.append("|");
      for(int y=0; y<board.length; y++){
        builder.append(board[x][y]);
      }
      builder.append("|\n");   
    }
    for(int i=0; i <= maxX + 1; ++i){
      builder.append("-");
    }
    return builder.toString();
  }
}
