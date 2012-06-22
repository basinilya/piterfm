package ru.piter.fm.exception;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.06.12
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class NoInternetException extends Exception {

    public NoInternetException(String detailMessage) {
        super(detailMessage);
    }

}
