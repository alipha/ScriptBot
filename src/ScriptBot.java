import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScriptBot {

	private String nick;
	private String line;
	private String[] tokens;
	private LineParser lineParser;
	private PrintWriter out;
	private HashMap<String, TokenNode> onListeners;
	private FuncDefNode wrapper;
	
	public static void main(String[] args) throws Exception {
		if(args.length > 0 && args[0].equals("irc"))
			new ScriptBot().run();
		else
			commandLine();
	}
	
	public void run() throws Exception {
		onListeners = new HashMap<String, TokenNode>();
		
		wrapper = new FuncDefNode();
		wrapper.names = new ArrayList<VariableNode>();
		wrapper.names.add(new VariableNode("line"));
		wrapper.names.add(new VariableNode("sender"));
		wrapper.names.add(new VariableNode("action"));
		wrapper.names.add(new VariableNode("dest"));
		wrapper.names.add(new VariableNode("msg"));

		nick = "AliphaBot";
		
	
		Socket socket = new Socket("irc.freenode.net", 6667);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		lineParser = new LineParser();
		runMessageLoop(in);
		
		out.close();
		in.close();
		socket.close();	
	}
	
	private void runMessageLoop(BufferedReader in) throws Exception {
		
		while((line = in.readLine()) != null) {
			System.out.println(line);
			lineParser.parse(line);

			if(lineParser.action.equals("")) {
				continue;
			}
			
			if(lineParser.action.equals("NOTICE")) {
				checkLogin(lineParser.message);
			} else if(lineParser.action.equals("PING")) {
				out.println("PONG " + lineParser.message);
				System.out.println("PONG :" + lineParser.message);
			} else if(lineParser.action.equals("PRIVMSG")) {
				if(lineParser.actionArg.equals(nick)) {
					handlePrivateMessage(lineParser.sender, lineParser.message);
				} else {
					handleChatroomMessage();
				}
			}
		}
	}
	
	private void checkLogin(String message) {
		if(message.equals("*** No Ident response")) {
			out.println("USER " + nick + " * * :" + nick);
			out.println("NICK " + nick);
			out.println("JOIN #aliphaBot");
			System.out.println("Sent login");
		}	
	}
	
	private void handlePrivateMessage(String sender, String message) {
		if(sender.equals("Alipha")) {
			out.println(message);
		}
	}
	
	private void handleChatroomMessage() {
		boolean run = lineParser.message.startsWith("!run ");
		
		try {
			if(run || lineParser.message.startsWith("!set ")) {
	//		if(message.startsWith(nick + ":") || message.startsWith(nick + " ")) {
	//			new Thread(new Runnable() {
	//				public void run() {
						Parser parser = new Parser();
						Object eval = parser.execute(lineParser.message.substring(4), run);
						String output = eval.toString();
						if(output.length() > 425)
							output = output.substring(0, 425);
						out.println("PRIVMSG " + lineParser.actionArg + " :" + lineParser.sender + ": " + output);
						
	//				}
	//			}).start();
				return;
			}
			
			if(lineParser.message.startsWith("!on ")) {
				int space = lineParser.message.indexOf(' ', 4);
				
				if(space > -1) {
					String name = lineParser.message.substring(4, space);
					Parser parser = new Parser();
					TokenNode tree = parser.createTree(parser.tokenize(lineParser.message.substring(space)));
					onListeners.put(name, tree);
				} else {
					onListeners.remove(lineParser.message.substring(4));
				}
				return;
			}
		} catch(Exception ex) {
			out.println("PRIVMSG " + lineParser.actionArg + " :" + lineParser.sender + ": error - " + ex.getMessage());
			Memory.reset(false);
		}
		
		
		ArrayList<Object> arg = new ArrayList<Object>();
		arg.add(new Variable("line", line));
		arg.add(new Variable("sender", lineParser.sender));
		arg.add(new Variable("action", lineParser.action));
		arg.add(new Variable("dest", lineParser.actionArg));
		arg.add(new Variable("msg", lineParser.message));
		
		for(String key : onListeners.keySet()) {	
			try {
				wrapper.operand = onListeners.get(key);
				Memory.getSystemContext().variables.put("*send", null);
				Object ret = wrapper.run(arg, Memory.getGlobalContext());
				
				System.out.println(key + "=" + ret.toString());
				
				Object value = null;
				Variable valueVariable = Memory.getSystemContext().variables.get("*send"); 
				if(valueVariable != null)
					value = valueVariable.value;
				
				if(value != null) {
					String output = value.toString();
					if(output.length() > 425)
						output = output.substring(0, 425);
					out.println("PRIVMSG " + lineParser.actionArg + " :" + output);	
					System.out.println("PRIVMSG " + lineParser.actionArg + " :" + output);
				}
			} catch(Exception ex) {
				ex.printStackTrace(System.out);
				Memory.reset(false);
			}
		}
	}
	
	public static void commandLine() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			String line = r.readLine();
			
			if(line.equals(""))
				break;
			
			Parser parser = new Parser();
			ArrayList<String> tokens = parser.tokenize(line);
			TokenNode tree = parser.createTree(tokens);
			System.out.println(tree.toString());
			
			Object value = tree.evaluate();
			if(value instanceof Variable)
				value = ((Variable)value).getValue();
			System.out.println("=> " + value);
			Memory.print();
			/*
			for(String token : tokens) {
				System.out.print("\"" + token + "\", ");
			}
			*/
		}
	}

}
