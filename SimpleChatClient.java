import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class SimpleChatClient {

	JTextField outgoing;
	JTextArea incoming;
	PrintWriter writer;
	Socket sock;
	static String ip = "127.0.0.1";
	static int port = 5000;
	
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
		
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(iScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		
		
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
			writer = new PrintWriter(sock.getOutputStream());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			// get the text from the text field and
			// send it to the server using the writer (a PrintWriter)
			String message = outgoing.getText();
			try {
				writer.println(message);
				writer.flush();
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
			
				InputStreamReader inStream = new InputStreamReader(sock.getInputStream());
				BufferedReader reader = new BufferedReader(inStream);
				String line;
				while((line = reader.readLine()) != null){
					System.out.println("received " + line);
					incoming.append(line + "\n");
				}
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		if(args.length >= 0){
			ip = args[0];
			port = Integer.parseInt(args[1]);
			System.out.println(port);
		}
		SimpleChatClient client = new SimpleChatClient();
		client.go();

	}

}
