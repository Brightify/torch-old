package org.brightify.torch.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface OrderLoader<ENTITY> {

    OrderDirectionLimitListLoader<ENTITY> orderBy(String columnName);

    public enum Direction {
        ASCENDING, DESCENDING
    }

}
