package serial5;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;


public class Serial_Base {
	//=============================================================================
	 //クラスのフィールド
	 SerialPort port;
	 CommPortIdentifier comID;

	 InputStream in;
	 OutputStream out;

	 String comName;
	 int baudRate;

	 //=============================================================================
	 //ポートオープンの関数
	 boolean Serial_open(String COM_NAME,int BAUD_RATE)
	 {

	  comName = COM_NAME;
	  baudRate = BAUD_RATE;

	  //----------------------------------------------------------------------------
	  //ポートIDを取得
	  try
	  {
	   //フィールドのcomNameの名前で新規にCOMポートを取得
	   comID = CommPortIdentifier.getPortIdentifier(comName);
	  }
	  catch(Exception e1)
	  {
	   JOptionPane.showMessageDialog(null,"ポートを取得できませんでした。","Serial_Base.open()",JOptionPane.INFORMATION_MESSAGE);
	   return false;
	  }

	  //----------------------------------------------------------------------------
	  //もしポートが既に開いていないなら、ポートを開いて各種設定を行います。
	  if(comID.isCurrentlyOwned()==false)
	  {
	   try
	   {
	    //シリアルポートのインスタンスを生成
	    port = (SerialPort)comID.open("serial_canvas",2000);

	    //ボーレート、データビット数、ストップビット数、パリティを設定
	    port.setSerialPortParams(baudRate,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );

	    //フロー制御はしない
	    port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

	    //入出力ストリームの設定
	    in = port.getInputStream();
	    out = port.getOutputStream();
	   }
	   catch(Exception e2)
	   {
	    JOptionPane.showMessageDialog(null,"うまく開けませんでした。","Serial_Base.open()",JOptionPane.INFORMATION_MESSAGE);
	    return false;
	   }
	  }
	  else
	  {
	   JOptionPane.showMessageDialog(null,"すでに開いています。","Serial_Base.open()",JOptionPane.INFORMATION_MESSAGE);
	   return false;
	  }

	  return true;

	 }

	 //=============================================================================
	 //ポートクローズの関数
	 boolean Serial_close()
	 {
	  if(comID.isCurrentlyOwned())
	  {
	   try
	   {
	    //全部クローズ。
	    port.close();
	    in.close();
	    out.close();
	   }
	   catch(IOException e3)
	   {
	    JOptionPane.showMessageDialog(null,"うまく閉じれませんでした。","Serial_Base.open()",JOptionPane.INFORMATION_MESSAGE);
	    return false;
	   }

	  }
	  else
	  {
	   JOptionPane.showMessageDialog(null,"すでに閉じています。","Serial_Base.open()",JOptionPane.INFORMATION_MESSAGE);
	   return false;
	  }

	  return true;
	 }

	 //=============================================================================
	 //文字列送信関数
	 void Serial_puts(String str)
	 {
	  try
	  {
	   //String型の引数をbyte配列にする。
	   byte[] data = str.getBytes();

	   //アウトプットストリームを介して送信します。
	   out.write(data);
	   out.flush();
	  }
	  catch(Exception e4)
	  {
	  }
	 }


}
