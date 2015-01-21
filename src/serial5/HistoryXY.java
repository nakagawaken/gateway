package serial5;

public class HistoryXY {


	// タグIDが
	private String tagid = "0";

	// 最新の座標
	private String nowX = "0";
	private String nowY = "0";

	// 前回の座標
	private String lastX =  "0";
	private String lastY =  "0";

	// クラウドにあげている座標　
	// 初期として0を入れておくこと（比較処理あり）
	private String cloudX = "0";
	private String cloudY = "0";

	public String getTagid() {
		return tagid;
	}
	public void setTagid(String tagid) {
		this.tagid = tagid;
	}
	public String getNowX() {
		return nowX;
	}
	public void setNowX(String nowX) {
		this.nowX = nowX;
	}
	public String getNowY() {
		return nowY;
	}
	public void setNowY(String nowY) {
		this.nowY = nowY;
	}
	public String getLastX() {
		return lastX;
	}
	public void setLastX(String lastX) {
		this.lastX = lastX;
	}
	public String getLastY() {
		return lastY;
	}
	public void setLastY(String lastY) {
		this.lastY = lastY;
	}

	public String getCloudX() {
		return cloudX;
	}
	public void setCloudX(String cloudX) {
		this.cloudX = cloudX;
	}
	public String getCloudY() {
		return cloudY;
	}
	public void setCloudY(String cloudY) {
		this.cloudY = cloudY;
	}


	// コンストラクタ（新規生成）
	public HistoryXY(String tagid, String nowX, String nowY){
		setTagid(tagid);
		setNowX(nowX);
		setNowY(nowY);
	}

	// 更新処理　前回のを後ろへ
	public void update(String nextX, String nextY){
		setLastX(getNowX());
		setLastY(getNowY());

		setNowX(nextX);
		setNowY(nextY);
	}

	// クラウドデータを設定する
	public void cloud(String nextX, String nextY){
		setCloudX(nextX);
		setCloudY(nextY);
	}

}
