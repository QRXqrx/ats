package nju.pa.ats.exception;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-15
 */
public class NullTargetMethodException extends RuntimeException {
    static final long serialVersionUID = 897190745766939L;

    public NullTargetMethodException() {
    }

    public NullTargetMethodException(String message) {
        super(message);
    }

}
