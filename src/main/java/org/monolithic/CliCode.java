package org.monolithic;

public enum CliCode {
    ERROR_NO_REPEAT_OP(-2),
    ERROR_REPEAT_OP(-1),

    MAIN_MENU(0),
    NO_ERROR_NO_REPEAT_OP(1);

    private final int code;

    CliCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
