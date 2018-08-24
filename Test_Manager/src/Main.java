import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main {

	private JFrame frame;
	private JTable table;
	private Scanner SC;
	private String str = null; 
	private Object Date [][]; //二維陣列
	private Vector<String []> str_vec = new Vector<String []>(); //暫存
	private String[] Colums = {"環境","服務","狀態"};
	private JLabel label;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Main() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @param <DefaulTableModel>
	 * @throws IOException 
	 */
	private <DefaulTableModel> void initialize() throws IOException {
		frame = new JFrame();
		frame.setResizable(true);
		frame.setBounds(100, 100, 672, 383);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane();
		
		JButton btnNewButton = new JButton("Checking Service");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Jtable reload
					table.setModel(new DefaultTableModel(null,Colums));
					ShowData();
					ServiceCheck();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnNewButton.setFont(new Font("微軟正黑體", Font.BOLD, 12));

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
						.addComponent(btnNewButton))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnNewButton)
					.addContainerGap(148, Short.MAX_VALUE))
		);
		
		
		scrollPane.setViewportView(table);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	public void ServiceCheck() throws IOException {
		
		String CKline;
		
		//確認服務
		String CMD = "cmd /c sc \\\\ 192.168.33.13 query | find \"Moon\"";
		// 執行CMD指令
		Process PS = Runtime.getRuntime().exec(CMD);
		// Getting the results
		PS.getOutputStream().close();
		//將Service放入line
		BufferedReader stdout = new BufferedReader(new InputStreamReader(PS.getInputStream()));
		CKline = stdout.readLine();
		System.out.println("Standard Output:"+CKline);
		stdout.close();
		
		
		if(CKline == null) {
			
			String Service = "Moon";
			String NOut = "RC1 "+Service+" Stop \r\n";
			System.out.println(NOut);
			//開啟寫入txt
			FileWriter NFW = new FileWriter("D:\\Service_Config.txt");
			NFW.write(NOut);
			NFW.close();
		}
		else {
			String[] Oline = CKline.split(":");
			String YOut = "RC1"+Oline[1]+" Running \r\n";
			FileWriter YFW = new FileWriter("D:\\Service_Config.txt");
			System.out.println(YOut);
			YFW.write(YOut);
			YFW.close();
			
		}
		
		
		/*
		//製作寫入.txt字串
		String[] Oline = CKline.split(":");
		//String Oline = "127.0.0.1 "+CKline+" Running ";
		String Oline1 = "127.0.0.1"+Oline[1]+" Running \r\n";
		
		//寫入txt
		FileWriter FW = new FileWriter("C:\\Users\\ejlin\\Desktop\\Service_Config.txt");
		FW.write(Oline1);
		FW.close();*/
	}
	
	
	//將資料放進JTable
	public void ShowData() throws IOException {
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		//Import Service_Config.txt
		FileReader FR = new FileReader("D:\\Service_Config.txt"); 
		str_vec.clear();//清除str_vec暫存
		Date = null;
		BufferedReader BR = new BufferedReader(FR);
		while((str = BR.readLine()) != null){
			//System.out.println(BR.readLine());
			
			str_vec.add(str.split("\\s")); //將此行以空白(white space)切成字串陣列, 存入 Vector暫存
			System.out.println(str_vec);
		}
		FR.close();
		
		Date = new String [str_vec.size()][]; //產生二維陣列, 依據暫存的size(txt檔的行數) 
		for (int i = 0; i < str_vec.size(); i++) {
			Date[i] = str_vec.elementAt(i); //將暫存區每一個元素(一行裡的字串陣列)放入二維陣列 
			model.addRow(Date[i]);
		}

		CorNalinha();
		//印出二維陣列內容 
		/*
		for (int i = 0; i < Date.length; i++) { 
			for (int j = 0; j < Date[i].length; j++) { 
				//System.out.print(Date[i][j] + " "); 
			}
		}
		model.setDataVector(Date, Colums);*/
		
	}
	
	//將JTable顯示顏色
	public void CorNalinha() {
		
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			
			String CK1 = "Running";
			String CK2 = "Stop";
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
				
				Object texto = table.getValueAt(row, 2);
				if( texto != null && CK1.equals(texto.toString())) {
					label.setBackground(Color.green);
					//table.setSelectionForeground(Color.BLACK);
				}
				else if(texto != null && CK2.equals(texto.toString())) {
					label.setBackground(Color.RED);
					//table.setSelectionForeground(Color.BLACK);
				}
				else {
					label.setBackground(Color.yellow);
					//table.setSelectionForeground(Color.BLACK);
				}
				
				return label;
			}
		});
	}
	
	
	
}
