package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 01/02/15.
 */
public interface RushSearchCallback<T> {
    public void complete(List<T> results);
}
