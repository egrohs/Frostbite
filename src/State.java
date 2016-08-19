import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class State implements Cloneable {
	public boolean moved;
	public Point p1;// = new Point(0, 0);
	public Point p2;// = new Point(1, 1);
	public byte[][] board;// = { { 1, 0 }, { 0, 2 } };
	public State pai;
	public List<State> filhos = new ArrayList<State>();
	public int player;
	//public boolean visitado;

	public State(int player, byte[][] b, boolean moved, Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.player = player;
		this.moved = moved;
		board = b.clone();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof State)) {
			return false;
		}
		State o2 = (State) obj;
		if (player != o2.player) {
			return false;
		}
		if (p1.x != o2.p1.x || p1.y != o2.p1.y) {
			return false;
		}

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != o2.board[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	// @Override
	public int hashCode2() {
		int sum = 0, sub = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				sum += board[i][j];
				sub -= board[i][j];
			}
		}
		// result = 37 * result + c
		return sub / (sum == 0 ? 1 : sum);
	}

	@Override
	public String toString() {
		String ac = moved ? "moveu" : "removeu";
		StringBuilder s = new StringBuilder("Player " + player + " " + ac + " hash:" + hashCode() + "\n");
		for (int i = 0; i < board.length; i++) {
			s.append("[");
			for (int j = 0; j < board[i].length; j++) {
				s.append(board[i][j] + ", ");
			}
			s.append("]\n");
		}
		return s.toString();
	}

	public int treeSize() {
		int count = 0;
		if (filhos.size() == 0) {
			return 1;
		}
		for (State c : filhos) {
			count += c.treeSize();
		}
		return count + 1;
	}

	public List<State> getAllTree() {
		List<State> all = new ArrayList<State>();
		if (filhos.size() != 0) {
			for (State c : filhos) {
				all.addAll(c.getAllTree());
			}
		}
		all.add(this);
		return all;
	}
}
