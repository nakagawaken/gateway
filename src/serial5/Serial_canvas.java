package serial5;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Serial_canvas {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ



		  //============================================================================
		  //Windows仕様のルック・アンド・フィールに変更。
		  try
		  {
		   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		  }
		  catch(Exception e)
		  {
		  }

		  MainFrame frame = new MainFrame();
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  frame.setSize(500,500);
		  frame.setVisible(true);
	}

}
