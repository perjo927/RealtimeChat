import ChatApp.*;          // The package containing our stubs
import org.omg.CosNaming.*; // HelloClient will use the naming service.
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;     // All CORBA applications need these classes.
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
import java.util.*;

 
class ChatCallbackImpl extends ChatCallbackPOA
{
  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  public void callback(String notification)
  {
    System.out.println(notification);
  }
}

public class ChatClient
{
  static Chat chatImpl;
    
  public static void main(String args[])
  {
    try {
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // create servant (impl) and register it with the ORB
      ChatCallbackImpl chatCallbackImpl = new ChatCallbackImpl();
      chatCallbackImpl.setORB(orb);

      // get reference to RootPOA and activate the POAManager
      POA rootpoa = 
        POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();
	    
      // get the root naming context 
      org.omg.CORBA.Object objRef = 
        orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    
      // resolve the object reference in naming
      String name = "Chat";
      chatImpl = ChatHelper.narrow(ncRef.resolve_str(name));
	    
      // obtain callback reference for registration w/ server
      org.omg.CORBA.Object ref = 
        rootpoa.servant_to_reference(chatCallbackImpl);
      ChatCallback cref = ChatCallbackHelper.narrow(ref);
        
      // Application code goes below
      String nick = "";
      String[] input;
      Scanner in = new Scanner(System.in);
      boolean is_active = false;
      boolean is_playing = false;
          
      while(true){
              
        input = in.nextLine().split(" ");

        if (input[0].equals("join")){
          if (input.length < 2){
            System.out.println("no name given at command line ");
          }
          else if (!is_active){
            nick = chatImpl.join(cref, input[1]);
            if (!(nick.equals("active"))){
              is_active = true;
            }
          }
          else{
            System.out.println("Don't join two times, " + nick);
          }
        }
    
        if (input[0].equals("post")){
          if (is_active){
            String msg = "";
            for(int i= 1; i < input.length; i++) {
              msg = msg + " " + input[i];
            }       
            chatImpl.post(cref, nick, msg);
          }
          else{
            System.out.println("You need to be active to do that ;)");
          }
        }
        
        if (input[0].equals("list")){
          chatImpl.list(cref, nick );
        }
        
        if (input[0].equals("leave")){
          if (is_active){
            chatImpl.leave(cref, nick);
            is_active = false;

            if (is_playing){
              chatImpl.leaveGame(nick); 
              is_playing = false;
            }
          }
          else{
            System.out.println("You cannot leave if you have not joined..");
          }
        }     

        if(input[0].equals("play")){
          if (is_active){
            if (input.length > 1){
              String color = input[1].substring(0,1); // only 1 character
              chatImpl.play(cref, nick, color);
              is_playing = true;
            }
          }
          else{
            System.out.println("Please join first");
          }
        }
        
        if (input[0].equals("put")){
          if (is_playing){
            if (input.length > 1){
              String pos = input[1];
              if (pos.matches("\\d{2}")){
                chatImpl.put(cref, nick, pos);
              }
              else{
                System.out.println("Only 2-digit numeric characters please");
              }
            }
            else{
              System.out.println("You must provide a 2-digit argument to put");
            }
          }
          else{
            System.out.println("Do yourself a favour and join a game first");
          }
        }

        if (input[0].equals("quit")){
          if  (!is_active){
            System.out.println("Bye " + nick + " :>");
            System.exit(0);
            
            // quit
          }
          else{
            System.out.println(nick + "! You are still active, leave first!!");
          }
        } 
      }              
    }
    catch(Exception e){
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
     }
  }
}
