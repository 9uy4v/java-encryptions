package gould;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class GouldEncryption {

    public static void main(String[] args) {
        File f = new File("assets\\test.png");
        System.err.println(generateKeyByFile(f));
    }

    public static boolean encrypt(File f) {
        String oKey = generateKeyByFile(f);

        byte[] keyMasks = createKeyMasks(oKey);

        byte[] file;
        try {
            file = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            System.out.println("Error reading file : " + e);
            return false;
        }

        for (int i = 0; i < file.length; i++) {
            int shuffleIndex = i % keyMasks.length;

            file[i] = (byte) (file[i] ^ keyMasks[shuffleIndex]);

            rotateKeyMasks(keyMasks, getRotationValue(oKey, i));
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

        byte[] keyMasks = createKeyMasks(oKey);

        for (int i = 0; i < fileData.length; i++) {
            int shuffleIndex = i % keyMasks.length;

            fileData[i] = (byte) (fileData[i] ^ keyMasks[shuffleIndex]);

            rotateKeyMasks(keyMasks, getRotationValue(oKey, i));
        }

        File decryptedFile = new File(f.getParentFile(), "Decrypted" + f.getName());

        try (FileOutputStream fos = new FileOutputStream(decryptedFile)) {
            fos.write(fileData);
        } catch (Exception e) {
            System.out.println("Error while writing decrypted file : " + e);
            return false;
        }

        return true;
    }

    private static byte[] createKeyMasks(String key) {
        byte[] masks = new byte[40];

        for (int i = 0; i < masks.length; i++) {
            String digitPair;

            int pos = (i * 2) % key.length();

            // Key size is NOT constant
            if (pos + 1 < key.length()) {
                digitPair = key.substring(pos, pos + 2);
            } else {
                digitPair = key.substring(pos) + key.charAt(0);
            }

            int maskValue = Integer.parseUnsignedInt(digitPair, 16) % 256;
            masks[i] = (byte) maskValue;
        }

        return masks;
    }

    private static int getRotationValue(String keyString, int position) {
        int charIndex = position % keyString.length();
        char keyChar = keyString.charAt(charIndex);

        return (keyChar % 3) + 1;
    }

    private static void rotateKeyMasks(byte[] masks, int rotateAmount) {
        byte[] tempMasks = Arrays.copyOf(masks, masks.length);

        for (int j = 0; j < masks.length; j++) {
            int newPosition = (j + rotateAmount) % masks.length;
            masks[newPosition] = tempMasks[j];
        }
    }

    private static String generateKeyByFile(File f) {
        int bufferSize = (int) (f.length() / 4);

        int[] chunkBuffer = new int[bufferSize];
        int[] sequence = getSequence(bufferSize);

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            // Move 4 bytes at a time to the buffer
            int index = 0;

            while (dis.available() >= 4) {
                chunkBuffer[index++] = dis.readInt();
            }
        } catch (Exception e) {
            System.out.println("Error reading file : " + e);

            return null;
        }

        for (int i = 0; i < bufferSize; i++) {
            chunkBuffer[i] ^= sequence[i];
        }

        return foldToString(chunkBuffer, 8);
    }

    private static String foldToString(int[] arr, int size) {
        StringBuilder foldedString = new StringBuilder();
        int[] buffer = new int[size];
        Arrays.fill(buffer, 0);

        int i = 0;

        while (i < arr.length) {
            for (int j = 0; j < buffer.length && i < arr.length; i++, j++) {
                buffer[j] ^= arr[i];
            }
        }

        for (int fChunk : buffer) {
            foldedString.append(String.format("%08X", fChunk & 0x7FFFFFFF)); // & removes any negative values
        }

        return foldedString.toString();
    }

    private static int[] getSequence(int t) {
        int[] sequence = new int[t];

        for (int i = 0; i < t; i++) {
            int count = 1, n = i;

            while (n > 0) {
                if ((n & 1) == 1) {
                    count *= 2;
                }
                n >>= 1;
            }
            sequence[i] = count;
        }

        return sequence;
    }
}
