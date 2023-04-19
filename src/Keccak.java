import java.util.Arrays;

public class Keccak {
    public static final int KECCAK_224 = 224;
    public static final int KECCAK_256 = 256;
    public static final int KECCAK_384 = 384;
    public static final int KECCAK_512 = 512;

    private final int NumOfRounds = 24;

    private final long[] RoundConstants = {
            0x00000001, 0x00008082, 0x0000808A, 0x80008000, 0x0000808B, 0x80000001,
            0x80008081, 0x00008009, 0x0000008A, 0x00000088, 0x00008009, 0x000000A0,
            0x8000808B, 0x8000008B, 0x80008089, 0x80008003, 0x00008002, 0x8000000A,
            0x0000808B, 0x000000A3, 0x80008081, 0x80000081, 0x00008080, 0x80008008,
            0x00000083, 0x00008003, 0x00000088, 0x80008082, 0x00008088, 0x8000000B,
            0x0000008B, 0x00000081, 0x00000082, 0x00000080, 0x0000800B, 0x80000001,
            0x80000008, 0x80008008, 0x00008081, 0x80000081, 0x80008001, 0x00008008
    };

    private final int[][] RotationOffsets = {
            { 0, 36, 3, 41, 18 },
            { 1, 44, 10, 45, 2 },
            { 62, 6, 43, 15, 61 },
            { 28, 55, 25, 21, 56 },
            { 27, 20, 39, 8, 14 }
    };

    private int bitSize;
    private int blockSize;
    private byte[] state;
    private long[] lane;

    public Keccak() {
        this(KECCAK_256);
    }

    public Keccak(int bitSize) {
        if (bitSize != KECCAK_224 && bitSize != KECCAK_256 && bitSize != KECCAK_384 && bitSize != KECCAK_512)
            throw new IllegalArgumentException("Invalid bit size: " + bitSize);
        this.bitSize = bitSize;
        this.blockSize = 200 - 2 * bitSize / 8;
        this.state = new byte[200];
        Arrays.fill(this.state, (byte) 0x00);
        this.lane = new long[25];
    }

    public byte[] hash(byte[] input) {
        byte[] paddedInput = padInput(input);
        for (int i = 0; i < paddedInput.length; i += blockSize) {
            for (int j = 0; j < blockSize; j++)
                state[j] ^= paddedInput[i + j];
            absorb();
        }

        byte[] output = squeeze();

        return output;
    }

    private byte[] padInput(byte[] input) {
        int inputLength = input.length;
        int paddingLength = blockSize - (inputLength % blockSize);
        byte[] paddedInput = new byte[inputLength + paddingLength];
        System.arraycopy(input, 0, paddedInput, 0, inputLength);
        paddedInput[inputLength] = (byte) 0x01;
        paddedInput[paddedInput.length - 1] |= (byte) 0x80;
        return paddedInput;
    }

    private static void bytesToLanes(byte[] input, long[] output) {
        for (int i = 0; i < 25; i++) {
            output[i] = 0;
            for (int j = 0; j < 8; j++) {
                output[i] |= ((long) input[i * 8 + j] & 0xFF) << (j * 8);
            }
        }
    }

    private static void lanesToBytes(long[] input, byte[] output) {
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 8; j++) {
                output[i * 8 + j] = (byte) ((input[i] >> (j * 8)) & 0xFF);
            }
        }
    }

    private void absorb() {
        bytesToLanes(state, lane);
        for (int i = 0; i < NumOfRounds; i++) {
            roundFunction(i);
        }
        lanesToBytes(lane, state);
    }

    private byte[] squeeze() {
        byte[] output = new byte[bitSize / 8];
        System.arraycopy(state, 0, output, 0, output.length);
        return output;
    }

    private void roundFunction(int round) {
        theta();
        rhoPi();
        chi();
        iota(round);
    }

    public void theta() {
        long[] c = new long[5];
        long[] d = new long[5];
        for (int i = 0; i < 5; i++) {
            c[i] = lane[i] ^ lane[i + 5] ^ lane[i + 10] ^ lane[i + 15] ^ lane[i + 20];
        }
        for (int i = 0; i < 5; i++) {
            d[i] = c[(i + 4) % 5] ^ Long.rotateLeft(c[(i + 1) % 5], 1);
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 25; j += 5) {
                lane[j + i] ^= d[i];
            }
        }
    }

    public void rhoPi() {
        long[] temp = new long[25];
        for (int i = 0; i < 25; i++) {
            temp[i] = lane[i];
        }
        for (int i = 0; i < 25; i++) {
            lane[piIndex(i)] = Long.rotateLeft(temp[i], RotationOffsets[i / 5][i % 5]);
        }
    }

    public int piIndex(int i) {
        return (i * 2) % 5 + (i * 3) % 5 * 5;
    }

    public void chi() {
        long[] temp = new long[25];
        for (int i = 0; i < 25; i++) {
            temp[i] = lane[i];
        }
        for (int i = 0; i < 25; i++) {
            lane[i] = temp[i] ^ ((~temp[(i + 1) % 5]) & temp[(i + 2) % 5]);
        }
    }

    public void iota(int round) {
        lane[0] ^= RoundConstants[round];
    }
}