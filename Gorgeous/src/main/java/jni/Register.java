package jni;

public class Register {
    public static native String EncodeExistRequest(String params, byte[] env);
    public static native String CodeRequest(String params, byte[] env);
    public static native String Register(String params, byte[] env);
}
