package it.unimib.flexfit.util;
import java.util.List;
public interface ResponseCallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
}