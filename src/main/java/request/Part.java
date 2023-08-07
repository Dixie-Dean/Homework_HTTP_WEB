package request;

public class Part {
    private String value;
    private byte[] data;

    public Part(byte[] data) {
        this.data = data;
    }

    public Part(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public byte[] getData() {
        return data;
    }
}
