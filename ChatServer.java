import ChatApp.*;          // The package containing our stubs. 
import org.omg.CosNaming.*; // HelloServer will use the naming service. 
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions. 
import org.omg.CORBA.*;     // All CORBA applications need these classes. 
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
import java.util.*;
 
class ChatImpl extends ChatPOA
{
  private ORB orb;
  Map<String, ChatCallback> clients = new HashMap<String, ChatCallback>();
  FiveInaRow game = new FiveInaRow();
  
  public void setORB(ORB orb_val) {
    orb = orb_val;
  }
  
  public String join(ChatCallback objref, String nick)
  {    
    if(clients.containsKey(nick)){
      objref.callback(nick + " is already an active chatter");
      return "active";
    }
    for (ChatCallback callback : clients.values())
    {
      callback.callback(nick + " has joined!"); // goes out to everyone
    }
    
    objref.callback("Welcome " + nick);
    clients.put(nick, objref);
    return nick;
    
  }
  
  public void post(ChatCallback objref, String nick, String msg)
  {
    for (ChatCallback callback : clients.values())
    {
      callback.callback(nick + " said: " + msg);
    }   
  }
  
  public void list(ChatCallback objref, String nick)
  {
    objref.callback("List of registered users: ");
       
    for(String joinedNicks : clients.keySet()){
      objref.callback(joinedNicks);
    }
  }

  public void leave(ChatCallback objref, String nick) 
  {
    clients.remove(nick); // remove post in hash
    for (ChatCallback callback : clients.values()) 
    {
      callback.callback(nick + " has left."); // broadcast message
    }
    
    objref.callback("Cheers " + nick);
  }

  public void play(ChatCallback objref, String nick, String color)
  {
    game.join(objref, nick, color);
  }
  public void put(ChatCallback objref, String nick, String pos)
  {
    game.put(objref, nick, pos); 
  }
  public void leaveGame(String nick)
  {
    game.leave(nick);
  }
}

public class ChatServer 
{
  public static void main(String args[]) 
  {
    try { 
      // create and initialize the ORB
      ORB orb = ORB.init(args, null); 

      // create servant (impl) and register it with the ORB
      ChatImpl chatImpl = new ChatImpl();
      chatImpl.setORB(orb); 

      // get reference to rootpoa & activate the POAManager
      POA rootpoa = 
        POAHelper.narrow(orb.resolve_initial_references("RootPOA"));  
      rootpoa.the_POAManager().activate(); 

      // get the root naming context
      org.omg.CORBA.Object objRef = 
        orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // obtain object reference from the servant (impl)
      org.omg.CORBA.Object ref = 
        rootpoa.servant_to_reference(chatImpl);
      Chat cref = ChatHelper.narrow(ref);

      // bind the object reference in naming
      String name = "Chat";
      NameComponent path[] = ncRef.to_name(name);
      ncRef.rebind(path, cref);

      System.out.println("ChatServer ready and waiting ...");
	    
      // wait for invocations from clients
      orb.run();
    }
	    
    catch(Exception e) {
      System.err.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }

    System.out.println("ChatServer Exiting ...");
  }

}
