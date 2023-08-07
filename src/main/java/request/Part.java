package request;

import java.util.Arrays;

public class Part {
    private String value;
    private byte[] data;

    public Part(byte[] data) {
        this.data = data;
    }

    public Part(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value != null) {
            return value;
        } else if (data != null) {
            return Arrays.toString(data);
        } else {
            return null + " | " + Arrays.toString((byte[]) null);
        }
    }
}
