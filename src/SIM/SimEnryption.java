package SIM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Arrays;

import SIM.models.PlayerCode;
import SIM.models.SimGraph;

public class SimEnryption {
    private static final int SIZE = 6;

    public static void main(String[] args) {
        File f = new File("assets\\test2.pdf");

        System.out.println(generateKeyByFile(f));
    }

    public static boolean encrypt(File f) {
        System.out.println("SIM encryption");

        String oKey = generateKeyByFile(f);

        int keySize = oKey.length();

        int baseNum = sumDigits(Integer.parseInt(oKey.substring(0, keySize / 2)));
        int shuffleNum = sumDigits(Integer.parseInt(oKey.substring(keySize / 2)));

        List<int[]> combs = threeNumSumComb(baseNum);
        Queue<int[]> shuffledCombs = shuffleCombs(combs, shuffleNum);
        byte[] file;

        try {
            file = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Error reading file : " + e);
            return false;
        }

        for (int i = 0; i < file.length; i++) {
            int[] comb = shuffledCombs.poll();

            if (comb == null) {
                System.out.println("Error: comb is null at index " + i);
                return false;
            }

            file[i] ^= comb[0];
            file[i] ^= comb[1];
            file[i] ^= comb[2];

            shuffledCombs.add(comb);
        }

        File encryptedFile = new File(f.getParentFile(), "Encrypted" + f.getName());

        try (FileOutputStream fos = new FileOutputStream(encryptedFile)) {
            fos.write(oKey.getBytes(StandardCharsets.UTF_8));

            fos.write((byte) '\n');

            fos.write(file);

        } catch (Exception e) {
            System.out.println("Error while writing encrypted file : " + e);
            return false;
        }

        return true;
    }

    public static boolean decrypt(File f) {
        System.out.println("SIM decryption");

        byte splitChar = (byte) '\n';
        byte[] file;

        try {
            file = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Error reading file : " + e);
            return false;
        }

        int newlineIndex;
        for (newlineIndex = 0; newlineIndex < file.length; newlineIndex++) {
            if (file[newlineIndex] == splitChar)
                break;
        }

        byte[] fileData = Arrays.copyOfRange(file, newlineIndex + 1, file.length);

        String oKey = new String(file, 0, newlineIndex, StandardCharsets.UTF_8);
        int keySize = oKey.length();

        int baseNum = sumDigits(Integer.parseInt(oKey.substring(0, keySize / 2)));
        int shuffleNum = sumDigits(Integer.parseInt(oKey.substring(keySize / 2)));

        List<int[]> combs = threeNumSumComb(baseNum);
        Queue<int[]> shuffledCombs = shuffleCombs(combs, shuffleNum);

        for (int i = 0; i < fileData.length; i++) {
            int[] comb = shuffledCombs.poll();

            if (comb == null) {
                System.out.println("Error: comb is null at index " + i);
                return false;
            }

            fileData[i] ^= comb[0];
            fileData[i] ^= comb[1];
            fileData[i] ^= comb[2];

            shuffledCombs.add(comb);
        }

        File decryptedFile = new File(f.getParentFile(), "Decrypted" + f.getName());

        try (FileOutputStream fos = new FileOutputStream(decryptedFile)) {
            fos.write(fileData);

        } catch (Exception e) {
            System.out.println("Error while writing encrypted file : " + e);
            return false;
        }

        return true;
    }

