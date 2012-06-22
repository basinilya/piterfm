package ru.piter.fm.exception;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.06.12
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class NoSDCardException extends Exception {

    public NoSDCardException(String detailMessage) {
        super(detailMessage);
    }
}
