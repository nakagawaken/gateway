package serial5;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Serial_Class extends Serial_Base {

	 //=============================================================================
	 //クラスのフィールド
	 JComboBox com_combo;
	 JComboBox baud_combo;
	 JTextArea tx;
	 JScrollPane scrollPane;

     // Gatewayがサーバーになる
     ServerSocket echoServer = null;
     Socket clientSocket = null;

     Socket echoSocket = null;
     DataOutputStream os = null;
     BufferedReader is = null;

	 //=============================================================================
	 //コンストラクタ
	 Serial_Class()
	 {
	  //テキストエリアを作成
	  tx = new JTextArea();

	  //スクロールペインを作成して、その上にテキストエリアを貼り付けておきます。
	  scrollPane = new JScrollPane(tx);
	  scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	  scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


	  //COMポート用のコンボボックスを作成して、アイテムを入れておきます。
	  com_combo = new JComboBox();
	  com_combo.addItem("COM1");
	  com_combo.addItem("COM2");
	  com_combo.addItem("COM3");
	  com_combo.addItem("COM4");
	  com_combo.addItem("COM5");
	  com_combo.addItem("COM6");
	  com_combo.addItem("COM7");
	  com_combo.addItem("COM8");
	  com_combo.addItem("COM9");
	  com_combo.addItem("COM10");
	  com_combo.addItem("COM26");
	  com_combo.addItem("COM27");
	  com_combo.addItem("COM28");

	  //デフォルト設定
	  com_combo.setSelectedItem("COM27");


	  //ボー・レート用のコンボボックスを作成して、アイテムを入れておきます。
	  baud_combo = new JComboBox();
	  baud_combo.addItem("4800");
	  baud_combo.addItem("9600");
	  baud_combo.addItem("19200");
	  baud_combo.addItem("38400");
	  baud_combo.addItem("57600");
	  baud_combo.addItem("115200");
	  baud_combo.addItem("230400");

	  //デフォルト設定
//	  baud_combo.setSelectedItem("115200");
	  baud_combo.setSelectedItem("38400");


	  // *** RSSIXYにあった関数を起動時に変更

      System.out.println("Gateway combo ServerSocket");
      // ポートを開く
      try {
          echoServer = new ServerSocket(10000);
      }
      catch (IOException e) {
          System.out.println(e);
      }

      // クライアントからの要求を受けるソケットを開く
      try {
          clientSocket = echoServer.accept();
          is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       //   os = new PrintStream(clientSocket.getOutputStream());
             os = new DataOutputStream(clientSocket.getOutputStream());
      }   catch (IOException e) {
                 System.out.println(e);
      }
      System.out.println("Gateway combo Stream");

	 }


	 //=============================================================================
	 //ポートオープンの関数
	 void open()
	 {
	  //正常にオープンできたかを調べるのに使うflagです。
	  boolean flag;

	  //コンボボックスから、選択されているCOMポート名を取得します。文字列にしておきます。
	  String COM_Name = (com_combo.getSelectedItem()).toString();

	  //コンボボックスから、選択されているボー・レートを取得します。int型に直しておきます。
	  int Baud_Rate = Integer.parseInt((baud_combo.getSelectedItem()).toString());

	  //開く。
	  flag = Serial_open(COM_Name,Baud_Rate);

		//正常にオープンできたなら…
		if(flag)
		{
			try
			{
				//受信イベントを登録します。
				port.addEventListener(new SerialPortListener());

		    	//このメソッドを呼んでおかないと、イベントが監視されません…。
		    	port.notifyOnDataAvailable(true);
			}
			catch(Exception e){}

			tx.append("接続しました。\n\n");
		}
	}


	 //=============================================================================
	 //ポートクローズの関数
	 void close()
	 {
		 //正常にクローズできたかを調べるのに使うflagです。
		 boolean flag;

		 //閉じる。
		 flag = Serial_close();

		 //正常にクローズできたなら…
		 if(flag)
		 {
			 tx.append("切断しました。\n\n");
		 }

         // 開いたソケットなどをクローズ
		 try {
	         os.close();
	         is.close();
	         echoSocket.close();
		 }catch (Exception e){

		 }

	 }


	 //*****************************************************************************
	 //内部クラスとして、イベントリスナを記述します。
	class SerialPortListener implements SerialPortEventListener
	{
		int output_mode = 0; // 0: text 1:binary
		int first_byte = 0; // 先頭文字の判定 0: 先頭 1:先頭以外

		//============================================================================
		//受信イベント発生時に呼び出されるメソッド
		public void serialEvent(SerialPortEvent Serial_event)
		{
			//受信データ1文字分を置いておくための変数です。
			int received_data = 0;
			//バッファーです。ここに受信データをためていきます。
			StringBuffer buffer = new StringBuffer();  // テキストデータ受信
			StringBuffer bufferBinary = new StringBuffer(); // バイナリデータ受信
			ByteArrayOutputStream ba = new ByteArrayOutputStream();

			System.out.println("1");

		   //受信完了の合図があれば・・・
		   if(Serial_event.getEventType() == SerialPortEvent.DATA_AVAILABLE)
		   {
			    System.out.println("2");
		    //--------------------------------------------------------------------------
			    //breakが来るまで回し続ける部分。

			    int ozvcnt = 0;
			    int ozvstart =0;

			    while(true)
			    {
				     try
				     {
				    	 received_data = in.read();// 入力ストリームから読み込み

						 System.out.println("ozvcnt "+ozvcnt+ "data=" + String.format("%02X", received_data)  );

					     //文字無しの場合はすぐに抜けます。
					     if(received_data == -1)
					     {
					    	  break;
					     }

					    //文字列の終わりではない場合は、バッファーに付け加えていきます。
//					    if ((char)received_data != '\r')
					    if (ozvcnt < 46) //　ここで終了
					    {
					    	// 先頭文字
					    	if (first_byte == 0 && (char)received_data == 0xFE ) {

								System.out.println("4 binary OZV");
					    		output_mode = 1; //binary

					    		first_byte = 1;
					    		ozvstart = 1; // 開始した

					    	} else if (first_byte == 0 ){
								System.out.println("4 text");

					    		output_mode = 0; // text
					    		first_byte = 1;
					    	} else {
					    		// ここは first_byte = 1の場合に来るが何もしなくて良い
					    	}

					    	if (output_mode == 1) {
					    		//bufferBinary.append(received_data);
					    		ba.write(received_data);
					    		ozvcnt++; // 受信毎に追加
					    	} else { // textモード
								buffer.append((char)received_data);

					    	}
					    }
					    //文字列の終わりなら、最後に改行コードを足して、テキストエリアへ表示。
					    else
					    {
							System.out.println("Gateway Data finish cnt="+ozvcnt);
							ozvcnt = 0;

					    	if (output_mode == 0) { // text

								System.out.println("Gateway Data finish text");

					    		//テキストエリア改行。
								buffer.append('\n');

								//バッファの内容を文字列にして、テキストエリアへ追加。
								tx.append("text mode:" + buffer.toString() );

							    // クラウド更新処理
							    updateStore(buffer.toString(), null);

							    //バッファを一応、空にしておきます。
							    buffer.delete(0,buffer.length()-1);


					    	} else { // バイナリー列
					    	//	byte[] bufferbyte = bufferBinary.toString().getBytes();
					    		byte[] bufferbyte = ba.toByteArray();

					    		StringBuffer buffertx = new StringBuffer();

								System.out.println("Gateway Data finish binary");


					    		for (int i = 0 ; i < bufferbyte.length; i++){
					    			buffertx.append( String.format("%02X", bufferbyte[i])  );
					    		}


					    		// 学習器の処理が入る●
					    		RSSIXY rssiXY = forest(bufferbyte);



							    // クラウド更新処理
							    updateStore(buffertx.toString(), rssiXY);

					    		buffertx.append('\n'); // 改行追加

					    		// バイナリ→テキスト変換
								//バッファの内容を文字列にして、テキストエリアへ追加。
								tx.append("binary mode:" + buffertx.toString() );

//					    		String binarystr = encode(bufferbyte);
//					    		tx.append("binary mode:" + binarystr );
					    	}

						    first_byte = 0;// 先頭文字に戻す
					    	//これをやらないと、受信イベントごとに自動でテキストエリアがスクロールしてくれません。
						    tx.setCaretPosition(tx.getText().length());

							System.out.println("5");

							break;
					     }


				    }  catch(IOException ex){
				    }
			    }  //while()文ここまで。
			} // ifここまで

		    System.out.println("6 end serialEvent");
	  } // serialEventメソッド

		// 学習器とのIF処理
		// 引数は OZVから受信したバイナリー列
		// 返却は、X, YのString
		public RSSIXY forest(byte[] bRssi){
    		RSSIXY rssiXY = new RSSIXY(null, null);

         	System.out.println("forest() bRssi.length="+ bRssi.length);
/*
            // ポートを開く　同じPC　かつ　ポート番号埋め込み●●
            // "localhost"ではつながらない
            try {
                //echoSocket = new Socket("10.247.97.178", 5550 );
                echoSocket = new Socket("172.22.2.40", 10000 );

                System.out.println("Gateway socket");

                os = new DataOutputStream(echoSocket.getOutputStream());

                System.out.println("Gateway DataOutput");

                is = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                System.out.println("Gateway InputStream");

            } catch (UnknownHostException e) {
                System.err.println("Don't know about host:");
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to: localhost");
            }
*/


            // サーバーにメッセージを送る
   //         if (echoSocket != null && os != null && is != null) {
                try {

    //	            for(int j=0; j<7 ;j++) {
    	                // メッセージを送ります
    	            	// バイナリメッセージを作成する
    	            	byte forestData[] = new byte[52];
    	            	// 学習器用のヘッダ
    	            	forestData[0] = 0x02; // STX
    	            	forestData[1] = 0x10; // コマンド
    	            	forestData[2] = 0x2F; // 47バイト長
    	            	forestData[3] = 0x00;
    	            	for (int i = 0; i < bRssi.length; i++){
    	            		forestData[i+4] = bRssi[i];
    	            	}
    	            	forestData[51] = 0x03; // ETX
    	            	//forestData[52] = '\r'; // 終了

    	                System.out.println("Gateway forestData");

    	            	os.write(forestData);

    	                System.out.println("Gateway write");

    	                //os.writeBytes("HELLO\n");

    	                // サーバーからのメッセージを受け取り画面に表示します
    /*
    	            	String responseLine;
    	                if ((responseLine = is.readLine()) != null) {
    	                	System.out.println("Server: " + responseLine);
    	                }
    */



    	            	// 学習器から受け取る
    	                int starttx = 0; // 0開始前 1:開始後
    	                int endtx = 0; // 0終了前 1:終了後
    	                int txcount = 0; // 何バイト読んだか
    	                // サーバーからのメッセージを待つ
    	        		ByteArrayOutputStream ba = new ByteArrayOutputStream();






    	        		int iRecv = 0;
    	            	while (true){
    	                	iRecv = is.read();
    	                	if ((char)iRecv == 0x02 && starttx == 0){
    	                		starttx = 1;
    	                		txcount++;
    	                		ba.write(iRecv);

    	                		System.out.println("Gateway STX");
    	                	} else if ((char)iRecv == 0x03 && txcount == 5){
    	                		ba.write(iRecv);
    	                		// 異常系の終了
    	                		System.out.println("Gateway Error ETX "+txcount);

    	                		byte[] bufferbyte = ba.toByteArray();

    	    		    		StringBuffer buffertx = new StringBuffer();
    	    		    		for (int i = 0 ; i < bufferbyte.length; i++){
    	    		    			buffertx.append( String.format("%02X", bufferbyte[i])  );
    	    		    		}
    	    		    		buffertx.append('\n'); // 改行追加
    	    		    		System.out.println(buffertx.toString());

    	    		    		// 初期化処理
    	    		    		ba.flush(); // streamにデータが残らないように
    	    		    		ba = new ByteArrayOutputStream();
    	                		starttx = 0; // フラグを戻す
    	                		txcount = 0; // カウンタを戻す

    	                		break;
    	                	} else if ((char)iRecv == 0x03 && txcount == 10) {
    	                		ba.write(iRecv);
    	                		// 位置情報の終了
    	                		System.out.println("Gateway XY ETX "+txcount);

    	                		byte[] bufferbyte = ba.toByteArray();

    	    		    		StringBuffer buffertx = new StringBuffer();
    	    		    		for (int i = 0 ; i < bufferbyte.length; i++){
    	    		    			buffertx.append( String.format("%02X", bufferbyte[i])  );
    	    		    		}
    	    		    		String strRSSI = buffertx.toString();

    	    		    		// 位置情報かどうかを判定する
    	    		    		if ("6".equals(strRSSI.substring(5,6))){
    	    		    			// 位置情報
    		    		    		System.out.println("Gateway1:"+strRSSI);

    		    		    		StringBuffer sbX = new StringBuffer();
    		    		    		StringBuffer sbY = new StringBuffer();


    		    		    		sbX.append(strRSSI.substring(10, 12)).append(strRSSI.substring(8,10));

    		    		    		sbY.append(strRSSI.substring(14, 16)).append(strRSSI.substring(12,14));
    		    		    		int iX = Integer.parseInt(sbX.toString(), 16);
    		    		    		int iY = Integer.parseInt(sbY.toString(), 16);
    		    		    		double dX = (double)iX / 10;
    		    		    		double dY = (double)iY / 10;

    		    		    		String strX = String.valueOf(dX);
    		    		    		String strY = String.valueOf(dY);

    		    		    		System.out.println("Gateway1: X="+strX +" Y="+strY);

    		    		    		rssiXY.setX(strX);
    		    		    		rssiXY.setY(strY);

    	    		    		} else if ("1".equals(strRSSI.substring(5,6))){
    	    		    			// エラー系
    		    		    		System.out.println("Gateway2:"+strRSSI);

    	    		    		} else {
    	    		    			// データに異常あり
    		    		    		System.out.println("Gateway3:"+strRSSI);

    	    		    		}




    	    		    		// 初期化処理
    	    		    		ba.flush(); // streamにデータが残らないように
    	    		    		ba = new ByteArrayOutputStream();
    	                		starttx = 0; // フラグを戻す
    	                		txcount = 0; // カウンタを戻す


    	                		break;

    	                	} else if (txcount > 20){
    	                		// 無限ループ対策
    	        	    		System.out.println("txcount="+txcount);


    	        	    		break;  // 終了
    	                	} else {// 開始終了以外
    	                		txcount++;
    	                		ba.write(iRecv);

    	                        System.out.println("Gateway count="+ txcount+
    	                        		" Data=" +String.format("%02X", iRecv) );

    	                	}
    	            	}


    	//                Thread.sleep(3000); // ３秒待つ
    	//            } forループ


                } catch (UnknownHostException e) {
                    System.err.println("Trying to connect to unknown host: " + e);
                } catch (IOException e) {
                    System.err.println("IOException: " + e);
  //              } catch (InterruptedException e) {
  //  				// TODO 自動生成された catch ブロック
  //  				e.printStackTrace();
    			}
   //         }



    		return rssiXY;
		}




		// 2014/12/10 未使用(意図通りに変換されない）
	    public String encode( byte[] be) {
	        String encodedStr = null;
	        ByteArrayOutputStream os = null;

			System.out.println("encode() len="+ be.length);
	        try {
	            os = new ByteArrayOutputStream();

	            for( int i=0 ; i < be.length; i++ ) {
	            	byte b = be[i];

	                    // 上の 4 ビット
	                    os.write( ( b >> 4 ) & 0x0F  );
	                    // 下の 4 ビット
	                    os.write( b & 0x0F  );
	            }
	            encodedStr = os.toString();
	        } finally {
	            try {
	                // close する意味はないが, 一応
	                if( os != null ) os.close();
	            } catch( IOException err ) {
	                err.printStackTrace();
	            }
	        }
	        return encodedStr;
	    }


		int i=2;
		String[] APs = {"AAAA", "1111", "1234", "ABCD"};

		 public void updateStore(String strOZV, RSSIXY rXY)
		 {
			 System.out.println("update");

			 URL url = null;
	         HttpURLConnection connection = null;

			 try {

					// 参考　http://www.freeshow.net.cn/ja/questions/4fa9d3164d45f65ab16f8cd17c02e997105e5fb0988a7a4c5d4230976ba0c5ef/

				 System.out.println("u2");
				 url = new URL("http://ricohintern2014.appspot.com/posupdate");

				 // Proxy認証設定
				 /*
				 ProxyAuthenticator pa = new ProxyAuthenticator("http://proxy.ricoh.co.jp:8080/", "z00s108018", "ken@1126");

				 Authenticator.setDefault(pa);
		*/

				 System.out.println("u3");


				 try {
						// 参考　http://www.freeshow.net.cn/ja/questions/4fa9d3164d45f65ab16f8cd17c02e997105e5fb0988a7a4c5d4230976ba0c5ef/
						// connection = (HttpURLConnection) url.openConnection();
					//Proxy proxyServer = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.6.248.80", 8080));
					 Proxy proxyServer = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.22.1.30", 8080));

					connection = (HttpURLConnection) url.openConnection(proxyServer);
	                connection.setDoOutput(true);
	                connection.setRequestMethod("POST");

					 System.out.println("u4");

					 PrintStream ps = new PrintStream(connection.getOutputStream());

					 System.out.println("u5");
					 i++; // 位置変更


					 String parameterString = ozv2cloudData(strOZV, rXY);
//					 String parameterString = new String(
//	                		"ItemID=0000&APIDs="+ ( APs[i % 4] ) +"&RSSIs=99,88,77&x="+i+
//	                		".0&y=3.0&oAcc=2&lat=36.1234&lng=136.4567&floor=2.0&iAcc=15.0&battery="+i+".5");


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


// 受信データ例
//  FE2A6A62112233262801440000010019040594C90414D9AE071111CC2222CA3333C94444C8111280111280111280CC
		 // OZVから受信したデータをクラウド用に整形する
		 String ozv2cloudData(String strOZV, RSSIXY rXY){

			 System.out.println("o1");


			 try {


			 // ItemIDの整形
			 StringBuffer sbItemID = new StringBuffer();
			 sbItemID.append(strOZV.substring(10,12)).append(strOZV.substring(8,10));
			 String strItemID = sbItemID.toString();

			 System.out.println("strItemID="+strItemID);

			 // APIDの整形
			 String strAPID = null;
			 StringBuffer sbAPID = new StringBuffer();
			 // APIDは７つある
			 sbAPID.append(strOZV.substring(52, 54)).append(strOZV.substring(50, 52));
			 sbAPID.append(",");
			 sbAPID.append(strOZV.substring(58, 60)).append(strOZV.substring(56, 58));
			 sbAPID.append(",");
			 sbAPID.append(strOZV.substring(64, 66)).append(strOZV.substring(62, 64));
			 sbAPID.append(",");
			 sbAPID.append(strOZV.substring(70, 72)).append(strOZV.substring(68, 70));
			 sbAPID.append(",");
			 sbAPID.append(strOZV.substring(76, 78)).append(strOZV.substring(74, 76));
			 sbAPID.append(",");
			 sbAPID.append(strOZV.substring(82, 84)).append(strOZV.substring(80, 82));
			 sbAPID.append(",");
			 sbAPID.append(strOZV.substring(88, 90)).append(strOZV.substring(86, 88));
			 strAPID = sbAPID.toString();

			 System.out.println("strAPID="+strAPID);

			 // フロア情報
			 String strFloor = null;
			 int fractionFloor = 0;
			 if ("1".equals(String.valueOf(strOZV.charAt(27) ) ) ) {
				 fractionFloor = 1;
			 }
			 int floor1 = Integer.parseInt(strOZV.substring(28, 32), 16);

			 // 小数１桁なのか、整数なのか
			 if (fractionFloor == 1) {
				 double dFloor = (double)floor1 / 10;
				 strFloor = String.valueOf(dFloor);
			 } else {
				 strFloor = String.valueOf(floor1);
			 }

			 System.out.println("strFloor="+strFloor);

			 // 緯度情報
			 String strLat = null;
			 int fracLat = 0;
			 fracLat = Integer.parseInt( String.valueOf(strOZV.charAt(33) )   );

			 double dLat = 0.0;
			 int iLat = Integer.parseInt(strOZV.substring(34, 40), 16  );
			 dLat = iLat / Math.pow(10, fracLat);
			 strLat = String.valueOf(dLat);

			 System.out.println("strLat="+strLat);

			 // 経度情報
			 String strLng = null;
			 int fracLng = 0;
			 fracLng = Integer.parseInt( String.valueOf(strOZV.charAt(41) ) );
			 // ↑0の場合の処理が必要●

			 double dLng = 0.0;
			 int iLng = Integer.parseInt(strOZV.substring(42, 48), 16  );
			 dLng = (double) iLng / Math.pow(10, fracLng);
			 strLng = String.valueOf(dLng);

			 //System.out.println("fracLng"+fracLng+" iLng="+iLng);
			 System.out.println("strLng=" + strLng);

			 if (rXY == null || rXY.getX() == null ){
				 // 位置情報の仮設定
				 System.out.println("Gateway x=1 y=1");
				 rXY = new RSSIXY("1", "1");
			 }

			 // まとめ
			 StringBuffer sbPost = new StringBuffer();

			 sbPost.append("ItemID=").append(strItemID);
			 sbPost.append("&APIDs=").append(strAPID);
			 sbPost.append("&RSSIs=99,-88,-10&x=");
			 sbPost.append(rXY.getX());  // 学習器から取得
			 sbPost.append("&y=");
			 sbPost.append(rXY.getY()); // 学習器から取得
			 sbPost.append("&oAcc=2&lat=");
			 sbPost.append(strLat);
			 sbPost.append("&lng=");
			 sbPost.append(strLng);
			 sbPost.append("&floor=");
			 sbPost.append(strFloor);
			 sbPost.append("&iAcc=15.0&battery=");
			 sbPost.append(fracLat); // 仮の値
			 sbPost.append(".5"); // 仮の値

			 String strPost = sbPost.toString();
			 return strPost;

			 } catch (Exception e) {

				 return "format error";
			 }


		 }
		//返却値例				 String parameterString = new String(
//  		"ItemID=0000&APIDs="+ ( APs[i % 4] ) +"&RSSIs=99,88,77&x="+i+
//  		".0&y=3.0&oAcc=2&lat=36.1234&lng=136.4567&floor=2.0&iAcc=15.0&battery="+i+".5");



	 }	 //内部クラスここまで。
	 //****************************************

}