    private static int sumDigits(int num) {
        int sum = 0;
        while (num != 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }

    private static Queue<int[]> shuffleCombs(List<int[]> combs, int shuffleNum) {
        Queue<int[]> result = new LinkedList<int[]>();

        while (!combs.isEmpty()) {
            for (int i = 0; i < shuffleNum; i++) {
                int[] temp = result.poll();

                if (temp == null) {
                    break;
                }

                result.add(temp);
            }

            result.add(combs.removeFirst());
        }

        return result;
    }

    private static List<int[]> threeNumSumComb(int baseNum) {
        List<int[]> result = new ArrayList<>();

        for (int i = 0; i <= baseNum / 3; i++) {
            for (int j = i; j <= (baseNum - i) / 2; j++) {
                int k = baseNum - i - j;
                result.add(new int[] { i, j, k });
            }
        }

        return result;
    }

    private static String generateKeyByFile(File f) {
        SimGraph game = generateGameByFile(f.toPath());
        PlayerCode result = PlayerCode.None;
        StringBuilder key = new StringBuilder();

        System.out.println("============Game Start=============");

        while (result == PlayerCode.None) {
            result = takeTurn(game, PlayerCode.PlayerOne, key);

            if (result != PlayerCode.None)
                break;

            result = takeTurn(game, PlayerCode.PlayerTwo, key);
        }

        System.out.println(result + " WON!!!");

        return key.toString();
    }

    private static SimGraph generateGameByFile(Path path) {
        System.out.println("============Graph Generation=============");
        byte[] movesBuffer = new byte[SIZE * SIZE];
        SimGraph game = new SimGraph(SIZE);

        try (InputStream in = Files.newInputStream(path)) {
            in.read(movesBuffer);
        } catch (IOException e) {
            System.out.println("Couldn't read the file : " + e);
            System.out.println("Returning empty game board");
            return game;
        }

        for (int i = 0; i < movesBuffer.length; i++) {
            byte curMove = movesBuffer[i];

            int a = (curMove >> 5) & 0b111; // 3 leftmost bits
            int b = curMove & 0b111; // 3 rightmost bits
            int playerBits = (curMove >> 3) & 0b11; // 2 middle bits

            PlayerCode player = switch (playerBits) {
                case 0b10 -> PlayerCode.PlayerOne;
                case 0b11 -> PlayerCode.PlayerTwo;
                default -> null;
            };

            if (player == null || a >= SIZE || b >= SIZE || a == b)
                continue;

            PlayerCode[][] board = game.getBoard();

            if (board[a][b] == PlayerCode.None && !wouldCreateTriangle(board, a, b, player)) {
                game.connect(a, b, player);
            }
        }

        return game;
    }

    private static PlayerCode takeTurn(SimGraph game, PlayerCode curPlayer, StringBuilder key) {
        int[] move = nextMove(game, curPlayer);
        PlayerCode res = game.connect(move[0], move[1], curPlayer);

        if (res != null) {
            key.append(move[0]);
            key.append(move[1]);
        }

        return res;
    }

    private static int[] nextMove(SimGraph game, PlayerCode curPlayer) {
        PlayerCode[][] board = game.getBoard();
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < game.getSize(); i++) {
            for (int j = i + 1; j < game.getSize(); j++) {
                if (board[i][j] != PlayerCode.None)
                    continue;

                int score = evaluateMove(board, i, j, curPlayer);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new int[] { i, j };
                }
            }
        }

        return bestMove;
    }

    private static int evaluateMove(PlayerCode[][] board, int a, int b, PlayerCode curPlayer) {
        int score = 1000;

        int degreeA = 0, degreeB = 0; // The amount of edges connected to a and b
        for (int i = 0; i < board.length; i++) {
            if (board[a][i] == curPlayer)
                degreeA++;
            if (board[b][i] == curPlayer)
                degreeB++;
        }

        score -= 100 * (degreeA >= 2 ? 1 : 0);
        score -= 100 * (degreeB >= 2 ? 1 : 0);
        score -= 20 * (degreeA + degreeB);

        if (wouldCreateTriangle(board, a, b, curPlayer)) {
            score -= 1000;
        }

        return score;
    }

    private static boolean wouldCreateTriangle(PlayerCode[][] board, int a, int b, PlayerCode color) {
        for (int i = 0; i < board.length; i++) {
            if (i == a || i == b)
                continue;
            if (board[a][i] == color && board[b][i] == color)
                return true;
        }
        return false;
    }
}
