import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frost {
	static byte[][] m1 = { { -1, 0 }, { 0, -1 } };
	static byte[][] m11 = { { -1, 0, 0}, { 0, -1, -1 } };
	static byte[][] m2 = { {-1, 0}, { 0, 0 }, { 0, -1 } };
	static byte[][] m22 = { { -1, 0 }, { 0, 0 }, { 0, 0 }, { 0, -1 } };
	static byte[][] m3 = { { -1, 0, 0 }, { 0, 0, 0 }, { 0, 0, -1 } };
	static byte[][] m4 = { { -1, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, -1 } };
	static byte[][] m68 = { { 0, 0, -1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, -1, 0, 0 } };
	static byte startingPlayer = 1;
	static State root = new State(startingPlayer, m68, false, new Point(0, 0), new Point(7, 5));
	static int wins1 = 0, wins2 = 0;

	public static void main(String[] args) throws CloneNotSupportedException {
		joga(root, startingPlayer);

		System.out.println(root.getAllTree().size() + " Nodos");
		System.out.println("Wins players 1: " + wins1);
		System.out.println("Wins players 2: " + wins2);
	}

	private static int joga(State n, byte player) throws CloneNotSupportedException {
		List<Point> moves = validMoves(player, n);
		if (moves.size() > 0) {
			for (Point dest : moves) {
				State f = move(n, player, dest);
				// System.out.println(f);
				if (!contains(f) /* && !n.visitado */) {
					n.filhos.add(f);
					f.pai = n;
					// System.out.println(root.getAllTree().size());
					State r = remove(f, player);
					if (r != null && !contains(r)) {
						f.filhos.add(r);
						r.pai = f;
						// recursao
						if (player == 1) {
							joga(r, (byte) 2);
						} else if (player == 2) {
							joga(r, (byte) 1);
						}
						// f.visitado = true;
					}
					// System.out.println(r);
				}
			}
		} else {
			if (player == 1) {
				wins2++;
			} else if (player == 2) {
				wins1++;
			}
		}
		return player;
	}

	private static State remove(State n, byte player) throws CloneNotSupportedException {
		for (int i = 0; i < n.board.length; i++) {
			for (int j = 0; j < n.board[i].length; j++) {
				if (n.board[i][j] == 0 && (n.p1.x != i || n.p1.y != j) && (n.p2.x != i || n.p2.y != j)) {
					// casa possui bloco e não tem player
					byte[][] b = deepCopy(n.board);
					b[i][j] = -1;
					State f = new State(player, b, false, (Point) n.p1.clone(), (Point) n.p2.clone());
					f.moved = false;
					f.board = b;
					return f;
				}
			}
		}
		if (player == 1) {
			wins1++;
		} else if (player == 2) {
			wins2++;
		}
		// n.visitado = true;
		return null;
	}

	private static State move(State n, byte player, Point move) {
		byte[][] b = deepCopy(n.board);
		Point p1 = new Point(n.p1.x, n.p1.y), p2 = new Point(n.p2.x, n.p2.y);
		if (player == 1) {
			p1 = new Point(move.x, move.y);
			// b[p1.x][p1.y] = -1;
		} else {
			p2 = new Point(move.x, move.y);
			// b[p2.x][p2.y] = -1;
		}
		State d = new State(player, b, true, p1, p2);
		return d;
	}

	private static boolean isMoveValido(State n, int player, Point dest) {
		if (n.board[dest.x][dest.y] == 0 && (n.p1.x != dest.x || n.p1.y != dest.y)
				&& (n.p2.x != dest.x || n.p2.y != dest.y)) {
			Point ori = player == 1 ? n.p1 : n.p2;// posicao(n, player);
			int deltaI = dest.x - ori.x;
			int deltaJ = dest.y - ori.y;
			if (deltaI != 0 && deltaJ != 0) {
				if (Math.abs(deltaI) != Math.abs(deltaJ)) {
					// movimento nao linear
					return false;
				}
			}
			int incI = deltaI == 0 ? 0 : deltaI > 0 ? 1 : -1;
			int incJ = deltaJ == 0 ? 0 : deltaJ > 0 ? 1 : -1;
			int distance = Math.max(Math.abs(deltaI), Math.abs(deltaJ));

			int count = 0;
			// verifica o path ate o destino esta ok
			for (int i = ori.x + incI, j = ori.y + incJ; i > -1 && i < n.board.length && j > -1 && j < n.board[i].length
					&& n.board[i][j] == 0 && count < distance; i += incI, j += incJ) {
				count++;
			}
			if (count > 0 && count == distance) {
				return true;
			}
		}
		return false;
	}

	private static List<Point> validMoves(int player, State s) {
		List<Point> moves = new ArrayList<Point>();
		for (int i = 0; i < s.board.length; i++) {
			for (int j = 0; j < s.board[i].length; j++) {
				Point p = new Point(i, j);
				if (isMoveValido(s, player, p)) {
					moves.add(p);
				}
			}

		}
		return moves;
	}

	public static byte[][] deepCopy(byte[][] original) {
		if (original == null) {
			return null;
		}

		final byte[][] result = new byte[original.length][];
		for (int i = 0; i < original.length; i++) {
			result[i] = Arrays.copyOf(original[i], original[i].length);
			// For Java versions prior to Java 6 use the next:
			// System.arraycopy(original[i], 0, result[i], 0,
			// original[i].length);
		}
		return result;
	}

	private static boolean contains(State n) {
		for (State d : root.getAllTree()) {
			if (d.equals(n)) {
				return true;
			}
		}
		return false;
	}
}