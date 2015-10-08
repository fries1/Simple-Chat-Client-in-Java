import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleChatClient {

	JTextField outgoing;
	JTextArea incoming;
	JTextArea whosOnline;
	PrintWriter writer;
	ObjectOutputStream oos;
	Socket sock;
	static String ip = "127.0.0.1";
	static int port = 5000;
	static String userName = "unknown";
	
	JFrame frame;
	
	public void go() {
		// make gui and register a listener with the send button
		// call the setUpNetworking() method
		
		frame = new JFrame("Simple Chat Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		
		outgoing = new JTextField(20);
		
		incoming = new JTextArea(15, 20);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
				
		JScrollPane iScroller = new JScrollPane(incoming);
		iScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		iScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		whosOnline = new JTextArea(5, 15);
		whosOnline.setEditable(false);
		
		JScrollPane whoScroller = new JScrollPane(whosOnline);
		iScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		iScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(iScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		mainPanel.add(whosOnline);
		
		
		
		
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		
		frame.setSize(800,500);
		frame.setVisible(true);
		
		setUpNetworking();
		
		Thread receiving = new Thread(new IncomingReader());
		receiving.start();
		
		
	}
	
	public void setUpNetworking() {
		// make a Socket, then make a PrintWriter
		// assign the PrintWriter to writer instance variable
		try {
			sock = new Socket(ip, port);
			//writer = new PrintWriter(sock.getOutputStream());
			UserData user = new UserData(userName);
			oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(user);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		//sendUserInfo();
	}
	
	/*
	public void sendUserInfo() {
		UserData user = new UserData(userName);
		
	}
	*/
	
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			// get the text from the text field and
			// send it to the server using the writer (a PrintWriter)
			String message = outgoing.getText();
			Message newMessage = new Message(userName, message);
			try {
				oos.writeObject(newMessage);
				//writer.println(message);
				//writer.flush();
				System.out.println("client sending " + message);
			}catch(Exception ex){
				ex.printStackTrace();
			}

			outgoing.setText("");
			outgoing.requestFocusInWindow();
		}
	}
	
	public class IncomingReader implements Runnable {
		public void run() {
			try {
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				Object obj;
				while((obj = (Object) ois.readObject()) != null){
					System.out.print("received an object");
					if(obj instanceof Message){
						System.out.println(" of kind Message");
						Message m = (Message) obj;
						incoming.append(m.userName + " " + m.message + "\n");
					}
				}
				/*
				InputStreamReader inStream = new InputStreamReader(sock.getInputStream());
				BufferedReader reader = new BufferedReader(inStream);
				String line;
				while((line = reader.readLine()) != null){
					System.out.println("received " + line);
					incoming.append(line + "\n");
				}
				*/
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		if(args.length == 2){
			//System.out.println("found arguments");
			ip = args[0];
			port = Integer.parseInt(args[1]);
		}else if(args.length == 3){
			userName = args[2];
		}else{

			System.out.println("Server IP needed: ");
			ip = sc.next();
			System.out.println("Port needed: ");
			port = sc.nextInt();
		}
		if(userName.equals("unknown")){
			System.out.println("Insert Username: ");
			userName = sc.next();
		}
		sc.close();
		SimpleChatClient client = new SimpleChatClient();
		client.go();
	}

}

class UserData implements Serializable{
	String userName;
	public UserData(String userName){
		this.userName = userName;
	}
}

class Message implements Serializable{
	String userName;
	String message;
	public Message(String userName, String msg){
		this.userName = userName;
		message = msg;
	}
}
