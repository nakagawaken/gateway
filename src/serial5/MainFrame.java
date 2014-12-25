package serial5;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MainFrame extends JFrame {

	Serial_Class serial;



	 MainFrame()
	 {

	  super("serial-canvas");
	  serial = new Serial_Class();


	  //============================================================================
	  //コンテントペインを作成。
	  JPanel cp = new JPanel();
	  cp.setLayout(null);
	  setContentPane(cp);

	  //============================================================================
	  //使用COMポートのためのコンボボックス
	  JLabel com_label = new JLabel("COMポート ：");
	  cp.add(com_label);
	  com_label.setBounds(50,20,100,20);
	  cp.add(serial.com_combo);
	  serial.com_combo.setBounds(120,20,80,20);

	  //============================================================================
	  //ボーレートのためのコンボボックス
	  JLabel baud_label = new JLabel("ボー・レート ：");
	  cp.add(baud_label);
	  baud_label.setBounds(250,20,100,20);
	  cp.add(serial.baud_combo);
	  serial.baud_combo.setBounds(350,20,80,20);

	  //============================================================================
	  //テキストフィールドを作成。
	  Font font = new Font("SansSerif",Font.PLAIN,16);
	  serial.tx.setFont(font);
	  serial.tx.setLineWrap(true);
	  serial.tx.addKeyListener(new KeyInput());
	  serial.scrollPane.setBounds(50,50,400,300);
	  cp.add(serial.scrollPane);

	  //============================================================================
	  //ボタンを作成。
	  JButton bt1 = new JButton(new open_action());
	  cp.add(bt1);
	  bt1.setBounds(50,400,150,40);

	  JButton bt2 = new JButton(new close_action());
	  cp.add(bt2);
	  bt2.setBounds(300,400,150,40);

	  // クラウド操作のテスト用
	  JButton bt_upd = new JButton(new update_action());
	  cp.add(bt_upd);
	  bt_upd.setBounds(50,500,150,40);

	  // 学習器との通信用ボタン
	  JButton bt_forest = new JButton(new forest_action());
	  cp.add(bt_forest);
	  bt_forest.setBounds(300,500,150,40);

	  //============================================================================
	  //ログレベルのためのコンボボックス
	  JLabel log_label = new JLabel("ログレベル：");
	  cp.add(log_label);
	  log_label.setBounds(50,600,100,20);
	  cp.add(serial.log_combo);
	  serial.log_combo.setBounds(350,600,80,20);


	  //ここまでがコンストラクタです。
	 }


	 //******************************************************************************
	 class open_action extends AbstractAction
	 {
	  //COMポートを開く際の処理
	  open_action()
	  {
	   putValue(Action.NAME,"接続");
	  }

	  public void actionPerformed(ActionEvent e)
	  {
	   serial.open();
	  }
	 }

	 //******************************************************************************
	 class close_action extends AbstractAction
	 {
	  //COMポートを閉じる際の処理
	  close_action()
	  {
	   putValue(Action.NAME,"切断");
	  }

	  public void actionPerformed(ActionEvent e)
	  {
	    serial.close();
	  }

	 }

	 //****学習器との通信サーバーを起動　　******************************************
	 class forest_action extends AbstractAction
	 {
		 forest_action()
		 {
			 putValue(Action.NAME,"学習器との通信");
		 }

	  public void actionPerformed(ActionEvent e)
	  {
	    serial.forestServer();

	  }

	 }

	 int i = 1;
	 String ret = "";

	 // http リクエスト
	 //******************************************************************************
	 class update_action extends AbstractAction
	 {
		  update_action()
		  {
		   putValue(Action.NAME,"更新");
		  }

		 public void actionPerformed(ActionEvent ev)
		 {
			 System.out.println("update");

			 URL url = null;
	         HttpURLConnection connection = null;

			 try {

					// 参考　http://www.freeshow.net.cn/ja/questions/4fa9d3164d45f65ab16f8cd17c02e997105e5fb0988a7a4c5d4230976ba0c5ef/

				 System.out.println("u2");
				 url = new URL("http://ricohintern2014.appspot.com/posupdate");

				 // Proxy認証設定
				 ProxyAuthenticator pa = new ProxyAuthenticator("http://proxy.ricoh.co.jp:8080/", "z00s108018", "ken@1126");

				 Authenticator.setDefault(pa);


				 System.out.println("u3");


				 try {
						// 参考　http://www.freeshow.net.cn/ja/questions/4fa9d3164d45f65ab16f8cd17c02e997105e5fb0988a7a4c5d4230976ba0c5ef/
	               // connection = (HttpURLConnection) url.openConnection();
					 Proxy proxyServer = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.6.248.80", 8080));
					 connection = (HttpURLConnection) url.openConnection(proxyServer);
	                connection.setDoOutput(true);
	                connection.setRequestMethod("POST");

					 System.out.println("u4");

					 PrintStream ps = new PrintStream(connection.getOutputStream());

					 System.out.println("u5");
					 i++; // 位置変更

					 String parameterString = new String(
	                		"ItemID=0000&APIDs=1111,2222,3333&RSSIs=99,88,77&x="+i+
	                		".0&y=3.0&oAcc=2&lat=36.1234&lng=136.4567&floor=2.0&iAcc=15.0&battery="+i+".5");


					 System.out.println("u6 " + parameterString);


					 ps.print(parameterString);
					 ps.close();

					 System.out.println("u7");

	                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                    try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(),
	                                                                       StandardCharsets.UTF_8);
	                         BufferedReader reader = new BufferedReader(isr)) {
	                        String line;
	                        while ((line = reader.readLine()) != null) {
	                            System.out.println(line);
	                        }
	                    }
	                }
				 } finally {
	                if (connection != null) {
	                    connection.disconnect();
	                }
				 }

	        } catch (IOException ei) {
	            ei.printStackTrace();

			}


			    // 受信結果をUIに表示
			//    tv.setText( ret );
		 } // actionPerformed


	 }



	 //******************************************************************************
	 class KeyInput extends KeyAdapter
	 {
	  public void keyPressed(KeyEvent e)
	  {
	   String txt_temp;
	   String[] txt_temp_array;
	   int lineNum;

	   if(e.getKeyCode() == KeyEvent.VK_ENTER)
	   {

	    serial.tx.append("\n");
	    txt_temp = serial.tx.getText();
	    txt_temp_array = txt_temp.split("\n",0);
	    lineNum = txt_temp_array.length - 1;

	    serial.Serial_puts(txt_temp_array[lineNum]+"\r");

	   }
	  }
	 }

}
